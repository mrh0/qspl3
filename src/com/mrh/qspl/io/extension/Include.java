package com.mrh.qspl.io.extension;

import java.net.MalformedURLException;

import com.mrh.qspl.internal.common.Common;
import com.mrh.qspl.io.console.Console;
import com.mrh.qspl.io.file.FileIO;
import com.mrh.qspl.syntax.tokenizer.Tokenizer;
import com.mrh.qspl.val.type.TObject;
import com.mrh.qspl.val.type.TUndefined;
import com.mrh.qspl.var.Var;
import com.mrh.qspl.vm.VM;

public class Include {
	public static TObject include(String path) {
		if(path.endsWith(".qs") || path.endsWith(".qspl")) {
			return fromFile(path);
		}
		if(path.endsWith(".jar")) {
			return fromJar(path);
		}
		Console.g.err("Unknown import type: '" + path + "'.");
		return new TObject();
	}
	
	public static TObject fromFile(String path) {
		String code = FileIO.readFromFile(path);
		if(code.length() == 0) 
			return new TObject();
		Tokenizer t = new Tokenizer();
		t.toTokens(code);
		VM vm = new VM(t, "import@"+path);
		vm.getCurrentScope().setVariable("this", new Var("this", TUndefined.getInstance()));
		Common.defineCommons(vm.getCurrentScope());
		vm.eval();
		TObject r = new TObject(vm.getGlobalScopeExports());
		return r;
	}
	
	public static TObject fromJar(String path) {
		String[] args = path.split("@");
		if(args.length != 2) {
			Console.g.err("Invalid jar import path: '" + path + "'.");
			return new TObject();
		}
		
		try {
			return fromExtension(JarLoader.getExtension(args[1], args[0]));
		} catch (ClassNotFoundException | MalformedURLException | InstantiationException | IllegalAccessException e) {
			Console.g.err("Error occured with loading extension jar: '" + path + "'.");
			e.printStackTrace();
		}
		return new TObject();
	}
	
	public static TObject fromExtension(Extension ext) {
		ExtensionScope es = new ExtensionScope();
		ext.extend(es);
		return es.getExports();
	}
}
