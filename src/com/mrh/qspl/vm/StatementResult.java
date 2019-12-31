package com.mrh.qspl.vm;

import java.util.Stack;

import com.mrh.qspl.val.Value;
import com.mrh.qspl.var.Var;
import com.mrh.qspl.vm.stack.ValStack;

public class StatementResult {
	public boolean pass;
	public ValStack vals; 
	public Stack<Var> vars;
	public Value ret;
	public StatementResult(boolean pass, ValStack vals, Stack<Var> vars, Value ret) {
		this.pass = pass;
		this.vals = vals;
		this.vars = vars;
		this.ret = ret;
	}
}
