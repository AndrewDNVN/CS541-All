// start of AST.java

import java.util.*;
// for the iterator class

import java.io.*;

class asgNode extends stmtNode {
	asgNode(nameNode n, exprNode e, int line, int col) {
		super(line, col);
		target = n;
		source = e;
	}

	void unparse(int indent) {
		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		target.unparse(0);
		System.out.print(" = ");
		source.unparse(0);
		System.out.println(";");
	}

	void checkSemantics(){
		target.checkSemantics();

		source.checkSemantics();

		// make sure not to assign into const
		if(target.kind.val == Kinds.Const){
			System.out.println(error() + "Cannot assign into const"
			 + "identifier.");
			semanticsErrors++;
			target.type = new Types(Types.Error);
		} //close kind.const error

		// rule 14 middle
		// see if this is whole array assignment
		else if((target.kind.val == Kinds.Array)
		 && (source.kind.val == Kinds.Array)){

			SymbolInfo chk_tar;
			
			// The info needed is inside the symbol table 
			// just need to pull it back out.

			// I did have to change the permission on varName from
			// private final to public final.
			// I don't foresee issues with this.
			chk_tar = (SymbolInfo) 
			st.localLookup(target.varName.idname);

			// May need to check globally
			// this could cause issues; But time is short.
			// Can be null but that was already found in the initial
			// checking of target.
			// So it would be type error.
			if (chk_tar == null){
				chk_tar = (SymbolInfo) 
				st.globalLookup(target.varName.idname);
			}

			// This probably has errors
			// but those should have been handled before 
			// here. 
			// Such as arr_size being null.
			if((chk_tar.returnSize() != source.arr_size) ||
			 (target.type.val != source.type.val)){



				System.out.println(error() + "Whole array "
					+ "assignment must have the same type and size.");
				System.out.print(" Size expected: "
				 +chk_tar.returnSize() + ", Size got: "+ source.arr_size
				 + ".\n" );
				semanticsErrors++;
				target.type = new Types(Types.Error);
			}

		} //close whole array assignment
		
		// rule 14 last part
		// see if this is string into char array 
		else if ((target.type.val == Types.Character) 
			&& (target.kind.val == Kinds.Array) 
			&& (source.type.val == Types.String)){
			// check size

			SymbolInfo chk;

			// see above for reasoning
			chk = (SymbolInfo) st.localLookup(target.varName.idname);

			if (chk == null){
				chk = (SymbolInfo) 
				st.globalLookup(target.varName.idname);
			}


			// Look up the name of the target and load it's info 
			// into 'chk'.
			// Then get the length of the source
			// it is already given that this info is present
			// due to the above checks.
			// Granted this is a bit weird on the access.
			if (chk.returnSize() != source.length){
				System.out.println(error() + "The target and source"
				 + " must have the same length when assigning" 
				 + " a String into a char array." );

				semanticsErrors++;
				target.type = new Types(Types.Error);
			}

		} // close of string to char array

		else{
			typesMustBeEqual(source.type.val, target.type.val,
				error() + "The assignment must have matching types.");

		} // final check
	}

	void cg(){


		// alot of the comments below are regurgitated from the project guide
		// it was used to help keep me straight

		// check if src is an array

		if (source.kind.val == Kinds.Array){
			// generate code to clone then save a ref to target

			source.cg();
			// need to check type of src before cloning
			if(source.type.val == Types.String){
				// this is least normal
				// 1st need to convert to char array
				gen("invokestatic", "CSXLib/convertString(Ljava/lang/String;)[C");
				// now the char array should be on the stack
				// so
				gen("invokestatic", "CSXLib/cloneCharArray([C)[C");

			} // str to char

			else if(source.type.val == Types.Character){
				// values should already be on the stack
				gen("invokestatic", "CSXLib/cloneCharArray([C)[C");
			} // char

			else if(source.type.val == Types.Boolean){
				// values should already be on the stack
				gen("invokestatic", "CSXLib/cloneCharArray([Z)[Z");
			} // bool

			else if(source.type.val == Types.Integer){
				// values should already be on the stack
				gen("invokestatic", "CSXLib/cloneCharArray([I)[I");
			} // int

			// now store a reference into target



		} // close array src check

		// normal case non array
		else{
			
			source.cg();
			// all values should be converted to ints for storage		

			// make sure not global
			if (target.varName.idinfo.varIndex != -42){
				gen("istore", target.varName.idinfo.varIndex);
			} // int

			// then figure out how to load
			target.cg();

			
		}


	} // close cg

	private final nameNode target;
	private final exprNode source;
} // class asgNode 

class nameNode extends exprNode {
	nameNode(identNode id, exprNode expr, int line, int col) {
		super(line, col, new Types(Types.Unknown),
		 new Kinds(Kinds.Var));
		varName = id;
		subscriptVal = expr;
	}

	// print what the node is
	void unparse(int indent) {
		varName.unparse(0); 
		if(subscriptVal.isNull()){
			// do nothing
		}
		else {
			// name rule 2 
			// ident [ expr ]
			subscriptVal.unparse(0);
		}
	}

	// from given
	void checkSemantics(){
		varName.checkSemantics();
		subscriptVal.checkSemantics();
		// check for array
		if(subscriptVal.isNull() == false){
			// is array 
			if (subscriptVal.type.val != Types.Integer){
				// make sure it follows the rules
				if ( subscriptVal.type.val != Types.Character){
					// this formatting is easier for me 
					// to read. This is the same as anding.
					System.out.print(error()+ "Only int or char" 
					+" types can index arrays.");

					semanticsErrors++; //increment the count
					type = new Types(Types.Error); // set to error
					// so that future computations
					// have type error as well

				} // close of char
			} // close of int

			// there is a value in subscript
			// making this an array of some kind
		} // close of null

		if (type.val != Types.Error){
			// set stuff if normal
			type = varName.type;
			kind = varName.kind;
		}
		
		// just a regular name node
		if (subscriptVal.isNull() == false){
			// set the kind to variable
			// other kinds handled elsewhere
			kind = new Kinds(Kinds.Var);
		}
	} // close of check

	void cg(){

		if(varName.idinfo.varIndex != -42)
			gen("iload",varName.idinfo.varIndex);
	}

	public final identNode varName;
	private final exprNode subscriptVal;
} // class nameNode 

class forNode extends stmtNode{
	forNode(exprNode e, blockNode b, int line, int col) {
		super(line, col);
	 	condition = e;
	 	loopBody = b;
	}

	void unparse(int indent){

		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		System.out.print(" for ");
		condition.unparse(0);
		genIndent(indent+1);
		loopBody.unparse(0);
	}

	void checkSemantics(){
		// check the condition
		condition.checkSemantics();

		// check that the types end up as boolean
		// same as if node
		typeMustBe(condition.type.val, Types.Boolean,
		error() + "Control flow must be of type boolean.");

		// save info so the loop can return
		loopBody.parentNodeName = this.parentNodeName;

		loopBody.checkSemantics();
	}

	void cg(){
		 
		 String top = buildlabel(labelCount);

		 labelCount++;
		
		 gen(top+":", "; top of loop label");

		 condition.cg();

		 String exit = buildlabel(labelCount++);

		 gen("if_cmpeq", exit, "; branching loop");

		 loopBody.cg();

		 gen("goto", top, "; return to top");

		 gen(exit+":", "; exit");

	}

	private final exprNode condition;
	private final blockNode loopBody;
} // class forNode 

class returnNode extends stmtNode {
	returnNode(exprNode e, int line, int col) {
		super(line, col);
		returnVal = e;
	}

	void unparse(int indent){

		// print linenum where found
		// print return
		// unparse the the name of the  
		// value returning to 
		// print semi colon

		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		System.out.print("return ");
		returnVal.unparse(0);
		System.out.println(";");
	}

	void checkSemantics(){

		returnVal.checkSemantics();

		SymbolInfo id;

		id = (SymbolInfo) st.globalLookup(this.parentNodeName);

		// rule 22
		if (returnVal.isNull()){
			if (id.type.val == Types.Void){
				System.out.println(error() + "Return must"
				+" be defined (not void) when called in a procedure.");
				semanticsErrors++;
			} //close void type check
		}// close null check

		// rule 
		else {

			if(id.type.val == Types.Void){
				System.out.println(error() + "Return must"
				+" be defined (not void) when called in a function.");
				semanticsErrors++;

			}//close type void check

			else {
				typesMustBeEqual(returnVal.type.val, id.type.val, 
				error() + "The return does not match the definition.");
			}

		}// return is not null
	}

	private final exprNode returnVal;
} // class returnNode 

class callNode extends stmtNode {
	callNode(nameNode id, argsNode a_S, int line, int col) {
		super(line, col);
		funcName = id;
		args = a_S;
	}

	
	void unparse(int indent){

		// print linenum where found
		// print function name then (
		// print all the internal args then );

		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		funcName.unparse(0);
		System.out.print(" ( ");
		args.unparse(0);
		System.out.print(" );");
	}


