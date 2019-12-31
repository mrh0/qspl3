package com.mrh.qspl.syntax.parser;

import java.util.Stack;

public class Parser {
	private Stack<Block> blockStack;
	private Statement last;
	
	public Parser(Block b) {
		blockStack = new Stack<>();
		blockStack.push(b);
	}
	
	public Block getCurrentBlock() {
		return blockStack.peek();
	}
	
	public void enterBlock(Block b) {
		blockStack.push(b);
	}
	
	public Block exitBlock() {
		return blockStack.pop();
	}
}
