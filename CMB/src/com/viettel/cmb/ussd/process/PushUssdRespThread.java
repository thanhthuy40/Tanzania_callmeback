/**
 * @desc:PushUssdRespThread.java - com.viettel.cmb.ussd.process
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.process;

import java.util.Date;

import org.apache.log4j.Logger;

import com.viettel.cmb.ussd.database.DbAdapter;
import com.viettel.cmb.ussd.transaction.TransactionMapping;
import com.viettel.cmb.ussd.utils.UtHis;
import com.viettel.gateway.ussdgw.common.UssdMessage;
import com.viettel.ussd.connection.TcpConnectionPool;
import com.viettel.ussd.connector.LogUssdMessage;
import com.viettel.ussd.connector.TCPConnector;

/**
 * @author thanhhn5
 */
public class PushUssdRespThread
		implements Runnable {
	private Logger				log	= Logger.getLogger(PushUssdRespThread.class);
	private UssdMessage			ussd;
	private TcpConnectionPool	pool;
	private TCPConnector		tcp;
	private UtHis				ut;
	private DbAdapter			db;

	public PushUssdRespThread(UssdMessage ussd, TcpConnectionPool pool, String pathDb) {
		this.ussd = ussd;
		this.pool = pool;
		this.ut = new UtHis();
		InitUt();
		this.db = DbAdapter.getInstancce(pathDb);
	}

	private void InitUt() {
		this.ut.setChanel(null);
		this.ut.setMessage(this.ussd.getUssdString());
		this.ut.setMsisdn(this.ussd.getMsisdn());
		this.ut.setSendTime(new Date());
		this.ut.setTrans(this.ussd.getTransId());
		this.ut.setUoId(-1L);
	}

	public void run() {
		this.log.info("--------------- sending Ussd Message ----------------------------------");
		if (this.ussd.getType() == 200) {
			this.log.info("Push ussd to client msisdn " + this.ussd.getMsisdn() +
					"  - Content : " + this.ussd.getUssdString());
		}
		else {
			this.log.info("Response ussd to client " + this.ussd.getMsisdn() +
					"  - Content : " + this.ussd.getUssdString());
		}
		LogUssdMessage.print(this.ussd);
		try {
			if (this.ussd.getType() == 200) {
				this.tcp = this.pool.getConnection(-1);
			}
			else {
				this.tcp = this.pool.getConnection(this.ussd.getConnectorId());
			}
			this.ussd.setConnectorId(this.tcp.getId());
			this.tcp.send(this.ussd);
			TransactionMapping.getInstance().add(this.ussd);
		}
		catch (Exception e) {
			this.log.error("PUSH_USSD_MESSAGE_01: Push ussd to client failed ", e);
		}

		this.log.info("insert MT  for msisdn " + this.ussd.getMsisdn());
		if (!this.db.insertUtHis(this.ut)) {
			this.log.error("Can't insert ut");
		}
		this.log.info("-----------------------------------------------------------------------");
	}
}
