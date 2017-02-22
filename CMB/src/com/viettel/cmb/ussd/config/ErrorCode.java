/**
 * @desc:ErrorCode.java - com.viettel.cmb.ussd.config
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017 
 */
package com.viettel.cmb.ussd.config;

/**
 * @author thanhhn5
 *
 */

public class ErrorCode
{
  public static final int SUCCESS = 0;
  public static final int CALLER_POST_PAID = 1;
  public static final int OVER_TRY = 2;
  public static final int CALLER_BLOCKED_2_WAY = 3;
  public static final int CALLEE_NOT_EXIST = 4;
  public static final int ERROR_CHECK_BCCS_CALLER = 5;
  public static final int ERROR_CHECK_BCCS_CALLEE = 6;
  public static final int SYS_ERROR = 7;
  public static final int CALLEE_INVALID = 8;
  public static final int SYNTAX_ERROR = -1;
}

