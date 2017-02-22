/**
 * @desc:ChangeLang.java - com.viettel.cmb.ussd.business
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.business;

import java.util.Date;

import org.apache.log4j.Logger;

import com.viettel.cmb.ussd.database.DbCmPosAdapter;
import com.viettel.cmb.ussd.database.DbCmPreAdapter;
import com.viettel.cmb.ussd.process.ProcessUssdThread;
import com.viettel.cmb.ussd.process.UssdQueue;
import com.viettel.cmb.ussd.utils.ActionType;
import com.viettel.cmb.ussd.utils.MessageUtils;
import com.viettel.cmb.ussd.utils.UssdUtils;
import com.viettel.gateway.ussdgw.common.UssdMessage;

/**
 * @author thanhhn5
 */
public class ChangeLang
		extends ProcessUssdThread {
	private Logger log = Logger.getLogger(ChangeLang.class);

	public ChangeLang(UssdMessage ussd, String dbPath, UssdQueue ussdQueue, DbCmPosAdapter pos, DbCmPreAdapter pre) {
		super(ussd, dbPath, ussdQueue, pos, pre);
	}

	public void initBeforeStart() {
		setUo();
	}

	public boolean validateContraint() {
		if (!this.db.checkSubscriberIsexist(this.ussd.getMsisdn())) {
			this.db.insertSubscriberHis(this.ussd);
		}
		return true;
	}

	protected boolean processUo() {
		if (isChangeToEnglish()) {
			this.log.info("receiver request change to english");

			if (!this.db.changeLanguge(this.ussd.getMsisdn(), UssdUtils.getPrefixLangEng())) {
				sendBackErrorMessage();
			}
			sendBackClient(false, this.ussd.getMsisdn());
			return true;
		}

		if (isChangeToSwalihi()) {
			this.log.info("receiver request change to swalihi");

			if (!this.db.changeLanguge(this.ussd.getMsisdn(), UssdUtils.getPrefixLangSwali())) {
				sendBackErrorMessage();
			}
			sendBackClient(true, this.ussd.getMsisdn());
			return true;
		}
		return true;
	}

	private void sendBackErrorMessage() {
		this.queue.enQueue(UssdUtils.buildMessage(
				getMsg("SYS_ERROR_MESSAGE_" + this.lang),
				this.ussd.getMsisdn(), this.ussd.getTransId(), false,
				this.ussd));
		this.uo.setErrCode(String.valueOf(7));
	}

	private void sendBackClient(boolean isToSwalihi, String msisdn) {
		if (!isToSwalihi) {
			this.queue.enQueue(UssdUtils.buildMessage(
					getMsg("MSG_CHANGE_TO_ENG_" + this.lang),
					this.ussd.getMsisdn(), this.ussd.getTransId(), false,
					this.ussd));
		}
		else {
			this.queue.enQueue(UssdUtils.buildMessage(
					getMsg("MSG_CHANGE_TO_SWALIHI_" + this.lang),
					this.ussd.getMsisdn(), this.ussd.getTransId(), false,
					this.ussd));
		}

		this.uo.setErrCode(String.valueOf(0));
	}

	private void setUo() {
		this.uo.setContent(this.ussd.getUssdString());
		this.uo.setChannel(null);
		this.uo.setActionType(ActionType.CALLBACK.ordinal());
		this.uo.setFee(0);
		this.uo.setMsisdn(this.ussd.getMsisdn());
		this.uo.setReceiverTime(new Date());
		this.uo.setSubId(-1);
		this.uo.setTransID(this.ussd.getTransId());
	}

	private String getMsg(String key) {
		if (this.lang.toUpperCase().equals("EN")) {
			return UssdUtils.formatMessage(MessageUtils.getMessage(key), true);
		}
		return UssdUtils.formatMessage(MessageUtils.getMessage(key), false);
	}

	private boolean isChangeToEnglish() {
		return this.ussd.getUssdString().matches(UssdUtils.getRegexToEnglish());
	}

	private boolean isChangeToSwalihi() {
		return this.ussd.getUssdString().matches(UssdUtils.getRegexToSwalihi());
	}
}
