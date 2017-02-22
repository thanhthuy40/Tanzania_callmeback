/**
 * @desc:UssdQueue.java - com.viettel.cmb.ussd.process
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.process;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.viettel.gateway.ussdgw.common.UssdMessage;

/**
 * @author thanhhn5
 */
public class UssdQueue
		extends ConcurrentLinkedQueue<UssdMessage> {
	private static final long	serialVersionUID	= 1L;
	private Logger				log					= Logger.getLogger(UssdQueue.class);
	private Object				mutex;

	public UssdQueue() {
		this.mutex = new Object();
	}

	public void enQueue(UssdMessage fileName) {
		synchronized (this.mutex) {
			if ((!contains(fileName)) &&
					(offer(fileName))) {
				this.log.info("Add UssdMessage  succ");
			}
		}
	}

	public UssdMessage deQueue() {
		synchronized (this.mutex) {
			if (!isEmpty()) {
				return (UssdMessage) poll();
			}
			return null;
		}
	}

	public int sizeQueue() {
		synchronized (this.mutex) {
			if (!isEmpty()) {
				return size();
			}
			return 0;
		}
	}
}
