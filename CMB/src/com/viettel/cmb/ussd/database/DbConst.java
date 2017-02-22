/**
 * @desc:DbConst.java - com.viettel.cmb.ussd.database
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.database;

/**
 * @author thanhhn5
 */
public class DbConst {
	public static final String	SELECT_PROCESS_MAP							= "SELECT a.process_map_id, a.regex, a.class, a.syntax  FROM process_map a ";
	public static final String	SELECT_CONFIG_MODULE						= "SELECT a.module, a.param_name, a.param_value, a.default_value, a.note   FROM config a ";
	public static final String	SELECT_CHECK_LENG_OF_SUB					= "SELECT a.lang FROM cmb_subs a where a.isdn=?";
	public static final String	INSERT_UO_HIS								= "INSERT INTO uo_his (UO_HIS_ID,MSISDN,CONTENT,ERR_CODE,CHANNEL,ACTION_TYPE,RECEIVE_TIME,ERR_EXCHANGE,FEE,SUB_TYPE,SUB_ID,TRANSID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String	SELECT_UO_ID								= "select uo_his_seq.nextval from dual";
	public static final String	SELECT_SUBSCRIBER_ID_SEQ					= "select cmb_subs_seq.nextval from dual";
	public static final String	SELECT_SUBSCIBER_ID							= "SELECT a.subs_id  FROM cmb_subs a where a.isdn =?";
	public static final String	INSERT_SUBSCRIBER							= "INSERT INTO cmb_subs (SUBS_ID,ISDN,CMB_AMOUNT,FIRST_TIME_USED_CMB,LAST_TIME_USED_CMB,LASTEST_TOPUP_DATETIME,LANG) VALUES(?,?,?,?,?,?,?)";
	public static final String	UPDATE_SUBSCRIBER_AMOUNT					= "update cmb_subs set cmb_amount= cmb_amount +1, lastest_topup_datetime =sysdate where isdn=?";
	public static final String	SELECT_LAST_SUBSCRIBER_ID					= "select  uo_his_id from uo_his where msisdn =?";
	public static final String	SELECT_AMOUNT_OF_SUBSCIBER					= "SELECT a.cmb_amount  FROM cmb_subs a where a.isdn =?";
	public static final String	UPDATE_LANG_OF_SUBSCIBER					= "update cmb_subs set lang=? where isdn =?";
	public static final String	INSERT_UT_HIS								= "INSERT INTO ut_his (UT_HIS_ID,MSISDN,MESSAGE,UO_HIS_ID,SENT_TIME,CHANNEL,ERROR_CODE,TRANS) VALUES(ut_his_seq.nextval,?,?,?,?,?,?,?)";
	public static final String	UPDATE_ERROR_CODE_UO						= "update uo_his set err_code =?,err_exchange=? where transid=?";
	public static final String	UPDATE_ERROR_CODE_UT						= "update ut_his set error_code = ? where trans=?";
	public static final String	CHECK_SUBSCRIBER_IS_EXIST					= "select * from cmb_subs where isdn=?";
	public static final String	CHECK_AMOUNT_IN_TODAY						= "select * from cmb_subs sub where trunc(sysdate) = trunc(sub.lastest_topup_datetime) and isdn=?";
	public static final String	RESET_AMOUNT								= "update cmb_subs sub set sub.cmb_amount  =0  where isdn =?";
	public static final String	CHECK_SUBSCRIBER_IS_POST					= "select isdn from cm_pos.sub_mb where isdn =? and act_status='000' ";
	public static final String	CHECK_SUBSCRIBER_IS_PREPAID_AND_NOT_BLOCK	= " select isdn,act_status from cm_pre.sub_mb where isdn =? ";
}
