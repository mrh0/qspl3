package com.mrh.qspl.val.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mrh.qspl.io.console.Console;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.func.IFunc;
import com.mrh.qspl.var.Var;
import com.mrh.qspl.vm.Scope;
import com.mrh.qspl.vm.VM;

public class TFunc implements Value{
	
	private static Map<String, Value> prototype = new HashMap<String, Value>();
	
	protected String[] paramaterList = {};
	
	public String[] getParameters() {
		return paramaterList;
	}

	private final IFunc internal;
	
	public TFunc() {
		internal = null;
	}
	
	public TFunc(IFunc f) {
		internal = f;
	}
	
	public TFunc(IFunc func, String...parameters) {
		internal = func;
		paramaterList = parameters;
	}
	
	public Value execute(ArrayList<Value> args, VM vm, Value pThis) {
		if(internal != null)
			return internal.execute(args, vm, pThis);
		return TUndefined.getInstance();
	}
	
	@Override
	public String toString() {
		String r = "";
		for(int i = 0; i < paramaterList.length; i++) {
			r += paramaterList[i];
			if(i+1 < paramaterList.length)
				r += ",";
		}
		return "ifunc("+r+")";
	}
	
	@Override
	public Value add(Value v) {
		return TUndefined.getInstance();
	}

	@Override
	public Value sub(Value v) {
		return TUndefined.getInstance();
	}

	@Override
	public Value multi(Value v) {
		return TUndefined.getInstance();
	}

	@Override
	public Value div(Value v) {
		return TUndefined.getInstance();
	}

	@Override
	public Value mod(Value v) {
		return TUndefined.getInstance();
	}

	@Override
	public Value pow(Value v) {
		return TUndefined.getInstance();
	}

	@Override
	public Value root() {
		return TUndefined.getInstance();
	}

	@Override
	public boolean bool() {
		return true;
	}

	@Override
	public boolean equals(Value v) {
		return this == v;
	}

	@Override
	public int compare(Value v) {
		return 0;
	}

	@Override
	public Object get() {
		return this;
	}

	@Override
	public Value duplicate() {
		return new TFunc();
	}

	@Override
	public boolean contains(Value v) {
		return false;
	}

	@Override
	public Value childObject(Value v) {
		return TUndefined.getInstance();
	}

	@Override
	public Value[] childObjects(Value v) {
		return new Value[0];
	}

	@Override
	public int getType() {
		return Types.FUNC;
	}

	@Override
	public boolean isUndefined() {
		return false;
	}

	@Override
	public Var accessor(Value[] v) {
		//if(v.length == 0)
		return new Var(TUndefined.getInstance());
		//return getPrototype().getOrDefault(TString.from(v[0]), TUndefined.getInstance());
	}

	@Override
	public int getSize() {
		return 0;
	}
	
	@Override
	public Value toType(int type) {
		if(type == Types.FUNC)
			return this;
		if(type == Types.STRING)
			return new TString(this.toString());
		return TUndefined.getInstance();
	}

	@Override
	public int intValue() {
		return -1;
	}
	
	public static TFunc from(Value v) {
		if(v instanceof TFunc)
			return (TFunc)v;
		Console.g.err(v + " is not a function.");
		return null;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
	
	public static Map<String, Value> getPrototype(){
		return prototype;
	}
	
	public static void addPrototype(Var v){
		prototype.put(v.getName(), v.get());
	}
}
