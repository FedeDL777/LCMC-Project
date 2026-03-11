grammar FOOL;
 
@lexer::members {
public int lexicalErrors=0;
}
   
/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

prog  : progbody EOF ;

progbody : LET (classdec+ dec* | dec+) IN exp SEMIC  #letInProg
         | exp SEMIC              #noDecProg
         ;

classdec : CLASS ID LPAR arg* RPAR CLPAR fun* CRPAR ;

dec : VAR ID COLON type ASS exp SEMIC  #vardec
    | fun   #fundec
    ;

fun : FUN ID COLON type LPAR arg? RPAR funbody ;

arg : (ID COLON type)(COMMA ID COLON type)* ;

funbody : (LET dec+ IN)? exp SEMIC ;




        // parser uses top-down priority: earlier alternatives have higher precedence
        // precedence (high to low): NOT > TIMES/DIV > PLUS/MINUS > EQ/LEQ/GEQ > AND > OR

exp     :NOT exp #not
        | exp (TIMES | DIV) exp #timesDiv
        | exp (PLUS | MINUS)  exp #plusMinus
        | exp (MIN_EQ | MAG_EQ) exp #minMag_eq
        | exp EQ exp   #eq

        | exp AND exp #and
        | exp OR exp #or

        | LPAR exp RPAR #pars
    	| MINUS? NUM #integer
    	//TODO unisci true false
	    | (TRUE | FALSE) #trueFalse
	    | NULL #null
	    | NEW ID LPAR (exp (COMMA exp)* )? RPAR #new
	    | IF exp THEN CLPAR exp CRPAR ELSE CLPAR exp CRPAR  #if   
	    | PRINT LPAR exp RPAR #print
	    | ID #id
	    | ID LPAR (exp (COMMA exp)* )? RPAR #call
	    //TODO da aggiustare nel caso di chiamta di oggetti a catena
	    | ID (DOT ID)+ LPAR (exp (COMMA exp)* )? RPAR #methodCall
        ;

             
type    : INT #intType
        | BOOL #boolType
        | ID #classType
 	    ;  
 	  		  
/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

PLUS  	: '+' ;
MINUS	: '-' ; 
TIMES   : '*' ;
DIV     : '/';
LPAR	: '(' ;
RPAR	: ')' ;
CLPAR	: '{' ;
CRPAR	: '}' ;
SEMIC 	: ';' ;
COLON   : ':' ; 
COMMA	: ',' ;
EQ	    : '==' ;	
ASS	    : '=' ;
MIN_EQ  : '<=' ;
MAG_EQ  : '>=' ;
NOT     : '!' ;
AND     : '&&' ;
OR      : '||' ;
TRUE	: 'true' ;
FALSE	: 'false' ;
IF	    : 'if' ;
THEN	: 'then';
ELSE	: 'else' ;
PRINT	: 'print' ;
LET     : 'let' ;	
IN      : 'in' ;	
VAR     : 'var' ;
FUN	    : 'fun' ;
//estensione oggetti
CLASS   : 'class';
NEW     : 'new';
NULL    : 'null';
DOT     : '.';


INT	    : 'int' ;
BOOL	: 'bool' ;
NUM     : '0' | ('1'..'9')('0'..'9')* ;

ID  	: ('a'..'z'|'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9')* ;

WHITESP  : ( '\t' | ' ' | '\r' | '\n' )+    -> channel(HIDDEN) ;

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
 
ERR   	 : . { System.out.println("Invalid char "+getText()+" at line "+getLine()); lexicalErrors++; } -> channel(HIDDEN); 


