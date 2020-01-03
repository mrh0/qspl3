package com.mrh.qspl.syntax.parser;

public enum StatementEndType {
	END(";"), IF(":"), WHILE("::"), FUNC("§");
	
	private String sign = "";
	private StatementEndType(String sign) {
		this.sign = sign;
	}
	
	@Override
	public String toString() {
		return this.sign;
	}
}
