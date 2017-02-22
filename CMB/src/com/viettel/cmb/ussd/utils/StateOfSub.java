/**
 * @desc:StateOfSub.java - com.viettel.cmb.ussd.utils
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017 
 */
package com.viettel.cmb.ussd.utils;

/**
 * @author thanhhn5
 *
 */
public enum StateOfSub
{
  BLOCK2WAY(1),  POST_SUB(2),  ERROR(3),  PRESUB(4),  UNKNOW(5);
  
  private StateOfSub(int type) {}
}
