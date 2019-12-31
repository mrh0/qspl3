package com.mrh.qspl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.mrh.qspl.debug.Debug;
import com.mrh.qspl.io.file.FileIO;
import com.mrh.qspl.syntax.tokenizer.Tokenizer;
import com.mrh.qspl.val.type.TFunc;
import com.mrh.qspl.vm.ExpressionEvaluator;
import com.mrh.qspl.vm.VM;

/**
 * Test area.
 * @author MRH0
 *
 */

public class Main {

	public static void main(String[] args) {
		System.out.println("Quick Statement Programming Language (QSPL) [ (C) MRH0 2019 ]");
		if(args.length == 0) {
			System.out.print("Run arguments: [path, (keys..)] but you gave: ");
			System.out.print("[");
			for(int i = 0; i < args.length; i++)
				System.out.print(args[i]+(i+1 < args.length?", ":""));
			System.out.println("]. Run 'help'.");
		}
		
		boolean outputCode = false;
		for(String k : args) {
			if(k.equals("-d") || k.equals("-debug"))
				Debug.enableDeepDebug = true;
			if(k.equals("-nr") || k.equals("-no-result"))
				Debug.noResult = true;
			if(k.equals("-oc") || k.equals("-output-code"))
				outputCode = true;
		}
		
		String code = "";
		if(args.length > 0) {
			if(args[0].equals("help")) {
				System.out.println("Run: [path, (keys..)]. Keys:(-debug, -no-result, -no-output, -output-code)");
				return;
			}
			code = FileIO.readFromFile(args[0]);
		}
		else
			code = FileIO.readFromFile("C:\\MRHLang\\debug.qs");
		if(!Debug.noResult)
			System.out.println("[PROGRAM OUTPUT]:");
		if(outputCode)
			System.out.println(code);
		long time = System.currentTimeMillis();
		new QSPL().insertCode(code).execute();
		System.out.println("[Runtime: " + (System.currentTimeMillis() - time) + "ms]");
	}
}