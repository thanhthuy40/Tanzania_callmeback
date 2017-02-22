/**
 * @desc:ProcessUssdThread.java - com.viettel.cmb.ussd.process
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.process;

import org.apache.log4j.Logger;

import com.viettel.cmb.ussd.database.DbAdapter;
import com.viettel.cmb.ussd.database.DbCmPosAdapter;
import com.viettel.cmb.ussd.database.DbCmPreAdapter;
import com.viettel.cmb.ussd.utils.StateOfSub;
import com.viettel.cmb.ussd.utils.SubType;
import com.viettel.cmb.ussd.utils.UoHis;
import com.viettel.gateway.ussdgw.common.UssdMessage;
import com.viettel.ussd.connector.LogUssdMessage;

/**
 * @author thanhhn5
 */
public abstract class ProcessUssdThread
		implements Runnable {
	private Logger				log	= Logger.getLogger(ProcessUssdThread.class);
	protected UssdMessage		ussd;
	protected UoHis				uo;
	protected DbAdapter			db;
	protected String			lang;
	protected UssdQueue			queue;
	protected DbCmPosAdapter	pos;
	protected DbCmPreAdapter	pre;

	public ProcessUssdThread(UssdMessage ussd, String dbPath, UssdQueue ussdQueue, DbCmPosAdapter pos, DbCmPreAdapter pre) {
		this.ussd = ussd;
		this.uo = new UoHis();
		this.queue = ussdQueue;
		this.db = DbAdapter.getInstancce(dbPath);
		this.lang = this.db.getLangugeOfSub(this.ussd.getMsisdn());
		if (this.lang == null) {
			this.lang = "EN";
		}
		this.pos = pos;
		this.pre = pre;
	}

	protected StateOfSub subsIsPre() {
		return this.pre.checkSubsciberIsPrepaidAndNotBlock2Way(this.ussd
				.getMsisdn());
	}

	protected StateOfSub subIsPost() {
		if (this.pos.CheckSubsciberIsPost(this.ussd.getMsisdn()))
			return StateOfSub.POST_SUB;
		return StateOfSub.ERROR;
	}

	public abstract void initBeforeStart();

	public abstract boolean validateContraint();

	protected StateOfSub subscriberIsCondition() {
		StateOfSub postState = subIsPost();
		if (postState == StateOfSub.POST_SUB) {
			this.log.info("Subscriber " + this.ussd.getMsisdn() +
					" is post ==> not enougt condition");
			this.uo.setErrCode(String.valueOf(1));
			this.uo.setSubType(SubType.POST.ordinal());
			return StateOfSub.POST_SUB;
		}
		StateOfSub statePre = subsIsPre();
		if (statePre == StateOfSub.UNKNOW) {
			this.log.info("Subscriber is unknow and not enought condition ==> Continues");
			this.uo.setErrCode(String.valueOf(4));
			this.uo.setSubType(SubType.UNKNOW.ordinal());
			return StateOfSub.ERROR;
		}
		if (statePre == StateOfSub.ERROR) {
			this.log.info("Query exchange is error ==> Continues");
			this.uo.setErrCode(String.valueOf(7));
			this.uo.setErrExchange(String.valueOf(7));
			this.uo.setSubType(SubType.UNKNOW.ordinal());
			return StateOfSub.ERROR;
		}
		if (statePre == StateOfSub.BLOCK2WAY) {
			this.log.info("Subscriber is Prepaid and not enough condition ==> Continues");
			this.uo.setErrCode(String.valueOf(3));
			this.uo.setSubType(SubType.PRE.ordinal());
			return StateOfSub.BLOCK2WAY;
		}
		this.uo.setSubType(SubType.PRE.ordinal());
		return StateOfSub.PRESUB;
	}

	protected String getSubscriber() {
		return this.ussd.getUssdString().substring(getLastIndex() + 1,
				this.ussd.getUssdString().length() - 1);
	}

	private int getLastIndex() {
		return this.ussd.getUssdString().lastIndexOf("*");
	}

	protected abstract boolean processUo();

	public void run() {
		initBeforeStart();
		this.log.info("------------------------------- Process USSD Request -----------------------");
		LogUssdMessage.print(this.ussd);

		this.log.info("Step 1: validate contraint for msisdn " +
				this.ussd.getMsisdn());
		if (!validateContraint()) {
			this.log.info(" validate request from " + this.ussd.getMsisdn() +
					" failed ==> return mesage ");
			insertUoHis();
			return;
		}

		this.log.info("Step 2: process Ussd for msisdn " + this.ussd.getMsisdn());
		if (!processUo()) {
			this.log.info("proces UO failed ==> return mesage ");
			insertUoHis();
			return;
		}
		this.log.info("Transaction " + this.ussd.getTransId() + " - msisdn :" +
				this.ussd.getMsisdn() + " - Command :" +
				this.ussd.getUssdString() + " - Error_code: 0 ");
	}

	private int insertUoHis() {
		this.log.info("Insert UO_HIS ");
		int subID = this.db.getSubscriberID(this.ussd.getMsisdn());
		if (subID == -1) {
			this.log.info("the firstime user send to app ==> insert new subscriber");
			subID = this.db.insertSubscriberHis(this.ussd);
		}
		this.uo.setSubId(subID);
		int er = this.db.insertUoHis(this.uo);
		if (er == -1) {
			this.log.error("USSD_INSERT_FAILED_01: Can't insert uo_his");
		}
		return er;
	}
}
