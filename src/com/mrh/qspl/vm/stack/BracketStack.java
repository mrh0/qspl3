package com.mrh.qspl.vm.stack;

import java.util.ArrayList;
import java.util.Stack;

import com.mrh.qspl.syntax.tokenizer.Token;
import com.mrh.qspl.val.Value;

public class BracketStack {
	
	
	
	private Stack<BracketItem> stack;
	
	public BracketStack() {
		stack = new Stack<BracketItem>();
	}
	
	public void push(BracketItem bi) {
		stack.push(bi);
	}
	
	public int size() {
		return stack.size();
	}
	
	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	public BracketItem pop() {
		return stack.pop();
	}
	
	public BracketItem peek() {
		return stack.peek();
	}
}
