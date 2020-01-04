package com.mrh.qspl.val.type;

import com.mrh.qspl.io.console.Console;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.var.Var;

public class TNumber implements Value<Double>, Comparable<Value>{

	double value;//final
	
	public TNumber() {
		value = 0;
	}
	
	public TNumber(double v) {
		value = v;
	}
	
	public TNumber(TNumber v) {
		value = v.get();
	}
	
	public TNumber(Value v) {
		value = (double) v.get();
	}
	
	@Override
	public String toString() {
		return value+"";
	}

	@Override
	public Double get() {
		return value;
	}
	
	@Override
	public Value add(Value v) {
		if(v instanceof TNumber) 
			return new TNumber(value + (double)v.get());
		if(v instanceof TString) 
			return new TString(value+((String)v.get()));
		return TUndefined.getInstance();
	}

	@Override
	public Value sub(Value v) {
		if(v instanceof TNumber) 
			return new TNumber(value - (double)v.get());
		if(v instanceof TString)
			System.err.println("Cannot subtract string from number");
		return TUndefined.getInstance();
	}

	@Override
	public Value multi(Value v) {
		if(v instanceof TNumber) 
			return new TNumber(value * (double)v.get());
		if(v instanceof TString)
			System.err.println("Cannot multiply string with a number");
		return TUndefined.getInstance();
	}

	@Override
	public Value div(Value v) {
		if(v instanceof TNumber) 
			return new TNumber(value / (double)v.get());
		if(v instanceof TString)
			System.err.println("Cannot multiply string with a number");
		return TUndefined.getInstance();
	}

	@Override
	public Value mod(Value v) {
		if(v instanceof TNumber) 
			return new TNumber(value % (double)v.get());
		if(v instanceof TString)
			System.err.println("Cannot multiply string with a number");
		return TUndefined.getInstance();
	}

	@Override
	public Value pow(Value v) {
		if(v instanceof TNumber) 
			return new TNumber(Math.pow(value, (double)v.get()));
		if(v instanceof TString)
			System.err.println("Cannot multiply string with a number");
		return TUndefined.getInstance();
	}

	@Override
	public Value root() {
		return new TNumber(Math.sqrt(value));
	}

	@Override
	public boolean bool() {
		return value > 0;
	}

	@Override
	public boolean equals(Value v) {
		return v.get().equals(value);
	}
	
	@Override
	public int compare(Value v) {
		return (((Double)value).compareTo(TNumber.from(v).get()));
	}

	@Override
	public Value duplicate() {
		return new TNumber(value);
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
		return Types.NUMBER;
	}
	
	@Override
	public boolean isUndefined() {
		return false;
	}

	@Override
	public Var accessor(Value[] v) {
		return new Var(TUndefined.getInstance());
	}

	@Override
	public int getSize() {
		return 0;
	}
	
	@Override
	public Value toType(int type) {
		if(type == Types.NUMBER)
			return this;
		if(type == Types.STRING)
			return new TString(this.toString());
		return TUndefined.getInstance();
	}

	@Override
	public int intValue() {
		return (int)Math.round(value);
	}

	@Override
	public int compareTo(Value o) {
		return compare(o);
	}
	
	public static TNumber from(Value v) {
		if(v instanceof TNumber)
			return (TNumber)v;
		Console.g.err(v + " is not a number.");
		return new TNumber();
	}
	
	public TNumber increment(double v) {
		value += v;
		return this;
	}
	
	public TNumber decrement(double v) {
		value -= v;
		return this;
	}
}
