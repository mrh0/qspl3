package com.mrh.qspl.val.type;

import com.mrh.qspl.val.Value;

public class TUndefined implements Value<Object>{
	
	private static TUndefined instance = null;
	
	private TUndefined() {
		instance = this;
	}
	
	public static TUndefined getInstance() {
		if(instance == null)
			new TUndefined();
		return instance;
	}
	
	@Override
	public String toString() {
		return "undefined";//UNDEFINED
	}

	@Override
	public Value add(Value v) {
		return v;
	}

	@Override
	public Value sub(Value v) {
		return v;
	}

	@Override
	public Value multi(Value v) {
		return new TNumber(0);
	}

	@Override
	public Value div(Value v) {
		return new TNumber(0);
	}

	@Override
	public Value mod(Value v) {
		return new TNumber(0);
	}

	@Override
	public Value pow(Value v) {
		return new TNumber(0);
	}

	@Override
	public Value root() {
		return getInstance();
	}

	@Override
	public boolean bool() {
		return false;
	}

	@Override
	public boolean equals(Value v) {
		return false;
	}

	@Override
	public int compare(Value v) {
		return 0;
	}

	@Override
	public Object get() {
		return getInstance();
	}

	@Override
	public Value duplicate() {
		return getInstance();
	}

	@Override
	public boolean contains(Value v) {
		return false;
	}

	@Override
	public Value childObject(Value v) {
		return getInstance();
	}

	@Override
	public Value[] childObjects(Value v) {
		return new Value[0];
	}

	@Override
	public int getType() {
		return Types.UNDEFINED;
	}

	@Override
	public boolean isUndefined() {
		return true;
	}

	@Override
	public Value accessor(Value[] v) {
		return getInstance();
	}

	@Override
	public int getSize() {
		return 0;
	}
	
	@Override
	public Value toType(int type) {
		if(type == Types.UNDEFINED)
			return this;
		if(type == Types.STRING)
			return new TString(this.toString());
		if(type == Types.ARRAY)
			return new TArray();
		if(type == Types.OBJECT)
			return new TObject();
		if(type == Types.NUMBER)
			return new TNumber(0);
		return TUndefined.getInstance();
	}

	@Override
	public int intValue() {
		return -1;
	}

	@Override
	public int compareTo(Value o) {
		return 0;
	}
}
