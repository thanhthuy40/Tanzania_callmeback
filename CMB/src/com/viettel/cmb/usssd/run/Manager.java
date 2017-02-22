/**
 * @desc:Manager.java - com.viettel.cmb.usssd.run
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.usssd.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.viettel.cmb.ussd.database.DbAdapter;
import com.viettel.cmb.ussd.database.DbCmPosAdapter;
import com.viettel.cmb.ussd.database.DbCmPreAdapter;
import com.viettel.cmb.ussd.process.ColectData;
import com.viettel.cmb.ussd.process.PushUssdManager;
import com.viettel.cmb.ussd.process.ReceiverUssdMsg;
import com.viettel.cmb.ussd.process.UssdQueue;
import com.viettel.thread.process.ThreadPool;
import com.viettel.ussd.connection.TcpConnectionPool;

/**
 * @author thanhhn5
 */
public class Manager {
	private Logger				log			= Logger.getLogger(Manager.class);
	private final String		pathConfig	= "../etc/CallMeBack.cfg";

	private static Manager		instance;

	private TcpConnectionPool	poolConnectionGw;

	private ThreadPool			poolValidate;

	private ReceiverUssdMsg		rec;

	private UssdQueue			ussdQueue;
	private ThreadPool			poolExecuateBusinees;
	private DbAdapter			db;
	private DbCmPosAdapter		pos;
	private DbCmPreAdapter		pre;
	private ColectData			colect;
	private PushUssdManager		push;
	private ThreadPool			poolPush;
	private String				pathPoolValidate;
	private String				linkDb;
	private String				exChangePath;
	private String				pathPoolProcessClass;
	private String				pathConfigUssdGW;
	private String				pathPoolPushUssd;
	private String				pathCmPos;
	private String				pathCmPre;

	public static Manager getInstance() {
		if (instance == null)
			instance = new Manager();
		return instance;
	}

	public Manager() {
		if (!initConfig()) {
			this.log.error("Can't read complete config ==> system will stop");
			System.exit(1);
		}
		loadConfigDatabase();
	}

	private void loadConfigDatabase() {
		this.db = DbAdapter.getInstancce(this.linkDb);
		this.db.loadAllConfigMessage();
		this.db.loadAllProcessMap();
		this.pos = DbCmPosAdapter.getInstance(this.pathCmPos);
		this.pre = DbCmPreAdapter.getInstance(this.pathCmPre);
	}

	public void start() throws Exception {
		this.log.info("Starting pool validate ");
		this.poolValidate = new ThreadPool(this.pathPoolValidate);
		this.log.info("init USSD queue");
		this.ussdQueue = new UssdQueue();
		this.log.info("stating pool process");
		this.poolExecuateBusinees = new ThreadPool(this.pathPoolProcessClass);
		this.log.info("starting receiver class");
		this.rec = new ReceiverUssdMsg(this.poolValidate, this.ussdQueue,
				this.linkDb, this.exChangePath, this.poolExecuateBusinees,
				this.pre, this.pos);
		this.log.info("staring pool connection with ussd gw");
		this.poolConnectionGw = new TcpConnectionPool(this.pathConfigUssdGW, this.rec);
		this.colect = new ColectData();
		this.colect.start();
		this.log.info("init pool push ussd");
		this.poolPush = new ThreadPool(this.pathPoolPushUssd);
		this.push = new PushUssdManager(this.ussdQueue, this.poolPush,
				this.poolConnectionGw, this.linkDb);
		this.push.start();
	}

	public void stop() {
		this.log.info("Stoping pool validate ");
		this.poolValidate.stop();
		this.log.info("Stoping pool process ");
		this.poolExecuateBusinees.stop();
		this.log.info("Stoping... pool connection");
		this.poolConnectionGw.shutdown();
		this.log.info("Stoping... colect ram");
		this.colect.stop();
		this.log.info("Stoping... pool push ussd connection");
		this.poolPush.stop();
		this.log.info("Stoping... pool push manager connection");
		this.push.stop();
	}

	private boolean initConfig() {
		if (pathConfig == null)
			return false;
		File fileConfig = new File("../etc/CallMeBack.cfg");

		FileInputStream fileReader = null;
		if (!fileConfig.exists()) {
			System.err.println("File config not found");
			return false;
		}
		Properties prop = new Properties();
		try {
			fileReader = new FileInputStream(fileConfig);
			prop.load(fileReader);
		}
		catch (FileNotFoundException e) {
			System.err.println("File config " + fileConfig + " not found");
			return false;
		}
		catch (IOException e) {
			System.err.println("Read file config " + fileConfig + " failed");
			return false;
		}

		String temp = prop.getProperty("POOL_VALIDATE");
		if (temp == null) {
			this.log.warn("Can't configuration  null for POOL_VALIDATE");
			return false;
		}
		if (!isExists(temp)) {
			this.log.error("Can't found file config " + temp);
			return false;
		}
		this.pathPoolValidate = temp;

		temp = prop.getProperty("DB_CONFG");
		if (temp == null) {
			this.log.warn("Can't configuration  null for DB_CONFG");
			return false;
		}
		if (!isExists(temp)) {
			this.log.error("Can't found file config " + temp);
			return false;
		}
		this.linkDb = temp;

		temp = prop.getProperty("EXCHANGE_PATH");
		if (temp == null) {
			this.log.warn("Can't configuration  null for EXCHANGE_PATH");
			return false;
		}
		if (!isExists(temp)) {
			this.log.error("Can't found file config " + temp);
			return false;
		}
		this.exChangePath = temp;

		temp = prop.getProperty("USSD_GW");
		if (temp == null) {
			this.log.warn("Can't configuration  null for USSD_GW");
			return false;
		}
		if (!isExists(temp)) {
			this.log.error("Can't found file config " + temp);
			return false;
		}
		this.pathConfigUssdGW = temp;

		temp = prop.getProperty("POOL_PROCESS");
		if (temp == null) {
			this.log.warn("Can't configuration  null for POOL_PROCESS");
			return false;
		}
		if (!isExists(temp)) {
			this.log.error("Can't found file config " + temp);
			return false;
		}
		this.pathPoolProcessClass = temp;

		temp = prop.getProperty("POOL_PUSH");
		if (temp == null) {
			this.log.warn("Can't configuration  null for POOL_PUSH");
			return false;
		}
		if (!isExists(temp)) {
			this.log.error("Can't found file config " + temp);
			return false;
		}
		this.pathPoolPushUssd = temp;

		temp = prop.getProperty("DB_CONFG_CM_POS");
		if (temp == null) {
			this.log.warn("Can't configuration  null for DB_CONFG_CM_POS");
			return false;
		}
		if (!isExists(temp)) {
			this.log.error("Can't found file config " + temp);
			return false;
		}
		this.pathCmPos = temp;

		temp = prop.getProperty("DB_CONFG_CM_PRE");
		if (temp == null) {
			this.log.warn("Can't configuration  null for DB_CONFG_CM_PRE");
			return false;
		}
		if (!isExists(temp)) {
			this.log.error("Can't found file config " + temp);
			return false;
		}
		this.pathCmPre = temp;
		return true;
	}

	private boolean isExists(String path) {
		File f = new File(path);
		return f.exists();
	}
}
