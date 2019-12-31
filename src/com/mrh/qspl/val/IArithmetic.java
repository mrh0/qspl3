package com.mrh.qspl.val;

public interface IArithmetic {
	public Value add(Value v);
	
	public Value sub(Value v);
	
	public Value multi(Value v);
	
	public Value div(Value v);
	
	public Value mod(Value v);
	
	public Value pow(Value v);
	
	public Value root();
	
	public boolean bool();
	
	public boolean equals(Value v);
	
	public int compare(Value v);
	
	public boolean contains(Value v);
	
	public Value childObject(Value v);
	
	public Value[] childObjects(Value v);
	
	public Value accessor(Value[] v);
	public default Value accessor(Value v) {
		Value[] k = {v};
		return accessor(k);
	}
	
	public int getType();
	
	public int getSize();
	
	public Value toType(int type);
}
