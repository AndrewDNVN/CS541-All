import java.io.*;
import java.util.*;

abstract class ASTNode {
// abstract superclass; only subclasses are actually created

	int linenum;
	int colnum;

	static void genIndent(int indent) {
		for (int i = 1; i <= indent; i += 1) {
			System.out.print("\t");
		}
	} // genIndent

	ASTNode() {
		linenum = -1;
		colnum = -1;
	} // ASTNode()

	ASTNode(int line, int col) {
		linenum = line;
		colnum = col;
	} // ASTNode(line, col)

	boolean isNull() {
		return false; // often redefined in a subclass
	} // isNull()

	void Unparse(int indent) {
		// This routine is normally redefined in a subclass
	} // Unparse()
} // class ASTNode

class nullNode extends ASTNode {
// This class definition probably doesn't need to be changed
	nullNode() {
		super();
	}

	boolean isNull() {
		return true;
	}

	void Unparse(int indent) {
		// no action
	}
} // class nullNode

class programNode extends ASTNode {
	// this node is the root of CSX_go programs
	programNode(identNode id, varDeclsNode v_S, funcDeclsNode f_S, int line, int col){
		super(line, col);
		packageName = id;
		vars = v_S;
		funcs = f_S;
	}

	void Unparse(int indent){

		System.out.print(linenum + ": program ");
		packageName.Unparse(0);
		vars.Unparse(0);
		funcs.Unparse(0);
		System.out.print(" " + linenum + ": {EOF}\n");
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

	void Unparse(int indent){
		thisVar.Unparse(indent);
		moreVars.Unparse(indent);
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
	void Unparse(int indent) {}
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
	varDeclNode(identNode id, typeNode t, exprNode e, int line, int col) {
		super(line, col);
		varName = id;
		varType = t;
		initValue = e;
	}

	void Unparse(int indent){
		
		System.out.print("\n"+ linenum + ": ");
		genIndent(indent);
		System.out.print("var ");
		
		varName.Unparse(0);

		if (varType.isNull()){
			// varDecl sub-rule 2
			// the equals sign is here
			System.out.print(" = ");
			initValue.Unparse(0);
		}
		else {
			// varDecl sub-rule 1
			System.out.print(" ");
			varType.Unparse(0);
		}

		System.out.print(";");
	}

	private final identNode varName;
	private final typeNode varType;
	private final exprNode initValue;
} // class varDeclNode

class constDeclNode extends declNode {
	constDeclNode(identNode id,  exprNode e, int line, int col) {
		super(line, col);
		constName = id;
		constValue = e;
	}

	void Unparse(int indent){
		
		System.out.print("\n"+linenum + ": ");
		
		genIndent(indent);
		
		System.out.print("const ");
		constName.Unparse(0);
		
		System.out.print(" = ");
		constValue.Unparse(0);

		System.out.print(";");
	}


	private final identNode constName;
	private final exprNode constValue;
} // class constDeclNode

class arrayDeclNode extends declNode {
	arrayDeclNode(identNode id, typeNode t, intLitNode lit, int line, int col) {
		super(line, col);
		arrayName = id;
		elementType = t;
		arraySize = lit;
	}

	void Unparse(int indent){

		System.out.print("\n"+linenum + ": ");
		
		genIndent(indent);
		System.out.print("var ");
		arrayName.Unparse(0);
		System.out.print(" ");
		elementType.Unparse(0);
		System.out.print(" [");
		arraySize.Unparse(0);
		System.out.print("];");

	}

	private final identNode arrayName;
	private final typeNode elementType;
	private final intLitNode arraySize;
} // class arrayDeclNode

class funcDeclsNode extends ASTNode {
	funcDeclsNode(){
		super();
	}
	funcDeclsNode(funcDeclNode f, funcDeclsNode f_S, int line, int col) {
		super(line, col);
		thisDecl = f;
	 	moreDecls = f_S;
	}

	void Unparse (int indent){
		thisDecl.Unparse(0);
		moreDecls.Unparse(0);
	}
	
	static nullFuncDeclsNode NULL = new nullFuncDeclsNode();
	private funcDeclNode thisDecl;
	private funcDeclsNode moreDecls;
} // class funcDeclsNode 

class nullFuncDeclsNode extends funcDeclsNode {
	nullFuncDeclsNode() {}
	boolean   isNull() {return true;}
	void Unparse(int indent) {}
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

	void Unparse(int indent){

		System.out.print("\n"+linenum + ": func ");
		genIndent(indent);
		name.Unparse(0);
		System.out.print(" (");
		args.Unparse(0);
		System.out.print(") ");
		
		if (returnType.isNull()){
			// do nothing
		}
		else
		{
			// print return type
			returnType.Unparse(0);
		}
		// now call to print the block
		block.Unparse(0);

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
	argDeclsNode(argDeclNode arg, argDeclsNode args, int line, int col) {
		super(line, col);
		thisDecl = arg;
		moreDecls = args;
	}
	
	void Unparse(int indent){

		thisDecl.Unparse(0);
		if (moreDecls.isNull()){
			// do nothing
		}
		else{
			System.out.print(" , ");
			moreDecls.Unparse(0);
		}

	}

	static nullArgDeclsNode NULL = new nullArgDeclsNode();
	private argDeclNode thisDecl;
	private argDeclsNode moreDecls;
} // class argDeclsNode 

class nullArgDeclsNode extends argDeclsNode {
	nullArgDeclsNode() {}
	boolean   isNull() {return true;}
	void Unparse(int indent) {}
} // class nullArgDeclsNode 


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
	void Unparse(int indent) {}
} // class nullStmtNode 

class stmtsNode extends ASTNode {
	stmtsNode(stmtNode stmt, stmtsNode stmts, int line, int col) {
		super(line, col);
		thisStmt = stmt;
		moreStmts = stmts;
	}
	stmtsNode() {}

	void Unparse(int indent) {
		thisStmt.Unparse(indent);
		moreStmts.Unparse(indent);
	} 

	static nullStmtsNode NULL = new nullStmtsNode();
	private stmtNode thisStmt;
	private stmtsNode moreStmts;
} // class stmtsNode 

class nullStmtsNode extends stmtsNode {
	nullStmtsNode() {}
	boolean   isNull() {return true;}
	void Unparse(int indent) {}

} // class nullStmtsNode 

class arrayArgDeclNode extends argDeclNode {
	arrayArgDeclNode(identNode id, typeNode t, int line, int col) {
		super(line, col);
		argName = id;
		elementType = t;
	}

	void Unparse(int indent){
		genIndent(indent);
		argName.Unparse(0);
		System.out.print(" [ ] ");
		elementType.Unparse(0);
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

	void Unparse(int indent){
		genIndent(indent);
		argName.Unparse(0);
		System.out.print(" ");
		argType.Unparse(0);
	}

	private final identNode argName;
	private final typeNode argType;
} // class valArgDeclNode 


class ifNode extends stmtNode {
	ifNode(exprNode e, blockNode b, int line, int col) {
		super(line, col);
		condition = e;
		thenPart = b;
	}

	void Unparse(int indent) {
		System.out.print("\n" + linenum + ":");
		genIndent(indent);
		System.out.print("if (");
		condition.Unparse(0);
		System.out.println(")");
		thenPart.Unparse(indent+1);

	}

	private final exprNode condition;
	private final blockNode thenPart;
} // class ifNode


class ifThenNode extends stmtNode {
	ifThenNode(exprNode e, blockNode b1, blockNode b2, int line, int col) {
		super(line, col);
		condition = e;
		thenPart = b1;
		elsePart = b2;
	}

	void Unparse(int indent) {
		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		System.out.print("if (");
		condition.Unparse(0);
		System.out.println(")");
		thenPart.Unparse(indent+1);
		genIndent(indent);
		System.out.print("else ");
		elsePart.Unparse(indent+1);

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

	void Unparse(int indent){

		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		System.out.print(" for ");
		condition.Unparse(0);
		genIndent(indent+1);
		loopBody.Unparse(0);
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

	void Unparse(int indent) {
		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		target.Unparse(0);
		System.out.print(" = ");
		source.Unparse(0);
		System.out.println(";");
	}

	private final nameNode target;
	private final exprNode source;
} // class asgNode 

class readNode extends stmtNode {
	readNode() {}
	readNode(nameNode n, readNode rn, int line, int col) {
		super(line, col);
		 targetVar = n;
		 moreReads = rn;
	}

	void Unparse(int indent) {

		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		System.out.print("(col) "+colnum+"-> read: ");
        targetVar.Unparse(indent);

        if (moreReads.isNull()) {
        	// do nothing
        }
        
        else {

            //System.out.print(", ");
            moreReads.Unparse(indent);
        }
    }

	static nullReadNode NULL = new nullReadNode();
	private nameNode targetVar;
	private readNode moreReads;
} // class readNode 

class nullReadNode extends readNode {
	nullReadNode() {}
	boolean   isNull() {return true;}
	void Unparse(int indent) {}
} // class nullReadNode 

class displayNode extends stmtNode {
	displayNode() {}
	displayNode(exprNode val, displayNode pn, int line, int col) {
		super(line, col);
		outputValue = val;
		moreDisplays = pn;
	}

	void Unparse(int indent) {

		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		System.out.print("(col) "+ colnum +"-> print : ");
        outputValue.Unparse(0);

        if (moreDisplays.isNull()) {
        	// do nothing
        }
        
        else {

            //System.out.print(", ");
            moreDisplays.Unparse(indent);
        }
    }


	static nullDisplayNode NULL = new nullDisplayNode();
	private exprNode outputValue;
	private displayNode moreDisplays;

} // class displayNode 

class nullDisplayNode extends displayNode {
	nullDisplayNode() {}
	boolean   isNull() {return true;}
	void Unparse(int indent) {}
} // class nullDisplayNode 

class callNode extends stmtNode {
	callNode(nameNode id, argsNode a, int line, int col) {
		super(line, col);
		funcName = id;
		args = a;
	}

	
	void Unparse(int indent){

		// print linenum where found
		// print function name then (
		// print all the internal args then );

		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		funcName.Unparse(0);
		System.out.print(" ( ");
		args.Unparse(0);
		System.out.print(" );");

	}


	private final nameNode funcName;
	private final argsNode args;
} // class callNode 



class returnNode extends stmtNode {
	returnNode(exprNode e, int line, int col) {
		super(line, col);
		returnVal = e;
	}

	void Unparse(int indent){

		// print linenum where found
		// print return
		// Unparse the the name of the  
		// value returning to 
		// print semi colon

		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		System.out.print("return ");
		returnVal.Unparse(0);
		System.out.println(";");

	}

	private final exprNode returnVal;
} // class returnNode 

class breakNode extends stmtNode {
	breakNode(identNode i, int line, int col) {
		super(line, col);
		label = i;
	}

	void Unparse(int indent){

		// print linenum where found
		// print break
		// Unparse the label where 
		// its going to 
		// print semi colon

		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		System.out.print("break ");
		label.Unparse(0);
		System.out.println(";");

	}

	private final identNode label;
} // class breakNode 

class continueNode extends stmtNode {
	continueNode(identNode i, int line, int col) {
		super(line, col);
		label = i;
	}

	void Unparse(int indent){

		// print linenum where found
		// print continue
		// Unparse the label where 
		// its going to 
		// print semi colon

		System.out.print("\n"+linenum + ":");
		genIndent(indent);
		System.out.print("continue ");
		label.Unparse(0);
		System.out.println(";");

	}

	private final identNode label;
} // class continueNode 


class blockNode extends stmtNode {
	blockNode(varDeclsNode f, stmtsNode s, int line, int col) {
		super(line, col);
		decls = f;
		stmts = s;
	}

	// print line where the block starts
	// print left brace '{'
	// call Unparse on the inner nodes
	// print line num again to set up for 
	// close brace '}'
	// new line to keep clean
	// added statements to keep better track of the nodes

	void Unparse(int indent){
		System.out.print("\n"+linenum + ": ");
		genIndent(indent);
		System.out.print("{ ");
		decls.Unparse(indent+1);
		stmts.Unparse(indent+1);
		System.out.print("\n" + linenum + ": ");
		genIndent(indent);
		System.out.print("}\n");

	}

	private final varDeclsNode decls;
	private final stmtsNode stmts;
} // class blockNode 

	abstract class typeNode extends ASTNode {
	// abstract superclass; only subclasses are actually created
		typeNode() {
			super();
		}
		typeNode(int l, int c) {
			super(l, c);
		}
		static nullTypeNode NULL = new nullTypeNode();
	} // class typeNode

	class nullTypeNode extends typeNode {
		nullTypeNode() {}
		boolean isNull() {
			return true;
		}
		void Unparse(int indent) {}
	} // class nullTypeNode

	class intTypeNode extends typeNode {
		intTypeNode(int line, int col) {
			super(line, col);
		}
		// print what the node is
		void Unparse(int indent) {
			genIndent(indent);
			System.out.print("int");
		}
	} // class intTypeNode

	class boolTypeNode extends typeNode {
		boolTypeNode(int line, int col) {
			super(line, col);
		}
		// print what the node is
		void Unparse(int indent) {
			genIndent(indent);
			System.out.print("bool");
		}
	} // class boolTypeNode

	class charTypeNode extends typeNode {
		charTypeNode(int line, int col) {
			super(line, col);
		}
		// print what the node is
		void Unparse(int indent) {
			genIndent(indent);
			System.out.print("char");
		}

	} // class charTypeNode

	// Void Type node Removed

class argsNode extends ASTNode {
	argsNode() {}
	argsNode(exprNode e, argsNode a, int line, int col) {
		super(line, col);
		argVal = e;
		moreArgs = a;
	}

	void Unparse(int indent) {
		argVal.Unparse(0);
		if (moreArgs.isNull()){
			// do nothing
		}
		else {
			//System.out.print(", ");
			moreArgs.Unparse(0);
		}
	}

	static nullArgsNode NULL = new nullArgsNode();
	private exprNode argVal;
	private argsNode moreArgs;
} // class argsNode 

class nullArgsNode extends argsNode {
	nullArgsNode() {
		// empty constructor
	}
	boolean   isNull() {return true;}
	void Unparse(int indent) {}
} // class nullArgsNode 



// abstract superclass; only subclasses are actually created
abstract class exprNode extends ASTNode {
	exprNode() {
		super();
	}
	exprNode(int l, int c) {
		super(l, c);
	}
	static nullExprNode NULL = new nullExprNode();
}

class nullExprNode extends exprNode {
	nullExprNode() {
		super();
	}
	boolean   isNull() {return true;}
	void Unparse(int indent) {}
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

	void Unparse(int indent) {
		System.out.print("(");
		leftOperand.Unparse(0);
		printOp(operatorCode);
		rightOperand.Unparse(0);
		System.out.print(")");
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
	void Unparse(int indent) {
		System.out.print("(");
		printOp(operatorCode);
		System.out.print(" ");
		operand.Unparse(0);
		System.out.print(")");
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

	void Unparse(int indent){

		resultType.Unparse(0);		
		System.out.print("(");
		operand.Unparse(0);
		System.out.print(")");
	}

	private final exprNode operand;
	private final typeNode resultType;
} // class castNode 

class fctCallNode extends exprNode {
	fctCallNode(nameNode id, argsNode a, int line, int col) {
		super(line, col);
		funcName = id;
		funcArgs = a;
	}

	void Unparse(int indent){
		
		funcName.Unparse(0);
		System.out.print(" ( ");
		
		if (funcArgs.isNull()) {
			// Do nothing
		}
		
		else {
			funcArgs.Unparse(0);
		}

		System.out.print(" ) ");

	}

	private final nameNode funcName;
	private final argsNode funcArgs;
} // class fctCallNode 


class strLitNode extends exprNode {
	strLitNode(String stringval, int line, int col) {
		super(line, col);
		strval = stringval;
	}
	// print what the node is
	void Unparse(int ident){
		System.out.print(strval);
	}

	private final String strval;
} // class strLitNode 

class intLitNode extends exprNode {
	intLitNode(int val, int line, int col) {
		super(line, col);
		intval = val;
	}
	// print what the node is
	void Unparse(int indent){
		System.out.print(intval);
	}

	private final int intval;
} // class intLitNode 

class charLitNode extends exprNode {
	charLitNode(char val, int line, int col) {
		super(line, col);
		charval = val;
	}
	// print what the node is
	void Unparse(int indent){
		System.out.print(charval);
	}

	private final char charval;
} // class charLitNode 

class trueNode extends exprNode {
	trueNode(int line, int col) {
		super(line, col);
	}
	// print what the node is
	void Unparse(int indent){
		System.out.print("true");
	}
} // class trueNode 

class falseNode extends exprNode {
	falseNode(int line, int col) {
		super(line, col);
	}
	// print what the node is
	void Unparse(int indent){
		System.out.print("false");
	}
} // class falseNode 

class nameNode extends exprNode {
	nameNode(identNode id, exprNode expr, int line, int col) {
		super(line, col);
		varName = id;
		subscriptVal = expr;
	}
	// print what the node is
	void Unparse(int indent) {
		varName.Unparse(0); 
		if(subscriptVal.isNull()){
			// do nothing
		}
		else {
			// name rule 2 
			// ident [ expr ]
			subscriptVal.Unparse(0);
		}
	}

	private final identNode varName;
	private final exprNode subscriptVal;
} // class nameNode 

class identNode extends exprNode {
	identNode(String identname, int line, int col) {
		super(line, col);
		idname   = identname;
	}
	// print what the node is
	void Unparse(int indent) {
		System.out.print(idname);
	}

	private final String idname;
} // class identNode 