	// This is not as clean as it could be but...
	void checkSemantics(){
		SymbolInfo id;
		funcName.checkSemantics();

		id = (SymbolInfo) st.globalLookup(funcName.varName.idname);

		args.parentNodeName = funcName.varName.idname;

		args.checkSemantics();

		if(id.typeList.size() != id.actualTypeList.size()){
			System.out.println(error() + "The number of types"
				+" must match.");
			semanticsErrors++;
		}

		/* I do not understand why this hash's differently
			// but it does for some reason

			output: when test code 31 is run
			1706234409
			1867750606
			[Integer] @F
			[Integer] @A


			// use hash-code to make sure the types are the same
			else if (id.typeList.hashCode() != id.actualTypeList.hashCode()){
			System.out.println(error() + "The Type of all types"
				+" must match.");
			semanticsErrors++;

			System.out.println(id.typeList.hashCode());
			System.out.println(id.actualTypeList.hashCode());

			System.out.println(id.typeList.toString() + " @F");
			System.out.println(id.actualTypeList.toString() + " @A");
		 }
		*/
		else{

			Iterator<Types> typeListITR = id.typeList.iterator();
			Iterator<Types> actualTypeListITR = 
			 id.actualTypeList.iterator();

			// check all types
			while(typeListITR.hasNext()){
				
				if (typeListITR.next().toString() !=
				 actualTypeListITR.next().toString()){
					System.out.println(error() + "The Type of the types"
					+" must match.");
					semanticsErrors++;
				}
			}//close while for type iterator
		
			Iterator<Kinds> kindListITR = id.kindList.iterator();
			Iterator<Kinds> actualKindListITR = id.actualKindList.iterator();

			while(kindListITR.hasNext()){

				String formal = kindListITR.next().toString();
				String actual = actualKindListITR.next().toString();

				if(formal.equals("ScalarParam") && actual.equals("Array") ){

					System.out.println(error() + "The Kinds must match"
					+" or be allowed.(Scalar Call)");
					semanticsErrors++;
				} //close scalar parameter to array check

				else if(formal.equals("ArrayParam") != actual.equals("Array")){

					System.out.println(error() + "The Kinds must match"
					+" or be allowed. (Array Call())");
					semanticsErrors++;
				}

			}//close kind list while

		}	
	}

	private final nameNode funcName;
	private final argsNode args;
} // class callNode 

class fctCallNode extends exprNode {
	fctCallNode(nameNode id, argsNode a_S, int line, int col) {
		super(line, col);
		funcName = id;
		args = a_S;
	}

	void unparse(int indent){
		
		funcName.unparse(0);
		System.out.print(" ( ");
		
		if (args.isNull()) {
			// Do nothing
		}
		
		else {
			args.unparse(0);
		}

		System.out.print(" ) ");

	}
	 // This is not as clean as it could be but...
	void checkSemantics(){
	 SymbolInfo id;
		funcName.checkSemantics();

		id = (SymbolInfo) st.globalLookup(funcName.varName.idname);

		type = funcName.type;

		kind = funcName.kind;

		args.parentNodeName = funcName.varName.idname;

		args.checkSemantics();

		if(id.typeList.size() != id.actualTypeList.size()){
			System.out.println(error() + "The number of types"
				+" must match.");
			semanticsErrors++;
		}

		else{

			Iterator<Types> typeListITR = id.typeList.iterator();
			Iterator<Types> actualTypeListITR = 
			 id.actualTypeList.iterator();

			// check all types
			while(typeListITR.hasNext()){
				
				if (typeListITR.next().toString() !=
				 actualTypeListITR.next().toString()){
					System.out.println(error() + "The Type of the types"
					+" must match. (fct)");
					semanticsErrors++;
				}
			}//close while for type iterator
		
			Iterator<Kinds> kindListITR = id.kindList.iterator();
			Iterator<Kinds> actualKindListITR = id.actualKindList.iterator();

			while(kindListITR.hasNext()){

				String formal = kindListITR.next().toString();
				String actual = actualKindListITR.next().toString();

				if(formal.equals("ScalarParam") && actual.equals("Array") ){

					System.out.println(error() + "The Kinds must match"
					+" or be allowed.(F Scalar)");
					semanticsErrors++;
				} //close scalar parameter to array check

				else if(formal.equals("ArrayParam") != 
					(actual.equals("Array"))){

					System.out.println(error() 
					+ "The Kinds must match or be allowed. (F Array)");
					semanticsErrors++;
				}

			}//close kind list while

		}	
	}

	private final nameNode funcName;
	private final argsNode args;
} // class fctCallNode 

class castNode extends exprNode {
	castNode(typeNode t, exprNode e, int line, int col) {
		super(line, col);
		operand = e;
		resultType = t;
	}

	void unparse(int indent){

		resultType.unparse(0);		
		System.out.print("(");
		operand.unparse(0);
		System.out.print(")");
	}

	void checkSemantics(){
		resultType.checkSemantics();
		operand.checkSemantics();
		
		if (operand.type.val != Types.Integer){
			
			if (operand.type.val != Types.Character){
				
				if (operand.type.val != Types.Boolean){
					
					// Easier for me to read this way
					typeMustBe(operand.type.val, Types.Boolean, 
					error() +" Cast error: only int, char or bool.");
					// just pick one type to force
					// already know its not one of the above.
				
				} // close Boolean if

			}// close Character if
		
		}// close Integer if

		// load in type info
		type = resultType.type;

	}



	private final exprNode operand;
	private final typeNode resultType;
} // class castNode 



// implemented for code gen
//=================================================

abstract class ASTNode {
	int linenum;
	int colnum;

	static int semanticsErrors = 0; 
	// Total number of semantics errors found 

	static void genIndent(int indent) {
		for (int i = 1; i<=indent; i++) {
			System.out.print("\t");
		}
	} // genIndent
	
	static void mustBe(boolean assertion) {
		if (! assertion) {
			System.out.println(" mustBe failed.");
			throw new RuntimeException();

		}
	} // mustBe
	
	static void typeMustBe(int testType,int requiredType,
		String errorMsg) {
		if ((testType != Types.Error) && (testType != requiredType))
		 {
			System.out.println(errorMsg);
			semanticsErrors++;
		}
	} // typeMustBe
	
	static void typesMustBeEqual(int type1,int type2,
		String errorMsg) {
		if ((type1 != Types.Error) && (type2 != Types.Error) &&
				(type1 != type2)) {
			System.out.println(errorMsg);
			semanticsErrors++;
		}
	} // typesMustBeEqual
	
	String error() {
		return "Error (line " + linenum + "): ";
	} // error

	String errorCol(){
		return "Error (line " + linenum + ", colnum "
		+ colnum+  "): ";
	} // added to help with read and print debugging

	public static SymbolTable st = new SymbolTable();

	ASTNode() {
		linenum=-1; colnum=-1;
	}

	ASTNode(int l, int c) {
		linenum = l;colnum=c;
	}
	
	boolean isNull() {
		return false;
	} // Is this node null?

	void unparse(int indent) {}
		// This will normally need to be redefined in a subclass

	void checkSemantics() {}
		// This will normally need to be redefined in a subclass

 //========================

 // generate an instruction w/ 0 operands
	static void    gen(String opcode){
        	afile.println("\t"+opcode);
	}

        // generate an instruction w/ 1 operand
	static void  gen(String opcode, String operand){
        	afile.println("\t"+opcode+"\t"+operand);
	}

        // generate an instruction w/ 1 operand
	static void  gen(String opcode, int operand){
        	afile.println("\t"+opcode+"\t"+operand);
	}


	//  generate an instruction w/ 2 operands
	static void  gen(String opcode, String operand1, String operand2){
        	afile.println("\t"+opcode+"\t"+ operand1+"  "+ operand2);
	}

	//  generate an instruction w/ 3 operands
	static void  gen(String opcode, String operand1, String operand2, String operand3){
        	afile.println("\t"+opcode+"\t"+ operand1+"  "+ operand2 +"  "+ operand3);
        	// used in some of var decls nodes
	}


	//  generate an instruction w/ 2 operands
	static void  gen(String opcode, String operand1, int operand2){
        	afile.println("\t"+opcode+"\t"+ operand1+"  "+operand2);
	}

	//      build label of form label
	String   buildlabel(int suffix){
                return "label"+suffix;
	}

	//      generate a label for an instruction
	void    genlab(String label){
        	afile.println(label+":");
	}

	// overloaded to be able to loop and print
	// not necessary but it made sense to me to add.
	String genlab(int suffix){
		return "label"+suffix+":";
	}

	

	//   code-gen translates the AST rooted by this node 
  	//      into JAM code which is written in asmFile.
	//   If no errors occur during code generation,
	//    TRUE is returned, and asmFile should contain a
        //    complete and correct JVM program. 
	//   Otherwise, FALSE is returned and asmFile need not
	//    contain a valid program.

       boolean codegen(PrintStream asmfile){
		throw new Error();};//This version of codegen
                                    // should never be called

    //      cg translates its AST node into JVM code
	//      The code which is written in the shared PrintStream  variable
	//      afile (set by codegen)

         void cg(){}; // This member is normally overridden in subclasses



	static PrintStream afile;	// File to generate JVM code into
	static int cgErrors =  0;       // Total number of code gen errors 

