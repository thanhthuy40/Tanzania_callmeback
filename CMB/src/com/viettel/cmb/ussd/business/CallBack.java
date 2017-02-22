/**
 * @desc:CallBack.java - com.viettel.cmb.ussd.business
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
import com.viettel.cmb.ussd.utils.StateOfSub;
import com.viettel.cmb.ussd.utils.UssdUtils;
import com.viettel.gateway.ussdgw.common.UssdMessage;

/**
 * @author thanhhn5
 */
public class CallBack
		extends ProcessUssdThread {
	private Logger	log	= Logger.getLogger(CallBack.class);
	private int		maxAmount;

	public CallBack(UssdMessage ussd, String dbPath, UssdQueue queue, DbCmPosAdapter pos, DbCmPreAdapter pre) {
		super(ussd, dbPath, queue, pos, pre);
	}

	public void initBeforeStart() {
		this.log.info("init initBeforeStart");
		try {
			this.maxAmount = UssdUtils.getLimitNumber();
		}
		catch (Exception ex) {
			this.log.error("Can't get Limit Number per day ==> default 5");
			this.maxAmount = 5;
		}
		setUo();
	}

	protected boolean processUo() {
		this.log.info("Send ussd to A ");
		sendBackClient(true, getSubscriber());
		this.log.info("Send ussd to B ");
		sendBackClient(false, this.ussd.getMsisdn());
		return true;
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

	public boolean validateContraint() {
		String msisdn = getSubscriber();
		if (msisdn == null) {
			this.log.warn("Can't found msisdn receiver messsage ==> send back to A error");
			sendBackErrorMessage();
			return false;
		}
		try {
			if (!UssdUtils.isCorrectPhoneNumber(msisdn)) {
				this.log.warn("Incorect format number B==> return error");
				sendBackInvalidMessage();
				return false;
			}
		}
		catch (Exception e) {
			this.log.error("Can't check isCorrectPhoneNumber ==> return error");
			sendBackErrorMessage();
			return false;
		}

		StateOfSub state = subscriberIsCondition();
		if (state == StateOfSub.ERROR) {
			this.log.info("Subscriber not enough condition");
			sendBackErrorMessage();
			this.uo.setErrCode(String.valueOf(7));
			return false;
		}

		if (state == StateOfSub.BLOCK2WAY) {
			this.log.info("Subscriber not enough condition");
			sendBackNotCondition("BLOCK2WAY_MSG_");
			this.uo.setErrCode(String.valueOf(3));

			return false;
		}
		if (state == StateOfSub.POST_SUB) {
			this.log.info("Subscriber not enough condition");
			sendBackNotCondition("SUBS_IS_POSTPREAID_");
			this.uo.setErrCode(String.valueOf(1));
			return false;
		}

		if (!this.db.isAmountInToday(this.ussd.getMsisdn())) {
			this.log.info("The amount is not today ==> reset it to 0 ");
			this.db.resetAmount(this.ussd.getMsisdn());
		}

		int currentSendTime = this.db.getAmountOfSub(this.ussd.getMsisdn());
		if (currentSendTime == -1) {
			sendBackErrorMessage();
			return false;
		}
		if (currentSendTime == -2) {
			this.log.info("Subscriber not exist ==> insert new and set current request is 1");
			this.db.insertSubscriberHis(this.ussd);
			currentSendTime = 1;
		}
		else if (currentSendTime >= this.maxAmount) {
			this.log.info("Subscriber use over " + currentSendTime + "/" +
					this.maxAmount + " call back times");
			sendBackMaxAmout(currentSendTime);
			return false;
		}

		return true;
	}

	private String getMsg(String key) {
		if (this.lang.toUpperCase().equals("EN")) {
			return UssdUtils.formatMessage(MessageUtils.getMessage(key), true);
		}
		return UssdUtils.formatMessage(MessageUtils.getMessage(key), false);
	}

	private void sendBackInvalidMessage() {
		this.queue.enQueue(UssdUtils.buildMessage(
				getMsg("NOT_VALID_MESSAGE_" + this.lang),
				this.ussd.getMsisdn(), this.ussd.getTransId(), false,
				this.ussd));
		this.uo.setErrCode(String.valueOf(8));
	}

	private void sendBackMaxAmout(int currentSendTime) {
		this.queue.enQueue(
				UssdUtils.buildMessage(
						getMsg("NOTIFY_OVER_LIMITED_MESSAGE_" +
								this.lang),
						this.ussd.getMsisdn(),
						this.ussd.getTransId(), false, this.ussd));
		this.uo.setErrCode(String.valueOf(2));
	}

	private void sendBackNotCondition(String key) {
		this.queue.enQueue(UssdUtils.buildMessage(getMsg(key + this.lang),
				this.ussd.getMsisdn(), this.ussd.getTransId(), false,
				this.ussd));
	}

	private void sendBackErrorMessage() {
		this.queue.enQueue(UssdUtils.buildMessage(
				getMsg("SYS_ERROR_MESSAGE_" + this.lang),
				this.ussd.getMsisdn(), this.ussd.getTransId(), false,
				this.ussd));
		this.uo.setErrCode(String.valueOf(7));
	}

	private void sendBackClient(boolean toA, String msisdn) {
		if (toA) {
			this.queue.enQueue(UssdUtils.buildMessage(
					getMsg("MSG_SUCS_BACK_TO_A_" + this.lang).replace(
							"%B%", msisdn),
					this.ussd.getMsisdn(),
					this.ussd.getTransId(), false, this.ussd));
		}
		else {
			this.queue.enQueue(UssdUtils.buildMessage(
					getMsg("MSG_SUCS_BACK_TO_B_" + this.lang).replace(
							"%A%", msisdn),
					getSubscriber(),
					this.ussd.getTransId(), true, this.ussd));
		}
		this.uo.setErrCode(String.valueOf(0));
	}
}
