package compiler;

import java.util.*;
import compiler.AST.*;
import compiler.exc.*;
import compiler.lib.*;

public class SymbolTableASTVisitor extends BaseASTVisitor<Void,VoidException> {

	private List<Map<String, STentry>> symTable = new ArrayList<>();
	private Map< String, Map<String,STentry> >  classTable = new HashMap<>();
	private int nestingLevel=0; // current nesting level
	private int decOffset=-2; // counter for offset of local declarations at current nesting level
	private int cldecOffset = -2;
	int stErrors=0;

	SymbolTableASTVisitor() {}
	SymbolTableASTVisitor(boolean debug) {super(debug);} // enables print for debugging

	private STentry stLookup(String id) {
		int j = nestingLevel;
		STentry entry = null;
		while (j >= 0 && entry == null)
			entry = symTable.get(j--).get(id);
		return entry;
	}

	@Override
	public Void visitNode(ProgLetInNode n) {
		if (print) printNode(n);
		Map<String, STentry> hm = new HashMap<>();
		symTable.add(hm);
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		symTable.remove(0);
		return null;
	}


	@Override
	public Void visitNode(ProgNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(ClassNode n) {
		if (print) printNode(n);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		Map<String, STentry> virtualTable = new HashMap<>();
		var classTypeNode = new ClassTypeNode();
		STentry classEntry = new STentry(nestingLevel, classTypeNode, cldecOffset--);

		if (hm.put(n.id, classEntry) != null) {
			System.out.println("Class id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}

		if (n.superId != null) {
			n.superEntry = symTable.get(0).get(n.superId);
			var superClassTypeNode = (ClassTypeNode) n.superEntry.type;
			classTypeNode.allFields.addAll(superClassTypeNode.allFields);
			classTypeNode.allMethods.addAll(superClassTypeNode.allMethods);

			var superClassVirtualTable = classTable.get(n.superId);
			virtualTable.putAll(superClassVirtualTable);
		}
		var fieldOffset = -classTypeNode.allFields.size() - 1;
		var methodOffset = classTypeNode.allMethods.size();

		classTable.put(n.id, virtualTable);
		symTable.add(virtualTable);

		nestingLevel++;
		for (FieldNode field : n.fields) {
			if (virtualTable.containsKey(field.id)) {
				var oldFieldOffset = virtualTable.get(field.id).offset;
				var newSTentry = new STentry(nestingLevel, field.getType(), oldFieldOffset);
				classTypeNode.allFields.set(-oldFieldOffset - 1, field.getType());
				virtualTable.put(field.id, newSTentry);
			}
			else {
				var newSTentry = new STentry(nestingLevel, field.getType(), fieldOffset--);
				classTypeNode.allFields.add(field.getType());
				virtualTable.put(field.id, newSTentry);
			}
		}
		//TODO: Spostare modifica della virtual table nella visit del methodNode
		// (classTypeNode modificato dopo la visita del metodo all'interno della classNode)
		for (MethodNode method : n.methods) {
			if (virtualTable.containsKey(method.id)) {
				var oldMethodOffset = virtualTable.get(method.id).offset;
				var newSTentry = new STentry(nestingLevel, method.getType(), oldMethodOffset);
				virtualTable.put(method.id, newSTentry);
				classTypeNode.allMethods.add((ArrowTypeNode) method.getType());
				method.offset = oldMethodOffset;
			}
			else {
				var newSTentry = new STentry(nestingLevel, method.getType(), methodOffset);
				virtualTable.put(method.id, newSTentry);
				classTypeNode.allMethods.add((ArrowTypeNode) method.getType());
				method.offset = methodOffset++;
			}
			visit(method);
		}


		symTable.remove(nestingLevel--);
		return null;
	}
	/*

	//TODO Visit Class Node
	@Override
	public Void visitNode(ClassNode n) {
		if (print) printNode(n);
		//referenza a symbol table
		Map<String, STentry> hm = symTable.get(nestingLevel);
		var node = new ClassTypeNode();
		STentry entry = new STentry(nestingLevel, node, decOffset--);
		var extendedNames = new HashSet<>();

		Map<String, STentry> virtualTable = new HashMap<>();
		classTable.put(n.id, virtualTable);

		var virtualFieldOffset = -1;
		var virtualMethOffset = 0;

		if (n.superId != null){
			// updating the superType map
			superType.put(n.id, n.superId);

			//otteniamo le classi che stanno a nesting level 0
			n.superEntry = symTable.get(0).get(n.superId);
			ClassTypeNode extendedType = (ClassTypeNode) n.superEntry.type;

			virtualFieldOffset = -extendedType.allFields.size() - 1;
			virtualMethOffset = extendedType.allMethods.size();

			node.allFields = extendedType.allFields;
			node.allMethods = extendedType.allMethods;
			extendedNames.add(extendedType.allFields);
			extendedNames.add(extendedType.allMethods);

			var extendedVirtualTable = classTable.get(n.superId);
			virtualTable.putAll(extendedVirtualTable);
		}

		// create new scope for class body
		nestingLevel++;
		for (FieldNode field : n.fieldList){
			if (extendedNames.contains(field)) {
				if (node.allFields.contains(field)) {
					var oldFieldEntry = virtualTable.get(field.id);
					var oldOffset = oldFieldEntry.offset;
					virtualTable.put(field.id, new STentry(nestingLevel, field.getType(), oldOffset));
					// node.allFields.remove(oldFieldEntry.type);
				}
				else {
					System.out.println("Field id " + field.id + " at line " + n.getLine() + " already declared");
					stErrors++;
				}
			}
			extendedNames.add(field);
			node.allFields.add(field.getType());
			virtualTable.put(field.id, new STentry(nestingLevel, field.getType(), virtualFieldOffset--));
		}

		for(MethodNode method : n.methodList){
			List<TypeNode> parTypes = new ArrayList<>();
			for (ParNode par : method.parlist) parTypes.add(par.getType());
			var methodType = new ArrowTypeNode(parTypes, method.retType);
			if (extendedNames.contains(method))
				if (node.allMethods.contains(method)) {
					var oldMethodEntry = virtualTable.get(method.id);
					var oldOffset = oldMethodEntry.offset;
					virtualTable.put(method.id, new STentry(nestingLevel, methodType, oldOffset));
					// node.allMethods.remove(oldMethodEntry.type);
				}
				else {
					System.out.println("Method id " + method.id + " at line " + n.getLine() + " already declared");
					stErrors++;
				}
			extendedNames.add(method);
			node.allMethods.add(methodType);
			virtualTable.put(method.id, new STentry(nestingLevel, methodType, virtualMethOffset++));
			visit(method);
		}

		// insert class ID into symbol table
		if (hm.put(n.id, entry) != null) {
			System.out.println("Class id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}

		symTable.add(virtualTable);
		// remove current scope's hashmap when exiting scope
		symTable.remove(nestingLevel--);
		return null;
	}
	 */


	//TODO Visit Method Node
	@Override
	public Void visitNode(MethodNode n) {
		if (print) printNode(n);
		//referenza a symbol table
		Map<String, STentry> hm = symTable.get(nestingLevel);
		List<TypeNode> parTypes = new ArrayList<>();
		STentry entry = new STentry(nestingLevel, new ArrowTypeNode(parTypes, n.retType), decOffset--);

		for (ParNode par : n.parlist)
			parTypes.add(par.getType());

		// insert method ID into symbol table
		if (hm.put(n.id, entry) != null) {
			System.out.println("Method id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}
		// create new scope for function body
		nestingLevel++;
		Map<String, STentry> hmn = new HashMap<>();
		symTable.add(hmn);
		int prevNLDecOffset = decOffset; // stores counter for offset of declarations at previous nesting level
		decOffset=-2;
		int parOffset=1;

		for (ParNode par : n.parlist)
			if (hmn.put(par.id, new STentry(nestingLevel, par.getType(),parOffset++)) != null) {
				System.out.println("Par id " + par.id + " at line "+ n.getLine() +" already declared");
				stErrors++;
			}
		for (Node dec : n.declist)
			visit(dec);
		visit(n.exp);
		// remove current scope's hashmap when exiting scope
		symTable.remove(nestingLevel--);
		decOffset = prevNLDecOffset; // restores counter for offset of declarations at previous nesting level
		return null;
	}

	//TODO Visit Empty Node
	@Override
	public Void visitNode(EmptyNode n) {
		if (print) printNode(n);
		return null;
	}

	//TODO Visit New Node
	@Override
	public Void visitNode(NewNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Class id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.entry = entry;
			n.nl = nestingLevel;
		}
		for (Node arg : n.expList) visit(arg);
		return null;
	}

	//TODO Visit ClassCall Node
	@Override
	public Void visitNode(ClassCallNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.idClass);
		if (entry == null) {
			System.out.println("Class id " + n.idClass + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.entry = entry;
			n.nl = nestingLevel;
		}
		// per ogni argomento del metodo li visita
		for (Node arg : n.arglist) visit(arg);
		return null;
	}

	@Override
	public Void visitNode(FunNode n) {
		if (print) printNode(n);
		//referenza a symbol table
		Map<String, STentry> hm = symTable.get(nestingLevel);
		List<TypeNode> parTypes = new ArrayList<>();
		for (ParNode par : n.parlist) parTypes.add(par.getType());
		STentry entry = new STentry(nestingLevel, new ArrowTypeNode(parTypes,n.retType),decOffset--);
		// insert function ID into symbol table
		if (hm.put(n.id, entry) != null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}
		// create new scope for function body
		nestingLevel++;
		Map<String, STentry> hmn = new HashMap<>();
		symTable.add(hmn);
		int prevNLDecOffset=decOffset; // stores counter for offset of declarations at previous nesting level 
		decOffset=-2;

		int parOffset=1;
		for (ParNode par : n.parlist)
			if (hmn.put(par.id, new STentry(nestingLevel, par.getType(),parOffset++)) != null) {
				System.out.println("Par id " + par.id + " at line "+ n.getLine() +" already declared");
				stErrors++;
			}
		for (Node dec : n.declist) visit(dec);
		visit(n.exp);
		// remove current scope's hashmap when exiting scope               
		symTable.remove(nestingLevel--);
		decOffset=prevNLDecOffset; // restores counter for offset of declarations at previous nesting level 
		return null;
	}

	@Override
	public Void visitNode(VarNode n) {
		if (print) printNode(n);
		visit(n.exp);
		Map<String, STentry> hm = symTable.get(nestingLevel);
		STentry entry = new STentry(nestingLevel, n.getType(),decOffset--);
		// insert variable ID into symbol table
		if (hm.put(n.id, entry) != null) {
			System.out.println("Var id " + n.id + " at line "+ n.getLine() +" already declared");
			stErrors++;
		}
		return null;
	}

	@Override
	public Void visitNode(PrintNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(IfNode n) {
		if (print) printNode(n);
		visit(n.cond);
		visit(n.th);
		visit(n.el);
		return null;
	}

	@Override
	public Void visitNode(EqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(TimesNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(PlusNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(NotNode n) {
		if (print) printNode(n);
		visit(n.exp);
		return null;
	}

	@Override
	public Void visitNode(MinusNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(DivNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(LessEqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(GreaterEqualNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(AndNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(OrNode n) {
		if (print) printNode(n);
		visit(n.left);
		visit(n.right);
		return null;
	}

	@Override
	public Void visitNode(CallNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Fun id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.entry = entry;
			n.nl = nestingLevel;
		}
		// per ogni argomento della funzione li visita
		for (Node arg : n.arglist) visit(arg);
		return null;
	}

	@Override
	public Void visitNode(IdNode n) {
		if (print) printNode(n);
		STentry entry = stLookup(n.id);
		if (entry == null) {
			System.out.println("Var or Par id " + n.id + " at line "+ n.getLine() + " not declared");
			stErrors++;
		} else {
			n.entry = entry;
			n.nl = nestingLevel;
		}
		return null;
	}

	@Override
	public Void visitNode(BoolNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}

	@Override
	public Void visitNode(IntNode n) {
		if (print) printNode(n, n.val.toString());
		return null;
	}
}
