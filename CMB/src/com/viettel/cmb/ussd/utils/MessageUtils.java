/**
 * @desc:MessageUtils.java - com.viettel.cmb.ussd.utils
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017 
 */
package com.viettel.cmb.ussd.utils;

/**
 * @author thanhhn5
 *
 */
public class MessageUtils
{
  public static String getMessage(String key)
  {
    if (key == null)
      throw new NullPointerException();
    if (!com.viettel.cmb.ussd.config.ConfigConst.lstConfig.containsKey(key))
      throw new NullPointerException("Incorect key");
    return ((com.viettel.cmb.ussd.config.ConfigInfo)com.viettel.cmb.ussd.config.ConfigConst.lstConfig.get(key)).getParamValue();
  }
  
  public static String getClassName(Integer key) throws java.security.InvalidAlgorithmParameterException
  {
    if ((key == null) || (key.intValue() < 0))
      throw new java.security.InvalidAlgorithmParameterException("Incorect key");
    return ((com.viettel.cmb.ussd.config.ProcessMap)com.viettel.cmb.ussd.config.ConfigConst.lstProcessMap.get(key)).getClassName();
  }
}