	static int numberOfLocals =  0; // Total number of local CSX-lite vars

	// number of globals
	//public int numberOfGlobals = 0;
	// no longer needed

	static int labelCount = 0;	// counter used to gen unique labels

	// used to hold the 'class name' for jasmin
	public String packageNameID = "test";

	void opGen(String a){

		// works mostly for the relational op's
		// builds code based of the operator passed in
		// set a comment explaining what is going on

		// very much from the class notes

		gen("; building code for op: " + a + " in src code at:<" +this.linenum+"," +this.colnum+">");

		gen(a, buildlabel(++labelCount)); // initial label L1

		// true
		gen("iconst_0");

		gen("goto", buildlabel(++labelCount)); // goto L2

		

		// false
		gen(genlab(labelCount)); // this is L1:
		labelCount--;

		gen("iconst_1");

		gen(genlab(labelCount)); // back to L2

		gen("; end code of " + a);

		labelCount++;


	} // used to gen code for operators


	// used to define when global variables should be used / defined
	// starts true on program init
	
	public boolean isGlobal;


	static void myAssert(boolean assertion){
		if (! assertion)
			 throw new RuntimeException();
	}


 //=======================
	// This is book style.
	// this style is used because of the parsing style implemented
	// I was not able to re-build into a list style in time.
	// In short it is used to set the name of the initial call
	// and reference that as needed to be able to move back up


	// save the name of the parent node
	protected String parentNodeName;
}  // abstract class ASTNode

class nullNode extends ASTNode {
 // This class definition probably doesn't need to be changed
	nullNode() {
		super();
	}

	boolean isNull() {
		return true;
	}

	void unparse(int indent) {
		// no action
	}

	// null node do nothing
	void checkSemantics(){
		// do nothing
	}

	void cg(){
		// Do nothing
	}
} // class nullNode

class programNode extends ASTNode {
	// this node is the root of CSX_go programs
	programNode(identNode id, varDeclsNode v_S, funcDeclsNode f_S, 
		int line, int col){
		super(line, col);
		packageName = id;
		vars = v_S;
		funcs = f_S;
	}

	void unparse(int indent){

		System.out.print(linenum + ": package ");

		packageName.unparse(0);
		vars.unparse(0);
		funcs.unparse(0);
		System.out.print(" " + linenum + ": {EOF}\n");
	}

	// from given startup file
	void checkSemantics() {
		// 'program name never conflicts'
		// from assignment sheet
		vars.checkSemantics(); //recursive call
		funcs.checkSemantics(); // recursive call

	} // checkSemantics

	boolean isSemanticsCorrect() {
		checkSemantics();
		return (semanticsErrors == 0);
	} // isSemanticsCorrect

	// From given code
	boolean codegen(PrintStream asmfile) {
        	afile = asmfile;
        	cg();
        	return (cgErrors == 0);
 	}

	void cg() {
        	
		// build out general info for jasmin
		// left as test

		// in this case use the package name to call for the csx class
       	//packageNameID = packageName.idname; 
			
		gen(".class","public", packageNameID);
        gen(".super","java/lang/Object");

        // build code for global vars
        // this is checked via the isGlobal 'Z'

        isGlobal = true;
        vars.cg();

        // this is not the cleanest
		// but should be accurate to the language
		// that all globals have to come before 
		// functions



		isGlobal = false;

        // the 'java' main
        gen(".method"," public static","main([Ljava/lang/String;)V");

		// call CSX main

       

        gen("invokestatic", packageNameID+"/main()V");
        gen("return");

        // end of code will be handled in function gen call
        // close out the java main call
        gen(".limit","locals",2);
        gen(".limit","stack",10);
        gen(".end","method");

        // build the rest of the functions
        funcs.cg();
        	
	}

	private identNode packageName; 
	private varDeclsNode vars;
	private funcDeclsNode funcs;
}

class varDeclsNode extends ASTNode {
	varDeclsNode() {
		super();
	}
	varDeclsNode(declNode d, varDeclsNode f, int line, int col) {
		super(line, col);
		thisVar = d;
		moreVars = f;
	}

	void unparse(int indent){
		thisVar.unparse(indent);
		moreVars.unparse(indent);
	}

	// recursive calls
	void checkSemantics(){
		thisVar.checkSemantics();
		moreVars.checkSemantics();
	}

	// recursive call to var nodes 
	void cg(){
		
		thisVar.cg();
		moreVars.cg();
	}

	static nullVarDeclsNode NULL = new nullVarDeclsNode();
	private declNode thisVar;
	private varDeclsNode moreVars;
} // class varDeclsNode

class nullVarDeclsNode extends varDeclsNode {
	nullVarDeclsNode() {}
	boolean isNull() {
		return true;
	}
	void unparse(int indent) {}
	
	// null node do nothing
	void checkSemantics(){
		// do nothing
	}
	void cg(){
		// Do nothing
	}
} // class nullVarDeclsNode

// abstract superclass; only subclasses are actually created
abstract class declNode extends ASTNode {
	declNode() {
		super();
	}
	declNode(int l, int c) {
		super(l, c);
	}
} // class declNode

class varDeclNode extends declNode {
	varDeclNode(identNode id, typeNode t, int line, int col) {
		super(line, col);
		varName = id;
		varType = t;
	}

	void unparse(int indent){
		
		System.out.print("\n"+ linenum + ": ");
		genIndent(indent);
		System.out.print("var ");
		
		varName.unparse(0);

		varType.unparse(0);

		System.out.print(";");
	}

	void checkSemantics(){
		// most below is from the provided code
		SymbolInfo id;

		id = (SymbolInfo) st.localLookup(varName.idname);

		// not found in the map
		if (id == null){

			// create new entry
			// I did change the order of the call,
			// to match the order used elsewhere in the code.
			// it is now id, type, kind
			id = new SymbolInfo(varName.idname, varType.type, 
				new Kinds(Kinds.Var));

			// load correct value into name
			varName.type = varType.type;

			// because the symbol table can generate exceptions
			// try block
			try {

				st.insert(id);

			} catch(DuplicateException d){
				// given

			} catch(EmptySTException e){
				// given
			}// close block

			varName.idinfo = id;
		} //close if

		else {
			// given
			System.out.println(error() + varName +
				" is already in use.");
			
			semanticsErrors++;
			// load in error type
			varName.type = new Types(Types.Error);
		}
	}

	// build for non array, ASG, or const vars
	void cg(){

		// build code for global
		if (isGlobal == true){

			// set to something to 
			// show that it is a global var
			// - (42) because its the meaning to everything
			varName.idinfo.varIndex = -42;

			// load path info into ST
			varName.idinfo.packagePath = packageNameID + "/" + varName.idname;

			gen(".field public static", varName.idname, varType.getTypeShort());

		}

		// build local variables
		else if (isGlobal == false){
			
			// set register where the value will be found
			varName.idinfo.varIndex = numberOfLocals;

			// store where the index is
			//gen("astore", varName.idinfo.varIndex);

			numberOfLocals++;
		}

		else{
			// this should not happen
			// but if it does print an error?
			System.out.println("An error occurred in codegen, with isGlobal being neither True or False.");
			errorCol();
			cgErrors++;
		}

	}

	private final identNode varName;
	private final typeNode varType;
} // class varDeclNode

	// Could not figure out a better way to do this so...
class varASGDeclNode extends declNode {
	varASGDeclNode(identNode id, exprNode e, int line, int col) {
		super(line, col);
		varName = id;
		initValue = e;
	}

	void unparse(int indent){
		
		System.out.print("\n"+ linenum + ": ");
		genIndent(indent);
		System.out.print("var ");
		
		varName.unparse(0);
		System.out.print(" = ");
		initValue.unparse(0);

		System.out.print(";");
	}

	void checkSemantics(){
		// most below is from the provided code
		SymbolInfo id;

		id = (SymbolInfo) st.localLookup(varName.idname);

		// check 
		if (id == null){
			// get the info for the init value
			initValue.checkSemantics();

			id = new SymbolInfo(varName.idname, initValue.type,
			new Kinds(Kinds.Var));

			varName.type = initValue.type;

			try {

				st.insert(id);

			} catch(DuplicateException d){
				// given

			} catch(EmptySTException e){
				// given
			}// close block

			varName.idinfo = id;

		}
		
		else {
			// given
			System.out.println(error() + varName +
				" is already in use.");
			
			semanticsErrors++;
			// load in error type
			varName.type = new Types(Types.Error);
		}
	}

	void cg(){

		// build code for global
		// could not get to work in 
		// time for submission

		
		// For future reference the solution is to define another method in which that information is calculated


		if (isGlobal == true){
			// same as above
			varName.idinfo.varIndex = -42;

			varName.idinfo.packagePath = packageNameID + "/" + varName.idname;

			// figure out the value of the expression and place it on the 
			// stack then use that to load into the field
			//initValue.cg();

			//gen(".field public static ", varName.idname, varType.getTypeShort());

		}

		// local variables
		else if (isGlobal == false){

			varName.idinfo.varIndex = numberOfLocals;
			// find out the initial value
			initValue.cg();

			// owing to how istore works
			// the initial value will already be on the stack
			gen("istore", varName.idinfo.varIndex);

			numberOfLocals++;
		}

		else{
			// this should not happen
			// but if it does print an error?
			System.out.println("An error occurred in codegen, with isGlobal being neither True or False.");
			errorCol();
			cgErrors++;
		}
	}

