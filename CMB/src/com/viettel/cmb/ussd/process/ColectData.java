/**
 * @desc:ColectData.java - com.viettel.cmb.ussd.process
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.process;

import jlibs.core.lang.RuntimeUtil;

/**
 * @author thanhhn5
 */
public class ColectData implements Runnable {
	private boolean IsRunning;

	public void run() {
		while (this.IsRunning) {
			RuntimeUtil.gc();
			try {
				Thread.sleep(10000L);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		Thread t = new Thread(this);
		t.start();
		this.IsRunning = true;
	}

	public void stop() {
		this.IsRunning = false;
	}
}
