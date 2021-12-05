import java.io.*;
import java_cup.runtime.*;

class P4 {

	public static void
	main(String args[]) throws java.io.IOException,  Exception {


		/*
		if (args.length != 1) {
			System.out.println("Error: Input file must be named on command line." );
			System.exit(-1);
		}
		*/

		if (args.length != 2) {
			System.out.println("Error: Input file must be named on command line." );
			System.out.println("cont: And the Unparse parameter must be set." );
			System.exit(-1);
		}

		String parseVal = args[1];

		// used to compare with make
		// using string to prevent weirdness
		java.io.Reader yyin = null;

		try {
			yyin = new BufferedReader(new FileReader(args[0]));
		} catch (FileNotFoundException notFound){
			System.out.print("Error: unable to open input file: ");
			System.out.println(args[0]);
			System.exit(1); // 1 means failure
		}

		Scanner.init(yyin); // Initialize Scanner class for parser
		final parser csxParser = new parser();
		System.out.println ("\n\n" + "Begin CSX_Go compilation of " + args[0] + ".\n");
		Symbol root=null;
		try {
			root = csxParser.parse(); // do the parse

			if ("y".equals(parseVal)){
				// Unparse for testing if needed.
				System.out.println("Unparsing:\n");
				((programNode)root.value).unparse(0);	
			}

			System.out.println ("CSX_Go program parsed correctly.");
		} catch (SyntaxErrorException e) {
			System.out.println ("Compilation terminated due to syntax errors.");
			System.exit(0);
		}

		final boolean ok = ((programNode)root.value).isSemanticsCorrect();
		if (ok) {
			System.out.println("No CSX_Go semantics errors detected.");
		} else {
			System.out.println("\nCSX_Go compilation halted due to semantics errors.");
		}
	} // main
} // class P4
