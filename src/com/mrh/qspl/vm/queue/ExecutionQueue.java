package com.mrh.qspl.vm.queue;

import java.util.ArrayList;
import com.mrh.qspl.vm.VM;

public class ExecutionQueue {
	private boolean run = true;
	private static int next = 0;
	private ArrayList<IQueueEntry> queue;
	public ExecutionQueue() {
		queue = new ArrayList<IQueueEntry>();
	}
	
	public int enqueue(IQueueEntry qi) {
		qi.setId(next++);
		queue.add(qi);
		return qi.getId();
	}
	
	public boolean dequeue(int id) {
		for(IQueueEntry q : queue) {
			if(q.getId() == id) {
				queue.remove(q);
				return true;
			}
		}
		return false;
	}
	
	public void queueLoop(VM vm) {
		while(!queue.isEmpty() && run) {
			for(IQueueEntry q : queue) {
				if(q.isReady()) {
					if(q.cancelAfterReady())
						queue.remove(q);
					q.execute(vm);
					break;
				}
			}
		}
	}
	
	public void cancelAll() {
		queue.clear();
		run = false;
	}
}
