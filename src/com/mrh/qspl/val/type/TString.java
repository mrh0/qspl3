package com.mrh.qspl.val.type;

import com.mrh.qspl.io.console.Console;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.var.Var;

public class TString implements Value<String>, Comparable<Value>{

	final String s;
	
	public TString(String v) {
		s = v;
	}
	
	public TString(TString v) {
		s = v.get();
	}
	
	@Override
	public String get() {
		return s;
	}
	
	@Override
	public String toString() {
		return get();
	}

	@Override
	public Value add(Value v) {
		if(v instanceof TNumber) 
			return new TString(s+v.get());
		if(v instanceof TString) 
			return new TString(s+v.get());
		return new TString(s+v.toString());
	}

	@Override
	public Value sub(Value v) {
		if(v instanceof TNumber) 
			return new TString(s.replaceAll((String)v.get(), ""));
		if(v instanceof TString) 
			return new TString(s.replaceAll((String)v.get(), ""));
		if(v instanceof TArray) {
			String r = s;
			TArray a = (TArray) v;
			for(Value k : a.getAll()) {
				r = r.replaceAll((String)k.toString(), "");
			}
			return new TString(r);
		}
		Console.g.err("Cannot preform operation subtract type " + Types.getName(v) + " with type String.");
		return this;
	}

	@Override
	public Value multi(Value v) {
		Console.g.err("Cannot preform operation multiply type " + Types.getName(v) + " with type String.");
		return this;
	}

	@Override
	public Value div(Value v) {
		if(v instanceof TString) {
			String[] k = s.split((String)v.get());
			Value[] j = new Value[k.length];
			for(int i = 0; i < k.length; i++)
				j[i] = new TString(k[i]);
			return new TArray(j);
		}
		Console.g.err("Cannot preform operation divide type " + Types.getName(v) + " with type String.");
		return this;
	}

	@Override
	public Value mod(Value v) {
		Console.g.err("Cannot preform operation modulo type " + Types.getName(v) + " with type String.");
		return this;
	}

	@Override
	public Value pow(Value v) {
		Console.g.err("Cannot preform operation pow type " + Types.getName(v) + " with type String.");
		return this;
	}

	@Override
	public Value root() {
		Console.g.err("Cannot preform operation sqrt on type String.");
		return this;
	}
	
	@Override
	public boolean bool() {
		return s.length() > 0;
	}

	@Override
	public boolean equals(Value v) {
		return v.get().equals(this.get());
	}

	@Override
	public int compare(Value v) {
		return ((String)v.get()).compareTo(s);
	}

	@Override
	public Value duplicate() {
		return new TString(s);
	}

	@Override
	public boolean contains(Value v) {
		if(v instanceof TString)
			return s.indexOf((String)v.get()) != -1;
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
		return Types.STRING;
	}
	
	@Override
	public boolean isUndefined() {
		return false;
	}

	@Override
	public Var accessor(Value[] v) {
		if(v.length == 0)
			return new Var(new TNumber(getSize()));
		if(v.length == 1)
			return new Var(new TString(s.charAt((int)Math.round((double)v[0].get()))+""));
		if(v.length >= 2)
			return new Var(new TString(s.substring((int)Math.round((double)v[0].get()), (int)Math.round((double)v[1].get()+1))+""));
		Console.g.err("Bad use of accessor.");
		return new Var(TUndefined.getInstance());
	}

	@Override
	public int getSize() {
		return s.length();
	}
	
	@Override
	public Value toType(int type) {
		if(type == Types.STRING)
			return this;
		if(type == Types.NUMBER)
			return new TNumber(Double.parseDouble(s));
		Console.g.err("Cannot convert type String to type" + Types.getName(type) + ".");
		return this;
	}

	@Override
	public int intValue() {
		return s.hashCode();
	}

	@Override
	public int compareTo(Value o) {
		return compare(o);
	}
	
	public static TString from(Value v) {
		if(v instanceof TString)
			return (TString)v;
		Console.g.err(v + " is not a String.");
		return null;
	}
}
