/**
 * @desc:DbCmPosAdapter.java - com.viettel.cmb.ussd.database
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.viettel.boncecp.DbProcess;
import com.viettel.cmb.ussd.utils.UssdUtils;

/**
 * @author thanhhn5
 */
public class DbCmPosAdapter {
	private DbProcess				dbCmPos	= null;
	private Logger					log		= Logger.getLogger(DbCmPosAdapter.class);
	private static DbCmPosAdapter	instance;
	private String					dbPath;

	public static DbCmPosAdapter getInstance(String path) {
		if (instance == null)
			instance = new DbCmPosAdapter(path);
		return instance;
	}

	private DbCmPosAdapter(String path) {
		this.dbPath = path;
		try {
			this.dbCmPos = new DbProcess(this.dbPath, this.log);
		}
		catch (IOException e) {
			this.log.error("ERROR", e);
		}
		catch (Exception e) {
			this.log.error("ERROR", e);
		}
	}

	public boolean CheckSubsciberIsPost(String msisdn) {
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		try {
			connection = this.dbCmPos.getConnection();
			if (connection == null) {
				this.log.error("CheckSubsciberIsPost get connection null");
				return false;
			}
			String msisdnCheck = UssdUtils.standardMsisdn(msisdn);
			pre = connection.prepareStatement("select isdn from cm_pos.sub_mb where isdn =? and act_status='000' and end_time not null");
			this.log.info("Check subscirber " + msisdnCheck + " is postpraid");
			pre.setString(1, msisdnCheck);
			set = pre.executeQuery();
			if (set.next()) {
				this.log.info("Subscriber is postpaid");
				return true;
			}
			return false;
		}
		catch (Exception e) {
			this.log.error("CheckSubsciberIsPost", e);
			return false;
		}
		finally {
			this.dbCmPos.closePreStatement(pre);
			this.dbCmPos.closeResultSet(set);
			this.dbCmPos.closeConnection(connection);
		}
	}
}
