package com.mrh.qspl.val.func;

import java.util.ArrayList;

import com.mrh.qspl.val.Value;
import com.mrh.qspl.vm.VM;

public interface IFunc {
	public Value execute(ArrayList<Value> args, VM vm, Value pThis);
	public static void defaultArguments(ArrayList<Value> args, Value...defaults) {
		for(int i = 0; i < defaults.length; i++) {
			if(i < args.size())
				args.set(i, defaults[i]);
			else
				args.add(defaults[i]);
		}
	}
}
