package com.mrh.qspl.vm.queue;

import java.util.ArrayList;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.type.TFunc;
import com.mrh.qspl.val.type.TUndefined;
import com.mrh.qspl.vm.VM;

public class AwaitThreadEntry extends CallbackEntry{
	
	protected Thread thread;
	protected ThreadRunnable rthread;
	protected TFunc f;
	
	public AwaitThreadEntry(TFunc f, IThreadFunc tf, ArrayList<Value> input) {
		this.f = f;
		rthread = new ThreadRunnable(tf, input);
		thread = new Thread(rthread);
		thread.start();
	}
	
	@Override
	public boolean isReady() {
		return !thread.isAlive();
	}

	@Override
	public Value execute(VM vm) {
		return f.execute(getArgs(), vm, getThis());
	}

	@Override
	public Value getThis() {
		return TUndefined.getInstance();
	}

	@Override
	public ArrayList<Value> getArgs() {
		return rthread.getValue();
	}

	@Override
	public TFunc getCallback() {
		return f;
	}

	public class ThreadRunnable implements Runnable {
		private volatile ArrayList<Value> value = new ArrayList<Value>();
	     private IThreadFunc f;
	     private ArrayList<Value> input;
	     
	     public ThreadRunnable(IThreadFunc f, ArrayList<Value> input) {
	    	 this.f = f;
	    	 this.input = input;
	     }

	     @Override
	     public void run() {
	        value.add(f.execute(input));
	     }

	     public ArrayList<Value> getValue() {
	         return value;
	     }
	 }
}
