package com.mrh.qspl.syntax.tokenizer;

import com.mrh.qspl.syntax.tokenizer.Tokens.TokenType;

public class Token {
	private String token;
	private TokenType type;
	
	public Token(String token, TokenType type) {
		this.token = token;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return token+"|";//return "[" + token + "](" + type.toString() + ")";
	}
	
	public TokenType getType() {
		return type;
	}
	
	public String getToken() {
		return token;
	}
	
	public boolean isOperator() {
		return type == TokenType.operator;
	}
	
	public boolean isLiteral() {
		return type == TokenType.literal;
	}
	
	public boolean isSeperator() {
		return type == TokenType.seperator;
	}
	
	public boolean isIdentifier() {
		return type == TokenType.identifier;
	}
}
