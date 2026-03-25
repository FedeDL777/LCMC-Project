package compiler;

import compiler.lib.*;

//STentry contiene le informazioni riguardanti la definizione di una funzione o di una classe. La loro dichiarazione.
//E' sempre affiancata da una variabile nl (nestinglevel) per specificare il livello a cui viene utilizzata la classe/funzione
public class STentry implements Visitable {
	int nl;
	TypeNode type;
	int offset;
	public STentry(int n, TypeNode t, int o) { nl = n; type = t; offset=o; }

	@Override
	public <S,E extends Exception> S accept(BaseASTVisitor<S,E> visitor) throws E {
		return ((BaseEASTVisitor<S,E>) visitor).visitSTentry(this);
	}
}
