package com.mrh.qspl.vm.stack;

import java.util.Stack;

import com.mrh.qspl.debug.Debug;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.type.TUndefined;
import com.mrh.qspl.var.Var;
import com.mrh.qspl.vm.ExpressionEvaluator;

public class ValStack{
	private class ValVarItem{
		public ValVarItem(Value val, Var var) {
			this.val = val;
			this.var = var;
		}
		public Value val;
		public Var var;
	}
	private Stack<ValVarItem> vals;
	private ExpressionEvaluator ee;
	public ValStack(ExpressionEvaluator ee) {
		this.vals = new Stack<ValVarItem>();
		this.ee = ee;
	}
	
	public void push(Value item) {
		if(Debug.enabled())
			System.out.println("PUSH:  " + item);
		this.vals.push(new ValVarItem(item, null));
	}
	
	public void push(Value item, Var var) {
		if(Debug.enabled())
			System.out.println("PUSH: VAR " + var);
		this.vals.push(new ValVarItem(item, var));
	}
	
	public boolean isEmpty() {
		return this.vals.isEmpty();
	}
	
	public int size() {
		return this.vals.size();
	}
	
	public Value pop(Stack<Var> vars) {
		if(isEmpty()) {
			if(Debug.enabled())
				System.out.println("POP: Empty UNDEF");
			return TUndefined.getInstance();
		}
		if(this.vals.peek().var != null) {
			vars.pop();
		}
		if(Debug.enabled())
			System.out.println("POP: " + this.vals.peek().val + (this.vals.peek().var!=null?" VAR: " + this.vals.peek().var:""));
		return this.vals.pop().val;
	}
	
	public Value peek() {
		if(isEmpty())
			return null;
		return vals.peek().val;
	}
}