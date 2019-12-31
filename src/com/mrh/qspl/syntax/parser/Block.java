package com.mrh.qspl.syntax.parser;

import java.util.ArrayList;

public class Block {
	private ArrayList<Statement> statements;
	
	public Block() {
		statements = new ArrayList<>();
	}
	
	public void add(Statement s) {
		statements.add(s);
	}
	
	public ArrayList<Statement> getAll(){
		return statements;
	}
}
