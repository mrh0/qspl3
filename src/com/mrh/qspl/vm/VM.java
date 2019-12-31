package com.mrh.qspl.vm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import com.mrh.qspl.internal.common.Common;
import com.mrh.qspl.io.console.Console;
import com.mrh.qspl.syntax.parser.Block;
import com.mrh.qspl.syntax.tokenizer.Tokenizer;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.type.TFunc;
import com.mrh.qspl.val.type.TObject;
import com.mrh.qspl.val.type.TUndefined;
import com.mrh.qspl.val.type.TUserFunc;
import com.mrh.qspl.var.Var;
import com.mrh.qspl.vm.queue.ExecutionQueue;
import com.mrh.qspl.vm.queue.IQueueEntry;

public class VM {
	protected Stack<Scope> scopeStack;
	protected Scope rootScope;
	
	private ExpressionEvaluator ev;
	private ExecutionQueue eq;
	
	public VM(Tokenizer t) {
		Common.initPrototypes();
		ev = new ExpressionEvaluator(this, t);
		eq = new ExecutionQueue();
		scopeStack = new Stack<Scope>();
		rootScope = createNewScope("origin");
		//rootScope.setVariable("this", new Var("this", TUndefined.getInstance()));
		//Common.defineCommons(rootScope);
	}
	
	public VM(Tokenizer t, String root) {
		Common.initPrototypes();
		ev = new ExpressionEvaluator(this, t);
		eq = new ExecutionQueue();
		scopeStack = new Stack<Scope>();
		rootScope = createNewScope(root);
	}
	
	public int queueExecution(IQueueEntry qi) {
		return eq.enqueue(qi);
	}
	
	public boolean cancelExecution(int id) {
		return eq.dequeue(id);
	}
	
	public void cancelAllExecution() {
		eq.cancelAll();
	}
	
	public Value evalBlock(Block b) {
		ev.exitCalledStack.push(null);
		Value r = ev.walkThrough(b);
		ev.exitCalledStack.pop();
		//eq.queueLoop(this);
		return r;
	}
	
	public Scope getCurrentScope() {
		return scopeStack.peek();
	}
	
	public Scope createNewScope(String name) {
		scopeStack.push(new Scope(name));
		Console.g.setScope(scopeStack.peek());
		return getCurrentScope();
	}
	
	public Scope createNewScope(String name, boolean lock) {
		scopeStack.push(new Scope(name, lock));
		Console.g.setScope(scopeStack.peek());
		return getCurrentScope();
	}
	
	public Scope popScope() {
		Scope s = scopeStack.pop();
		Console.g.setScope(scopeStack.peek());
		return s;
	}
	
	public Value executeFunction(TFunc func, ArrayList<Value> args, Value _this) {
		this.createNewScope("func:"+func);
		Value rv = ((TFunc) func).execute(args, this, _this);
		this.popScope();
		return rv;
	}
	
	private Var getVar(String name, boolean checkLock) {
		Var v;
		ListIterator<Scope> it = scopeStack.listIterator(scopeStack.size());
		while(it.hasPrevious()) {
			Scope s = it.previous();
			v = s.getVariable(name);
			if(s.isLocked() && checkLock)
				break;
			if(s.isLocked())
				continue;
			
			if(v != null) 
				return v;
		}
		v = new Var(name, TUndefined.getInstance(), name.toUpperCase().equals(name));
		getCurrentScope().setVariable(name, v);
		return v;
	}
	
	protected Var getVar(String name) {
		return getVar(name, false);
	}
	
	public Value getValue(String name) {
		return getVar(name).get();
	}
	
	public void setValue(String name, Value v) {
		getVar(name, true).set(v);
	}
	
	public TObject getGlobalScopeAsObject() {
		return new TObject(getCurrentScope().getAllValues());
	}
	
	public Map<String, Value> getGlobalScopeExports() {
		return getCurrentScope().getExports();
	}
	
	protected void deleteVar(Var v) {
		getCurrentScope().deleteVar(v);
	}
	
	public void eval() {
		ev.eval();
		eq.queueLoop(this); // New!
	}
}
