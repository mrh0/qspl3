package com.mrh.qspl.vm.queue;

import java.util.ArrayList;

import com.mrh.qspl.val.Value;

public interface IThreadFunc {
	public Value execute(ArrayList<Value> input);
}
