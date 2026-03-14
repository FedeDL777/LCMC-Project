package compiler;

import java.util.*;
import compiler.lib.*;

public class AST {

	public static class ProgLetInNode extends Node {
		List<Node> declist;
		Node exp;

		ProgLetInNode(List<Node> d, Node e) {
			declist = d;
			exp = e;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ProgNode extends Node {
		Node exp;

		ProgNode(Node e) {
			exp = e;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ClassNode extends DecNode {
		String id;
		List<FieldNode> fieldList;
		List<MethodNode> methodList;
		STentry entry;
		int nl;

		ClassNode(String i, List<FieldNode> fd, List<MethodNode> ml){
			id = i;
			fieldList = fd;
			methodList = ml;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class FieldNode extends DecNode {
		String id;

		FieldNode(String i, TypeNode t) {
			id = i;
			setType(t);
		}

		public void setType(TypeNode t){
			type = t;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class MethodCallNode extends Node {
		String idClass;
		String idMethod;
		List<Node> arglist = new ArrayList<Node>();
		STentry entry;
		int nl;

		MethodCallNode(String idC, String idM, List<Node> args){
			idClass = idC;
			idMethod = idM;
			arglist = args;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class MethodNode extends FunNode {
		String label;
		Integer offSet;

		MethodNode(String i, TypeNode rt, List<ParNode> pl, List<Node> dl, Node e, String lbl, Integer offS){
			super(i, rt, pl, dl, e);
			label = lbl;
			offSet = offS;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class NewNode extends Node {
		String id;
		List<Node> nodeList;

		NewNode(String i, List<Node> ndLst){
			id = i;
			nodeList = ndLst;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class EmptyNode extends Node{
		EmptyNode(){}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}

	}

	public static class FunNode extends DecNode {
		String id;
		TypeNode retType;
		List<ParNode> parlist;
		List<Node> declist;
		Node exp;

		FunNode(String i, TypeNode rt, List<ParNode> pl, List<Node> dl, Node e) {
			id = i;
			retType = rt;
			parlist = pl;
			declist = dl;
			exp = e;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ParNode extends DecNode {
		String id;

		ParNode(String i, TypeNode t) {
			id = i;
			setType(t);
		}

		public void setType(TypeNode t){
			type = t;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class VarNode extends DecNode {
		String id;
		Node exp;

		VarNode(String i, TypeNode t, Node v) {
			id = i;
			setType(t);
			exp = v;
		}

		public void setType(TypeNode t){
			type = t;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class PrintNode extends Node {
		Node exp;

		PrintNode(Node e) {
			exp = e;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class IfNode extends Node {
		Node cond;
		Node th;
		Node el;

		IfNode(Node c, Node t, Node e) {
			cond = c;
			th = t;
			el = e;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class LessEqualNode extends Node {
		Node left;
		Node right;

		LessEqualNode(Node l, Node r) {
			left = l;
			right = r;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	public static class GreaterEqualNode extends Node{
		Node left;
		Node right;

		GreaterEqualNode(Node l, Node r) {
			left = l;
			right = r;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	public static class AndNode extends Node{
		Node left;
		Node right;

		AndNode(Node l, Node r) {
			left = l;
			right = r;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	public static class OrNode extends Node{
		Node left;
		Node right;

		OrNode(Node l, Node r) {
			left = l;
			right = r;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	public static class DivNode extends Node{
		Node left;
		Node right;

		DivNode(Node l, Node r) {
			left = l;
			right = r;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	public static class MinusNode extends Node{
		Node left;
		Node right;

		MinusNode(Node l, Node r) {
			left = l;
			right = r;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}
	public static class NotNode extends Node{
		Node exp;

		NotNode(Node e) {
			exp = e;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class EqualNode extends Node {
		Node left;
		Node right;

		EqualNode(Node l, Node r) {
			left = l;
			right = r;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class TimesNode extends Node {
		Node left;
		Node right;

		TimesNode(Node l, Node r) {
			left = l;
			right = r;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class PlusNode extends Node {
		Node left;
		Node right;

		PlusNode(Node l, Node r) {
			left = l;
			right = r;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class CallNode extends Node {
		String id;
		List<Node> arglist = new ArrayList<Node>();
		STentry entry;
		int nl;

		CallNode(String i, List<Node> p) {
			id = i;
			arglist = p;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class IdNode extends Node {
		String id;
		STentry entry;
		int nl;

		IdNode(String i) {
			id = i;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class BoolNode extends Node {
		Boolean val;

		BoolNode(boolean n) {
			val = n;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class IntNode extends Node {
		Integer val;

		IntNode(Integer n) {
			val = n;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ArrowTypeNode extends TypeNode {
		List<TypeNode> parlist;
		TypeNode ret;

		ArrowTypeNode(List<TypeNode> p, TypeNode r) {
			parlist = p;
			ret = r;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class BoolTypeNode extends TypeNode {

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class IntTypeNode extends TypeNode {

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	//new types
	public static class  RefTypeNode extends TypeNode {
		String id;

		RefTypeNode(String i){
			id = i;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class  EmptyTypeNode extends TypeNode {

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}
	}

	public static class ClassTypeNode extends TypeNode{
		ArrayList<TypeNode> allFields;
		ArrayList<ArrowTypeNode> allMethods;

		ClassTypeNode(ArrayList<TypeNode> fields,ArrayList<ArrowTypeNode> methods){
			allFields = fields;
			allMethods = methods;
		}

		@Override
		public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {return visitor.visitNode(this);}

	}
}