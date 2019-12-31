package com.mrh.qspl.vm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import com.mrh.qspl.debug.Debug;
import com.mrh.qspl.io.console.Console;
import com.mrh.qspl.io.extension.Include;
import com.mrh.qspl.io.file.FileIO;
import com.mrh.qspl.syntax.parser.Block;
import com.mrh.qspl.syntax.parser.Statement;
import com.mrh.qspl.syntax.parser.StatementEndType;
import com.mrh.qspl.syntax.tokenizer.Token;
import com.mrh.qspl.syntax.tokenizer.Tokenizer;
import com.mrh.qspl.syntax.tokenizer.Tokens;
import com.mrh.qspl.syntax.tokenizer.Tokens.TokenType;
import com.mrh.qspl.val.Value;
import com.mrh.qspl.val.type.TArray;
import com.mrh.qspl.val.type.TFunc;
import com.mrh.qspl.val.type.TNumber;
import com.mrh.qspl.val.type.TObject;
import com.mrh.qspl.val.type.TString;
import com.mrh.qspl.val.type.TUndefined;
import com.mrh.qspl.val.type.TUserFunc;
import com.mrh.qspl.val.type.Types;
import com.mrh.qspl.var.Var;
import com.mrh.qspl.vm.stack.BracketItem;
import com.mrh.qspl.vm.stack.BracketStack;
import com.mrh.qspl.vm.stack.ValStack;

/**
 * Expression Evaluator.
 * @author MRH0
 */

public class ExpressionEvaluator {
	
	private VM vm;
	private Tokenizer tokens;
	private boolean breakCalled;
	private boolean continueCalled;
	private boolean inCalled;
	private boolean ofCalled;
	protected Stack<Boolean> exitCalledStack;
	
	public ExpressionEvaluator(VM vm, Tokenizer tokens) {
		this.vm = vm;
		this.tokens = tokens;
		breakCalled  = false;
		exitCalledStack = new Stack<Boolean>();
		inCalled = false;
		ofCalled = false;
		continueCalled = false;
	}
	
	public void eval() {
		this.vm.evalBlock(this.tokens.getRootBlock());
	}
	
