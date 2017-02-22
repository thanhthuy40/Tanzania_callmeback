/**
 * @desc:UtHis.java - com.viettel.cmb.ussd.utils
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.utils;

import java.util.Date;

/**
 * @author thanhhn5
 */
public class UtHis {
	private long	utId;
	private String	msisdn;
	private String	message;
	private long	uoId;
	private Date	sendTime;
	private String	chanel;
	private String	errorCode;
	private String	trans;

	public String toString() {
		return

		"UtHis [utId=" + this.utId + ", msisdn=" + this.msisdn + ", message=" + this.message + ", uoId=" + this.uoId + ", sendTime=" + this.sendTime + ", chanel=" + this.chanel
				+ ", errorCode=" + this.errorCode + ", trans=" + this.trans + "]";
	}

	public long getUtId() {
		return this.utId;
	}

	public void setUtId(long utId) {
		this.utId = utId;
	}

	public String getMsisdn() {
		return this.msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getUoId() {
		return this.uoId;
	}

	public void setUoId(long uoId) {
		this.uoId = uoId;
	}

	public Date getSendTime() {
		return this.sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getChanel() {
		return this.chanel;
	}

	public void setChanel(String chanel) {
		this.chanel = chanel;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getTrans() {
		return this.trans;
	}

	public void setTrans(String trans) {
		this.trans = trans;
	}
}
