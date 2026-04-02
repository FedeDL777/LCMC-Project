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
		STentry classEntry = new STentry(nestingLevel, classTypeNode, decOffset--);

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
		var names = new HashSet<String>(); //TODO: find better name
		for (FieldNode field : n.fields) {
			if (names.contains(field.id)) {
				System.out.println("Field id " + field.id + " at line " + n.getLine() + " already declared");
				stErrors++;
			} else {
				names.add(field.id);
			}
			if (virtualTable.containsKey(field.id)) { // se faccio overriding
				var oldFieldEntry = virtualTable.get(field.id);
				if (oldFieldEntry.type instanceof ArrowTypeNode) {
					System.out.println("Can't override a method with a field name " + field.id + " at line " + n.getLine());
					stErrors++;
				} else {
					var oldFieldOffset = oldFieldEntry.offset;
					var newSTentry = new STentry(nestingLevel, field.getType(), oldFieldOffset);
					classTypeNode.allFields.set(-oldFieldOffset - 1, field.getType());
					virtualTable.put(field.id, newSTentry);
				}
			}
			else { // se è un nuovo field
				var newSTentry = new STentry(nestingLevel, field.getType(), fieldOffset--);
				classTypeNode.allFields.add(field.getType());
				virtualTable.put(field.id, newSTentry);
			}
		}
		//TODO: Spostare modifica della virtual table nella visit del methodNode
		// (classTypeNode modificato dopo la visita del metodo all'interno della classNode)
		for (MethodNode method : n.methods) {
			if (names.contains(method.id)) {
				System.out.println("Method id " + method.id + " at line " + n.getLine() + " already declared");
				stErrors++;
			} else {
				names.add(method.id);
			}
			if (virtualTable.containsKey(method.id)) { // se faccio overriding
				var oldMethodEntry = virtualTable.get(method.id);
				if (!(oldMethodEntry.type instanceof ArrowTypeNode)) {
					System.out.println("Can't override a field with a method name " + method.id + " at line " + n.getLine());
					stErrors++;
				} else {
					var oldMethodOffset = oldMethodEntry.offset;
					var newSTentry = new STentry(nestingLevel, method.getType(), oldMethodOffset);
					virtualTable.put(method.id, newSTentry);
					// classTypeNode.allMethods.set(oldMethodOffset, (ArrowTypeNode) method.getType());
					// method.offset = oldMethodOffset;
				}
			}
			else { // se è un nuovo metodo
				var newSTentry = new STentry(nestingLevel, method.getType(), methodOffset++);
				virtualTable.put(method.id, newSTentry);
				// classTypeNode.allMethods.add((ArrowTypeNode) method.getType());
				// method.offset = methodOffset++;
			}
			visit(method);
			if (method.offset < classTypeNode.allMethods.size()) {
				classTypeNode.allMethods.set(method.offset, (ArrowTypeNode) method.getType());
			} else {
				classTypeNode.allMethods.add((ArrowTypeNode) method.getType());
			}
		}


		symTable.remove(nestingLevel--);
		return null;
	}

	//TODO Visit Method Node
	@Override
	public Void visitNode(MethodNode n) {
		if (print) printNode(n);
		//referenza a symbol table
		Map<String, STentry> hm = symTable.get(nestingLevel);
		List<TypeNode> parTypes = new ArrayList<>();

		n.offset = hm.get(n.id).offset;
		STentry entry = new STentry(nestingLevel, new ArrowTypeNode(parTypes, n.retType), n.offset);

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
			if (!(entry.type instanceof RefTypeNode)) {
				System.out.println("Class type " + entry.type + " at line "+ entry.nl + " is not a RefTypeNode");
				stErrors++;
			} else {
				var classVirtualTable = classTable.get(n.idClass);
				if (classVirtualTable != null) {
					var methodEntry = classVirtualTable.get(n.idMethod);
					if (methodEntry != null) {
						n.methodEntry = methodEntry;
					} else {
						System.out.println("Method id " + n.idMethod + " at line "+ n.getLine() + " not declared");
						stErrors++;
					}
				}
			}
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
