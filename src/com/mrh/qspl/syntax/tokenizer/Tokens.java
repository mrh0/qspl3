package com.mrh.qspl.syntax.tokenizer;

public class Tokens {
	public enum TokenType{
		keyword, 		//reserved
		identifier,		//variable
		operator,		//operators
		seperator,		//space, tab, new line, .,;()[]{}
		literal,		//numbers
		string,			//quotes
		none			//nothing
	}
	
	
	
	public static boolean isNewSymbol(String s) {
		return s.equals("new");// || s.equals("$");
	}
	
	@Deprecated
	private static String[] keywords = {"out", "error", "exit", "else", "break", "prev", "continue", "in", "of", "func", "new", "$", "#", "import", "export", "from", "delete"}; // as, is
	@Deprecated
	public static boolean isKeywordLegacy(String s) {
		for(String x : keywords)
			if(x.equals(s))
				return true;
		return false;
	}
	
	public static boolean isKeyword(String s) {
		switch(s) {
			case "out":
				return true;
			case "error":
				return true;
			case "exit":
				return true;
			case "else":
				return true;
			case "break":
				return true;
			case "prev":
				return true;
			case "continue":
				return true;
			case "in":
				return true;
			case "of":
				return true;
			case "func":
				return true;
			case "new":
				return true;
			case "$":
				return true;
			case "#":
				return true;
			case "import":
				return true;
			case "export":
				return true;
			case "from":
				return true;
			case "delete":
				return true;
		}
		return false;
	}
	
	public static boolean canBeLiteral(char c) {
		if(c == '-')
			return true;
		if(c == '.')
			return true;
		if(c >= '0' && c <= '9')
			return true;
		return false;
	}
	
	public static boolean canBeStartOfIdentifier(char c) {
		if(c >= 'A' && c <= 'Z')
			return true;
		if(c >= 'a' && c <= 'z')
			return true;
		return false;
	}
	
	public static boolean isIndent(char c) {
		return c == '\t';
	}
	
	public static boolean isEndOfStatement(char c) {
		return c == ';' || c == ':';
	}
	
	public static boolean canBeIdentifier(char c) {
		return canBeStartOfIdentifier(c) || canBeLiteral(c);
	}
	
	public static boolean isString(char c) {
		if(c == '\'')
			return true;
		if(c == '\"')
			return true;
		return false;
	}
	
	public static boolean isSeperator(char c) {
		if(c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == '\t' || c == '\n' || c == '\r' || c == ' ' || c == ';')
			return true;
		return false;
	}
	
	public static boolean isBracket(char c) {
		if(c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}')
			return true;
		return false;
	}
	
	public static boolean isOpenBracket(char c) {
		return (c == '(' || c == '['|| c == '{');
	}
	
	public static boolean isCloseBracket(char c) {
		return (c == ')' || c == ']'|| c == '}');
	}
	
	public static char getClosedBracket(char c) {
		if(c == '(')
			return ')';
		if(c == '[')
			return ']';
		if(c == '{')  
			return '}';
		return '0';
	}
	
	public static char getOpenBracket(char c) {
		if(c == ')')
			return '(';
		if(c == ']')
			return '[';
		if(c == '}')  
			return '{';
		return '0';
	}
	
	public static boolean isWhitespace(char c) {
		if(c == '\t' || c == '\n' || c == '\r' || c == ' ')
			return true;
		return false;
	}
	
	public static boolean isOperator(char c) {
		if(c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '!' || c == '=' || c == '<' || c == '>' || c == '&' || c == '|' || c == '?' || c == ':' || c == '~' || c == '^' || c == ',')
			return true;
		return false;
	}
	
	public static TokenType tokenSwapType(String t, TokenType prev) {
		if(t.equals("is"))
			return TokenType.operator;
		if(t.equals("as"))
			return TokenType.operator;
		return prev;
	}
	
	public static boolean isComment(String c) {
		return c.equals("//");
	}
	
	public static boolean isOpenComment(String s) {
		return s.equals("/*");
	}
	
	public static boolean isCloseComment(String s) {
		return s.equals("*/");
	}
	
	public static boolean isLineComment(String s) {
		return s.equals("//");
	}
}
