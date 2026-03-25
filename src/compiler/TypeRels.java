package compiler;

import compiler.AST.*;
import compiler.lib.*;

import java.util.HashMap;
import java.util.Map;

public class TypeRels {
	public static Map<String, String> superType = new HashMap<>();

	// valuta se il tipo "a" e' <= al tipo "b", dove "a" e "b" sono tipi di base: IntTypeNode o BoolTypeNode
	public static boolean isSubtype(TypeNode a, TypeNode b) {
		if (a.getClass().equals(b.getClass()))
			return true;

		if (a instanceof BoolTypeNode && b instanceof IntTypeNode)
			return true;

		// un tipo EmptyTypeNode sottotipo di un qualsiasi tipo riferimento RefTypeNode
		if (a instanceof EmptyTypeNode && b instanceof RefTypeNode)
			return true;

		// un tipo riferimento RefTypeNode sottotipo di un altro in base alla funzione superType
		if (a instanceof RefTypeNode && b instanceof RefTypeNode) {
			var idA = ((RefTypeNode) a).getId();
			var idB = ((RefTypeNode) b).getId();
			while (idA != null) {
				if (idA.equals(idB))
					return true;
				idA = superType.get(idA);
			}
			return false;
		}

		// un tipo funzionale ArrowTypeNode sottotipo di un altro
		if (a instanceof ArrowTypeNode && b instanceof ArrowTypeNode) {
			var retTypeA = ((ArrowTypeNode) a).ret;
			var retTypeB = ((ArrowTypeNode) b).ret;
			var parListA = ((ArrowTypeNode) a).parlist;
			var parListB = ((ArrowTypeNode) b).parlist;

			//check covarianza sul tipo di ritorno
			if (!isSubtype(retTypeA, retTypeB)) {
				return false;
			}
			if (parListA.size() != parListB.size()) {
				return false;
			}
			for (int i = 0; i < parListA.size(); i++) {
				var parB = parListB.get(i);
				var parA = parListA.get(i);
				if (!isSubtype(parB, parA)) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	/*
	ArrowTypeNode
	//covarianza sul ritorno
	fun morso(String panino): Cane;
	fun morso(String panino); BarboncinoCalciabile;

	//controvarianza sul tipo dei parametri
	fun morso(BarboncinoCalciabile panino): String;
	fun morso(Cane panino); String;

	// esempio insieme
	fun morso(BarboncinoCalciabile panino): Cane;
	fun morso(Cane panino); BarboncinoCalciabile;
	 */


	/*
	caso RefTypeNode
	class Animale
	class Cane exted Animale
	class BarboncinoCalciabile extend Gatto
	 */
}