	private final identNode varName;
	private final exprNode initValue;
} // class varDeclNode

class constDeclNode extends declNode {
	constDeclNode(identNode id, exprNode e, int line, int col) {
		super(line, col);
		constName = id;
		constValue = e;
	}

	void unparse(int indent){
		
		System.out.print("\n"+linenum + ": ");
		
		genIndent(indent);
		
		System.out.print("const ");
		constName.unparse(0);
		
		System.out.print(" = ");
		constValue.unparse(0);

		System.out.print(";");
	}

	void checkSemantics(){
		// most below is from the provided code
		// modified now to work with const
		constValue.checkSemantics();
		SymbolInfo id;

		id = (SymbolInfo) st.localLookup(constName.idname);

		// not found in the map
		if (id == null){

			// create new entry
			// I did change the order of the call,
			// to match the order used elsewhere in the code.
			// it is now id, type, kind
			id = new SymbolInfo(constName.idname, constValue.type, 
				new Kinds(Kinds.Const));

			// load correct value into name
			constName.type = constValue.type;

			// because the symbol table can generate exceptions
			// try block
			try {

				st.insert(id);

			} catch(DuplicateException d){
				// given

			} catch(EmptySTException e){
				// given
			}// close block

			constName.idinfo = id;
		} //close if

		else {
			// given
			System.out.println(error() + constName +
				" is already in use.");
			
			semanticsErrors++;
			// load in error type
			constName.type = new Types(Types.Error);
		}
	}

	void cg(){

		// build code for global
		// could not figure out in time
		if (isGlobal == true){

			// same as above
			constName.idinfo.varIndex = -42;

			constName.idinfo.packagePath = packageNameID + "/" + constName.idname;

			//gen

		}

		// local variables
		else if (isGlobal == false){
			// similar to above

			constName.idinfo.varIndex = numberOfLocals;

			numberOfLocals++;

			// figure out the value
			constValue.cg();

			// same as above
			gen("istore", constName.idinfo.varIndex);
		}

		else{
			// this should not ever happen
			// but if it does print an error?
			System.out.println("An error occurred in codegen, with isGlobal being neither True or False.");
			errorCol();
			cgErrors++;
		}

	}

	private final identNode constName;
	private final exprNode constValue;
} // class constDeclNode


class nullReadNode extends readNode {
	nullReadNode() {}
	boolean   isNull() {return true;}
	void unparse(int indent) {}
	// null node do nothing
	void checkSemantics(){
		// do nothing
	}
} // class nullReadNode 

class arrayDeclNode extends declNode {
	arrayDeclNode(identNode id, typeNode t, intLitNode lit,
	 int line, int col) {
		super(line, col);
		arrayName = id;
		elementType = t;
		arraySize = lit;
	}

	void unparse(int indent){

		System.out.print("\n"+linenum + ": ");
		
		genIndent(indent);
		System.out.print("var ");
		arrayName.unparse(0);
		System.out.print(" ");
		elementType.unparse(0);
		System.out.print(" [");
		arraySize.unparse(0);
		System.out.print("];");

	}

	void checkSemantics(){
		// most below is from the provided code
		// modified now to work with array
		SymbolInfo id;

		id = (SymbolInfo) st.localLookup(arrayName.idname);

		// not found in the map
		if (id == null){

			// now check the size of the array
			// must be greater than 0
			// added a return to the intLitNode
			if(arraySize.returnIntVal() > 0){

				// create new entry
				// I did change the order of the call,
				// to match the order used elsewhere in the code.
				// it is now id, type, kind, and in this case 
				// use the overload to set the size
				id = new SymbolInfo(arrayName.idname,
				 elementType.type, new Kinds(Kinds.Array), 
				 arraySize.returnIntVal());

				// load correct value into name
				arrayName.type = elementType.type;

				// because the symbol table can generate exceptions
				// try block
				try {

					st.insert(id);

				} catch(DuplicateException d){
					// given

				} catch(EmptySTException e){
					// given
				}// close block

				arrayName.idinfo = id;

			} //close of try
			// array size < 0
			else {
				System.out.println(error() + arrayName + 
					" does not have a size < 0.");
				semanticsErrors++;
				arrayName.type = new Types(Types.Error);
			} 

		} //close if of null

		else {
			// given
			System.out.println(error() + " " + arrayName +
				" is already given.");
			
			semanticsErrors++;
			// load in error type
			arrayName.type = new Types(Types.Error);
		}
	}

	void cg(){

		// build code for global
		if (isGlobal == true){
			// same as above
			arrayName.idinfo.varIndex = -42;

			arrayName.idinfo.packagePath = packageNameID +"/"+arrayName.idname;


			// [ is used to define an array
			gen(".field public static", arrayName.idname, "[" + elementType.getTypeShort());
		}

		// local variables
		else if (isGlobal == false){

		}

		else{
			// this should not happen
			// but if it does print an error?
			errorCol();
		}

	}

	private final identNode arrayName;
	private final typeNode elementType;
	private final intLitNode arraySize;
} // class arrayDeclNode

class funcDeclsNode extends ASTNode {
	funcDeclsNode(){
		super();
	}
	funcDeclsNode(funcDeclNode f, funcDeclsNode f_S, 
		int line, int col) {
		super(line, col);
		funcDecl = f;
	 	funcDecls = f_S;
	}

	void unparse (int indent){
		funcDecl.unparse(0);
		funcDecls.unparse(0);
	}
	// recursive calls
	void checkSemantics(){
		funcDecl.checkSemantics();
		funcDecls.checkSemantics();
	}

	// recursive call
	void cg(){
		// build current decl

		// build the functions

		funcDecl.cg();
		funcDecls.cg();
	}
	
	static nullFuncDeclsNode NULL = new nullFuncDeclsNode();
	private funcDeclNode funcDecl;
	private funcDeclsNode funcDecls;
} // class funcDeclsNode 

class nullFuncDeclsNode extends funcDeclsNode {
	nullFuncDeclsNode() {}
	boolean   isNull() {return true;}
	void unparse(int indent) {}

	// null node do nothing
	void checkSemantics(){
		// do nothing
	}

	void cg(){
		// do nothing
	}
} // class nullFuncDeclsNode 

class funcDeclNode extends ASTNode {
	funcDeclNode(identNode id, argDeclsNode a, typeNode t,
			blockNode b, int line, int col) {
		super(line, col);
		name = id;
		args = a;
		returnType = t;
		block = b;
	}

	void unparse(int indent){

		System.out.print("\n"+linenum + ": func ");
		genIndent(indent);
		name.unparse(0);
		System.out.print(" (");
		args.unparse(0);
		System.out.print(") ");
		
		if (returnType.isNull()){
			// do nothing
		}
		else
		{
			// print return type
			returnType.unparse(0);
		}
		// now call to print the block
		block.unparse(0);
	}
	// lots of settings defined here

	void checkSemantics(){
		SymbolInfo id;
		
		// see if this is the main func
		if(name.idname.equals("main")){
			// check the rules
			if(returnType.type.val != Types.Void){
				System.out.println(error() +" Main must "
					+"have a void return type.");
				semanticsErrors++;
			}//close void return
			// check args should be null if correct main
			if(!args.isNull()){
				System.out.println(error() +" Main must "
					+"not have parameters.");
				semanticsErrors++;
			}//close null

		}// close main rule checks

		// see if main is declared already 
		id = (SymbolInfo) st.localLookup("main");
		
		// if id not null it means that it is defined after main
		// because main now has a value
		if (id != null){
			System.out.println(error() + name.idname +
					": declared after main.");
				semanticsErrors++;
		}//close after main null check

		// now lookup the actual function
		id = (SymbolInfo) st.localLookup(name.idname);
		
		// id is not defined!
		// no need to check global
		// functions cannot be declared in other functions
		if (id == null){
			// set info
			id = new SymbolInfo(name.idname, returnType.type, 
				 new Kinds(Kinds.Function));
			
			name.type = returnType.type;

			try {

				st.insert(id);

			} catch(DuplicateException d){
				// given

			} catch(EmptySTException e){
				// given
			}// close block

			name.idinfo = id;

		} // close null
		else {
			System.out.println(error() + name.idname + 
				" is already declared. (funcDecl)");

			semanticsErrors++;
			
			name.type = new Types(Types.Error);
		}

		// Save the name of this node in 
		// a field so it can be called later.
		args.parentNodeName = name.idname;

		block.parentNodeName = name.idname;

		// checked
		args.checkSemantics();

		// check and correct
		block.checkSemantics();
	}

