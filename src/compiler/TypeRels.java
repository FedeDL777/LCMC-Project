package compiler;

import compiler.AST.*;
import compiler.lib.*;

import java.util.HashMap;
import java.util.Map;

public class TypeRels {
	public static Map<String, String> superType = new HashMap<>();

	// valuta se il tipo "a" e' <= al tipo "b"
	public static boolean isSubtype(TypeNode a, TypeNode b) {
		// tipi primitivi identici
		if (a instanceof IntTypeNode && b instanceof IntTypeNode) return true;
		if (a instanceof BoolTypeNode && b instanceof BoolTypeNode) return true;

		// Bool e' sottotipo di Int
		if (a instanceof BoolTypeNode && b instanceof IntTypeNode) return true;

		// null (EmptyType) e' sottotipo di qualsiasi tipo riferimento
		if (a instanceof EmptyTypeNode && b instanceof RefTypeNode) return true;

		// RefType: A <= B se A == B oppure A ha B come antenato nella gerarchia
		if (a instanceof RefTypeNode && b instanceof RefTypeNode) {
			var idA = ((RefTypeNode) a).getId();
			var idB = ((RefTypeNode) b).getId();
			while (idA != null) {
				if (idA.equals(idB)) return true;
				idA = superType.get(idA);
			}
			return false;
		}

		// ClassTypeNode: A <= B se A ha almeno i campi/metodi di B nelle stesse posizioni con tipi compatibili
		if (a instanceof ClassTypeNode ca && b instanceof ClassTypeNode cb) {
			if (ca.allFields.size() < cb.allFields.size()) return false;
			if (ca.allMethods.size() < cb.allMethods.size()) return false;
			for (int i = 0; i < cb.allFields.size(); i++)
				if (!isSubtype(ca.allFields.get(i), cb.allFields.get(i))) return false;
			for (int i = 0; i < cb.allMethods.size(); i++)
				if (!isSubtype(ca.allMethods.get(i), cb.allMethods.get(i))) return false;
			return true;
		}

		// ArrowTypeNode: controvarianza sui parametri, covarianza sul ritorno
		if (a instanceof ArrowTypeNode aa && b instanceof ArrowTypeNode ab) {
			if (aa.parlist.size() != ab.parlist.size()) return false;
			if (!isSubtype(aa.ret, ab.ret)) return false;
			for (int i = 0; i < aa.parlist.size(); i++)
				if (!isSubtype(ab.parlist.get(i), aa.parlist.get(i))) return false;
			return true;
		}

		return false;
	}

	public static TypeNode lowestCommonAncestor(TypeNode a, TypeNode b){
		if (a instanceof EmptyTypeNode)
			return b;
		if (b instanceof EmptyTypeNode) {
			return a;
		}

		//TODO: modify comment
		// ca risale la catena delle classi fino a che cb non è sottotipo di ca (vedi immagine)
		if (a instanceof RefTypeNode ca && b instanceof RefTypeNode cb) {
			String caId = ca.id;
			while (caId != null) {
				ca = new RefTypeNode(caId);
				if (isSubtype(cb, ca))
					return ca;
				caId = superType.get(caId);
			}
			return null;
		}
		if (a instanceof IntTypeNode || b instanceof IntTypeNode )
			return new IntTypeNode();
		if (a instanceof BoolTypeNode && b instanceof BoolTypeNode )
			return new BoolTypeNode();

		return null;
	}



	/*
	ArrowTypeNode
	//covarianza sul ritorno
	fun morso(String panino): Obj;
	fun morso(String panino); Integer;

	//controvarianza sul tipo dei parametri
	fun morso(Barboncino panino): String;
	fun morso(Cane panino); String;

	// esempio insieme
	fun morso(Barboncino panino): Cane;
	fun morso(Cane panino); Barboncino;
	 */


	/*
	caso RefTypeNode
	class Animale
	class Cane extend Animale
	class Barboncino extend Cane
	 */
}
