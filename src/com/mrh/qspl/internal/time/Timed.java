package com.mrh.qspl.internal.time;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.type.TFunc;
import com.mrh.qspl.val.type.TUndefined;
import com.mrh.qspl.vm.VM;

public class Timed {
	private class TimedEvent{
		public TFunc f;
		public long when;
		public TimedEvent(long when, TFunc f) {
			this.f = f;
			this.when = when;
		}
	}
	
	public void insert(long when, TFunc f) {
		for(int i = 0; i < queue.size(); i++) {
			if(when < queue.get(i).when)
				queue.add(new TimedEvent(when, f));
		}
	}
	
	public Thread timeThread;
	private boolean run = true;
	private ArrayList<TimedEvent> queue;
	private VM vm;
	public void init(VM vm) {
		this.vm = vm;
		queue = new ArrayList<TimedEvent>();
		timeThread = new Thread(() -> {
			long last = System.nanoTime();
			while(run) {
				if(last+1000000 < System.nanoTime()) {
					last = System.nanoTime();
					if(queue.get(queue.size()-1).when < last) {
						queue.get(queue.size()-1).f.execute(new ArrayList<Value>(), this.vm, TUndefined.getInstance());
						queue.remove(queue.size()-1);
					}
				}
			}
		});
	}
}
