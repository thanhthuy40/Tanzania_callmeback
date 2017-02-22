/**
 * @desc:UssdUtils.java - com.viettel.cmb.ussd.utils
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.utils;

import java.security.InvalidAlgorithmParameterException;
import java.util.Date;

import com.viettel.gateway.ussdgw.common.UssdMessage;

/**
 * @author thanhhn5
 */
public class UssdUtils {
	private static int	charSet	= 1;
	private static int	TIMEOUT	= 120;
	public static int	UNKNOW	= -1;

	public static boolean isCorrectPhoneNumber(String msisdn)
			throws InvalidAlgorithmParameterException {
		return msisdn.matches(MessageUtils.getMessage("PHONE_REGEXES"));
	}

	public static String getNational() {
		return "255";
	}

	public static String getRegexToSwalihi() {
		return MessageUtils.getMessage("COMMAND_TO_SWAHILI");
	}

	public static String getRegexToEnglish() {
		return MessageUtils.getMessage("COMMAND_TO_ENGLISH");
	}

	public static String getPrefixLangSwali() {
		return MessageUtils.getMessage("SWALIHI_LANG");
	}

	public static String getPrefixLangEng() {
		return MessageUtils.getMessage("ENGLISH_LANG");
	}

	public static UssdMessage buildMessage(String content, String msisdn, String transId, boolean isPushToB, UssdMessage ussd) {
		UssdMessage msg = null;
		if (!isPushToB) {
			msg = new UssdMessage(203);
			msg.setHlrGT(ussd.getHlrGT());
			msg.setImsi(ussd.getImsi());
			msg.setDlgId(ussd.getDlgId());
			msg.setMsisdn(msisdn);
		}
		else {
			msg = new UssdMessage(
					201);

			if (msisdn.startsWith("0")) {
				msg.setMsisdn(getNational() + msisdn.substring(1));
			}
			else if (msisdn.startsWith("00")) {
				msg.setMsisdn(getNational() + msisdn.substring(2));
			}
			else if (msisdn.startsWith(getNational())) {
				msg.setMsisdn(msisdn);
			}
			else if (msisdn.startsWith("+")) {
				String temp = msisdn.substring(1);
				if (temp.startsWith(getNational())) {
					msg.setMsisdn(temp);
				}
				else {
					msg.setMsisdn(getNational() + temp);
				}
			}
		}

		msg.setCharSet(charSet);
		msg.setUssdString(content);

		msg.setTimeout(TIMEOUT);
		msg.setTransId(transId);
		return msg;
	}

	public static String getServiceName() {
		return MessageUtils.getMessage("SERVICE_NAME");
	}

	public static String getCCNumber() {
		return MessageUtils.getMessage("CCNumber");
	}

	public static int getLimitNumber() {
		return Integer.parseInt(
				MessageUtils.getMessage("LIMITED_NUMBER_MESSAGE"));
	}

	public static String getOperator() {
		return MessageUtils.getMessage("Operator");
	}

	public static String standardMsisdn(String msisdn) {
		String msg = null;
		if (msisdn.startsWith("0")) {
			msg = getNational() + msisdn.substring(1);
		}
		else if (msisdn.startsWith("00")) {
			msg = getNational() + msisdn.substring(2);
		}
		else if (msisdn.startsWith(getNational())) {
			msg = msisdn.replace(getNational(), "");
		}
		else if (msisdn.startsWith("+")) {
			String temp = msisdn.substring(1);
			if (temp.startsWith(getNational())) {
				msg = temp;
			}
			else {
				msg = getNational() + temp;
			}
		}
		return msg;
	}

	public static void main(String[] args) {
		String msisdn = "255621000048";
		System.out.println("ressuft :" + standardMsisdn(msisdn));
	}

	public static String formatMessage(String msg, boolean en) {
		String temp = msg;
		StringBuffer jobs = null;
		if (en) {
			jobs = new StringBuffer(getServiceName()).append("\n");
			temp = temp.replace("%SERVICE_NAME%", jobs);
		}
		else {
			jobs = new StringBuffer("Tafadhali Nipigie").append("\n");
			temp = jobs.toString().concat(temp);
		}

		temp = temp.replace("%CCNumber%", getCCNumber());
		temp = temp.replace("%LIMITED_NUMBER_MESSAGE%",
				String.valueOf(getLimitNumber()));
		temp = temp.replace("%Operator%", getOperator());

		return temp;
	}

	public static int getMaxSendToB() {
		return Integer.parseInt(MessageUtils.getMessage("MAXLENGTH_CMB"));
	}

	public static UoHis convertToUoHis(UssdMessage ussd, int errorCode, int SubID, int errExchange, int fee, int subType, int actionType, String transID) {
		UoHis uo = new UoHis();
		uo.setMsisdn(ussd.getMsisdn());
		uo.setChannel(null);
		uo.setContent(ussd.getUssdString());
		uo.setErrCode(String.valueOf(errorCode));
		uo.setReceiverTime(new Date());
		uo.setSubId(SubID);
		uo.setErrExchange(String.valueOf(errExchange));
		uo.setFee(fee);
		uo.setSubType(subType);
		uo.setActionType(actionType);
		uo.setTransID(transID);
		return uo;
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
