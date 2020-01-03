package com.mrh.qspl.syntax.tokenizer;

import java.util.ArrayList;
import java.util.Stack;

import com.mrh.qspl.io.console.Console;
import com.mrh.qspl.syntax.parser.Block;
import com.mrh.qspl.syntax.parser.Statement;
import com.mrh.qspl.syntax.parser.StatementEndType;
import com.mrh.qspl.syntax.tokenizer.Tokens.TokenType;

public class Tokenizer {
	ArrayList<Token> ts;
	TokenType cur = TokenType.none;
	TokenType last = TokenType.none;
	TokenType lasti = TokenType.none;
	Token prevToken = null;
	
	Stack<Character> bracketStack;
	Stack<Character> stringStack;
	Stack<Block> blockStack;
	
	GMCL mc;
	
	Statement lastStatement;
	
	int lineIndent;
	int prevIndent;
	
	String w;
	
	int curLine = 1;
	
	boolean lastWasPeriod = false;
	boolean isFuncDef = false;
	boolean isInclude = false;
	boolean isIncludeFrom = false;
	boolean isExport = false;
	boolean isDelete = false;
	
	public Tokenizer() {
		w = "";
		
		bracketStack = new Stack<>();
		stringStack = new Stack<>();
		
		blockStack = new Stack<>();
		lastStatement = new Statement(0, new Token[0]);
		lastStatement.setBlock(new Block());
		blockStack.add(lastStatement.getNext());
		
		mc = new GMCL();
		mc.push();
		
		lineIndent = 0;
		prevIndent = 0;
	}
	
