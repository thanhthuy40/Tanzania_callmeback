/**
 * @desc:SubType.java - com.viettel.cmb.ussd.utils
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017 
 */
package com.viettel.cmb.ussd.utils;

/**
 * @author thanhhn5
 *
 */
public enum SubType
{
  PRE(1),  POST(0),  UNKNOW(-1);
  
  private SubType(int type) {}
}
