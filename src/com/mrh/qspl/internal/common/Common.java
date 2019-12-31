package com.mrh.qspl.internal.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mrh.qspl.io.file.FileIO;
import com.mrh.qspl.io.network.Http;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.func.IFunc;
import com.mrh.qspl.val.type.TArray;
import com.mrh.qspl.val.type.TFunc;
import com.mrh.qspl.val.type.TNumber;
import com.mrh.qspl.val.type.TObject;
import com.mrh.qspl.val.type.TString;
import com.mrh.qspl.val.type.TUndefined;
import com.mrh.qspl.val.type.Types;
import com.mrh.qspl.var.Var;
import com.mrh.qspl.vm.Scope;
import com.mrh.qspl.vm.VM;
import com.mrh.qspl.vm.queue.AwaitThreadEntry;
import com.mrh.qspl.vm.queue.IThreadFunc;
import com.mrh.qspl.vm.queue.IntervalEntry;
import com.mrh.qspl.vm.queue.TimeoutEntry;

public class Common {
	public static Scope defaultScope = null;
	
	public static void initPrototypes() {
		if(defaultScope != null)
			return;
		defaultScope = defineCommons(new Scope("default"));
		
		TObject.addPrototype(defaultScope.getVariable("set"));
	}
	
	public static TFunc getFunc(String s) {
		return (TFunc) defaultScope.getVariable(s).get();
	}
	
