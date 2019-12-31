package com.mrh.qspl.syntax.tokenizer;

import java.util.ArrayList;
import java.util.Stack;

public class MapContext {
	public Stack<Token> opStack;
	public Stack<Boolean> parFuncStack;
	public ArrayList<Token> tokStack;
	public ArrayList<Token> postfixList;
	
	public MapContext() {
		opStack = new Stack<Token> ();
		postfixList = new ArrayList<Token>();
		parFuncStack = new Stack<Boolean> ();
		tokStack = new ArrayList<Token> ();
	}
}
