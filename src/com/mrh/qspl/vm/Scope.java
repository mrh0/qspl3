package com.mrh.qspl.vm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mrh.qspl.internal.common.Common;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.var.Var;

public class Scope {
	private String name = "unknown scope";
	private Map<String, Var> variables;
	private Map<String, Value> exports;
	private ArrayList<String> keyOrder = null;
	private boolean depthLock = false;
	
	public Scope(String name) {
		this.name = name;
		this.variables = new HashMap<String, Var>();
	}
	
	public Scope(String name, boolean lock) {
		this.depthLock = true;
		this.name = name;
		this.variables = new HashMap<String, Var>();
		this.exports = null;
		if(this.depthLock)
			keyOrder = new ArrayList<String>();
	}
	
	public boolean isLocked() {
		return depthLock;
	}
	
	public Var getVariable(String name) {
		return variables.get(name);
	}
	
	public Var setVariable(String name, Var v) {
		if(this.keyOrder != null)
			if(!this.keyOrder.contains(name))
				this.keyOrder.add(name);
		return variables.put(name, v);
	}
	
	public Var setVariable(Var v) {
		return setVariable(v.getName(), v);
	}
	
	@Override
	public String toString() {
		return "SCOPE:"+name+":"+variables.size();
	}
	
	protected Map<String, Var> getAllVariables(){
		return variables;
	}
	
	protected Map<String, Value> getAllValues(){
		Set<String> c = variables.keySet();
		Map<String, Value> map = new HashMap<>();
		for(String s : c) {
			map.put(s, this.getVariable(s).get());
		}
		return map;
	}
	
	public String[] getKeyOrder() {
		return keyOrder.toArray(new String[0]);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void export(Var v) {
		if(this.exports == null)
			this.exports = new HashMap<String, Value>();
		this.exports.put(v.getName(), v.get());
	}
	
	public void deleteVar(Var v) {
		this.variables.remove(v.getName());
	}
	
	public Map<String, Value> getExports(){
		return this.exports;
	}
}
