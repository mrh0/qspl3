package com.mrh.qspl.syntax.parser;

import com.mrh.qspl.syntax.tokenizer.Token;

public class Statement {
	private Token[] tokens;
	private Block block;
	private StatementEndType endType = StatementEndType.END;
	private int line;
	
	public Statement(int line, Token[] tokens) {
		this.tokens = tokens;
		this.line = line;
	}
	
	public Statement(int line, Token[] tokens, StatementEndType endType) {
		this.tokens = tokens;
		this.endType = endType;
		this.line = line;
	}
	
	public int getLine() {
		return this.line;
	}
	
	public Token[] getTokens() {
		return tokens;
	}
	
	public Block getNext() {
		return block;
	}
	
	public boolean hasNext() {
		return block != null;
	}
	
	public void setBlock(Block b) {
		block = b;
	}
	
	public String toString() {
		String r = "";
		for(Token t : tokens) {
			r += t.toString();
		}
		return r + ";\n";
	}
	
	public StatementEndType getEndType() {
		return endType;
	}
}
