/**
 * @desc:UoHis.java - com.viettel.cmb.ussd.utils
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.utils;

import java.util.Date;

/**
 * @author thanhhn5
 */
public class UoHis {
	private int		id;
	private String	msisdn;
	private String	content;
	private String	errCode;
	private String	channel;
	private int		actionType;
	private Date	receiverTime;
	private String	errExchange;
	private int		fee;
	private int		subType;
	private int		subId;
	private String	transID;

	public String getTransID() {
		return this.transID;
	}

	public void setTransID(String transID) {
		this.transID = transID;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsisdn() {
		return this.msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getErrCode() {
		return this.errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getChannel() {
		return this.channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getActionType() {
		return this.actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	public Date getReceiverTime() {
		return this.receiverTime;
	}

	public void setReceiverTime(Date receiverTime) {
		this.receiverTime = receiverTime;
	}

	public String getErrExchange() {
		if (this.errExchange == null) {
			return "0";
		}
		return this.errExchange;
	}

	public void setErrExchange(String errExchange) {
		this.errExchange = errExchange;
	}

	public int getFee() {
		return this.fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	public int getSubType() {
		return this.subType;
	}

	public void setSubType(int subType) {
		this.subType = subType;
	}

	public int getSubId() {
		return this.subId;
	}

	public void setSubId(int subId) {
		this.subId = subId;
	}

	public UoHis(int id, String msisdn, String content, String errCode, String channel, int actionType, Date receiverTime, String errExchange, int fee, int subType, int subId) {
		this.id = id;
		this.msisdn = msisdn;
		this.content = content;
		this.errCode = errCode;
		this.channel = channel;
		this.actionType = actionType;
		this.receiverTime = receiverTime;
		this.errExchange = errExchange;
		this.fee = fee;
		this.subType = subType;
		this.subId = subId;
	}

	public UoHis() {
	}

	public String toString() {
		return
		"UoHis [id=" + this.id + ", msisdn=" + this.msisdn + ", content=" + this.content + ", errCode=" + this.errCode + ", channel=" + this.channel + ", actionType="
				+ this.actionType + ", receiverTime=" + this.receiverTime + ", errExchange=" + this.errExchange + ", fee=" + this.fee + ", subType=" + this.subType + ", subId="
				+ this.subId + "]";
	}
}