	private StatementResult evalStatement(Statement statement, Value previousResult) {
		Stack<String> ops;
		ValStack vals;
		Stack<Var> vars;
		Stack<TokenType> types;
		BracketStack brackets;
		ops = new Stack<String>();
		vals = new ValStack(this);
		vars = new Stack<Var>();
		types = new Stack<TokenType>();
		brackets = new BracketStack();
		Token prev = null;
		Token beforeprev = null;
		
		boolean thisCalledExit = false;
		
		boolean outCalled = false;
		boolean errorCalled = false;
		boolean includeCalled = false;
		boolean includeFromCalled = false;
		boolean exportCalled = false;
		boolean deleteCalled = false;

		boolean onceFunc = false;
		
		ArrayList<String> funcArgNames = new ArrayList<String>();
		if(Debug.enabled())
			System.out.println("ln:"+statement.getLine());
		
		Console.g.setCurrentLine(statement.getLine());
		try {
		for(Token token : statement.getTokens()) {
			String s = token.getToken();
			TokenType t = token.getType();
			if(s.equals("[")) {
				Value vt = (Tokens.isNewSymbol(prev.getToken())?null:vals.pop(vars));
				brackets.push(new BracketItem('[', vt, beforeprev!= null && beforeprev.getToken().equals("#")));
			}
			else if(s.equals("{")) {
				Value vt = (Tokens.isNewSymbol(prev.getToken())?null:vals.pop(vars));
				brackets.push(new BracketItem('{', vt, beforeprev!= null && prev.getToken().equals("#")) );
				vm.createNewScope("object", true);
			}
			else if(s.equals(",")) {
				if(!brackets.isEmpty())
					brackets.peek().add(vals.pop(vars));
			}
			else if(s.equals("}")) {
				Scope oScope = vm.popScope();
				if(!brackets.isEmpty()) {
					BracketItem bi = brackets.pop();
					if(!prev.getToken().equals("{"))
						bi.add(vals.pop(vars));
					vals.push(new TObject(oScope.getAllValues(), oScope.getKeyOrder()));
				}
			}
			else if(s.equals("]")) {
				if(!brackets.isEmpty()) {
					BracketItem bi = brackets.pop();
					if(!prev.getToken().equals("[")) 
						bi.add(vals.pop(vars));
					if(bi.getPrev() == null) {
						vals.push(new TArray(bi.getParameters()));
					}
					else {
						Value prevp = bi.getPrev();
						if(prevp.getType() == Types.FUNC) {
							Value _this = bi.isSubOp()?vals.pop(vars):TUndefined.getInstance();
							Value vt = ((TFunc) prevp).execute(bi.getParameters(), vm, _this);
							vals.push(vt);
							if(vt == null && Debug.enabled())
								System.out.println("RESULT NULL");
							if(Debug.enabled())
								System.out.println("EXEC:" + prevp.toString() + ":" + _this + " par:" + bi.getParameters() + " ret:" + vals.peek()+" sub: " + bi.isSubOp());
						}
						else {
							//vals.push(prevp); //Used as this value if function needs it. Will increase stack (KEEP IN MIND)
							Value vt = prevp.accessor(bi.getParameters().toArray(new Value[0]));
							vals.push(vt);
						}
					}
				}
			}
			else if(t == TokenType.operator) {
				ops.push(s);
				if(!ops.isEmpty()) {
					String op = ops.pop();
					Value v = vals.pop(vars);
					Var k;
					switch(op) {
						case "+":
							v = vals.pop(vars).add(v);
							break;
						case "-":
							v = vals.pop(vars).sub(v);
							break;
						case "*": 
							v = vals.pop(vars).multi(v);
							break;
						case "/":
							v = vals.pop(vars).div(v);
							break;
						case "%":
							v = vals.pop(vars).mod(v);
							break;
						case "==":
							v = new TNumber(vals.pop(vars).equals(v)?1:0);
							break;
						case"!=":
							v = new TNumber(vals.pop(vars).equals(v)?0:1);
							break;
						case"<":
							v = new TNumber(vals.pop(vars).compare(v)==-1?1:0);
							break;
						case ">":
							v = new TNumber(vals.pop(vars).compare(v)==1?1:0);
							break;
						case "<=": 
							v = new TNumber(vals.pop(vars).compare(v)<=0?1:0);
							break;
						case ">=":
							v = new TNumber(vals.pop(vars).compare(v)>=0?1:0);
							break;
						case "!":
							v = new TNumber(v.bool()?0:1);
							break;
						case "&&":
							v = new TNumber(v.bool() && vals.pop(vars).bool()?1:0);
							break;
						case "||": 
							v = new TNumber(v.bool() || vals.pop(vars).bool()?1:0);
							break;
						case "&":
							v = new TNumber(vals.pop(vars).intValue() & v.intValue());
							break;
						case "|":
							v = new TNumber(vals.pop(vars).intValue() | v.intValue());
							break;
						case "^":
							v = new TNumber(vals.pop(vars).intValue() ^ v.intValue());
							break;
						case "?": //Contains
							v = new TNumber(vals.pop(vars).contains(v)?1:0);
							break;
						case "is": //is type
							v = new TNumber((v.getType() == vals.pop(vars).getType())?1:0);
							break;
						case "as": //as type
							v = vals.pop(vars).toType(v.getType());
							break;
						case "--":
							v = TNumber.from(v).decrement(1);
							break;
						case "++":
							v = TNumber.from(v).increment(1);
							break;
						case "=":
							k = vars.peek();
							vm.setValue(k.getName(), v);
							vals.pop(vars);
							v = k.get();
							break;
						case "+=":
							k = vars.peek();
							vm.setValue(k.getName(), vm.getValue(k.getName()).add(v));
							vals.pop(vars);
							v = k.get();
							break;
						case "-=":
							k = vars.peek();
							vm.setValue(k.getName(), vm.getValue(k.getName()).sub(v));
							vals.pop(vars);
							v = k.get();
							break;
						case "*=":
							k = vars.peek();
							vm.setValue(k.getName(), vm.getValue(k.getName()).multi(v));
							vals.pop(vars);
							v = k.get();
							break;
						case "/=":
							k = vars.peek();
							vm.setValue(k.getName(), vm.getValue(k.getName()).div(v));
							vals.pop(vars);
							v = k.get();
							break;
						case "%=":
							k = vars.peek();
							vm.setValue(k.getName(), vm.getValue(k.getName()).mod(v));
							vals.pop(vars);
							v = k.get();
							break;
						default:
							Console.g.err("Unknown operator: '" + op + "'");
							break;
					}
					if(v != null)
						vals.push(v);
				}
			}
			else if(t.equals(TokenType.literal))
				vals.push(new TNumber(Double.parseDouble(s)));
			else if(t.equals(TokenType.string))
				vals.push(new TString(s));
			else if(t.equals(TokenType.keyword)) {
				switch(s) {
					case "out":
						outCalled = true;
						break;
					case "error":
						errorCalled = true;
						break;
					case "import":
						if(includeFromCalled) {  
							TString from = TString.from(vals.pop(vars));
							TObject toInclude = TObject.from(vals.pop(vars));
							//System.out.println("INCLUDE: " + toInclude + " FROM: " + from);
							TObject included = Include.include(from.get());
							for(String key : toInclude.getMap().keySet()) {
								if(key.equals("ALL")) {
									for(String akey : included.getKeys()) {
										vm.setValue(akey, included.get(akey));
									}
									break;
								}
								Value v = included.get(key);
								if(v == null || v == TUndefined.getInstance())
									Console.g.err("Undefined import '" + key + "' from " + from);
								vm.setValue(key, v);
							}
						}
						else {
							TObject obj = TObject.from(vals.pop(vars));
							System.out.println("INCLUDE: " + obj + " FROM: INTERNAL");
						}
						includeFromCalled = false;
						Console.g.setScope(vm.getCurrentScope()); // return to origin scope;
						break;
					case "export":
						exportCalled = true;
						break;
					case "from":
						includeFromCalled = true;
						break;
					case "delete":
						deleteCalled = true;
						break;
					case "func":
						TObject o = TObject.from(vals.pop(vars));
						vals.push(new TUserFunc(statement.getNext(), o, o.getSpecialOrder()));
						onceFunc = true;
						break;
					case "exit":
						exitCalledStack.push(true);
						thisCalledExit = true;
						break;
					case "prev":
						vals.push(previousResult);
						break;
					case "else":
						vals.push(new TNumber(previousResult.bool()?0:1));
						break;
					case "break":
						breakCalled = true;
						return new StatementResult(false, vals, vars, null);
					case "continue":
						continueCalled = true;
						return new StatementResult(false, vals, vars, null);
					case "in":
						inCalled = true;
						break;
					case "of":
						ofCalled = true;
						break;
				}
			}
			else if(t.equals(TokenType.identifier)) {
				Var k = vm.getVar(s);
				if(Debug.enabled())
					System.out.println("Pushed var " + s + ":" + k);
				vars.push(k);
				vals.push(k.get(), k);
			}
			else
				Console.g.err("Unexpected token: '" + s + "'");
			types.push(t);
			beforeprev = prev;
			prev = token;
		}
		if(outCalled) {
			if(!vals.isEmpty())
				Console.g.log(vals.peek());
		}
		if(errorCalled) {
			if(!vals.isEmpty())
				Console.g.err(vals.peek());
		}
		if(exportCalled) {
			if(!vars.isEmpty())
				vm.getCurrentScope().export(vars.peek());
			else
				Console.g.err("Attempting export on non variable.");
		}
		if(deleteCalled) {
			if(!vars.isEmpty())
				vm.deleteVar(vars.peek());
			else
				Console.g.err("Attempting delete on non variable.");
		}
		}
		catch(Exception e) {
			Console.g.err(e.getMessage());
			e.printStackTrace(); //Debug Info
		}

		boolean pass = statement.getEndType() != StatementEndType.END && !vals.isEmpty() && vals.peek().bool() && !onceFunc;//do sub statement?
		return new StatementResult(pass, vals, vars, thisCalledExit?vals.peek():null);
	}

