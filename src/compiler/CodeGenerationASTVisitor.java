package compiler;

import compiler.AST.*;
import compiler.lib.*;
import compiler.exc.*;

import java.util.ArrayList;
import java.util.List;

import static compiler.lib.FOOLlib.*;

public class CodeGenerationASTVisitor extends BaseASTVisitor<String, VoidException> {

	CodeGenerationASTVisitor() {}
	CodeGenerationASTVisitor(boolean debug) {super(false,debug);} //enables print for debugging
	List<List<String>> dispatchTables = new ArrayList<>();
	final static int MEMSIZE = 10000;
	@Override
	public String visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		String declCode = null;
		for (Node dec : n.declist) declCode=nlJoin(declCode,visit(dec));
		return nlJoin(
				"push 0",
				declCode, // generate code for declarations (allocation)
				visit(n.exp),
				"halt",
				getCode()
		);
	}

	@Override
	public String visitNode(ProgNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.exp),
				"halt"
		);
	}

	//TODO
	@Override
	public String visitNode(ClassNode n) {
		List<String> dispatchTable;

		if(n.superId != null){

			dispatchTable = new ArrayList<>(dispatchTables.get(-n.superEntry.offset-2));
		}
		else {
			dispatchTable = new ArrayList<>();
		}
		dispatchTables.add(dispatchTable);
		for (MethodNode method : n.methods){
			visitNode(method);
			String label = method.label;
			int offset = method.offset;


			if(offset < dispatchTable.size()){
				dispatchTable.set(offset, label);
			}
			else {
				dispatchTable.add(offset, label);
			}
		}


		String saveDispatchTableHeap = null;

		for (String label : dispatchTable){
			saveDispatchTableHeap = nlJoin(
					saveDispatchTableHeap,
					"push "+label,
					"lhp",
					"sw",
					"lhp", //incremento valore di hp
					"push 1",
					"add",
					"shp" //salvo il nuovo valore di hp
					);
		}


		return
				nlJoin(
					"lhp", //load $hp sullo stack
						saveDispatchTableHeap
				);
	}

	//TODO: check if return null
	@Override
	public String visitNode(MethodNode n) {
		if (print) printNode(n,n.id);
		String declCode = null, popDecl = null, popParl = null;
		for (Node dec : n.declist) {
			declCode = nlJoin(declCode,visit(dec));
			popDecl = nlJoin(popDecl,"pop");
		}
		for (int i=0;i<n.parlist.size();i++) popParl = nlJoin(popParl,"pop");
		n.label = freshLabel();
		putCode(
				nlJoin(
						n.label+":",
						"cfp", // set $fp to $sp value
						"lra", // load $ra value
						declCode, // generate code for local declarations (they use the new $fp!!!)
						visit(n.exp), // generate code for function body expression
						"stm", // set $tm to popped value (function result)
						popDecl, // remove local declarations from stack
						"sra", // set $ra to popped value
						"pop", // remove Access Link from stack
						popParl, // remove parameters from stack
						"sfp", // set $fp to popped value (Control Link)
						"ltm", // load $tm value (function result)
						"lra", // load $ra value
						"js"  // jump to to popped address
				)
		);
		//return void code
		return null;
	}

	@Override
	public String visitNode(EmptyNode n) {
		if (print) printNode(n);
		return nlJoin(
				"push -1"
		);
	}


	@Override
	public String visitNode(ClassCallNode n) {
		if (print) printNode(n);
		String argCode = null, getAR = null;
		for (int i=n.arglist.size()-1;i>=0;i--) argCode=nlJoin(argCode,visit(n.arglist.get(i)));
		for (int i = 0;i<n.nl-n.entry.nl;i++) getAR=nlJoin(getAR,"lw");
		return nlJoin(
				"lfp", // load Control Link (pointer to frame of function "id" caller)
				argCode, // generate code for argument expressions in reversed order

				"lfp", getAR, // retrieve address of frame containing "id1" declaration
				// by following the static chain (of Access Links)
				"push "+n.entry.offset, "add", // compute address of "id1" declaration
				"lw", // load object pointer

				"stm", // set $tm to popped value (with the aim of duplicating top of stack)
				"ltm", // load Access Link (pointer to frame of function "id" declaration)
				"ltm", // duplicate top of stack

				"lw", //load dispatch pointer(dispatch table address)

				"push "+n.methodEntry.offset, "add", //calculate the right method address by adding offset
				"lw", // load address of method "id2"
				"js"  // jump to popped address (saving address of subsequent instruction in $ra)
		);

	}

	//TODO
	@Override
	public String visitNode(NewNode n) {
		if (print) printNode(n,n.id);
		String argCode = null;
		for (int i=n.expList.size()-1;i>=0;i--) argCode=nlJoin(argCode,visit(n.expList.get(i)));
		String moveElementToHeap = null;
		for (int i=0; i<=n.expList.size(); i++) {
			moveElementToHeap = nlJoin(moveElementToHeap,
					"lhp", //load hp value on the stack
					"sw",  // save element in memory hp
					"lhp", "push 1", "add", "shp" //incremento valore di hp e salvo il nuovo valore di hp
			);
		}

		var address = MEMSIZE + n.entry.offset;

		return nlJoin(argCode,
				moveElementToHeap,
				"push " + address,
				"lw", //load the dispatch pointer
				"lhp", //load hp value on the stack
				"sw", // write in the heap the dispatch pointer

				"lhp", //load hp value on the stack
				"lhp", "push 1", "add", "shp" //incremento valore di hp e salvo il nuovo valore di hp

				);
	}

	@Override
	public String visitNode(FunNode n) {
		if (print) printNode(n,n.id);
		String declCode = null, popDecl = null, popParl = null;
		for (Node dec : n.declist) {
			declCode = nlJoin(declCode,visit(dec));
			popDecl = nlJoin(popDecl,"pop");
		}
		for (int i=0;i<n.parlist.size();i++) popParl = nlJoin(popParl,"pop");
		String funl = freshFunLabel();
		putCode(
				nlJoin(
						funl+":",
						"cfp", // set $fp to $sp value
						"lra", // load $ra value
						declCode, // generate code for local declarations (they use the new $fp!!!)
						visit(n.exp), // generate code for function body expression
						"stm", // set $tm to popped value (function result)
						popDecl, // remove local declarations from stack
						"sra", // set $ra to popped value
						"pop", // remove Access Link from stack
						popParl, // remove parameters from stack
						"sfp", // set $fp to popped value (Control Link)
						"ltm", // load $tm value (function result)
						"lra", // load $ra value
						"js"  // jump to to popped address
				)
		);
		return "push "+funl;
	}

	@Override
	public String visitNode(VarNode n) {
		if (print) printNode(n,n.id);
		return visit(n.exp);
	}

	@Override
	public String visitNode(PrintNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.exp),
				"print"
		);
	}

	@Override
	public String visitNode(IfNode n) {
		if (print) printNode(n);
		String l1 = freshLabel();
		String l2 = freshLabel();
		return nlJoin(
				visit(n.cond),
				"push 1",
				"beq "+l1,
				visit(n.el),
				"b "+l2,
				l1+":",
				visit(n.th),
				l2+":"
		);
	}

	@Override
	public String visitNode(EqualNode n) {
		if (print) printNode(n);
		String l1 = freshLabel();
		String l2 = freshLabel();
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"beq "+l1,
				"push 0",
				"b "+l2,
				l1+":",
				"push 1",
				l2+":"
		);
	}

	@Override
	public String visitNode(TimesNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"mult"
		);
	}

	@Override
	public String visitNode(PlusNode n) {
		if (print) printNode(n);
		return nlJoin(
				visit(n.left),
				visit(n.right),
				"add"
		);
	}


	@Override
	public String visitNode(CallNode n) {
		if (print) printNode(n,n.id);
		String argCode = null, getAR = null;
		for (int i=n.arglist.size()-1;i>=0;i--) argCode=nlJoin(argCode,visit(n.arglist.get(i)));
		for (int i = 0;i<n.nl-n.entry.nl;i++) getAR=nlJoin(getAR,"lw");

		if(n.entry.offset >= 0){ //chiamata di un metodo dentro un metodo di una classe
			return nlJoin(
					"lfp", // load Control Link (pointer to frame of function "id" caller)
					argCode, // generate code for argument expressions in reversed order
					"lfp", getAR, // retrieve address of frame containing "id" declaration
					// by following the static chain (of Access Links)
					"stm", // set $tm to popped value (with the aim of duplicating top of stack)
					"ltm", // load Access Link (pointer to frame of function "id" declaration)
					"ltm", // duplicate top of stack

					"lw", //load object pointer -> dispatch pointer
					"push "+n.entry.offset, "add", // compute address of "id" declaration
					"lw", // load address of "id" function
					"js"  // jump to popped address (saving address of subsequent instruction in $ra)
			);
		}
		else {
			return nlJoin(
					"lfp", // load Control Link (pointer to frame of function "id" caller)
					argCode, // generate code for argument expressions in reversed order
					"lfp", getAR, // retrieve address of frame containing "id" declaration
					// by following the static chain (of Access Links)
					"stm", // set $tm to popped value (with the aim of duplicating top of stack)
					"ltm", // load Access Link (pointer to frame of function "id" declaration)
					"ltm", // duplicate top of stack
					"push "+n.entry.offset, "add", // compute address of "id" declaration
					"lw", // load address of "id" function
					"js"  // jump to popped address (saving address of subsequent instruction in $ra)
			);
		}

	}

	@Override
	public String visitNode(IdNode n) {
		if (print) printNode(n,n.id);
		String getAR = null;
		for (int i = 0;i<n.nl-n.entry.nl;i++) getAR=nlJoin(getAR,"lw");
		return nlJoin(
				"lfp", getAR, // retrieve address of frame containing "id" declaration
				// by following the static chain (of Access Links)
				"push "+n.entry.offset, "add", // compute address of "id" declaration
				"lw" // load value of "id" variable
		);
	}

	@Override
	public String visitNode(BoolNode n) {
		if (print) printNode(n,n.val.toString());
		return "push "+(n.val?1:0);
	}

	@Override
	public String visitNode(IntNode n) {
		if (print) printNode(n,n.val.toString());
		return "push "+n.val;
	}
}