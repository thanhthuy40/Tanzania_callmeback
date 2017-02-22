/**
 * @desc:Start.java - com.viettel.cmb.usssd.run
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.usssd.run;

import org.apache.log4j.PropertyConfigurator;

/**
 * @author thanhhn5
 */
public class Start {
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("../etc/log4j.cfg");
		Manager.getInstance().start();
	}
}