	public static Scope defineCommons(Scope s) {
		s.setVariable("true", new Var("true", new TNumber(1), true));
		s.setVariable("false", new Var("false", new TNumber(0), true));
		s.setVariable("UNDEFINED", new Var("UNDEFINED", TUndefined.getInstance(), true));
		s.setVariable("undefined", new Var("undefined", TUndefined.getInstance(), true));
		s.setVariable("UNDEF", new Var("UNDEF", TUndefined.getInstance(), true));
		s.setVariable("null", new Var("null", TUndefined.getInstance(), true));
		
		s.setVariable("NUMBER", new Var("NUMBER", new TNumber(), true));
		s.setVariable("FUNC", new Var("FUNC", new TFunc(), true));
		s.setVariable("FUNCTION", new Var("FUNCTION", new TFunc(), true));
		s.setVariable("STRING", new Var("STRING", new TString(""), true));
		s.setVariable("ARRAY", new Var("ARRAY", new TArray(), true));
		s.setVariable("OBJECT", new Var("OBJECT", new TObject(), true));
		
		s.setVariable("PI", new Var("PI", new TNumber(Math.PI), true));
		s.setVariable("INF", new Var("INF", new TNumber(Double.POSITIVE_INFINITY), true));
		s.setVariable("INFINITY", new Var("INFINITY", new TNumber(Double.POSITIVE_INFINITY), true));
		s.setVariable("NEGINF", new Var("NEGINF", new TNumber(Double.NEGATIVE_INFINITY), true));
		s.setVariable("NEGATIVE_INFINITY", new Var("NEGATIVE_INFINITY", new TNumber(Double.NEGATIVE_INFINITY), true));
		
		s.setVariable("PROGRAM_PATH", new Var("PROGRAM_PATH", new TString(FileIO.getPath()), true));
		s.setVariable("START_TIME_MILIS", new Var("START_TIME_MILIS", new TNumber(System.currentTimeMillis()), true));
		
		IFunc f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return new TNumber(0);
			Value min = args.get(0);
			for(Value v : args) {
				if(v.compare(min) < 0)
					min = v;
			}
			return min;
		};
		s.setVariable("min", new Var("min", new TFunc(f, "v", "min"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return new TNumber(0);
			Value max = args.get(0);
			for(Value v : args) {
				if(v.compare(max) > 0)
					max = v;
			}
			return max;
		};
		s.setVariable("max", new Var("max", new TFunc(f, "v", "max"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() < 3)
				return new TNumber(0);
			double v = TNumber.from(args.get(0)).get();
			double min = TNumber.from(args.get(1)).get();
			double max = TNumber.from(args.get(2)).get();
			v = v < min?min:v;
			v = v > max?max:v;
			return new TNumber(v);
		};
		s.setVariable("clamp", new Var("clamp", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return new TNumber(0);
			return new TNumber(args.get(0).bool()?1:0);
		};
		s.setVariable("if", new Var("if", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() < 3)
				return new TNumber(0);
			return args.get(0).bool()?args.get(1):args.get(2);
		};
		s.setVariable("condition", new Var("condition", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 1)
				return new TString(args.get(0).bool()?"true":"false");
			return TUndefined.getInstance();
		};
		s.setVariable("bool", new Var("bool", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY)
				return new TArray();
			if(_this.getType() == Types.OBJECT)
				return new TObject();
			return TUndefined.getInstance();
		};
		s.setVariable("clone", new Var("clone", new TFunc(f), true));
		
		
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			String r = "";
			for(int i = 0; i < args.size(); i++) {
				r+=args.get(i).get();
				if(i+1 < args.size())
					r+=", ";
			}
			System.out.print(r);
			return new TString(r);
		};
		s.setVariable("print", new Var("print", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			String r = "";
			for(int i = 0; i < args.size(); i++) {
				r+=args.get(i).get();
				if(i+1 < args.size())
					r+=", ";
			}
			System.out.println(r);
			return new TString(r);
		};
		s.setVariable("println", new Var("println", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			Scanner scan = new Scanner(System.in);
			Value r = TUndefined.getInstance();
			if(args.size() >= 1) {
				if(args.get(0).getType() == Types.NUMBER) {
					return new TNumber(scan.nextDouble());
				}
			} 
			return new TString(scan.nextLine());
		};
		s.setVariable("read", new Var("read", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			System.exit(0);
			return TUndefined.getInstance();
		};
		s.setVariable("stop", new Var("stop", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			try {
				Thread.sleep(args.get(0).intValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return TUndefined.getInstance();
		};
		s.setVariable("sleep", new Var("sleep", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			double from = 0;
			double to = 1;
			if(args.size() == 1)
				to = TNumber.from(args.get(0)).get();
			if(args.size() == 2) {
				from = TNumber.from(args.get(0)).get();
				to = TNumber.from(args.get(1)).get();
			}
			if(from > to)
				return new TNumber(0);
			if(from == to)
				return new TNumber(from);
			return new TNumber(Math.random()*(to-from)+from);
		};
		s.setVariable("random", new Var("random", new TFunc(f, "n", "to"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			return new TNumber((double)Math.round(TNumber.from(args.get(0)).get()));
		};
		s.setVariable("round", new Var("round", new TFunc(f, "n"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			return new TNumber((double)Math.floor(TNumber.from(args.get(0)).get()));
		};
		s.setVariable("floor", new Var("floor", new TFunc(f, "n"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			return new TNumber((double)Math.ceil(TNumber.from(args.get(0)).get()));
		};
		s.setVariable("ceil", new Var("ceil", new TFunc(f, "n"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			return new TNumber((double)Math.abs(TNumber.from(args.get(0)).get()));
		};
		s.setVariable("abs", new Var("abs", new TFunc(f, "n"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			return new TNumber(Math.sqrt(TNumber.from(args.get(0)).get()));
		};
		s.setVariable("sqrt", new Var("sqrt", new TFunc(f, "n"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() < 2)
				return TUndefined.getInstance();
			return new TNumber(Math.pow(TNumber.from(args.get(0)).get(),TNumber.from(args.get(1)).get()));
		};
		s.setVariable("pow", new Var("pow", new TFunc(f, "n", "x"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			return vm.getValue(TString.from(args.get(0)).get());
		};
		s.setVariable("valueOf", new Var("valueOf", new TFunc(f, "name"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			return new TArray(TFunc.from(args.get(0)).getParameters());
		};
		s.setVariable("parametersOf", new Var("parametersOf", new TFunc(f, "func"), true));
		
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return _this;
			if(args.get(0).getType() != Types.FUNC)
				return TUndefined.getInstance();
			if(_this.getType() == Types.ARRAY) {
				TArray a = (TArray) _this;
				ArrayList<Value> parms = new ArrayList<>();
				parms.add(TUndefined.getInstance());
				parms.add(TUndefined.getInstance());
				parms.add(a);
				for(int i = 0; i < a.getSize(); i++) {
					parms.set(0, a.getIndex(i));
					parms.set(1, new TNumber(i));
					a.setIndex(i, vm.executeFunction((TFunc)args.get(0), parms, _this));
				}
				return _this;
			}
			if(_this.getType() == Types.STRING) {
				TString a = (TString) _this;
				ArrayList<Value> parms = new ArrayList<>();
				parms.add(TUndefined.getInstance());
				parms.add(TUndefined.getInstance());
				parms.add(a);
				String ret = "";
				for(int i = 0; i < a.getSize(); i++) {
					parms.set(0, new TString(a.get().charAt(i)+""));
					parms.set(1, new TNumber(i));
					ret += (TString.from(vm.executeFunction((TFunc)args.get(0), parms, _this))).get();
				}
				return new TString(ret);
			}
			return _this;
		};
		s.setVariable("map", new Var("map", new TFunc(f, "func"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return _this;
			if(args.get(0).getType() != Types.FUNC)
				return _this;
			if(_this.getType() == Types.ARRAY) {
				TArray a = TArray.from(_this);
				ArrayList<Value> parms = new ArrayList<>();
				ArrayList<Value> ret = new ArrayList<>();
				parms.add(TUndefined.getInstance());
				parms.add(TUndefined.getInstance());
				parms.add(a);
				for(int i = 0; i < a.getSize(); i++) {
					parms.set(0, a.getIndex(i));
					parms.set(1, new TNumber(i));
					if(vm.executeFunction(TFunc.from(args.get(0)), parms, _this).bool())
						ret.add(a.getIndex(i));
				}
				return new TArray(ret);
			}
			if(_this.getType() == Types.STRING) {
				TString a = (TString) _this;
				ArrayList<Value> parms = new ArrayList<>();
				String ret = "";
				parms.add(TUndefined.getInstance());
				parms.add(TUndefined.getInstance());
				parms.add(a);
				ArrayList<Value> ns = new ArrayList<>();
				for(int i = 0; i < a.getSize(); i++) {
					parms.set(0, new TString(a.get().charAt(i)+""));
					parms.set(1, new TNumber(i));
					if(vm.executeFunction(TFunc.from(args.get(0)), parms, _this).bool())
						ret += a.get().charAt(i);
				}
				return new TString(ret);
			}
			return _this;
		};
		s.setVariable("filter", new Var("filter", new TFunc(f, "func"), true));
		
		
		
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY) {
				TArray a = (TArray) _this;
				String rs = "";
				for(Value vs : a.getAll())
					rs += vs.toString();
				return new TString(rs);
			}
			return _this;
		};
		s.setVariable("collapse", new Var("collapse", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY && args.size() == 2) {
				TArray a = (TArray) _this;
				if(args.get(0).getType() == Types.NUMBER)
					a.setIndex(args.get(0).intValue(), args.get(1));
				if(args.get(0).getType() == Types.ARRAY) {
					TArray ar = (TArray) args.get(0);
					for(int i = 0; i < ar.getSize(); i++) {
						a.setIndex(ar.getIndex(i).intValue(), args.get(1));
					}
				}
			}
			if(_this.getType() == Types.OBJECT && args.size() > 1) {
				TObject a = (TObject) _this;
				a.getMap().put(TString.from(args.get(0)).get(), args.get(1));
			}
			return _this;
		};
		s.setVariable("set", new Var("set", new TFunc(f, "index", "value"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY && args.size() > 0) {
				TArray a = (TArray) _this;
				a.getAll().addAll(args);
			}
			return _this;
		};
		s.setVariable("push", new Var("push", new TFunc(f, "value"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY && args.size() == 1) {
				TArray a = (TArray) _this;
				a.getAll().add(args.get(0));
			}
			if(_this.getType() == Types.ARRAY && args.size() > 1) {
				TArray a = (TArray) _this;
				a.getAll().add(args.get(0).intValue(), args.get(1));
			}
			if(_this.getType() == Types.OBJECT && args.size() > 1) {
				TObject a = (TObject) _this;
				a.getMap().put(TString.from(args.get(0)).get(), args.get(1));
			}
			return _this;
		};
		s.setVariable("add", new Var("add", new TFunc(f, "x", "value"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY) {
				TArray a = (TArray) _this;
				if(a.getSize() == 0)
					System.err.println("Err: pop: empty array");
				return a.getAll().remove(a.getSize()-1);
			}
			return _this;
		};
		s.setVariable("pop", new Var("pop", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY) {
				TArray a = (TArray) _this;
				if(a.getSize() == 0) {
					System.err.println("Err: peek: empty array: " + a);
				}
				return a.getAll().get(a.getSize()-1);
			}
			return _this;
		};
		s.setVariable("peek", new Var("peek", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY) {
				TArray a = (TArray) _this;
				if(a.getSize() == 0)
					System.err.println("Err: dequeue: empty array");
				return a.getAll().remove(0);
			}
			return TUndefined.getInstance();
		};
		s.setVariable("dequeue", new Var("dequeue", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY && args.size() == 1) {
				TArray a = (TArray) _this;
				return a.getAll().remove(args.get(0).intValue());
			}
			return TUndefined.getInstance();
		};
		s.setVariable("removeAt", new Var("removeAt", new TFunc(f, "index"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY && args.size() == 1) {
				TArray a = (TArray) _this;
				Value vt = a.find(args.get(0));
				if(!vt.isUndefined())
					a.getAll().remove(vt);
			}
			if(_this.getType() == Types.OBJECT && args.size() == 1) {
				TObject a = (TObject) _this;
				a.getMap().remove(TString.from(args.get(0)).get());
			}
			return _this;
		};
		s.setVariable("remove", new Var("remove", new TFunc(f, "value"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.OBJECT) 
				return new TArray(TObject.from(_this).getKeys());
			return TUndefined.getInstance();
		};
		s.setVariable("keys", new Var("keys", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.OBJECT) 
				return new TArray(TObject.from(_this).getValues());
			if(_this.getType() == Types.ARRAY) 
				return _this;
			return TUndefined.getInstance();
		};
		s.setVariable("values", new Var("values", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY) {
				Collections.sort(TArray.from(_this).getAll());
				return _this;
			}
			return TUndefined.getInstance();
		};
		s.setVariable("sort", new Var("sort", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() == Types.ARRAY)
				return new TString(TArray.from(_this).toJSON().toString());
			if(_this.getType() == Types.OBJECT)
				return new TString(TObject.from(_this).toJSON().toString());
			return TUndefined.getInstance();
		};
		s.setVariable("toJSON", new Var("toJSON", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			if(_this.getType() == Types.ARRAY)
				return TArray.from(_this).fromJSON(new JSONArray(TString.from(args.get(0)).get()));
			if(_this.getType() == Types.OBJECT)
				return TObject.from(_this).fromJSON(new JSONObject(TString.from(args.get(0)).get()));
			return TUndefined.getInstance();
		};
		s.setVariable("fromJSON", new Var("fromJSON", new TFunc(f, "json"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			long l = 0;
			ArrayList<Value> a = new ArrayList<Value>();
			if(args.size() >= 1)
				l  = (new Double(TNumber.from(args.get(0)).get()).longValue() * 1000000L);
			if(args.size() == 2)
				a = TArray.from(args.get(1)).getAll();
			if(_this.getType() == Types.FUNC)
				return new TNumber(vm.queueExecution(new TimeoutEntry(TFunc.from(_this), System.nanoTime() + l, a)));
			return TUndefined.getInstance();
		};
		s.setVariable("timeout", new Var("timeout", new TFunc(f, "time"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			long l = 0;
			ArrayList<Value> a = new ArrayList<Value>();
			if(args.size() >= 1)
				l  = (new Double(TNumber.from(args.get(0)).get()).longValue() * 1000000L);
			if(args.size() == 2)
				a = TArray.from(args.get(1)).getAll();
			if(_this.getType() == Types.FUNC)
				return new TNumber(vm.queueExecution(new IntervalEntry(TFunc.from(_this), l, a)));
			return TUndefined.getInstance();
		};
		s.setVariable("interval", new Var("interval", new TFunc(f, "time"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			return new TNumber(vm.cancelExecution(TNumber.from(args.get(0)).intValue())?1:0);
		};
		s.setVariable("cancelExecution", new Var("cancelExecution", new TFunc(f, "id"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			vm.cancelAllExecution();
			return TUndefined.getInstance();
		};
		s.setVariable("cancelAllExecution", new Var("cancelAllExecution", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			TFunc func = TFunc.from(args.get(0));
			if(args.size() == 0) {
				if(_this.getType() != Types.FUNC)
					return TUndefined.getInstance();
				return func.execute(new ArrayList<Value>(), vm, _this);
			}
			if(args.size() == 1) {
				if(args.get(0).getType() == Types.ARRAY)
					return func.execute(TArray.from(args.get(0)).getAll(), vm, _this);
				return func.execute(new ArrayList<Value>(), vm, args.get(0));
			}
			if(args.size() == 2)
				return func.execute(TArray.from(args.get(0)).getAll(), vm, args.get(1));
			return TUndefined.getInstance();
		};
		s.setVariable("call", new Var("call", new TFunc(f, "args", "this"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			TFunc func = TFunc.from(args.get(0));
			if(args.size() == 0) {
				if(_this.getType() != Types.FUNC)
					return TUndefined.getInstance();
				return func.execute(new ArrayList<Value>(), vm, _this);
			}
			if(args.size() == 1) {
				if(args.get(0).getType() == Types.ARRAY)
					return func.execute(TArray.from(args.get(0)).getAll(), vm, _this);
				return func.execute(new ArrayList<Value>(), vm, args.get(0));
			}
			if(args.size() == 2)
				return func.execute(TArray.from(args.get(0)).getAll(), vm, args.get(1));
			return TUndefined.getInstance();
		};
		s.setVariable("callAsync", new Var("callAsync", new TFunc(f), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			//URL, type, data
			String url = "";
			String type = "";
			if(args.size() == 0)
				return TUndefined.getInstance();
			if(args.size() == 1) {
				url = TString.from(args.get(0)).get();
				type = "application/json";
			}
			if(args.size() == 2) {
				url = TString.from(args.get(0)).get();
				type = TString.from(args.get(1)).get();
			}
			if(args.size() == 3) {
				url = TString.from(args.get(0)).get();
				type = TString.from(args.get(1)).get();
			}
			TObject out = Http.executeRequest(url, "", type, "GET");
			return out;
		};
		s.setVariable("httpGet", new Var("httpGet", new TFunc(f, "url", "type"), true));
		
		IThreadFunc httpGetAsyncFunc = (ArrayList<Value> args) -> {
			//URL, type, data
			String url = "";
			String type = "";
			if(args.size() == 0)
				return TUndefined.getInstance();
			if(args.size() == 1) {
				url = TString.from(args.get(0)).get();
				type = "application/json";
			}
			if(args.size() == 2) {
				url = TString.from(args.get(0)).get();
				type = TString.from(args.get(1)).get();
			}
			if(args.size() == 3) {
				url = TString.from(args.get(0)).get();
				type = TString.from(args.get(1)).get();
			}
			return Http.executeRequest(url, "", type, "GET");
		};
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() != Types.FUNC)
				return TUndefined.getInstance();
			return new TNumber(
					vm.queueExecution(
							new AwaitThreadEntry(TFunc.from(_this), httpGetAsyncFunc, args)));
		};
		s.setVariable("httpGetAsync", new Var("httpGetAsync", new TFunc(f, "url", "type"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			//URL, type, data
			String url = "";
			String type = "";
			String data = "";
			if(args.size() == 0)
				return TUndefined.getInstance();
			if(args.size() == 1) {
				url = TString.from(args.get(0)).get();
				type = "application/json";
				data = "";
			}
			if(args.size() == 2) {
				url = TString.from(args.get(0)).get();
				type = TString.from(args.get(2)).get();
				data = "";
			}
			if(args.size() == 3) {
				url = TString.from(args.get(0)).get();
				type = TString.from(args.get(2)).get();
				data = TString.from(args.get(1)).get();
			}
			return Http.executeRequest(url, data, type, "POST");
		};
		s.setVariable("httpPost", new Var("httpPost", new TFunc(f, "url", "type", "data"), true));
		
		IThreadFunc httpPostAsyncFunc = (ArrayList<Value> args) -> {
			//URL, type, data
			String url = "";
			String type = "";
			String data = "";
			if(args.size() == 0)
				return TUndefined.getInstance();
			if(args.size() == 1) {
				url = TString.from(args.get(0)).get();
				type = "application/json";
				data = "";
			}
			if(args.size() == 2) {
				url = TString.from(args.get(0)).get();
				type = TString.from(args.get(2)).get();
				data = "";
			}
			if(args.size() == 3) {
				url = TString.from(args.get(0)).get();
				type = TString.from(args.get(2)).get();
				data = TString.from(args.get(1)).get();
			}
			return Http.executeRequest(url, data, type, "POST");
		};
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() != Types.FUNC)
				return TUndefined.getInstance();
			return new TNumber(
					vm.queueExecution(
							new AwaitThreadEntry(TFunc.from(_this), httpPostAsyncFunc, args)));
		};
		s.setVariable("httpPostAsync", new Var("httpPostAsync", new TFunc(f, "url", "type", "data"), true));
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			return FileIO.readFile(TString.from(args.get(0)).get());
		};
		s.setVariable("readFile", new Var("readFile", new TFunc(f, "path"), true));
		
		IThreadFunc readFileAsyncFunc = (ArrayList<Value> args) -> {
			if(args.size() == 0)
				return TUndefined.getInstance();
			return FileIO.readFile(TString.from(args.get(0)).get());
		};
		
		f = (ArrayList<Value> args, VM vm, Value _this) -> {
			if(_this.getType() != Types.FUNC)
				return TUndefined.getInstance();
			return new TNumber(
					vm.queueExecution(
							new AwaitThreadEntry(TFunc.from(_this), readFileAsyncFunc, args)));
		};
		s.setVariable("readFileAsync", new Var("readFileAsync", new TFunc(f, "path"), true));
		
		/*
			To Add:
			union...
			isDisjoint
			isDistinct
		*/
		
		
		return s;
	}
}