	// gen code for funcs
	void cg(){

		// save the number of locals
		int tmp = numberOfLocals;

		// need to reset so the internal to the block can be used
		numberOfLocals = 1;

		// make sure that args have correct info
		

		if (args.isNull()){
			// define the signature of the function
			// with no arges
			name.idinfo.signature = name.idname+"()"+returnType.getTypeShort();
		}

		else{

			String signature = null;

			args.cg();
			// work in progress
			// time to look into the linked list with all of the args
			// to build the method signature

			// first figure out what kinds of values are being dealt with
			Iterator<Kinds> actualKindListITR = name.idinfo.actualKindList.iterator();
			Iterator<Types> actualTypeListITR = name.idinfo.actualTypeList.iterator();

			String kind;
			String type;

			// both should match up
			while(actualKindListITR.hasNext()){

				// load in next value
				kind = actualKindListITR.next().toString();
				type = actualTypeListITR.next().toString();

				// see if array
				if (kind.equals("Array")){
					// if array add [ to the signature
					// add correct type to signature
					switch (type){
						case "Character":{
							signature += "[C";
							break;
						}
						
						case "Integer":{
							signature += "[I";
							break;
						}
						
						case "Boolean":{
							signature += "[Z";
							break;
						}

						case "String":{
							// can only be passed as char arrays
							signature += "[C";
							break;
						}

					} // close switch

				}//close array check

				// Not an array
				else {
					switch (type){
						case "Character":{
							signature += "C";
							break;
						}
						
						case "Integer":{
							signature += "I";
							break;
						}
						
						case "Boolean":{
							signature += "Z";
							break;
						}

						//case "String":{
							
						//	signature += "C";
						//}
					}
				} // close else


			}// close while

			// now build the signature
			name.idinfo.signature = ".method" + "public static" + name.idname +"("+ signature +")"+ returnType.getTypeShort();


		}// close else

		gen(".method", "public static", name.idinfo.signature);

		// Unsure of why exactly this needs to be here but it seems to work
		gen(".limit", "locals", numberOfLocals*250);

		block.cg();
		gen("return");
		
		// always at-least 1
		
		

		// set number of locals
		
		// why not the stack is large
		gen(".limit", "stack", 2500);
		gen(".end", "method");

		// reset the number of locals
		numberOfLocals = tmp;
	}
	
	private final identNode name;
	private final argDeclsNode args;
	private final typeNode returnType;
	private final blockNode block;
} // class funcDeclNode 

class readListNode extends stmtNode{
	readListNode() {}
	readListNode(readNode pn, int line, int col) {
		super(line, col);
		outputValue = pn;
	}

	void unparse(int indent){

	System.out.print("\n"+linenum + ": read ");
	genIndent(indent);

	outputValue.unparse(0);
	System.out.print(")");

	}

	void checkSemantics(){
		outputValue.checkSemantics();
	}

	void cg(){
		outputValue.cg();		
	}

	private readNode outputValue;
}

class readNode extends stmtNode {
	readNode() {}
	readNode(nameNode n, readNode rn, int line, int col) {
		super(line, col);
		 targetVar = n;
		 moreReads = rn;
	}

	void unparse(int indent) {

		targetVar.unparse(0);

        if (moreReads.isNull()) {
        	// do nothing
        }
        
        else {

            System.out.print(", ");
            moreReads.unparse(0);
        }
    }

    // rule 13
    void checkSemantics(){
    	
    	targetVar.checkSemantics();
    	moreReads.checkSemantics();

    	// nested if
    	if (targetVar.type.val != Types.Integer){
    		if (targetVar.type.val != Types.Character){
    			System.out.println(errorCol() + "Reads can "+
    				"only have Types of int or char." );
    			semanticsErrors++;	
    		}

    	} //close int check	
    }

    void cg(){
    	// gen value for reading into
    	if (targetVar.type.val == Types.Character){
    		gen("invokestatic", "CSXLib/readChar()C");
    	}

    	else if (targetVar.type.val == Types.Integer){
    		gen("invokestatic", "CSXLib/readInt()I");
    	}

    	// now store the value
    		// check global 
    	if(targetVar.varName.idinfo.varIndex != -42){
    		gen("istore", targetVar.varName.idinfo.varIndex);
    	}

    	else{
    		// global
    	}

    	if (!moreReads.isNull())
    		moreReads.cg();
    }

	static nullReadNode NULL = new nullReadNode();
	private nameNode targetVar;
	private readNode moreReads;
} // class readNode 

// Maybe finished

// abstract superclass; only subclasses are actually created
abstract class argDeclNode extends ASTNode {
	argDeclNode() {
		super();
	}
	argDeclNode(int l, int c) {
		super(l, c);
	}
}

class argDeclsNode extends ASTNode {
	argDeclsNode() {}
	argDeclsNode(argDeclNode arg, argDeclsNode args,
	 int line, int col) {
		super(line, col);
		thisArg = arg;
		moreArgs = args;
	}
	
	void unparse(int indent){

		thisArg.unparse(0);
		if (moreArgs.isNull()){
			// do nothing
		}
		else{
			System.out.print(" , ");
			moreArgs.unparse(0);
		}

	}
	// recursive calls
	void checkSemantics(){

		// setting the parent node framework
		thisArg.parentNodeName = this.parentNodeName;
		moreArgs.parentNodeName = this.parentNodeName;

		thisArg.checkSemantics();
		moreArgs.checkSemantics();
	}

	void cg(){
		thisArg.cg();
		moreArgs.cg();
	}

	static nullArgDeclsNode NULL = new nullArgDeclsNode();
	private argDeclNode thisArg;
	private argDeclsNode moreArgs;
} // class argDeclsNode 

class nullArgDeclsNode extends argDeclsNode {
	nullArgDeclsNode() {}
	boolean   isNull() {return true;}
	void unparse(int indent) {}

	// null node do nothing
	void checkSemantics(){
		// do nothing
	}
} // class nullArgDeclsNode 

class arrayArgDeclNode extends argDeclNode {
	arrayArgDeclNode(identNode id, typeNode t, int line, int col) {
		super(line, col);
		argName = id;
		elementType = t;
	}

	void unparse(int indent){
		genIndent(indent);
		argName.unparse(0);
		System.out.print(" [ ] ");
		elementType.unparse(0);
	}

	void checkSemantics(){
		SymbolInfo id_parent;

		id_parent = (SymbolInfo) st.globalLookup(this.parentNodeName);

		if (id_parent != null){

			id_parent.typeList.add(new Types(elementType.type.val));
			id_parent.kindList.add(new Kinds(Kinds.ArrayParam));
		}

		SymbolInfo id;

		id = (SymbolInfo) st.localLookup(argName.idname);

		if (id == null){
			id = new SymbolInfo (argName.idname, elementType.type,
				new Kinds(Kinds.ArrayParam));

			argName.type = elementType.type;

			try {
                st.insert(id);
            } catch (DuplicateException d) {
                /* can't happen */
            } catch (EmptySTException e) {
                /* can't happen */
            }
            argName.idinfo = id;
		}

		else {
			System.out.println(error()+ "variable already declared.");
			semanticsErrors++;
			argName.type = new Types(Types.Error);
		}
	}

	private final identNode argName;
	private final typeNode elementType;
} // class arrayArgDeclNode 

class valArgDeclNode extends argDeclNode {
	valArgDeclNode(identNode id, typeNode t, int line, int col) {
		super(line, col);
		argName = id;
		argType = t;
	}

	void unparse(int indent){
		genIndent(indent);
		argName.unparse(0);
		System.out.print(" ");
		argType.unparse(0);
	}

	void checkSemantics(){
		SymbolInfo id_parent;

		id_parent = (SymbolInfo) st.globalLookup(this.parentNodeName);

		if (id_parent != null){

			id_parent.typeList.add(new Types(argType.type.val));
			id_parent.kindList.add(new Kinds(Kinds.ScalarParam));
		}

		SymbolInfo id;

		id = (SymbolInfo) st.localLookup(argName.idname);

		if (id == null){
			id = new SymbolInfo (argName.idname, argType.type,
				new Kinds(Kinds.ScalarParam));

			argName.type = argType.type;

			try {
                st.insert(id);
            } catch (DuplicateException d) {
                /* can't happen */
            } catch (EmptySTException e) {
                /* can't happen */
            }
            argName.idinfo = id;
		}

		else {
			System.out.println(error()+ "variable already declared.");
			semanticsErrors++;
			argName.type = new Types(Types.Error);
		}
	}

	private final identNode argName;
	private final typeNode argType;
} // class valArgDeclNode 

// finished 

class ifNode extends stmtNode {
	ifNode(exprNode e, blockNode b, int line, int col) {
		super(line, col);
		condition = e;
		body = b;
	}

	void unparse(int indent) {
		System.out.print("\n" + linenum + ":");
		genIndent(indent);
		System.out.print("if (");
		condition.unparse(0);
		System.out.println(")");
		body.unparse(indent+1);

	}

	void checkSemantics(){

		// check the condition
		condition.checkSemantics();

		// check that the types end up as boolean
		typeMustBe(condition.type.val, Types.Boolean,
		error() + "Control flow must be of type boolean.");

		// save the info of this node so the body can return	
		body.parentNodeName = this.parentNodeName;

		body.checkSemantics();
	}

