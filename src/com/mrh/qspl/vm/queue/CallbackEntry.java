package com.mrh.qspl.vm.queue;

import java.util.ArrayList;

import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.type.TFunc;
import com.mrh.qspl.vm.VM;

public abstract class CallbackEntry implements IQueueEntry{
	protected int id = -1;
	public CallbackEntry() {
	}
	public int getId() {
		return this.id;
	}
	public abstract Value getThis();
	public abstract ArrayList<Value> getArgs();
	public abstract TFunc getCallback();
	
	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public boolean cancelAfterReady() {
		return true;
	}
}
