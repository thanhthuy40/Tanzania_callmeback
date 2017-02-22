/**
 * @desc:UssdValidateThread.java - com.viettel.cmb.ussd.process
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.process;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.viettel.cmb.ussd.config.ProcessMap;
import com.viettel.cmb.ussd.database.DbAdapter;
import com.viettel.cmb.ussd.database.DbCmPosAdapter;
import com.viettel.cmb.ussd.database.DbCmPreAdapter;
import com.viettel.cmb.ussd.transaction.TransactionMapping;
import com.viettel.cmb.ussd.utils.MessageUtils;
import com.viettel.cmb.ussd.utils.UoHis;
import com.viettel.cmb.ussd.utils.UssdUtils;
import com.viettel.gateway.ussdgw.common.UssdMessage;
import com.viettel.thread.process.ThreadPool;

public class UssdValidateThread implements Runnable {
	private Logger			log	= Logger.getLogger(UssdValidateThread.class);

	private UssdMessage		ussd;
	private DbAdapter		db;
	private UssdQueue		queue;
	private String			path;
	private ThreadPool		pathConfigPoolExecute;
	private DbCmPosAdapter	pos;
	private DbCmPreAdapter	pre;

	public UssdValidateThread(UssdMessage ussd, String pathDb, UssdQueue queue, ThreadPool pathConfigPoolExecute, DbCmPosAdapter pos, DbCmPreAdapter pre) {
		if (ussd == null)
			throw new NullPointerException();
		this.ussd = ussd;
		this.path = pathDb;
		this.pathConfigPoolExecute = pathConfigPoolExecute;
		this.db = DbAdapter.getInstancce(pathDb);
		this.queue = queue;
		this.pos = pos;
		this.pre = pre;
	}

	public void run() {
		if (this.ussd.getType() == UssdMessage.USSDMSG_TYPE_SUB_RECV_OK) {
			this.log.info("receiver info from subscriber ==> update UT messaage status");
			this.log.info("Update time and amount to next");
			UssdMessage ussds = TransactionMapping.getInstance().get(
					this.ussd.getTransId());
			if (ussds == null) {
				this.log.error("Transaction not exist ");
				return;
			}
			this.db.updateSubscriberAmount(ussds.getMsisdn());

			this.db.updateResuftToUoHis(0, 0, ussds.getTransId());

			this.db.updateResuftToUtHis(0, ussds.getTransId());
			return;
		}
		if (this.ussd.getType() == UssdMessage.USSDMSG_TYPE_TRANS_ERR) {
			UssdMessage ussds = TransactionMapping.getInstance().get(
					this.ussd.getTransId());
			if (ussds == null) {
				this.log.error("Transaction not exist ");
				return;
			}
			this.log.info("receiver info from subscriber error ==> update UT messaage status");

			this.db.updateResuftToUoHis(104,
					104, ussds.getTransId());

			this.db.updateResuftToUtHis(104,
					ussds.getTransId());
			//TODO: thạnhhn5 - add fix log
			return;
		}
		String lang = this.db.getLangugeOfSub(this.ussd.getMsisdn());
		if (lang == null) {
			this.log.info("Subscriber not exist ==> default lang = EN");
			lang = "EN";
		}
		this.log.info("msisdn:" + this.ussd.getMsisdn() + " - Languge : " + lang);

		int processId = isVaildSyntax();
		if (processId == -1) {
			this.log.info("msisdn :" + this.ussd.getMsisdn() + " - Error code : " +
					7);
			this.queue.enQueue(UssdUtils.buildMessage(
					getMsg("SYNTAXERROR_" + lang), this.ussd.getMsisdn(),
					this.ussd.getTransId(), false, this.ussd));
			insertUoHis(-1);
			return;
		}

		try {
			routingMessage(processId);
		}
		catch (Exception e) {
			this.queue.enQueue(UssdUtils.buildMessage(
					getMsg("SYS_ERROR_MESSAGE_" + lang),
					this.ussd.getMsisdn(), this.ussd.getTransId(), false,
					this.ussd));
			this.log.error("Have error int rounting class ", e);
			insertUoHis(-1);
			return;
		}
	}

	private String getMsg(String key) {
		return UssdUtils.formatMessage(MessageUtils.getMessage(key), true);
	}

	private void insertUoHis(int errorCode) {
		int subID = this.db.getSubscriberID(this.ussd.getMsisdn());
		if (subID == -1) {
			this.log.info("the firstime user send to app ==> insert new subscriber");
			subID = this.db.insertSubscriberHis(this.ussd);
			if (subID == -1) {
				this.log.error("Can't insert to Subscriber ==> Failed");
			}
		}
		else {
			this.log.info("Update time and amount to next");
			this.db.updateSubscriberAmount(this.ussd.getMsisdn());
		}
		UoHis uo = UssdUtils.convertToUoHis(this.ussd, errorCode, subID,
				UssdUtils.UNKNOW, 0, UssdUtils.UNKNOW, UssdUtils.UNKNOW,
				this.ussd.getTransId());
		if (this.db.insertUoHis(uo) == -1) {
			this.log.warn("insert UO_HIS failed");
		}
	}

	private void routingMessage(int processId)
			throws ClassNotFoundException, java.security.InvalidAlgorithmParameterException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		long timeStart = System.currentTimeMillis();
		ClassLoader cl = new ClassLoader() {
		};
		Class<?> c = cl.loadClass(MessageUtils.getClassName(Integer.valueOf(processId)));
		this.log.info("===> Load class: " + c.getName());
		long end = System.currentTimeMillis();
		this.log.info("======================================================");
		this.log.info("Excute load class time :" + (end - timeStart) + " ms");
		this.log.info("========================================================");
		this.pathConfigPoolExecute.addTask(
				(Runnable) c.getDeclaredConstructor(new Class[] { UssdMessage.class, String.class, UssdQueue.class, DbCmPosAdapter.class, DbCmPreAdapter.class })
						.newInstance(new Object[] {
								this.ussd, this.path, this.queue, this.pos, this.pre }));
	}

	private int isVaildSyntax() {
		int rs = -1;
		String syntax = this.ussd.getUssdString();
		if (syntax == null) {
			this.log.info("Can't read synctax of subscriber " +
					this.ussd.getMsisdn());
			return rs;
		}
		Iterator<?> localIterator = com.viettel.cmb.ussd.config.ConfigConst.lstProcessMap.entrySet().iterator();
		while (localIterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<Integer, ProcessMap> entry = (Map.Entry<Integer, ProcessMap>) localIterator.next();
			Integer key = entry.getKey();
			ProcessMap value = entry.getValue();
			System.out.println("REGEX :" + value.getRegex());
			System.out.println("SYNTAX :" + syntax);
			if (syntax.trim().matches(value.getRegex().trim())) {
				this.log.info("Receiver valid ussd from " + this.ussd.getMsisdn() +
						" with syntax : " + syntax);
				rs = key.intValue();
				break;
			}
		}
		return rs;
	}
}