	void cg(){

		// dealing with labels to far to long for me for some reason

		int tmp = labelCount;

		// build code for condition
		condition.cg();

		gen("; building code for if in src code at:<" +this.linenum+"," +this.colnum+">");

		gen("if_cmpeq", buildlabel(tmp)); // initial label L1

		// true
		gen("iconst_0");

		labelCount++;

		body.cg();

		gen("; end code of body");

		tmp++;

		gen("goto", buildlabel(tmp)); // goto L2

		tmp--;

		// false
		gen(genlab(tmp)); // this is L1:

		gen("iconst_1");

		tmp++;

		gen(genlab(tmp)); // back to L2

		gen("; end code of if");

		labelCount = labelCount + tmp + 1;	

	}


	private final exprNode condition;
	private final blockNode body;
} // class ifNode

class ifThenNode extends stmtNode {
	ifThenNode(exprNode e, blockNode b1, blockNode b2, 
		int line, int col) {
		super(line, col);
		condition = e;
		thenPart = b1;
		elsePart = b2;
	}

	void unparse(int indent) {
		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		System.out.print("if ");
		condition.unparse(0);
		thenPart.unparse(indent+1);
		elsePart.unparse(indent+1);
	}

	void checkSemantics(){
		// check the expression
		condition.checkSemantics();

		// check that the types end up as boolean
		// same as if node
		typeMustBe(condition.type.val, Types.Boolean,
		error() + "Control flow must be of type boolean.");

		// save the name of the parent node so they can return
		thenPart.parentNodeName = this.parentNodeName;
		elsePart.parentNodeName = thenPart.parentNodeName;

		// first block
		thenPart.checkSemantics();
		elsePart.checkSemantics();
	}

	void cg(){

		condition.cg();

		int tmp = labelCount;

		labelCount++;

		gen("; building code for if then in src code at:<" +this.linenum+"," +this.colnum+">");

		gen("ifeq", buildlabel(tmp)); // initial label L1

		// true
		gen("iconst_0");

		thenPart.cg();

		gen("; end code of body");

		tmp++;

		gen("goto", buildlabel(tmp)); // goto L2

		tmp--;

		// false
		gen(genlab(tmp)); // this is L1:

		gen("iconst_1");

		labelCount++;

		elsePart.cg();

		tmp++;

		gen(genlab(tmp)); // back to L2

		gen("; end code of if then");

		tmp++;

		labelCount = labelCount + tmp;


	}

	private final exprNode condition;
	private final blockNode thenPart;
	private final blockNode elsePart;
} // class ifThenNode 

class binaryOpNode extends exprNode {
	binaryOpNode(exprNode e1, int op, exprNode e2, int line, int col) {
		super(line, col);
		operatorCode = op;
		leftOperand = e1;
		rightOperand = e2;
	}

	// modified from normal
	// Covers: + _ * /
	// Covers: < > <= >= == !=
	// Covers: && ||
	// not really a binary operations but it made
	// sense to house them all here

	static void printOp(int op) {
		switch (op) {
			case sym.PLUS:
				System.out.print(" + ");
				break;
			case sym.MINUS:
				System.out.print(" - ");
				break;
			case sym.STAR:
				System.out.print(" * ");
				break;
			case sym.SLASH:
				System.out.print(" / ");
				break;
			case sym.L_THAN:
				System.out.print(" < ");
				break;
			case sym.G_THAN:
				System.out.print(" > ");
				break;
			case sym.L_THAN_EQ:
				System.out.print(" <= ");
				break;
			case sym.G_THAN_EQ:
				System.out.print(" >= ");
				break;
			case sym.D_EQUAL:
				System.out.print(" == ");
				break;
			case sym.NOT_EQUAL:
				System.out.print(" != ");
				break;
			case sym.OR:
				System.out.print(" || ");
				break;
			case sym.D_AND:
				System.out.print(" && ");
				break;
			case -1:
				break;
			default:
				throw new Error("printOp: case not found");
		}
	}

	void unparse(int indent) {
		System.out.print("(");
		leftOperand.unparse(0);
		printOp(operatorCode);
		rightOperand.unparse(0);
		System.out.print(")");
	}

	void checkSemantics(){

		leftOperand.parentNodeName = this.parentNodeName;
		rightOperand.parentNodeName = this.parentNodeName;
		leftOperand.checkSemantics();
		rightOperand.checkSemantics();

		// This is ugly due to the
		// parsing style implemented.
		mustBe(
			operatorCode == sym.D_AND 		|| 
			operatorCode == sym.OR 			||
			operatorCode == sym.NOT_EQUAL 	||
			operatorCode == sym.D_EQUAL 	||
			operatorCode == sym.G_THAN_EQ 	||
			operatorCode == sym.L_THAN_EQ 	||
			operatorCode == sym.G_THAN 		||
			operatorCode == sym.L_THAN 	 	||
			operatorCode == sym.SLASH 	 	||
			operatorCode == sym.STAR 	 	||
			operatorCode == sym.MINUS 		||
			operatorCode == sym.PLUS		);

		// Due to the structure of cup and AST
		// everything is inside of this.
		// already know that it is one of the above symbols
		switch(operatorCode){

			// arithmetic Op's rule 1
			case sym.STAR:
			case sym.SLASH:
			case sym.PLUS:
			case sym.MINUS:{

				if(leftOperand.type.val != Types.Integer){
					if(leftOperand.type.val != Types.Character){
						System.out.println(error() + 
							"Left hand op has to be of type" +
							" char or int.");
						semanticsErrors++;
						type = new Types(Types.Error);
					} //close char
				} //close int

				if(rightOperand.type.val != Types.Integer){
					if(rightOperand.type.val != Types.Character){
						System.out.println(error() + 
							"right hand op has to be of type" +
							" char or int.");
						semanticsErrors++;
						type = new Types(Types.Error);
					} //close char
				} //close int

				type = new Types(Types.Integer);

				break;
			} //close of arithmetic Op's

			// logical Op's rule 2
			case sym.D_AND:
			case sym.OR:{

				if(leftOperand.type.val != Types.Boolean){
						System.out.println(error() + 
							"Left hand op has to be of type" +
							" char or int.");
						semanticsErrors++;
						type = new Types(Types.Error);
				} //close bool

				if(rightOperand.type.val != Types.Boolean){
					
						System.out.println(error() + 
							"right hand op has to be of type" +
							" char or int.");
						semanticsErrors++;
						type = new Types(Types.Error);
				} //close bool
				
				type = new Types(Types.Boolean);

				break;
			}// close of Logical Op's
			
			// relational Op's rule 3
			case sym.NOT_EQUAL:
			case sym.D_EQUAL:
			case sym.G_THAN_EQ:
			case sym.L_THAN_EQ:
			case sym.G_THAN:
			case sym.L_THAN:{

				if(leftOperand.type.val != Types.Boolean){
					if(leftOperand.type.val != Types.Integer){
						if(leftOperand.type.val != Types.Character){
							System.out.println(error() + 
								"Left hand op has to be of type" +
								" char or int.");
							semanticsErrors++;
							type = new Types(Types.Error);
						} //close char
					} //close int
				} // close bool

				if(rightOperand.type.val != Types.Boolean){
					if(rightOperand.type.val != Types.Integer){
						if(rightOperand.type.val != Types.Character){
							System.out.println(error() + 
								"right hand op has to be of type" +
								" char or int.");
							semanticsErrors++;
							type = new Types(Types.Error);
						} //close char
					} //close int
				} // close bool

				
				type = new Types(Types.Boolean);

				break;
			} //close of relational Op's

		}//close of switch
	}

	void cg(){

		// use switches to work

		leftOperand.cg();

		int tmp = 0;

		switch(operatorCode){

			// arithmetic Op's rule 1
			// just put on the stack the 
			// other values will be there
			case sym.STAR:{
				
				rightOperand.cg();
				
				gen("imul");
				break;
			}
			case sym.SLASH:{
				rightOperand.cg();
				gen("idiv");
			}
			case sym.PLUS:{
				rightOperand.cg();
				gen("iadd");
				break;
			}
			case sym.MINUS:{
				rightOperand.cg();
				gen("isub");
				break;
			} //close of arithmetic Op's


			// due to the style of my code
			// I couldn't easily make 
			// the iand or the ior INS work
			// so I am using the class mentioned method
			
			// logical Op's rule 2
			case sym.D_AND:{
				
				rightOperand.cg();
				gen("iand");
				
				break;
			}
			case sym.OR:{
				
				rightOperand.cg();
				gen("ior");

				break;
			}// close of Logical Op's
			
			// relational Op's rule 3
			case sym.NOT_EQUAL:{
				rightOperand.cg();
				opGen("if_icmpne");
				break;
			}
			case sym.D_EQUAL:{
				rightOperand.cg();
				opGen("if_icmpeq");
				break;
			}
			case sym.G_THAN_EQ:{
				rightOperand.cg();
				opGen("if_icmpge");
				break;
			}
			case sym.L_THAN_EQ:{
				rightOperand.cg();
				opGen("if_icmple");
				break;
			}
			case sym.G_THAN:{
				rightOperand.cg();
				opGen("if_icmpgt");
				break;
			}
			case sym.L_THAN:{
				rightOperand.cg();
				opGen("if_icmplt");
				break;
			} //close of relational Op's
		}
	}

