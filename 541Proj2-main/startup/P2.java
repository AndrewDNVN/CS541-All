import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;

class P2 {

  public static void
  main(String args[]) throws java.io.IOException {

	if (args.length != 1) {
		System.out.println(
			"Error: Input file must be named on command line." );
		System.exit(-1);
	}

	Reader yyin = null;

	try {
		yyin = new BufferedReader(new FileReader(args[0]));
	} catch (FileNotFoundException notFound){
		System.out.println ("Error: unable to open input file.");
		System.exit(-1);
	}

    // lex is a JLex-generated scanner that reads from yyin
	final Yylex lex = new Yylex(yyin);	

	System.out.println ("Begin test of CSX scanner.");

	/**********************************************
	
	You should enter a code here that thoroughly tests your scanner.

	Be sure to test extreme cases, like very long symbols or lines,
	invalid tokens, unrepresentable integers, and invalid strings.
	
	The following is only a starting point.
	
	**********************************************/


	Symbol token = lex.yylex();

	while ( token.sym != sym.EOF) {

		System.out.print( token.value.linenum + ":"
				+ token.value.colnum + "\t");

		switch (token.sym) {
			// Used the given format with slight changes as required.

			// Each line is very similar to keep the output simple and easy to read.


			case sym.IDENTIFIER:
				System.out.println("ID (" +
					((CSXIdentifierToken) token.value).identifierText + ")");
				break;

			// single position special

			case sym.SEMI:
				System.out.println("Symbol (" + ";" + ")");
				break;

			case sym.L_PAREN:
				System.out.println("Symbol (" + "(" + ")");
				break;

			case sym.R_PAREN:
				System.out.println("Symbol (" + ")" + ")");
				break;

		  case sym.L_BRACE:
				System.out.println("Symbol (" + "{" + ")");
				break;

			case sym.R_BRACE:
				System.out.println("Symbol (" + "}" + ")");
				break;

			case sym.PLUS:
				System.out.println("Symbol (" + "+" + ")");
				break;

			case sym.MINUS:
				System.out.println("Symbol (" + "-" + ")");
				break;

			case sym.EQUALS:
				System.out.println("Symbol (" + "=" + ")");
				break;

			case sym.STAR:
				System.out.println("Symbol (" + "*" + ")");
				break;

			case sym.COMMA:
				System.out.println("Symbol (" + "," + ")");
				break;

			case sym.NOT:
				System.out.println("Symbol (" + "!" + ")");
				break;
		
			case sym.L_BRACK:
				System.out.println("Symbol (" + "[" + ")");
				break;	

			case sym.R_BRACK:
				System.out.println("Symbol (" + "]" + ")");
				break;

			case sym.COLON:
				System.out.println("Symbol (" + ":" + ")");
				break;

			case sym.L_THAN:
				System.out.println("Symbol (" + "<" + ")");
				break;

			case sym.G_THAN:
				System.out.println("Symbol (" + ">" + ")");
				break;

			case sym.SLASH:
				System.out.println("Symbol (" + "/" + ")");
				break;

			// Two-position special

			case sym.D_EQUAL:
				System.out.println("Symbol (" + "==" + ")");
				break;

			case sym.NOT_EQUAL:
				System.out.println("Symbol (" + "!=" + ")");
				break;

			case sym.D_AND:
				System.out.println("Symbol (" + "&&" + ")");
				break;

			case sym.OR:
				System.out.println("Symbol (" + "||" + ")");
				break;

			case sym.L_THAN_EQ:
				System.out.println("Symbol (" + "<=" + ")");
				break;

			case sym.G_THAN_EQ:
				System.out.println("Symbol (" + ">=" + ")");
				break;

			// Reserved Words

			case sym.BOOL:
				System.out.println("Reserved (" + "bool" + ")");
				break;

			case sym.BREAK:
				System.out.println("Reserved (" + "break" + ")");
				break;

			case sym.CHAR:
				System.out.println("Reserved (" + "char" + ")");
				break;

			case sym.CONST:
				System.out.println("Reserved (" + "const" + ")");
				break;

			case sym.CONTINUE:
				System.out.println("Reserved (" + "continue" + ")");
				break;

			case sym.ELSE:
				System.out.println("Reserved (" + "else" + ")");
				break;

			case sym.FOR:
				System.out.println("Reserved (" + "for" + ")");
				break;

			case sym.FUNC:
				System.out.println("Reserved (" + "func" + ")");
				break;

			case sym.IF:
				System.out.println("Reserved (" + "if" + ")");
				break;

			case sym.INT:
				System.out.println("Reserved (" + "int" + ")");
				break;

			case sym.PACKAGE:
				System.out.println("Reserved (" + "package" + ")");
				break;

			case sym.PRINT:
				System.out.println("Reserved (" + "print" + ")");
				break;

			case sym.RETURN:
				System.out.println("Reserved (" + "return" + ")");
				break;

			case sym.VAR:
				System.out.println("Reserved (" + "var" + ")");
				break;

			case sym.READ:
				System.out.println("Reserved (" + "read" + ")");
				break;

			// Literals

			case sym.BOOL_LIT_T:
				System.out.println("Boolean Literal (" + "true" + ")");
				break;

			case sym.BOOL_LIT_F:
				System.out.println("Boolean Literal (" + "false" + ")");
				break;

			case sym.INT_LIT:
				System.out.println("Integer literal(" + ((CSXIntLitToken) token.value).intValue + ")");
				break;

			case sym.CHAR_LIT:
				System.out.println("Char literal(" + ((CSXCharLitToken) token.value).charValue + ")");
				break;

			case sym.STRING_LIT:
				System.out.println("String literal(" + ((CSXStringLitToken) token.value).stringText + ")");
				break;

			// Error's

			case sym.error:
				System.out.println("Unrecognized Symbol(" + ((CSXStringLitToken) token.value).stringText + ")");
				break;

			case sym.INT_LIT_MAX_SIZE:
				System.out.println("Integer Literal Max Size error set to Integer Max (" + ((CSXIntLitToken) token.value).intValue  + ")");
				break;

			case sym.INT_LIT_MIN_SIZE:
				System.out.println("Integer Literal Minimum Size error set to Integer Minimum (" + ((CSXIntLitToken) token.value).intValue + ")");
				break;

			case sym.CHAR_LIT_ERR_ILL:
				System.out.println("Illegal Char Error (" + ((CSXStringLitToken) token.value).stringText + ")");
				// stored as a string to keep all information
				break;

			case sym.CHAR_LIT_ERR_UNC:
				System.out.println("Unclosed Char Error (" + ((CSXStringLitToken) token.value).stringText + ")");
				// stored as a string to keep all information
				break;

			case sym.STRING_LIT_ERR_UNC:
				System.out.println("Unclosed String Error (" + ((CSXStringLitToken) token.value).stringText + ")");
				break;

			case sym.STRING_LIT_ERR_ILL:
				System.out.println("Illegal String Error (" + ((CSXStringLitToken) token.value).stringText + ")");
				break;

			// Special print states for Char

			case sym.CHAR_LIT_TAB:
				System.out.println("Char literal(\\t)");
				break;

			case sym.CHAR_LIT_NEWLINE:
				System.out.println("Char literal(\\n)");
				break;

			
			
		default:
			// Not really needed for the current style but left in 
			// case something errant happens.
			System.out.println("unrecognized token type: " + token.value);
		} // switch(token.sym)

		token = lex.yylex(); // get next token

	} // not at EOF

	System.out.println("End test of CSX scanner.");

  } // main()

} // class P2
