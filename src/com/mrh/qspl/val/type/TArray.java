package com.mrh.qspl.val.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mrh.qspl.io.console.Console;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.type.TObject.TObjectKeyIterator;
import com.mrh.qspl.var.Var;

public class TArray implements Value{
	private ArrayList<Var> values;
	private static Map<String, Value> prototype = new HashMap<String, Value>();
	
	public TArray() {
		values = new ArrayList<Var>();
	}
	
	public TArray(ArrayList<Value> v) {
		values = new ArrayList<Var>();
		for(Value k : v)
			values.add(new Var(k));
	}
	
	public TArray(Value...v) {
		values = new ArrayList<Var>();
		for(Value k : v)
			values.add(new Var(k));
	}
	
	public TArray(String...v) {
		values = new ArrayList<Var>();
		for(String k : v)
			values.add(new Var(new TString(k)));
	}
	
	public TArray(List<Value> v) {
		values = new ArrayList<Var>();
		for(Value k : v)
			values.add(new Var(k));
	}
	
	public Value find(Value v) {
		for(Var k : values)
			if(k.get().equals(v))
				return k.get();
		return TUndefined.getInstance();
	}

	@Override
	public Value add(Value v) {
		if(v.getType() == Types.ARRAY)
			for(Value k : TArray.from(v).getAll())
				values.add(new Var(k));
			//values.addAll(((TArray)v).getAll());
		else
			values.add(new Var(v));
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
		if(v.getType() != Types.NUMBER) {
			Console.g.err("Cannot preform operation divide type " + Types.getName(v) + " with type Array.");
			return this;
		}
		int index  = v.intValue();
		if(index < getSize() && index >= 0)
			values.remove(index);
		return this;
	}

	@Override
	public Value mod(Value v) {
		Console.g.err("Cannot preform operation modulo type " + Types.getName(v) + " with type Array.");
		return this;
	}

	@Override
	public Value pow(Value v) {
		Console.g.err("Cannot preform operation pow type " + Types.getName(v) + " with type Array.");
		return this;
	}

	@Override
	public Value root() {
		Console.g.err("Cannot preform operation sqrt on type Array.");
		return this;
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
		return new TArray();//new TArray(values); // Won't duplicate value references!!!
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
			return values.get(v[0].intValue());
		if(v.length == 2) {
			int a1 = v[0].intValue();
			int a2 = v[1].intValue();
			if(a2 > a1)
				return new Var(TUndefined.getInstance());
			if(a2 < 0)
				return new Var(new TArray(extractValues(values.subList(a1, values.size()+a2))));
			return new Var(new TArray(extractValues(values.subList(a1, a2))));
		}
		Console.g.err("Bad use of accessor.");
		return new Var(TUndefined.getInstance());
	}
	
	public ArrayList<Value> getValues(){
		ArrayList<Value> r = new ArrayList<Value>();
		for(Var v : values) {
			r.add(v.get());
		}
		return r;
	}
	
	private static ArrayList<Value> extractValues(List<Var> l){
		ArrayList<Value> r = new ArrayList<Value>();
		for(Var v : l) {
			r.add(v.get());
		}
		return r;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i = 0; i < values.size(); i++) {
			Value v = values.get(i).get();
			sb.append(v.getType() == Types.STRING?"'"+v+"'":v);
			if(i+1 < values.size())
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int getSize() {
		return values.size();
	}
	
	public Value getIndex(int i) {
		return values.get(i).get();
	}
	
	public Value setIndex(int i, Value v) {
		values.set(i, new Var(v));
		return this;
	}
	
	public ArrayList<Value> getAll(){
		return extractValues(values);
	}
	
	@Deprecated
	public double sum() {
		double d = 0;
		for(Var vt : values) {
			if(vt.get().getType() == Types.ARRAY)
				d += ((TArray)vt.get()).sum();
			if(vt.get().getType() == Types.NUMBER)
				d += ((TNumber)vt.get()).get();
		}
		return d;
	}

	@Override
	public Value toType(int type) {
		if(type == Types.ARRAY)
			return this;
		if(type == Types.STRING)
			return new TString(this.toString());
		Console.g.err("Cannot convert type Array to type" + Types.getName(type) + ".");
		return this;
	}

	@Override
	public int intValue() {
		return values.hashCode();
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
		values.add(new Var(v));
	}
	
	public JSONArray toJSON() {
		JSONArray o = new JSONArray();
		for(int i = 0; i < values.size(); i++) {
			Var vt = values.get(i);
			if(vt.get().getType() == Types.OBJECT)
				o.put(i, TObject.from(vt.get()).toJSON());
			else if(vt.get().getType() == Types.ARRAY)
				o.put(i, TArray.from(vt.get()).toJSON());
			else if(vt.get().getType() == Types.NUMBER)
				o.put(i, TNumber.from(vt.get()).get());
			else if(vt.get().getType() == Types.STRING)
				o.put(i, TString.from(vt.get()).get());
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