	private final exprNode leftOperand;
	private final exprNode rightOperand;
	private final int operatorCode; // Token code of the operator
} // class binaryOpNode 

class unaryOpNode extends exprNode {
	unaryOpNode(int op, exprNode e, int line, int col) {
		super(line, col);
		operand = e;
		operatorCode = op;
	}
	// copied from above

	static void printOp(int op) {
		switch (op) {
			case sym.NOT:
				System.out.print(" ! ");
				break;
			case -1:
				break;
			default:
				throw new Error("printOp: case not found");
		}
	}

	void unparse(int indent) {
		System.out.print("(");
		printOp(operatorCode);
		System.out.print(" ");
		operand.unparse(0);
		System.out.print(")");
	}

	void checkSemantics(){
		operand.checkSemantics();
		type = new Types(Types.Boolean);
		
		if(operatorCode == sym.NOT){
			// check type
			typeMustBe(operand.type.val, Types.Boolean, 
				error() + " Only bool values can be applied.");
		}
		// wrong type info in operand
		if(operand.type.val != Types.Boolean){
			// set to error type
			type = new Types(Types.Error);
		}
	}

	void cg(){
		opGen("ifne");
	}



	private final exprNode operand;
	private final int operatorCode; // Token code of the operator
} // class unaryOpNode 

// abstract superclass; only subclasses are actually created
abstract class stmtNode extends ASTNode {
	stmtNode() {
		
		super();
	}
	stmtNode(int l, int c) {
		super(l, c);
	}
	static nullStmtNode NULL = new nullStmtNode();
}

class nullStmtNode extends stmtNode {
	nullStmtNode() {}
	boolean   isNull() {return true;}
	void unparse(int indent) {}

	// null node do nothing
	void checkSemantics(){
		// do nothing
	}
} // class nullStmtNode 

class stmtsNode extends ASTNode {
	stmtsNode(stmtNode stmt, stmtsNode stmts, int line, int col) {
		super(line, col);
		thisStmt = stmt;
		moreStmts = stmts;
	}
	stmtsNode() {}

	void unparse(int indent) {
		thisStmt.unparse(indent);
		moreStmts.unparse(indent);
	} 


	void checkSemantics(){
		thisStmt.parentNodeName = this.parentNodeName;
		moreStmts.parentNodeName = this.parentNodeName;
		thisStmt.checkSemantics();
		moreStmts.checkSemantics();
	}

	void cg(){

		thisStmt.cg();
		
		if (!moreStmts.isNull()){
			moreStmts.cg();
		}
		
	}

	static nullStmtsNode NULL = new nullStmtsNode();
	private stmtNode thisStmt;
	private stmtsNode moreStmts;
} // class stmtsNode 

class nullStmtsNode extends stmtsNode {
	nullStmtsNode() {}
	boolean   isNull() {return true;}
	void unparse(int indent) {}

	// null node do nothing
	void checkSemantics(){
		// do nothing
	}
} // class nullStmtsNode 

// top

class printListNode extends stmtNode{
	printListNode() {}
	printListNode(printNode pn, int line, int col) {
		super(line, col);
		morePrints = pn;
	}

	void unparse(int indent){

	System.out.print("\n"+linenum + ": Print ");
	genIndent(indent);
	morePrints.unparse(0);

	}

	void checkSemantics(){

		morePrints.checkSemantics();
	}

	void cg(){
		morePrints.cg();
	}

	private printNode morePrints;
}

class printNode extends stmtNode {
	printNode() {}
	printNode(exprNode val, printNode p ,int line, int col) {
		super(line, col);
		outputValue = val;
		morePrints = p;
	}

	void unparse(int indent) {

		outputValue.unparse(0);
    }

    void checkSemantics(){
    	outputValue.checkSemantics();
    	//rule 12
    	// check for char array
    	if (outputValue.kind.val == Kinds.Array){
    		typeMustBe(outputValue.kind.val, Types.Character,
    			error() + "Only char arrays can be printed.");
    	}
    	// check the types 

    	else if (outputValue.type.val != Types.Character){
    		
    		if(outputValue.type.val != Types.Boolean) {
    			
    			if(outputValue.type.val != Types.Integer){    			 
    				
    				if (outputValue.type.val != Types.String){

			    		System.out.println(errorCol() + 
			    			"Prints must be of type int, "+
			    			"bool, char or string.");
		    				semanticsErrors++;
    				} //close string
    			}//close int	
			}//close bool
		}//close Char
    
		morePrints.checkSemantics();
    }

    void cg(){
    	// make sure the value to print is present
    	outputValue.cg();

    	if (outputValue.type.val == Types.String){
    		// use the CSX lib to print the string
    		gen("invokestatic CSXLib/printString(Ljava/lang/String;)V");
    	}
    	
    	else if (outputValue.type.val == Types.Integer) {
            gen("invokestatic","CSXLib/printInt(I)V");
        }

        else if (outputValue.type.val == Types.Boolean) {
            gen("invokestatic","CSXLib/printBool(Z)V");
        }

        else if (outputValue.type.val == Types.Character){

        	// char array
        	if (outputValue.kind.val == Kinds.Array || outputValue.kind.val == Kinds.ArrayParam){
        		 gen("invokestatic","CSXLib/printChar([C)V");
        	}
        	// just a char
        	else{
        		 gen("invokestatic","CSXLib/printChar(C)V");
        	}

        }

    	if (!morePrints.isNull())
    		morePrints.cg();
    }

    static 	nullPrintNode NULL = new nullPrintNode();
	private exprNode outputValue;
	private printNode morePrints;
} // class printNode 

class nullPrintNode extends printNode {
	nullPrintNode() {}
	boolean   isNull() {return true;}
	void unparse(int indent) {}
	// null node do nothing
	void checkSemantics(){
		// do nothing
	}
	//ast inherited
} // class nullPrintNode 

// Subclasses done for CG
abstract class typeNode extends ASTNode {
 // abstract superclass; only subclasses are actually created
		typeNode() {
			super();
		}
		// does not need a kind info
		typeNode(int l, int c) {
			super(l, c);
			type = new Types();
		}
		
		typeNode(int l, int c, Types t) {
			super(l, c);
			type = t;	
		}

		typeNode(Types t){
			type = t;
		}


		// CG not needed
		// Only uses the two returns either the
		// full name or the char.

		String getTypeShort(){return null;}
		String getTypeFull(){return null;}

		static nullTypeNode NULL = new nullTypeNode();
		Types type; 

	} // class typeNode

	class nullTypeNode extends typeNode {
		nullTypeNode() {
		}
		boolean isNull() {
			return true;
		}
		void unparse(int indent) {}

		// All type Nodes are type correct.
		void checkSemantics(){
		// do nothing
		}
	} // class nullTypeNode

	class intTypeNode extends typeNode {
		intTypeNode(int line, int col) {
			super(line, col, new Types(Types.Integer));
		}
		// print what the node is
		void unparse(int indent) {
			genIndent(indent);
			System.out.print("int");
		}

		// All type Nodes are type correct.
		void checkSemantics(){
		// do nothing
		}

		// Code gen info
			
			// for var decls
			String getTypeShort(){
				return "I";
			}

			// for arrays
			String getTypeFull(){
				return "int";
			}

	} // class intTypeNode

	class boolTypeNode extends typeNode {
		boolTypeNode(int line, int col) {
			super(line, col, new Types(Types.Boolean));
		}
		// print what the node is
		void unparse(int indent) {
			genIndent(indent);
			System.out.print("bool");
		}

		// All type Nodes are type correct.
		void checkSemantics(){
		// do nothing
		}

		// Code gen info
			
			// for var decls
			String getTypeShort(){
				return "Z";
				// Still don't know why it is Capital Z
				// For boolean
			}

			// for arrays
			String getTypeFull(){
				return "boolean";
			}

	} // class boolTypeNode

	class charTypeNode extends typeNode {
		charTypeNode(int line, int col) {
			super(line, col, new Types(Types.Character));
		}
		// print what the node is
		void unparse(int indent) {
			genIndent(indent);
			System.out.print("char");
		}

		// All type Nodes are type correct.
		void checkSemantics(){
		// do nothing
		}

		// Code gen info
			
			// for var decls
			String getTypeShort(){
				return "C";
			}

			// for arrays
			String getTypeFull(){
				return "char";
			}

	} // class charTypeNode

	// lots of changes to make this work
	class voidTypeNode extends typeNode {
		voidTypeNode() {
			super(new Types(Types.Void));
		} // voidTypeNode

		void unparse(int indent) {
				genIndent(indent);
				System.out.print("void");
			}

		void checkSemantics() {
			// No type checking needed
		} // checkSemantics

		// Code gen info
			
			// for var decls
			String getTypeShort(){
				return "V";
			}

			// for arrays
			String getTypeFull(){
				return "void";
			}
	
	} // class voidTypeNode 

// abstract superclass; only subclasses are actually created
abstract class exprNode extends ASTNode {
	exprNode() {
		super();
	}
	exprNode(int l, int c) {
		super(l, c);
		type = new Types();
		kind = new Kinds();

	}

