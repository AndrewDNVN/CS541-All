/* This file probably doesn't need to be changed */

import java_cup.runtime.Symbol;
import java.io.Reader;
import java.io.IOException;

class Scanner {
	private	static Yylex lex = null;

	public static void init(java.io.Reader yyin) {
		if (lex == null) {
			lex = new Yylex(yyin);
		} else {
			System.err.println("Scanner is already initialized.");
		}
	} // init

	public static Symbol next_token() throws IOException {
		if (lex == null) {
			System.err.println("Scanner is not yet initialized.");
			System.exit(-1);
		} else {
			return lex.yylex();
		}
		return null; // To appease javac
	} // next_token

} // class Scanner