	protected Value walkThrough(Block b) {
		Value retv = null;
		Iterator iter = null;
		StatementResult sr = new StatementResult(false, null, null, TUndefined.getInstance());
		Value previousResult = TUndefined.getInstance();
		ArrayList<Statement> a = b.getAll();
		for(int i = 0; i < a.size(); i++) {
			Statement s = a.get(i);
			sr = evalStatement(s, previousResult);
			if(sr.ret != null)
				retv = sr.ret;
			previousResult = sr.vals.peek();
			boolean didIter = false;
			if(iter == null) {
				if(inCalled) { //Values
					inCalled = false;
					Value p = sr.vals.pop(sr.vars);
					if(p.getType() == Types.ARRAY)
						iter = TArray.from(p).getAll().iterator();
					if(p.getType() == Types.OBJECT)
						iter = TObject.from(p).getMap().values().iterator();
				}
				if(ofCalled) { //Keys
					ofCalled = false;
					Value p = sr.vals.pop(sr.vars);
					if(p.getType() == Types.ARRAY)
						iter = TArray.from(p).keyIterator(); //fix should be index
					if(p.getType() == Types.OBJECT)
						iter = TObject.from(p).keyIterator();
				}
				if(sr.pass && s.hasNext() && s.getEndType() == StatementEndType.IF) { // if iter != null else if(...)
					Value y = walkThrough(s.getNext());
					if(y != null)
						retv = y;
				}
			}
			else { //if iter != null
				while(iter.hasNext() && !breakCalled) {
					vm.setValue(s.getTokens()[0].getToken(), (Value)iter.next());
					Value y = walkThrough(s.getNext());
					if(y != null)
						retv = y;
				}
				breakCalled = false;
				iter = null;
				didIter = true;
			}
			
			if(Debug.enabled() && exitCalledStack.peek() != null)
				System.out.println("EXIT CALLED: " + sr.vals.peek());
			if(s.getEndType() == StatementEndType.WHILE && sr.pass && !(exitCalledStack.peek() != null) && !didIter) {
				if(!breakCalled)
					i--;
				breakCalled = false;
			}
			if(continueCalled || breakCalled || exitCalledStack.peek() != null)
				continueCalled = false;
		}
		return retv;
	}
}