	exprNode (int l, int c, Types t, Kinds k){
		super(l, c);
		type = t;
		kind = k;
	}

	exprNode (int l, int c, Types t, Kinds k, int lg){
		super(l, c);
		type = t;
		kind = k;
		length = lg;
	}

	public void setType(Types t){
		type = t;
	}

	// The two int's below probably could be redefined
	// but in this manner it makes more sense to me.

	// used to store the length of string
	public int length;

	// used to store the size of an array
	public int arr_size;

	final static nullExprNode NULL = new nullExprNode();
	protected Types type;
	protected Kinds kind;
}

class nullExprNode extends exprNode {
	nullExprNode() {
		super();
	}
	boolean   isNull() {return true;}
	void unparse(int indent) {}
	void checkSemantics(){}
	// ast Inherited
	// do nothing
} // class nullExprNode 

class nullArgsNode extends argsNode {
	nullArgsNode() {
		// empty constructor
	}
	boolean   isNull() {return true;}
	void unparse(int indent) {}

	// Null node 
	void checkSemantics(){
		// do nothing
		}
	// ast Inherited
} // class nullArgsNode 

class argsNode extends ASTNode {
	// actuals node
	argsNode() {}
	argsNode(exprNode e, argsNode a, int line, int col) {
		super(line, col);
		argVal = e;
		moreArgs = a;
	}

	void unparse(int indent) {
		argVal.unparse(0);
		if (moreArgs.isNull()){
			// do nothing
		}
		else {
			//System.out.print(", ");
			moreArgs.unparse(0);
		}
	}

	void checkSemantics(){


		// can be lambda but it doesn't matter the values still need
		// to be set.
		// that way it can be called in a procedure.
		
		argVal.parentNodeName = this.parentNodeName;
		moreArgs.parentNodeName = this.parentNodeName;

		argVal.checkSemantics();

		// This is where things need to be placed into
		SymbolInfo id_parent;

		id_parent = (SymbolInfo) st.globalLookup(argVal.parentNodeName);

		if(argVal.type != null && argVal.kind != null){
			id_parent.actualTypeList.add(argVal.type);
			id_parent.actualKindList.add(argVal.kind);
		}

			moreArgs.checkSemantics();		
	}

	void cg(){
		argVal.cg();
		moreArgs.cg();
	}

	static nullArgsNode NULL = new nullArgsNode();
	private exprNode argVal;
	private argsNode moreArgs;
} // class argsNode 

class blockNode extends stmtNode {
	blockNode(varDeclsNode f, stmtsNode s, int line, int col) {
		super(line, col);
		decls = f;
		stmts = s;
	}

	// print line where the block starts
	// print left brace '{'
	// call unparse on the inner nodes
	// print line num again to set up for 
	// close brace '}'
	// new line to keep clean
	// added statements to keep better track of the nodes

	void unparse(int indent){
		System.out.print("\n"+linenum + ": ");
 		// prints "{" with line number "-1" in "if".
		genIndent(indent);
		System.out.print("{ ");
		decls.unparse(indent+1);
		stmts.unparse(indent+1);
		System.out.print("\n" + linenum + ": ");
		genIndent(indent);
		System.out.print("}\n");
	}

	void checkSemantics(){
		// create new internal to the block scope
		st.openScope();

		decls.parentNodeName = this.parentNodeName;
		stmts.parentNodeName = this.parentNodeName;
		
		// check, varDecls
		decls.checkSemantics();

		stmts.checkSemantics();

		try{
		// end of block info
		// close the scope
			st.closeScope();
			
			} catch(EmptySTException e){
			// do nothing
		}
	}

	void cg(){
		decls.cg();
		stmts.cg();
	}

	private final varDeclsNode decls;
	private final stmtsNode stmts;
} // class blockNode 

 // need to work on
class strLitNode extends exprNode {
	// again using the overload
	strLitNode(String stringval, int line, int col) {
		super(line, col, new Types(Types.String), 
			new Kinds(Kinds.Value), stringval.length());
		strval = stringval;
		// copy of value with escaped chars
	}
	// print what the node is
	void unparse(int ident){
		System.out.print(strval);
	}

	// All literals are type correct.
	void checkSemantics(){
		// do nothing
	}

	void cg(){

		gen("ldc", strval);
	}

	private final String strval;
} // class strLitNode 

class intLitNode extends exprNode {
	intLitNode(int val, int line, int col) {
		super(line, col, new Types(Types.Integer), 
			new Kinds(Kinds.Value));
		intval = val;
	}
	// print what the node is
	void unparse(int indent){
		
		if (intval >= 0){
		
		System.out.print(intval);
		
		}
		
		else {
			// the value is negative and needs a ~
			System.out.print("~" + intval);
			}
	}

	// All literals are type correct.
	void checkSemantics(){
		// do nothing
	}

	// added to ease in getting the size of arrayDecls
	int returnIntVal(){
		return intval;
	}

	void cg(){
		gen("ldc", intval);
	}

	private final int intval;
} // class intLitNode 

class charLitNode extends exprNode {
	charLitNode(char val, int line, int col) {
		super(line, col, new Types(Types.Character),
		 new Kinds(Kinds.Value));
		charval = val;
	}
	// print what the node is
	void unparse(int indent){
		System.out.print(charval);
 // doesn't print character escape codes.  -1
	}

	// All literals are type correct.
	void checkSemantics(){
		// do nothing
	}

	void cg(){

		// set the char value to an int value
		// c_2_I Char to Int
		// This should not break due to CSX go only
		// allowing for ASCII values
		// reason for not casting is for trouble shooting

		int c_2_I = charval;

		gen("ldc", c_2_I);
	}

	private final char charval;
} // class charLitNode 

class trueNode extends exprNode {
	trueNode(int line, int col) {
		super(line, col, new Types(Types.Boolean),
		 new Kinds(Kinds.Value));
	}
	// print what the node is
	void unparse(int indent){
		System.out.print("true");
	}

	// All literals are type correct.
	void checkSemantics(){
		// do nothing
	}

	// from proj def
	void cg(){
		gen("iconst_1");
	}
} // class trueNode 

class falseNode extends exprNode {
	falseNode(int line, int col) {
		super(line, col, new Types(Types.Boolean),
		 new Kinds(Kinds.Value));
	}

	// print what the node is
	void unparse(int indent){
		System.out.print("false");
	}

	// All literals are type correct.
	void checkSemantics(){
		// do nothing
	}

	// from spec
	void cg(){
		gen("iconst_0");
	}
} // class falseNode 

class identNode extends exprNode {
	identNode(String identname, int line, int col) {
		super(line, col, new Types(Types.Unknown),
		 new Kinds(Kinds.Var));
		idname   = identname;
		nullFlag = false;
	}

	// from provided code
	// most of below is directly from the proved code
	identNode(boolean flag){
		super(0,0, new Types(Types.Unknown),
		 new Kinds(Kinds.Var));
		idname = "";
		nullFlag = flag;
	}

	boolean isNull() {
		return nullFlag;
	}

	// print what the node is
	void unparse(int indent) {
		System.out.print(idname);
	}

	static identNode NULL = new identNode(true);

	void checkSemantics(){
		// alot from given code

		SymbolInfo id;

		id = (SymbolInfo) st.localLookup(idname);
		
		// not found locally check global
		if (id == null){
			id = (SymbolInfo) st.globalLookup(idname);
		}

		if (id == null){
			// not found in either scope
			// error
			System.out.println(error()+ 
				"Variable not declared locally or globally.");
			semanticsErrors++;
			type = new Types(Types.Error);
		}
		// ident found
		else if (id != null) {
			type = id.type;
			kind = id.kind;
			arr_size = id.size;
			idinfo = id;
		}

		else {
			System.out.println(error() + "is not declared.");
			semanticsErrors++;
			type = new Types(Types.Error);
		}
		
	}

	//cg from astNode

	public String idname;
	public SymbolInfo idinfo;
	// unsure of use
	private final boolean nullFlag;
} // class identNode 

// =================================
// Break and continue below

// Not implemented for semantics checking but left in for future use
	class breakNode extends stmtNode {
		breakNode(identNode i, int line, int col) {
			super(line, col);
			label = i;
		}

		void unparse(int indent){

			// print linenum where found
			// print break
			// unparse the label where 
			// its going to 
			// print semi colon

			System.out.print("\n"+linenum + ":");
			genIndent(indent);
			System.out.print("break ");
			label.unparse(0);
			System.out.println(";");

		}

		private final identNode label;
	} // class breakNode 


	// Not implemented for semantics checking or code gen
	class continueNode extends stmtNode {
		continueNode(identNode i, int line, int col) {
			super(line, col);
			label = i;
		}

		void unparse(int indent){

			// print linenum where found
			// print continue
			// unparse the label where 
			// its going to 
			// print semi colon

			System.out.print("\n"+linenum + ":");
			genIndent(indent);
			System.out.print("continue ");
			label.unparse(0);
			System.out.println(";");

		}

		private final identNode label;
	} // class continueNode

// fin