	public ArrayList<Token> toTokens(String s) {
		ts = new ArrayList<Token>();
		char c = ' ';
		boolean inComment = false;
		boolean inLineComment = false;
		
		for(int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			String cs = ((""+c)+(i+1<s.length()?s.charAt(i+1):' '));
			
			if(c == '\n') {
				curLine++;
			}
			Console.g.setCurrentLine(curLine);
			if(stringStack.isEmpty()) {
				if(Tokens.isOpenComment(cs))
					inComment = true;
				if(Tokens.isLineComment(cs))
					inLineComment = true;
				if(c =='\n' && inLineComment)
					inLineComment = false;
				if(Tokens.isCloseComment(cs)) {
					i++;
					inComment = false;
					continue;
				}
				if(inComment || inLineComment)
					continue;
			}
			
			if(c == '\n')
				lineIndent = 0;
			
			if(Tokens.isString(c)) {
				if(stringStack.isEmpty()) {
					end();
					next(TokenType.string);
				}
				if(!stringStack.isEmpty() && c == stringStack.peek()) {
					stringStack.pop();
					if(stringStack.isEmpty())
						end();
					continue;
				}
				else {
					stringStack.push(c);
					continue;
				}
			}
			
			if(!stringStack.isEmpty()) {
				next(c, TokenType.string);
				continue;
			}
			if(c == '.' && (cur == TokenType.identifier || w.length() == 0)) {
				end();
				next(c, TokenType.identifier);
				continue;
			}
			if(!Tokens.canBeLiteral(c) && w.equals(".") && Tokens.canBeStartOfIdentifier(c)) {
				end();
				next(c, TokenType.identifier);
				continue;
			}
			if(c == '#') {
				end();
				next(c, TokenType.keyword);
				end();
				continue;
			}
			if(Tokens.isEndOfStatement(c) && stringStack.isEmpty()) {
				StatementEndType set = StatementEndType.END;
				if(c == ':') {
					set = StatementEndType.IF;
					if(i+1 < s.length())
						if(s.charAt(i+1) == ':') {
							i++;
							set = StatementEndType.WHILE;
						}
				}
				if(isFuncDef && set == StatementEndType.IF)
					set = StatementEndType.FUNC;
				end();
				if(isFuncDef) {
					gotNewToken(new Token("}", TokenType.seperator));
					gotNewToken(new Token("func", TokenType.keyword));
					if(set != StatementEndType.FUNC)
						Console.g.err("Invalid end to function definition.");
				}
				else if(isInclude) {
					if(!isIncludeFrom)
						gotNewToken(new Token("}", TokenType.seperator));
					gotNewToken(new Token("import", TokenType.keyword));
					if(set != StatementEndType.END)
						Console.g.err("Invalid end to import statement.");
				}
				else if(isDelete) {
					gotNewToken(new Token("}", TokenType.seperator));
					gotNewToken(new Token("delete", TokenType.keyword));
					if(set != StatementEndType.END)
						Console.g.err("Invalid end to delete statement.");
				}
				else if(isExport) {
					gotNewToken(new Token("}", TokenType.seperator));
					gotNewToken(new Token("export", TokenType.keyword));
					if(set != StatementEndType.END)
						Console.g.err("Invalid end to export statement.");
				}
				isFuncDef = false;
				isInclude = false;
				isIncludeFrom = false;
				isDelete = false;
				isExport = false;
				endStatement(set);
				continue;
			}
			if(Tokens.isSeperator(c)) {
				if(Tokens.isWhitespace(c)) {
					if(Tokens.isIndent(c) && isPreStatement()) {
						lineIndent++;
						continue;
					}
					end();
					continue;
				}
				if(Tokens.isBracket(c)){
					if(Tokens.isOpenBracket(c)) 
						bracketStack.push(c);
					else{
						if(bracketStack.isEmpty() || Tokens.getOpenBracket(c) != bracketStack.pop())
							Console.g.err("Unbalanced Brackets"); //replace
					}
				}
				end();
				next(c, TokenType.seperator);
				end();
				continue;
			}
			if(Tokens.isOperator(c)) {
				if(cur != TokenType.operator)
					end();
				next(c, TokenType.operator);
				continue;
			}
			if(c == '$') {
				end();
				next(c, TokenType.identifier);
				end();
				continue;
			}
			if(Tokens.canBeLiteral(c)) {
				if(cur == TokenType.identifier && w.equals(".")) {
					next(c, TokenType.literal);
					continue;
				}
				if(cur == TokenType.identifier) {
					next(c, TokenType.identifier);
					continue;
				}
				if(cur == TokenType.literal) {
					next(c, TokenType.literal);
					continue;
				}
				else {
					end();
					next(c, TokenType.literal);
					continue;
				}
			}
			if(Tokens.canBeIdentifier(c)) {
				if(cur == TokenType.literal) {
					next(c, TokenType.literal);
					continue;
				}
				if(cur != TokenType.identifier) {
					if(Tokens.canBeStartOfIdentifier(c)) {
						end();
					}
				}
				
				next(c, TokenType.identifier);
			}
			
		}
		end();
		//System.out.println("\nTokenized");
		return ts;
	}
	
	private void next(char c,  TokenType type) {
		cur = type;
		w += c;
	}
	
	private void next(TokenType type) {
		cur = type;
	}
	
	private int opValue(String s) {
		if(s.equals("++"))
			return 11;
		if(s.equals("--"))
			return 11;
		if(s.equals("is"))
			return 11;
		if(s.equals("as"))
			return 11;
		if(s.equals("?"))
			return 11;
		if(s.equals("!"))
			return 11;
		if(s.equals("~"))
			return 11;
		
		if(s.equals("*"))
			return 10;
		if(s.equals("/"))
			return 10;
		if(s.equals("%"))
			return 10;
		
		if(s.equals("+"))
			return 9;
		if(s.equals("-"))
			return 9;
		
		if(s.equals("<"))
			return 8;
		if(s.equals("<="))
			return 8;
		if(s.equals(">"))
			return 8;
		if(s.equals(">="))
			return 8;
		if(s.equals("!="))
			return 7;
		if(s.equals("=="))
			return 7;
		
		
		if(s.equals("&"))
			return 6;
		if(s.equals("^"))
			return 5;
		if(s.equals("|"))
			return 4;
		
		if(s.equals("&&"))
			return 3;
		if(s.equals("||"))
			return 2;
		
		if(s.equals("("))
			return 1;
		
		if(s.equals("="))
			return 0;
		if(s.equals("+="))
			return 0;
		if(s.equals("-="))
			return 0;
		if(s.equals("*="))
			return 0;
		if(s.equals("/="))
			return 0;
		if(s.equals("%="))
			return 0;
		if(s.equals(",")) //Quick fix
			return 0;
		Console.g.err("Unidentified Operator: '" + s + "'");
		return 0;
	}
	
