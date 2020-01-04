package com.mrh.qspl.var;

import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.type.TUndefined;

public class Var {
	private Value value;
	private String name = "";
	private boolean constant = false;
	
	public Var(String name, Value v) {
		this.value = v;
		this.name = name;
	}
	
	public Var(String name) {
		this.name = name;
		this.value = TUndefined.getInstance();
	}
	
	public Var(Value v) {
		this.name = "?";
		this.value = v;
	}
	
	public Var(String name, Value v, boolean constant) {
		this.value = v;
		this.constant = constant;
		this.name = name;
	}
	
	public void set(Value v) {
		if(!constant || this.value instanceof TUndefined)
			this.value = v;
		else
			System.err.println("Tried to set constant value");
	}
	
	public Value get() {
		return value;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "("+name+":"+value+")";
	}
}
