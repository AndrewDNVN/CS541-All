import java_cup.runtime.Symbol;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

class P5 {

	public static void
	main(String args[]) throws java.io.IOException,  Exception {

		if (args.length != 1) {
			System.out.println(
			"Error: Input file must be named on command line." );
			System.exit(-1);
		}

		java.io.Reader yyin = null;

		try {
			yyin = new BufferedReader(new FileReader(args[0]));
		} catch (FileNotFoundException notFound){
			System.out.print("Error: unable to open input file: ");
			System.out.println(args[0]);
			System.exit(1); // 1 means failure
		}

		Scanner.init(yyin); // Initialize Scanner class for parser
		parser csxParser = new parser();
		System.out.println ("\n\n" + "CSX_go compilation of " + args[0]);
		Symbol root=null;
		try {
			root = csxParser.parse(); // do the parse
			System.out.println ("Program parsed correctly.");
		} catch (SyntaxErrorException e){
			System.out.println ("Compilation terminated due to syntax errors.");
			System.exit(0);
		}
		if (!((programNode)root.value).isSemanticsCorrect()) {
			System.out.println("Compilation halted due to semantic errors.");
			return;
		}
		java.io.PrintStream outFile = null;
		String outFileName = "test.j";
		try {
			outFile = new java.io.PrintStream(
				new java.io.FileOutputStream(outFileName));
		} catch (FileNotFoundException notFound){
			System.out.println ("Error: unable to open output file " + outFileName);
			System.exit(-1);
		}
		if (((programNode)root.value).codegen(outFile)) {
			System.out.println ("Program translated; result is in " + outFileName);
		} else {
			System.out.println ("Error in translating CSX_go program");
		}
	} // main
} // class P5
	
