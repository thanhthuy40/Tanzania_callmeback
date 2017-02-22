/**
 * @desc:ConfigInfo.java - com.viettel.cmb.ussd.config
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.config;

/**
 * @author thanhhn5
 */
public class ConfigInfo {
	private String	module;
	private String	paramName;
	private String	paramValue;
	private String	defaultValue;
	private String	note;

	public String getModule() {
		return this.module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getParamName() {
		return this.paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return this.paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String toString() {
		return

		"ConfigInfo [module=" + this.module + ", paramName=" + this.paramName + ", paramValue=" + this.paramValue + ", defaultValue=" + this.defaultValue + ", note=" + this.note
				+ "]";
	}
}