	//Fix for long standing bug for values not getting assigned in objects: new {a, b}; becomes: new {a=undefined, b=undefined};
	private boolean hasAsigned = true;
	private boolean preGotWasIdent = false;
	private boolean lastOpenWasCurly = false;
	
	private void gotNewToken(Token t) {
		TokenType tt = t.getType();
		String tss = t.getToken();
		
		if(tss.equals("[") || tss.equals("{")) {
			if(tss.equals("{"))
				lastOpenWasCurly = true;
			else
				lastOpenWasCurly = false;
			hasAsigned = false;
			mc.push();
			gmc().postfixList.add(t);
		}
		else if(tss.equals("]") || tss.equals("}")) {
			if(tss.equals("}") && !hasAsigned && preGotWasIdent) {
				gotNewToken(new Token("=", TokenType.operator));
				gotNewToken(new Token("undefined", TokenType.identifier));
			}
			hasAsigned = false;
			finishPart();
			gmc().postfixList.add(t);
			ArrayList<Token> tl = mc.pop();
			gmc().postfixList.addAll(tl);
		}
		else if(tss.equals(",")) {
			if(!hasAsigned && lastOpenWasCurly) {
				gotNewToken(new Token("=", TokenType.operator));
				gotNewToken(new Token("undefined", TokenType.identifier));
			}
			hasAsigned = false;
			finishPart();
			ArrayList<Token> tl = mc.pop();
			gmc().postfixList.addAll(tl);
			mc.push();
			gmc().postfixList.add(t);
		}
		else if(tt == TokenType.literal || tt == TokenType.identifier || tt == TokenType.string || tt == TokenType.keyword) {
			gmc().postfixList.add(t);
		}
		else if(tss.equals("(")) {
			gmc().opStack.push(t);
		}
		else if(tss.equals(")")) {
			Token top = gmc().opStack.pop();
			while(!top.getToken().equals("(")) {
				gmc().postfixList.add(top);
				top = gmc().opStack.pop();
			}
		}
		else if(tt == TokenType.operator) {
			if(tss.equals("="))
				hasAsigned = true;
			while(!gmc().opStack.isEmpty() && (opValue(gmc().opStack.peek().getToken()) >= opValue(tss))) {
				gmc().postfixList.add(gmc().opStack.pop());
			}
			gmc().opStack.push(t);
		}
		else {
			gmc().tokStack.add(t);
		}
		preGotWasIdent = tt == TokenType.identifier;
	}
	
	
	private Token end() {
		//check if w is keyword
		cur = Tokens.tokenSwapType(w, cur);
		
		if(cur == TokenType.identifier)
			if(Tokens.isKeyword(w)) {
				cur = TokenType.keyword;
				if(w.equals("$"))
					w = "new";
			}
		
		if(cur == TokenType.operator)
			opValue(w);
		if(w.equals("*") && isInclude)
			gotNewToken(new Token("ALL", TokenType.identifier));
		
		if(w.length() > 0 || cur == TokenType.string) {
			Token t = null;
			
			if(cur == TokenType.keyword) {
				if(w.equals("func")) {
					isFuncDef = true;
					gotNewToken(new Token("new", TokenType.keyword));
					gotNewToken(new Token("{", TokenType.seperator));
				}
				else if(w.equals("import")) {
					isInclude = true;
					gotNewToken(new Token("new", TokenType.keyword));
					gotNewToken(new Token("{", TokenType.seperator));
				}
				else if(w.equals("from")) {
					isIncludeFrom = true;
					gotNewToken(new Token("}", TokenType.keyword));
				}
				else if(w.equals("delete")) {
					isDelete = true;
					gotNewToken(new Token("new", TokenType.keyword));
					gotNewToken(new Token("{", TokenType.seperator));
				}
				else if(w.equals("export")) {
					isExport = true;
					gotNewToken(new Token("new", TokenType.keyword));
					gotNewToken(new Token("{", TokenType.seperator));
				}
			}
			
			if(lastWasPeriod) {
				lastWasPeriod = false;
				gotNewToken(new Token("[", TokenType.seperator));
				t = new Token(w, TokenType.string);
				gotNewToken(t);
				gotNewToken(new Token("]", TokenType.seperator));
				cur = TokenType.seperator;
			}
			else {
				if(w.equals("."))
					lastWasPeriod = true;
				else {
					if(!w.equals("func") && !w.equals("import") && !w.equals("delete") && !w.equals("export") && !(w.equals("*") && isInclude)) {
						t = new Token(w, cur);
						gotNewToken(t);
					}
				}
			}
			
			w = "";
			
			if(cur != TokenType.none)
				lasti = cur;
			
			last = cur;
			cur = TokenType.none;
			prevToken = t;
			return t;
		}
		w = "";
		
		last = cur;
		cur = TokenType.none;
		prevToken = null;
		return null;
	}
	
