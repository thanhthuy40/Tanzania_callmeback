/**
 * @desc:Help.java - com.viettel.cmb.ussd.business
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.business;

import java.util.Date;

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
public class Help
		extends ProcessUssdThread {
	public Help(UssdMessage ussd, String dbPath, UssdQueue ussdQueue, DbCmPosAdapter pos, DbCmPreAdapter pre) {
		super(ussd, dbPath, ussdQueue, pos, pre);
	}

	public void initBeforeStart() {
		setUo();
	}

	public boolean validateContraint() {
		return true;
	}

	private String getMsg(String key) {
		if (this.lang.toUpperCase().equals("EN")) {
			return UssdUtils.formatMessage(MessageUtils.getMessage(key), true);
		}
		return UssdUtils.formatMessage(MessageUtils.getMessage(key), false);
	}

	protected boolean processUo() {
		sendBackClient();
		return false;
	}

	private void setUo() {
		this.uo.setContent(this.ussd.getUssdString());
		this.uo.setChannel(null);
		this.uo.setActionType(ActionType.HELP.ordinal());
		this.uo.setFee(0);
		this.uo.setMsisdn(this.ussd.getMsisdn());
		this.uo.setReceiverTime(new Date());
		this.uo.setSubId(-1);
		this.uo.setTransID(this.ussd.getTransId());
	}

	private void sendBackClient() {
		this.queue.enQueue(UssdUtils.buildMessage(
				getMsg("MSG_HELP_BACK_TO_A_" + this.lang),
				this.ussd.getMsisdn(), this.ussd.getTransId(), false,
				this.ussd));
		this.uo.setErrCode(String.valueOf(0));
	}
}
