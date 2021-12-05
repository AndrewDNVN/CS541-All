// start of AST.java

import java.util.*;
// for the iterator class

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


	// This is book style.
	// this style is used because of the parsing style implemented
	// I was not able to re-build into a list style in time.
	// In short it is used to set the name of the initial call
	// and reference that as needed to be able to move back up


	// save the name of the parent node
	protected String parentNodeName;
} // abstract class ASTNode

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

	private final identNode varName;
	private final exprNode initValue;
} // class varDeclNode

class constDeclNode extends declNode {
	constDeclNode(identNode id,  exprNode e, int line, int col) {
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


	private final identNode constName;
	private final exprNode constValue;
} // class constDeclNode

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

	private final identNode name;
	private final argDeclsNode args;
	private final typeNode returnType;
	private final blockNode block;
} // class funcDeclNode 

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

	private final exprNode condition;
	private final blockNode thenPart;
	private final blockNode elsePart;
} // class ifThenNode 

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

	private final exprNode condition;
	private final blockNode loopBody;
} // class forNode 

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

	private final nameNode target;
	private final exprNode source;
} // class asgNode 

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

	static nullReadNode NULL = new nullReadNode();
	private nameNode targetVar;
	private readNode moreReads;
} // class readNode 

class nullReadNode extends readNode {
	nullReadNode() {}
	boolean   isNull() {return true;}
	void unparse(int indent) {}
	// null node do nothing
	void checkSemantics(){
		// do nothing
	}
} // class nullReadNode 

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
} // class nullPrintNode 

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

	private final varDeclsNode decls;
	private final stmtsNode stmts;
} // class blockNode 

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
} // class voidTypeNode 

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

	static nullArgsNode NULL = new nullArgsNode();
	private exprNode argVal;
	private argsNode moreArgs;
} // class argsNode 

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
	// do nothing
} // class nullExprNode 

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



	private final exprNode operand;
	private final int operatorCode; // Token code of the operator
} // class unaryOpNode 

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

// need to work on
class strLitNode extends exprNode {
	// again using the overload
	strLitNode(String stringval, int line, int col) {
		super(line, col, new Types(Types.String), 
			new Kinds(Kinds.Value), stringval.length());
		strval = stringval;
		// copy of value with escaped chars
		fullstr = stringval;
	}
	// print what the node is
	void unparse(int ident){
		System.out.print(strval);
	}

	// All literals are type correct.
	void checkSemantics(){
		// do nothing
	}

	private final String fullstr;
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
} // class falseNode 

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

	public final identNode varName;
	private final exprNode subscriptVal;
} // class nameNode 

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

	public String idname;
	public SymbolInfo idinfo;
	// unsure of use
	private final boolean nullFlag;
} // class identNode 


// =================================

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


// Not implemented for semantics checking
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