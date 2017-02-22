/**
 * @desc:PushUssdManager.java - com.viettel.cmb.ussd.process
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.process;

import org.apache.log4j.Logger;

import com.viettel.cmb.ussd.utils.UssdUtils;
import com.viettel.gateway.ussdgw.common.UssdMessage;
import com.viettel.thread.process.ThreadPool;
import com.viettel.ussd.connection.TcpConnectionPool;

/**
 * @author thanhhn5
 */
public class PushUssdManager
		implements Runnable {
	private Logger				log	= Logger.getLogger(PushUssdManager.class);
	private boolean				IsRunning;
	private UssdQueue			queue;
	private ThreadPool			pool;
	private TcpConnectionPool	poolConnection;
	private String				pathDb;

	public PushUssdManager(UssdQueue queue, ThreadPool pool, TcpConnectionPool poolConnection, String pathDb) {
		this.queue = queue;
		this.pool = pool;
		this.poolConnection = poolConnection;
		this.pathDb = pathDb;
	}

	public void run() {
		while (this.IsRunning) {
			if (this.queue.size() > 0) {
				UssdMessage ussd = this.queue.deQueue();
				if (ussd != null) {
					this.log.info("Add new process send  to ussd push queue");
					this.pool.addTask(new PushUssdRespThread(ussd,
							this.poolConnection, this.pathDb));
				}
			}
			UssdUtils.sleep(100L);
		}
	}

	public void start() {
		Thread t = new Thread(this);
		t.setName("Push Ussd Process");
		t.start();
		this.IsRunning = true;
	}

	public void stop() {
		this.IsRunning = false;
	}
}
