package com.mrh.qspl.vm.stack;

import java.util.ArrayList;
import com.mrh.qspl.val.Value;

public class BracketItem {
	private Value prev;
	private char opener;
	private ArrayList<Value> values;
	private boolean subOp = false;
	
	public BracketItem(char opener, Value prev, boolean subOp) {
		this.prev = prev;
		this.opener = opener;
		this.values = new ArrayList<Value>();
		this.subOp = subOp;
	}
	
	public ArrayList<Value> getParameters() {
		return this.values;
	}
	
	public void add(Value v) {
		values.add(v);
	}
	
	public Value getPrev() {
		return this.prev;
	}
	
	public char getOpener() {
		return opener;
	}
	
	public boolean isSubOp() {
		return subOp;
	}
	
	@Override
	public String toString() {
		return "BracketItem:"+opener+":"+prev+":"+values.size();
	}
}
