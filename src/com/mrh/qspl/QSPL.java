package com.mrh.qspl;

import java.nio.file.Path;
import java.util.ArrayList;
import com.mrh.qspl.debug.Debug;
import com.mrh.qspl.internal.common.Common;
import com.mrh.qspl.io.console.Console;
import com.mrh.qspl.syntax.tokenizer.Tokenizer;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.type.TFunc;
import com.mrh.qspl.val.type.TUndefined;
import com.mrh.qspl.var.Var;
import com.mrh.qspl.vm.VM;

/**
 * QSPL - API
 * @author MRH0
 */

public class QSPL {
	private Tokenizer tokens;
	private VM vm;
	private Console console;
	@Deprecated
	private String programPath = "";
	
	public QSPL() {
		console = new Console();
		tokens = new Tokenizer();
		vm = new VM(tokens);
		vm.getCurrentScope().setVariable("this", new Var("this", TUndefined.getInstance()));
		Common.defineCommons(vm.getCurrentScope());
	}
	
	public QSPL insertCode(String c) {
		tokens.toTokens(c);
		if(Debug.enabled())
			System.out.println(getTokensString());
		return this;
	}
	
	public String getTokensString() {
		return tokens.toString();
	}
	
	public Value executeFunction(TFunc func, ArrayList<Value> args, Value _this) {
		return vm.executeFunction(func, args, _this);
	}
	
	public QSPL clearCode() {
		tokens = new Tokenizer();
		return this;
	}
	
	public void setGlobalVariable(String name, Value value) {
		vm.setValue(name, value);
	}
	
	public Value getGlobalVariable(String name) {
		return vm.getValue(name);
	}
	
	public void setOrigin(Path path) {
		
	}
	
	@Deprecated
	public void setProgramPath(String path) {
		this.programPath = path;
	}
	
	public boolean execute() {
		vm.eval();
		return true;
	}
}
