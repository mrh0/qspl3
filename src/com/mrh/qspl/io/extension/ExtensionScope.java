package com.mrh.qspl.io.extension;

import java.util.HashMap;
import java.util.Map;

import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.type.TObject;

public class ExtensionScope {
	private Map<String, Value> exports;
	
	protected ExtensionScope() {
		exports = new HashMap<String, Value>();
	}
	
	public void export(String name, Value value) {
		exports.put(name, value);
	}
	
	protected TObject getExports() {
		return new TObject(exports);
	}
}
