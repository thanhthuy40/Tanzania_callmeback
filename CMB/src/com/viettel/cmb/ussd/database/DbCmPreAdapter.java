/**
 * @desc:DbCmPreAdapter.java - com.viettel.cmb.ussd.database
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
import com.viettel.cmb.ussd.utils.StateOfSub;
import com.viettel.cmb.ussd.utils.UssdUtils;

/**
 * @author thanhhn5
 */
public class DbCmPreAdapter {
	private DbProcess				dbCmPos	= null;
	private Logger					log		= Logger.getLogger(DbCmPreAdapter.class);
	private static DbCmPreAdapter	instance;
	private String					dbPath;

	public static DbCmPreAdapter getInstance(String path) {
		if (instance == null)
			instance = new DbCmPreAdapter(path);
		return instance;
	}

	private DbCmPreAdapter(String path) {
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

	public StateOfSub checkSubsciberIsPrepaidAndNotBlock2Way(String msisdn) {
		Connection connection = null;
		PreparedStatement pre = null;
		ResultSet set = null;
		try {
			connection = this.dbCmPos.getConnection();
			if (connection == null) {
				this.log.error("checkSubsciberIsPrepaidAndNotBlock2Way get connection null");
				return StateOfSub.ERROR;
			}
			String msisdnCheck = UssdUtils.standardMsisdn(msisdn);
			this.log.info("Check subscirber " + msisdnCheck + " is PrePaid");
			pre = connection
					.prepareStatement(" select isdn,act_status from cm_pre.sub_mb where isdn =? and act_status='00' and end_time not null");
			pre.setString(1, msisdnCheck);
			set = pre.executeQuery();
			if (set.next()) {
				String actStatus = set.getString("act_status");
				this.log.info("State of subscriber is " + actStatus);
				if (actStatus.equals("00")) {
					this.log.info("Subscriber is prepaid");
					return StateOfSub.PRESUB;
				}
				this.log.info("Subscriber is block2 way");
				return StateOfSub.BLOCK2WAY;
			}

			this.log.info("Subscriber is unknow subscriber");
			return StateOfSub.UNKNOW;
		}
		catch (Exception e) {
			this.log.error("checkSubsciberIsPrepaidAndNotBlock2Way", e);
			return StateOfSub.ERROR;
		}
		finally {
			this.dbCmPos.closePreStatement(pre);
			this.dbCmPos.closeResultSet(set);
			this.dbCmPos.closeConnection(connection);
		}
	}
}
