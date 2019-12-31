package com.mrh.qspl.val.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mrh.qspl.io.console.Console;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.type.TObject.TObjectKeyIterator;
import com.mrh.qspl.var.Var;

public class TArray implements Value{
	private ArrayList<Value> values;
	private static Map<String, Value> prototype = new HashMap<String, Value>();
	
	public TArray() {
		values = new ArrayList<Value>();
	}
	
	public TArray(ArrayList<Value> v) {
		values = v;
	}
	
	public TArray(Value...v) {
		values = new ArrayList<Value>();
		for(Value k : v)
			values.add(k);
	}
	
	public TArray(String...v) {
		values = new ArrayList<Value>();
		for(String k : v)
			values.add(new TString(k));
	}
	
	public TArray(List<Value> v) {
		values = new ArrayList<Value>();
		for(Value k : v)
			values.add(k);
	}
	
	public Value find(Value v) {
		for(Value k : values)
			if(k.equals(v))
				return k;
		return TUndefined.getInstance();
	}

	@Override
	public Value add(Value v) {
		if(v.getType() == Types.ARRAY)
			values.addAll(((TArray)v).getAll());
		else
			values.add(v);
		return this;
	}

	@Override
	public Value sub(Value v) {
		Value vt = find(v);
		if(!vt.isUndefined())
			values.remove(vt);
		return this;
	}

	@Override
	public Value multi(Value v) {
		/*if(v.getType() == Types.NUMBER) {
			for(int i = 0; i < getSize(); i++) {
				values.set(i, values.get(i).multi(v));
			}
			return this;
		}*/
		if(v.getType() == Types.NUMBER) {
			TArray a = new TArray();
			for(int i = 0; i < v.intValue(); i++) {
				a.add(this);
			}
			return a;
		}
		if(v.getType() == Types.STRING) {
			String s = "";
			String j = TString.from(v).get();
			for(int i = 0; i < getSize(); i++) {
				s += values.get(i).toString() + (i+1 < getSize()?j:"");
			}
			return new TString(s);
		}
		return TUndefined.getInstance();
	}

	@Override
	public Value div(Value v) {
		int index  = v.intValue();
		if(index < getSize() && index >= 0)
			values.remove(index);
		return this;
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
		return getSize() > 0;
	}

	@Override
	public boolean equals(Value v) {
		if(!(v instanceof TArray))
			return false;
		if(getSize() != v.getSize())
			return false;
		TArray va = (TArray)v;
		for(int i = 0; i < getSize(); i++) {
			if(!va.getIndex(i).equals(getIndex(i)))
				return false;
		}
		return true;
	}

	@Override
	public int compare(Value v) {
		return 0;
	}

	@Override
	public boolean contains(Value v) {
		for(int i = 0; i < getSize(); i++) {
			if(v.equals(getIndex(i)))
				return true;
		}
		return false;
	}

	@Override
	public Value childObject(Value v) {
		return null;
	}

	@Override
	public Value[] childObjects(Value v) {
		return null;
	}

	@Override
	public int getType() {
		return Types.ARRAY;
	}

	@Override
	public Object get() {
		return this;
	}

	@Override
	public Value duplicate() {
		return new TArray(values);
	}

	@Override
	public boolean isUndefined() {
		return false;
	}

	@Override
	public Value accessor(Value[] v) {
		if(v.length == 0)
			return new TNumber(getSize());
		if(v.length == 1)
			return values.get(v[0].intValue());
		if(v.length == 2) {
			int a1 = v[0].intValue();
			int a2 = v[1].intValue();
			if(a2 > a1)
				return TUndefined.getInstance();
			if(a2 < 0)
				return new TArray(values.subList(a1, values.size()+a2));
			return new TArray(values.subList(a1, a2));
		}
		return TUndefined.getInstance();
	}
	
	@Override
	public String toString() {
		String r = "[";
		for(int i = 0; i < values.size(); i++) {
			r += values.get(i);
			if(i+1 < values.size())
				r += ",";
		}
		return r+"]";
	}

	@Override
	public int getSize() {
		return values.size();
	}
	
	public Value getIndex(int i) {
		return values.get(i);
	}
	
	public Value setIndex(int i, Value v) {
		values.set(i, v);
		return this;
	}
	
	public ArrayList<Value> getAll(){
		return values;
	}
	
	public double sum() {
		double d = 0;
		for(Value vt : values) {
			if(vt.getType() == Types.ARRAY)
				d += ((TArray)vt).sum();
			if(vt.getType() == Types.NUMBER)
				d += ((TNumber)vt).get();
		}
		return d;
	}

	@Override
	public Value toType(int type) {
		if(type == Types.ARRAY)
			return this;
		if(type == Types.STRING)
			return new TString(this.toString());
		return TUndefined.getInstance();
	}

	@Override
	public int intValue() {
		return -1;
	}
	
	public static TArray merge(Value v1, Value v2) {
		TArray a = new TArray();
		a.add(v1);
		a.getAll().add(v2);
		return a;
	}
	
	public static TArray from(Value v) {
		if(v instanceof TArray)
			return (TArray)v;
		Console.g.err(v + " is not an array.");
		return null;
	}

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}
	
	public void put(Value v) {
		values.add(v);
	}
	
	public JSONArray toJSON() {
		JSONArray o = new JSONArray();
		for(int i = 0; i < values.size(); i++) {
			Value vt = values.get(i);
			if(vt.getType() == Types.OBJECT)
				o.put(i, TObject.from(vt).toJSON());
			else if(vt.getType() == Types.ARRAY)
				o.put(i, TArray.from(vt).toJSON());
			else if(vt.getType() == Types.NUMBER)
				o.put(i, TNumber.from(vt).get());
			else if(vt.getType() == Types.STRING)
				o.put(i, TString.from(vt).get());
			else
				o.put(i, vt.toString());
		}
		return o;
	}
	
	public TArray fromJSON(JSONArray j) {
		TArray o = this;
		for(Object i : j.toList()) {
			if(i instanceof JSONObject)
				o.put(new TObject().fromJSON((JSONObject)i));
			else if(i instanceof JSONArray)
				o.put(new TArray().fromJSON((JSONArray)i));
			else if(i instanceof Double)
				o.put(new TNumber((double)i));
			else if(i instanceof Integer)
				o.put(new TNumber(new Double((int)i)));
			else
				o.put(new TString(i.toString()));
		}
		return o;
	}
	
	public static Map<String, Value> getPrototype(){
		return prototype;
	}
	
	public static void addPrototype(Var v){
		prototype.put(v.getName(), v.get());
	}
	
	protected class TArrayKeyIterator implements Iterator<Value>{
		private int i = 0;
		private TArray a;
		public TArrayKeyIterator(TArray a) {
			this.a = a;
		}
		
		@Override
		public boolean hasNext() {
			return i < a.getSize();
		}

		@Override
		public TNumber next() {
			return new TNumber(i++);
		}
		
	}
	
	public Iterator keyIterator() {
		return new TArrayKeyIterator(this);
	}
}
