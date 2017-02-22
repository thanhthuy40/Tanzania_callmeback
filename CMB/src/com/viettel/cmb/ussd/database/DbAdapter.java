/**
 * @desc:DbAdapter.java - com.viettel.cmb.ussd.database
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.viettel.boncecp.DbProcess;
import com.viettel.cmb.ussd.config.ConfigConst;
import com.viettel.cmb.ussd.config.ConfigInfo;
import com.viettel.cmb.ussd.config.ProcessMap;
import com.viettel.cmb.ussd.utils.UoHis;
import com.viettel.cmb.ussd.utils.UtHis;
import com.viettel.gateway.ussdgw.common.UssdMessage;

/**
 * @author thanhhn5
 */
public class DbAdapter {
	private DbProcess			dbOracle;
	private Logger				log	= Logger.getLogger(DbAdapter.class);
	private static DbAdapter	instance;

	public static DbAdapter getInstancce(String path) {
		if (instance == null)
			instance = new DbAdapter(path);
		return instance;
	}

	private DbAdapter(String path) {
		try {
			this.dbOracle = new DbProcess(path, this.log);
		}
		catch (IOException e) {
			this.log.error("ERROR", e);
		}
		catch (Exception e) {
			this.log.error("ERROR", e);
		}
	}

	public boolean loadAllProcessMap() {
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("loadAllProcessMap get connection null");
				return false;
			}
			pre = connection.prepareStatement("SELECT a.process_map_id, a.regex, a.class, a.syntax  FROM process_map a ");
			set = pre.executeQuery();
			this.log.info("-------------------------------- Process map -----------------------");
			while (set.next()) {
				ProcessMap map = new ProcessMap();
				map.setId(set.getInt("process_map_id"));
				map.setClassName(set.getString("class"));
				map.setRegex(set.getString("regex"));
				map.setSyntax(set.getString("syntax"));
				this.log.info(map.toString());
				ConfigConst.lstProcessMap.put(Integer.valueOf(map.getId()), map);
			}
			this.log.info("--------------------------------------------------------------------");
			return true;
		}
		catch (Exception e) {
			this.log.error("loadAllProcessMap", e);
			return false;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
	}

	public boolean loadAllConfigMessage() {
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("loadAllConfigMessage get connection null");
				return false;
			}
			pre = connection.prepareStatement("SELECT a.module, a.param_name, a.param_value, a.default_value, a.note   FROM config a ");
			set = pre.executeQuery();
			this.log.info("-------------------------------- Config messsage -----------------------");
			while (set.next()) {
				ConfigInfo info = new ConfigInfo();
				info.setModule(set.getString("module"));
				info.setNote(set.getString("note"));
				info.setDefaultValue(set.getString("default_value"));
				info.setParamName(set.getString("param_name"));
				info.setParamValue(set.getString("param_value"));
				this.log.info(info.toString());
				ConfigConst.lstConfig.put(info.getParamName(), info);
			}
			this.log.info("--------------------------------------------------------------------");
			return true;
		}
		catch (Exception e) {
			this.log.error("loadAllConfigMessage", e);
			return false;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
	}

	public String getLangugeOfSub(String msisdn) {
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		String lang = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("getLangugeOfSub get connection null");
				return "EN";
			}
			pre = connection.prepareStatement("SELECT a.lang FROM cmb_subs a where a.isdn=?");
			pre.setString(1, msisdn);
			set = pre.executeQuery();
			if (set.next()) {
				lang = set.getString("lang").toUpperCase();
				if (lang == null)
					lang = "EN";
			}
			return lang;
		}
		catch (Exception e) {
			this.log.error("getLangugeOfSub", e);
			return "EN";
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
	}

	private int getUoHisId() {
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		int uoHisId = -1;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("getUoHisId get connection null");
				return uoHisId;
			}
			pre = connection.prepareStatement("select uo_his_seq.nextval from dual");
			set = pre.executeQuery();
			if (set.next()) {
				uoHisId = set.getInt("nextval");
			}
			return uoHisId;
		}
		catch (Exception e) {
			this.log.error("getUoHisId", e);
			return uoHisId;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
	}

	public int insertUoHis(UoHis uo) {
		Connection connection = null;
		PreparedStatement pre = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("insertUoHis get connection null");
				return -1;
			}

			int uoHisID = getUoHisId();
			if (uoHisID == -1) {
				this.log.info("Can't found UO sequence ==> can't insert UO_HIS");
				return -1;
			}
			pre = connection.prepareStatement(
					"INSERT INTO uo_his (UO_HIS_ID,MSISDN,CONTENT,ERR_CODE,CHANNEL,ACTION_TYPE,RECEIVE_TIME,ERR_EXCHANGE,FEE,SUB_TYPE,SUB_ID,TRANSID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");

			pre.setInt(1, uoHisID);
			pre.setString(2, uo.getMsisdn());
			pre.setString(3, uo.getContent());
			pre.setString(4, uo.getErrCode());
			pre.setString(5, uo.getChannel());
			pre.setInt(6, uo.getActionType());
			pre.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			pre.setString(8, uo.getErrExchange());
			pre.setInt(9, uo.getFee());
			pre.setInt(10, uo.getSubType());
			pre.setInt(11, uo.getSubId());
			pre.setString(12, uo.getTransID());
			int rs = pre.executeUpdate();
			if (rs > 0) {
				return uoHisID;
			}
			return -1;
		}
		catch (Exception e) {
			this.log.error("insertUoHis", e);
			return -1;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeConnection(connection);
		}
	}

	public boolean changeLanguge(String msisdn, String lang) {
		Connection connection = null;
		PreparedStatement pre = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("changeLanguge get connection null");
				return false;
			}
			pre = connection.prepareStatement("update cmb_subs set lang=? where isdn =?");
			pre.setString(1, lang);
			pre.setString(2, msisdn);
			int rs = pre.executeUpdate();
			if (rs > 0) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			this.log.error("changeLanguge", e);
			return false;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeConnection(connection);
		}
	}

	public boolean checkSubscriberIsexist(String msisdn) {
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("checkSubscriberIsexist get connection null");
				return false;
			}
			pre = connection.prepareStatement("select * from cmb_subs where isdn=?");
			pre.setString(1, msisdn);
			set = pre.executeQuery();
			boolean ishaveData = false;
			if (set.next()) {
				ishaveData = true;
			}
			return ishaveData;
		}
		catch (Exception e) {
			this.log.error("checkSubscriberIsexist", e);
			return false;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
	}

	public int getLastSubscriberSeqid(String msisdn) {
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		int uoHisId = -1;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("getLastSubscriberSeqid get connection null");
				return uoHisId;
			}
			pre = connection.prepareStatement("select  uo_his_id from uo_his where msisdn =?");
			pre.setString(1, msisdn);
			set = pre.executeQuery();
			if (set.next()) {
				uoHisId = set.getInt("uo_his_id");
			}
			return uoHisId;
		}
		catch (Exception e) {
			this.log.error("getLastSubscriberSeqid", e);
			return uoHisId;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
	}

	public int getSubscriberID(String msisdn) {
		int resq = -1;
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("getUoHisId get connection null");
				return resq;
			}
			pre = connection.prepareStatement("SELECT a.subs_id  FROM cmb_subs a where a.isdn =?");
			pre.setString(1, msisdn);
			set = pre.executeQuery();
			if (set.next())
				resq = set.getInt("subs_id");
		}
		catch (Exception e) {
			this.log.error("getUoHisId", e);
			return resq;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
		this.dbOracle.closePreStatement(pre);
		this.dbOracle.closeResultSet(set);
		this.dbOracle.closeConnection(connection);

		return resq;
	}

	public int getAmountOfSub(String msisdn) {
		int resq = -1;
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("getAmountOfSub get connection null");
				return resq;
			}
			pre = connection.prepareStatement("SELECT a.cmb_amount  FROM cmb_subs a where a.isdn =?");
			pre.setString(1, msisdn);
			set = pre.executeQuery();
			if (set.next()) {
				resq = set.getInt("cmb_amount");
			}
			else {
				return -2;
			}
		}
		catch (Exception e) {
			this.log.error("getAmountOfSub", e);
			return resq;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
		this.dbOracle.closePreStatement(pre);
		this.dbOracle.closeResultSet(set);
		this.dbOracle.closeConnection(connection);

		return resq;
	}

	public boolean resetAmount(String msidsn) {
		boolean resq = false;
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("updateSubscriberAmount get connection null");
				return resq;
			}
			pre = connection.prepareStatement("update cmb_subs sub set sub.cmb_amount  =0  where isdn =?");
			pre.setString(1, msidsn);
			int i = pre.executeUpdate();
			if (i > 0)
				resq = true;
		}
		catch (Exception e) {
			this.log.error("updateSubscriberAmount", e);
			return resq;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
		this.dbOracle.closePreStatement(pre);
		this.dbOracle.closeResultSet(set);
		this.dbOracle.closeConnection(connection);

		return resq;
	}

	public boolean isAmountInToday(String msisdn) {
		boolean resq = false;
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("updateSubscriberAmount get connection null");
				return resq;
			}
			pre = connection.prepareStatement("select * from cmb_subs sub where trunc(sysdate) = trunc(sub.lastest_topup_datetime) and isdn=?");
			pre.setString(1, msisdn);
			int i = 0;
			set = pre.executeQuery();
			while (set.next()) {
				i++;
			}
			if (i > 0)
				resq = true;
		}
		catch (Exception e) {
			this.log.error("updateSubscriberAmount", e);
			return resq;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
		this.dbOracle.closePreStatement(pre);
		this.dbOracle.closeResultSet(set);
		this.dbOracle.closeConnection(connection);

		return resq;
	}

	public boolean updateSubscriberAmount(String msisdn) {
		boolean resq = false;
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("updateSubscriberAmount get connection null");
				return resq;
			}
			pre = connection.prepareStatement("update cmb_subs set cmb_amount= cmb_amount +1, lastest_topup_datetime =sysdate where isdn=?");
			pre.setString(1, msisdn);
			this.log.info("Truyen vao +" + msisdn);
			int i = pre.executeUpdate();
			if (i > 0)
				resq = true;
		}
		catch (Exception e) {
			this.log.error("updateSubscriberAmount", e);
			return resq;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
		this.dbOracle.closePreStatement(pre);
		this.dbOracle.closeResultSet(set);
		this.dbOracle.closeConnection(connection);

		return resq;
	}

	public int getSubscriberId() {
		int resq = -1;
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("getSubscriberId get connection null");
				return resq;
			}
			pre = connection.prepareStatement("select cmb_subs_seq.nextval from dual");
			set = pre.executeQuery();
			if (set.next())
				resq = set.getInt("nextval");
		}
		catch (Exception e) {
			this.log.error("getSubscriberId", e);
			return resq;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeResultSet(set);
			this.dbOracle.closeConnection(connection);
		}
		this.dbOracle.closePreStatement(pre);
		this.dbOracle.closeResultSet(set);
		this.dbOracle.closeConnection(connection);

		return resq;
	}

	public int insertSubscriberHis(UssdMessage ussd) {
		Connection connection = null;
		PreparedStatement pre = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("insertUoHis get connection null");
				return -1;
			}

			int subscriberId = getSubscriberId();
			if (subscriberId == -1) {
				this.log.info("Can't found SUBSCRIBER sequence ==> can't insert SUBSCRIBER");
				return -1;
			}
			pre = connection
					.prepareStatement("INSERT INTO cmb_subs (SUBS_ID,ISDN,CMB_AMOUNT,FIRST_TIME_USED_CMB,LAST_TIME_USED_CMB,LASTEST_TOPUP_DATETIME,LANG) VALUES(?,?,?,?,?,?,?)");

			pre.setInt(1, subscriberId);
			pre.setString(2, ussd.getMsisdn());
			pre.setInt(3, 0);
			pre.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			pre.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			pre.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			pre.setString(7, "EN");
			int rs = pre.executeUpdate();
			if (rs > 0) {
				return subscriberId;
			}
			return -1;
		}
		catch (Exception e) {
			this.log.error("insertSubscriberHis", e);
			return -1;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeConnection(connection);
		}
	}

	public boolean updateResuftToUtHis(int errorCode, String trans) {
		Connection connection = null;
		PreparedStatement pre = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("updateResuftToUtHis get connection null");
				return false;
			}

			pre = connection.prepareStatement("update ut_his set error_code = ? where trans=?");
			pre.setString(1, String.valueOf(errorCode));
			pre.setString(2, trans);
			int rs = pre.executeUpdate();
			if (rs > 0) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			this.log.error("updateResuftToUtHis", e);
			return false;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeConnection(connection);
		}
	}

	public boolean updateResuftToUoHis(int errorCode, int erroCodeExchage, String transid) {
		Connection connection = null;
		PreparedStatement pre = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("updateResuftToUoHis get connection null");
				return false;
			}

			pre = connection.prepareStatement("update uo_his set err_code =?,err_exchange=? where transid=?");
			pre.setString(1, String.valueOf(errorCode));
			pre.setString(2, String.valueOf(erroCodeExchage));
			pre.setString(3, transid);
			int rs = pre.executeUpdate();
			if (rs > 0) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			this.log.error("updateResuftToUoHis", e);
			return false;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeConnection(connection);
		}
	}

	public boolean insertUtHis(UtHis uo) {
		Connection connection = null;
		PreparedStatement pre = null;
		try {
			connection = this.dbOracle.getConnection();
			if (connection == null) {
				this.log.error("insertUtHis get connection null");
				return false;
			}

			pre = connection
					.prepareStatement("INSERT INTO ut_his (UT_HIS_ID,MSISDN,MESSAGE,UO_HIS_ID,SENT_TIME,CHANNEL,ERROR_CODE,TRANS) VALUES(ut_his_seq.nextval,?,?,?,?,?,?,?)");
			pre.setString(1, uo.getMsisdn());
			pre.setString(2, uo.getMessage());
			pre.setLong(3, uo.getUoId());
			pre.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			pre.setString(5, null);
			pre.setString(6, "-1");
			pre.setString(7, uo.getTrans());
			int rs = pre.executeUpdate();
			if (rs > 0) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			this.log.error("insertUtHis", e);
			return false;
		}
		finally {
			this.dbOracle.closePreStatement(pre);
			this.dbOracle.closeConnection(connection);
		}
	}
}
