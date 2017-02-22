/**
 * @desc:ProcessMap.java - com.viettel.cmb.ussd.config
 * @author thanhhn5 - thanhhn5@viettel.com.vn
 * @created_at:22 Feb 2017
 */
package com.viettel.cmb.ussd.config;

/**
 * @author thanhhn5
 */
public class ProcessMap {
	private int		id;

	private String	regex;

	private String	className;

	private String	syntax;

	public int getId() {
		return this.id;
	}

	public String toString() {
		return "ProcessMap [id=" + this.id + ", regex=" + this.regex + ", className=" + this.className + ", syntax=" + this.syntax + "]";
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRegex() {
		return this.regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSyntax() {
		return this.syntax;
	}

	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}
}
