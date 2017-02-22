/**
 * @desc:ConfigConst.java - com.viettel.cmb.ussd.config
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.config;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author thanhhn5
 */
public class ConfigConst {
	public static ConcurrentHashMap<Integer, ProcessMap>	lstProcessMap	= new ConcurrentHashMap<Integer, ProcessMap>();
	public static ConcurrentHashMap<String, ConfigInfo>		lstConfig		= new ConcurrentHashMap<String, ConfigInfo>();
}