	public String toString() {
		return tsrec(blockStack.peek());
	}
	int depth = -1;
	private String tsrec(Block b) {
		depth++;
		String r = "";
		for(Statement s : b.getAll()) {
			for(int i = 0; i < depth; i++)
				r += "\t";
			r += s.toString();
			if(s.hasNext())
				r += tsrec(s.getNext());
		}
		depth--;
		return r;
	}
	
	public Block getRootBlock() {
		return blockStack.peek();
	}
	
	private boolean inString() {
		return !stringStack.isEmpty();
	}
	
	private class GMCL{
		Stack<MapContext> stack;
		public GMCL() {
			stack = new Stack<MapContext>();
		}
		
		public MapContext get() {
			if(stack.isEmpty()) {
				Console.g.err("Critical stack error. Likely caused by unbalanced {} or [].");
			}
			return stack.peek();
		}
		
		public ArrayList<Token> pop() {
			return stack.pop().postfixList;
		}
		
		public void push() {
			stack.add(new MapContext());
		}
	}
	
	private MapContext gmc() {
		return mc.get();
	}
	
	//Postfix last step
	private void finishPart() {
		while(!gmc().opStack.isEmpty())
			gmc().postfixList.add(gmc().opStack.pop());
		ts = gmc().postfixList;
	}
	
	private Statement endStatement(StatementEndType set) {
		finishPart();
		
		mc = new GMCL();
		mc.push();
		
		Statement s = new Statement(curLine, ts.toArray(new Token[0]), set);
		String ind = "";
		ts.clear();
		
		if(prevIndent > lineIndent) {
			for(int i = 0; i < prevIndent - lineIndent; i++) {
				blockStack.pop();
			}
		}
		
		if(prevIndent < lineIndent) {
			for(int i = 0; i < lineIndent - prevIndent; i++) {
				blockStack.push(new Block());
			}
			if(lastStatement!=null)
				lastStatement.setBlock(blockStack.peek());
		}
		
		if(blockStack.isEmpty()) 
			Console.g.err("Block stack empty!");
		blockStack.peek().add(s);
		for(int i = 0; i < lineIndent; i++)
			ind+="\t";
		
		prevIndent = lineIndent;
		
		lineIndent = 0;
		lastStatement = s;
		return s;
	}
	
	private boolean isPreStatement() {
		return ts.size() == 0;
	}
}
