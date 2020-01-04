package com.mrh.qspl.val.type;

import java.util.ArrayList;

import com.mrh.qspl.syntax.parser.Block;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.var.Var;
import com.mrh.qspl.vm.Scope;
import com.mrh.qspl.vm.VM;

public class TUserFunc extends TFunc{
	private Block ref;
	private TObject parameterObject = null;
	
	public TUserFunc(Block st, String[] pars) {
		ref = st;
		paramaterList = pars;
	}
	
	public TUserFunc(Block st, TObject pars, String[] parOrder) {
		ref = st;
		parameterObject = pars;
		paramaterList = parOrder;
	}

	@Override
	public String toString() {
		if(parameterObject != null) {
			String r = "func(";
			for(int i = 0; i < paramaterList.length; i++) {
				r += paramaterList[i];
				if(parameterObject.get(paramaterList[i]) != null && parameterObject.get(paramaterList[i]).get() != TUndefined.getInstance())
					r += "=" + parameterObject.get(paramaterList[i]);
				if(i+1 < paramaterList.length)
					r += ",";
			}
			return r+")";
		}
		String r = "";
		for(int i = 0; i < paramaterList.length; i++) {
			r += paramaterList[i];
			if(i+1 < paramaterList.length)
				r += ",";
		}
		return "user:func("+r+")";
	}
	
	@Override
	public Value execute(ArrayList<Value> args, VM vm, Value pThis) {
		vm.createNewScope(this.toString());
		Scope scope = vm.getCurrentScope();
		scope.setVariable("this", new Var("this", pThis, true));
		if(parameterObject != null) {
			int i = 0;
			for(String key : parameterObject.getKeys()) {
				scope.setVariable(key, new Var(key, (i < args.size() && args.get(i) != TUndefined.getInstance())?args.get(i):parameterObject.get(key).get(), false));
				i++;
			}
		}
		else {
			for(int i = 0; i < paramaterList.length; i++) {
				scope.setVariable(paramaterList[i], new Var(paramaterList[i], (i < args.size())?args.get(i):TUndefined.getInstance(), false));
			}
		}
		Value r = vm.evalBlock(ref);
		vm.popScope();
		//System.out.println("UFUNC_RET: " + r);
		return r;
	}
	
	public Block getRefBlock() {
		return ref;
	}
}
