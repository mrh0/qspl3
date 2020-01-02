package com.mrh.qspl.val.type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mrh.qspl.io.console.Console;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.var.Var;

public class TObject implements Value{
	
	private Map<String, Value> map;
	private static Map<String, Value> prototype = new HashMap<String, Value>();
	private String[] argOrder = null;
	
	public TObject() {
		map = new HashMap<>();
	}
	
	public TObject(Map<String, Value> m) {
		map = m;
		if(map == null) {
			Console.g.err("Object defined as null, defauling to empty.");
			map = new HashMap<>();
		}
	}
	
	public TObject(Map<String, Value> m, String[] keys) {
		map = m;
		if(map == null) {
			Console.g.err("Object defined as null, defauling to empty.");
			map = new HashMap<>();
		}
		argOrder = keys;
	}
	
	public String[] getSpecialOrder() {
		return argOrder;
	}

	@Override
	public Value add(Value v) {
		if(v.getType() == Types.OBJECT) {
			TObject o = TObject.from(v);
			for(String key : o.getKeys()) {
				this.set(key, o.get(key));
			}
		}
		else
			Console.g.err("Cannot preform operation add type " + Types.getName(v) + " to type Object.");
		return this;
	}

	@Override
	public Value sub(Value v) {
		if(v.getType() == Types.STRING) {
			TString s = TString.from(v);
			this.map.remove(s);
			return TUndefined.getInstance();
		}
		Console.g.err("Cannot preform operation subtact type " + Types.getName(v) + " from type Object.");
		return TUndefined.getInstance();
	}

	@Override
	public Value multi(Value v) {
		Console.g.err("Cannot preform operation multiply type " + Types.getName(v) + " with type Object.");
		return this;
	}

	@Override
	public Value div(Value v) {
		Console.g.err("Cannot preform operation divide type " + Types.getName(v) + " with type Object.");
		return this;
	}

	@Override
	public Value mod(Value v) {
		Console.g.err("Cannot preform operation modulo type " + Types.getName(v) + " with type Object.");
		return this;
	}

	@Override
	public Value pow(Value v) {
		Console.g.err("Cannot preform operation pow type " + Types.getName(v) + " with type Object.");
		return this;
	}

	@Override
	public Value root() {
		Console.g.err("Cannot preform operation sqrt on type Object.");
		return this;
	}

	@Override
	public boolean bool() {
		return !map.isEmpty();
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
	public boolean contains(Value v) {
		for(Value vv : map.values()) {
			if(v.equals(vv))
				return true;
		}
		return false;
	}

	@Override
	public Value childObject(Value v) {
		return map.getOrDefault(TString.from(v).get(), TUndefined.getInstance());
	}

	@Override
	public Value[] childObjects(Value v) {
		return map.values().toArray(new Value[0]);
	}

	@Override
	public Value accessor(Value[] v) {
		if(v.length == 0)
			return new TNumber(map.size());
		if(v.length == 1) {
			String key = TString.from(v[0]).get();
			return map.getOrDefault(key, getPrototype().getOrDefault(key, TUndefined.getInstance()));
		}
		TObject o = new TObject();
		for(Value vv : v) {
			String k = TString.from(vv).get();
			o.set(k, map.getOrDefault(k, TUndefined.getInstance()));
		}
		return o;
	}

	@Override
	public int getType() {
		return Types.OBJECT;
	}

	@Override
	public int getSize() {
		return map.size();
	}

	@Override
	public Value toType(int type) {
		if(type == Types.OBJECT)
			return this;
		if(type == Types.STRING)
			return new TString(this.toString());
		return TUndefined.getInstance();//convert into json if string
	}

	@Override
	public Object get() {
		return map;
	}

	@Override
	public Value duplicate() {
		return TUndefined.getInstance();
	}

	@Override
	public boolean isUndefined() {
		return false;
	}

	@Override
	public int intValue() {
		return 0;
	}

	public static TObject from(Value v) {
		if(v instanceof TObject)
			return (TObject)v;
		Console.g.err(v + " is not an object.");
		return null;
	}
	
	public TObject set(String key, Value v) {
		map.put(key, v);
		return this;
	}
	
	public Value get(String key) {
		if(map == null) {
			Console.g.err("Broken Object.");
			return TUndefined.getInstance();
		}
		return map.getOrDefault(key, TUndefined.getInstance());
	}
	
	public String[] getKeys() {
		if(argOrder != null)
			return argOrder;
		return map.keySet().toArray(new String[0]);
	}
	
	public Value[] getValues() {
		return map.values().toArray(new Value[0]);
	}
	
	public Map<String, Value> getMap() {
		return map;
	}
	
	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
	
	public void put(String key, Value v) {
		map.put(key, v);
	}
	
	public JSONObject toJSON() {
		JSONObject o = new JSONObject();
		for(String key : map.keySet()) {
			Value vt = map.get(key);
			if(vt.getType() == Types.OBJECT)
				o.put(key, TObject.from(vt).toJSON());
			else if(vt.getType() == Types.ARRAY)
				o.put(key, TArray.from(vt).toJSON());
			else if(vt.getType() == Types.NUMBER)
				o.put(key, TNumber.from(vt).get());
			else if(vt.getType() == Types.STRING)
				o.put(key, TString.from(vt).get());
			else
				o.put(key, vt.toString());
		}
		return o;
	}
	
	public TObject fromJSON(JSONObject j) {
		TObject o = this;
		for(String key : j.keySet()) {
			Object i = j.get(key);
			if(i instanceof JSONObject)
				o.put(key, new TObject().fromJSON((JSONObject)i));
			else if(i instanceof JSONArray)
				o.put(key, new TArray().fromJSON((JSONArray)i));
			else if(i instanceof Double)
				o.put(key, new TNumber((double)i));
			else if(i instanceof Integer)
				o.put(key, new TNumber(new Double((int)i)));
			else
				o.put(key, new TString(i.toString()));
		}
		return o;
	}
	
	public static Map<String, Value> getPrototype(){
		return prototype;
	}
	
	public static void addPrototype(Var v){
		prototype.put(v.getName(), v.get());
	}
	
	protected class TObjectKeyIterator implements Iterator<Value>{
		private Iterator<String> obj;
		public TObjectKeyIterator(TObject obj) {
			this.obj = obj.getMap().keySet().iterator();
		}
		
		@Override
		public boolean hasNext() {
			return obj.hasNext();
		}

		@Override
		public TString next() {
			return new TString(obj.next());
		}
		
	}
	
	public Iterator keyIterator() {
		return new TObjectKeyIterator(this);
	}
}
