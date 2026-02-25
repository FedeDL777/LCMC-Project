grammar FOOL;
 
@lexer::members {
public int lexicalErrors=0;
}
   
/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

prog  : progbody EOF ;
     
progbody : LET dec+ IN exp SEMIC  #letInProg
         | exp SEMIC              #noDecProg
         ;
 
dec : VAR ID COLON type ASS exp SEMIC  #vardec
    | FUN ID COLON type LPAR (ID COLON type (COMMA ID COLON type)* )? RPAR 
        	(LET dec+ IN)? exp SEMIC   #fundec
    ;
        // parser uses top-down priority: earlier alternatives have higher precedence
        // precedence (high to low): NOT > TIMES/DIV > PLUS/MINUS > EQ/LEQ/GEQ > AND > OR
exp     :NOT exp #not
        | exp TIMES exp #times
        | exp DIV exp   #div
        | exp PLUS  exp #plus
        | exp MINUS exp #minus
        | exp EQ  exp   #eq
        | exp MIN_EQ  exp   #min_eq
        | exp MAG_EQ  exp   #mag_eq
        | exp AND exp #and
        | exp OR exp #or

        | LPAR exp RPAR #pars
    	| MINUS? NUM #integer
	    | TRUE #true     
	    | FALSE #false
	    | IF exp THEN CLPAR exp CRPAR ELSE CLPAR exp CRPAR  #if   
	    | PRINT LPAR exp RPAR #print
	    | ID #id
	    | ID LPAR (exp (COMMA exp)* )? RPAR #call
        ; 
             
type    : INT #intType
        | BOOL #boolType
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
INT	    : 'int' ;
BOOL	: 'bool' ;
NUM     : '0' | ('1'..'9')('0'..'9')* ; 

ID  	: ('a'..'z'|'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9')* ;


WHITESP  : ( '\t' | ' ' | '\r' | '\n' )+    -> channel(HIDDEN) ;

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
 
ERR   	 : . { System.out.println("Invalid char "+getText()+" at line "+getLine()); lexicalErrors++; } -> channel(HIDDEN); 


