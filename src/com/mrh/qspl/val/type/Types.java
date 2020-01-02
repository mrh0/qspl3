package com.mrh.qspl.val.type;

import com.mrh.qspl.val.Value;

public class Types {
	public static int UNDEFINED	 = 0;
	public static int NUMBER	 = 1;
	public static int STRING	 = 2;
	public static int OBJECT	 = 3;
	public static int ARRAY		 = 4;
	public static int FUNC		 = 5;
	public static int CLASS		 = 6;
	public static int OBFUSC	 = 7;
	
	private static String[] names = {"Undefined", "Number", "String", "Object", "Array", "Func", "Class", "Obfuscated"};
	
	public static String getName(Value v) {
		return getName(v.getType());
	}
	
	public static String getName(int i) {
		return i >= 0 && i < names.length?names[i]:"Unknown";
	}
}
