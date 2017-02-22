/**
 * @desc:ReceiverUssdMsg.java - com.viettel.cmb.ussd.process
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.process;

import org.apache.log4j.Logger;

import com.viettel.cmb.ussd.database.DbCmPosAdapter;
import com.viettel.cmb.ussd.database.DbCmPreAdapter;
import com.viettel.gateway.ussdgw.common.UssdMessage;
import com.viettel.thread.process.ThreadPool;
import com.viettel.ussd.connector.LogUssdMessage;
import com.viettel.ussd.interfaces.IMessage;

/**
 * @author thanhhn5
 */
public class ReceiverUssdMsg implements IMessage {
	private ThreadPool		pool;
	private UssdQueue		queueSent;
	private String			path;
	private Logger			log	= Logger.getLogger(ReceiverUssdMsg.class);
	private ThreadPool		ConfigPoolExecute;
	private DbCmPreAdapter	dbPre;
	private DbCmPosAdapter	dbPos;

	public ReceiverUssdMsg(ThreadPool pool, UssdQueue queue, String pathDb, String exchangePath, ThreadPool pathConfigPoolExecute, DbCmPreAdapter pre, DbCmPosAdapter pos)
			throws Exception {
		if (pool == null)
			throw new NullPointerException();
		this.queueSent = queue;
		this.pool = pool;
		this.path = pathDb;
		this.ConfigPoolExecute = pathConfigPoolExecute;
		this.dbPos = pos;
		this.dbPre = pre;
	}

	public void onReceiver(UssdMessage ussd) {
		this.log.info("---------------------------- incomming message -----------------------------------------");
		LogUssdMessage.print(ussd);
		this.log.info("-----------------------------------------------------------------------------------------");
		this.pool
				.addTask(new UssdValidateThread(ussd, this.path,
						this.queueSent, this.ConfigPoolExecute, this.dbPos,
						this.dbPre));
	}
}
