import java_cup.runtime.*;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

class P3 {

	public static void main(String args[]) throws java.io.IOException {
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
		final parser csxParser = new parser();
		Symbol root = null;
		try {
			// root = csxParser.debug_parse(); // do the parse
			root = csxParser.parse(); // do the parse
			System.out.println ("CSX_go program parsed correctly.");
		} catch (Exception e){
			System.out.println ("Compilation terminated due to syntax errors.");
			//e.printStackTrace();
			// not needed cup catches the parse errors now
			System.exit(0);
		}
		System.out.println ("Here is its unparsing:");
		((programNode)root.value).Unparse(0);
	} // main

} // class P3
