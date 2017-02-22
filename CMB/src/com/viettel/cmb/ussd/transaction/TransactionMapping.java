/**
 * @desc:TransactionMapping.java - com.viettel.cmb.ussd.transaction
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.transaction;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.viettel.gateway.ussdgw.common.UssdMessage;

/**
 * @author thanhhn5
 */
public class TransactionMapping {
	private Logger									log	= Logger.getLogger(TransactionMapping.class);
	private ConcurrentHashMap<String, UssdMessage>	lstTransaction;
	private static TransactionMapping				instance;

	private TransactionMapping() {
		this.lstTransaction = new ConcurrentHashMap<String, UssdMessage>();
	}

	public static TransactionMapping getInstance() {
		if (instance == null)
			instance = new TransactionMapping();
		return instance;
	}

	public void add(UssdMessage ussd) {
		if (!this.lstTransaction.containsKey(ussd.getTransId())) {
			this.log.info("add  transation commit messagee " + ussd.getTransId() +
					" to queue");
			this.lstTransaction.put(ussd.getTransId(), ussd);
		}
		else {
			this.log.warn("Transaction have exist ==> continues");
		}
	}

	public UssdMessage get(String transaction) {
		if (this.lstTransaction.containsKey(transaction)) {
			this.log.info("Transaction found ==> remove it");
			UssdMessage ussd = (UssdMessage) this.lstTransaction.get(transaction);
			this.lstTransaction.remove(transaction);
			return ussd;
		}
		return null;
	}
}
