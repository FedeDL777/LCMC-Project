// Generated from C:/Users/feded/OneDrive/UniMagistrale/PrimoAnno/PrimoSemestre/LinguaggiECompilatori/Progetto LCMC/LCMC-Project/Project_LCMC/src/svm/SVM.g4 by ANTLR 4.13.2
package svm;

import java.util.HashMap;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SVMParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PUSH=1, POP=2, ADD=3, SUB=4, MULT=5, DIV=6, STOREW=7, LOADW=8, BRANCH=9, 
		BRANCHEQ=10, BRANCHLESSEQ=11, JS=12, LOADRA=13, STORERA=14, LOADTM=15, 
		STORETM=16, LOADFP=17, STOREFP=18, COPYFP=19, LOADHP=20, STOREHP=21, PRINT=22, 
		HALT=23, COL=24, LABEL=25, NUMBER=26, WHITESP=27, ERR=28;
	public static final int
		RULE_assembly = 0, RULE_instruction = 1;
	private static String[] makeRuleNames() {
		return new String[] {
			"assembly", "instruction"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'push'", "'pop'", "'add'", "'sub'", "'mult'", "'div'", "'sw'", 
			"'lw'", "'b'", "'beq'", "'bleq'", "'js'", "'lra'", "'sra'", "'ltm'", 
			"'stm'", "'lfp'", "'sfp'", "'cfp'", "'lhp'", "'shp'", "'print'", "'halt'", 
			"':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "PUSH", "POP", "ADD", "SUB", "MULT", "DIV", "STOREW", "LOADW", 
			"BRANCH", "BRANCHEQ", "BRANCHLESSEQ", "JS", "LOADRA", "STORERA", "LOADTM", 
			"STORETM", "LOADFP", "STOREFP", "COPYFP", "LOADHP", "STOREHP", "PRINT", 
			"HALT", "COL", "LABEL", "NUMBER", "WHITESP", "ERR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SVM.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	public int[] code = new int[ExecuteVM.CODESIZE];
	private int i = 0;
		
	private HashMap<String,Integer> labelDef = new HashMap<String,Integer>();
	private HashMap<Integer,String> labelRef = new HashMap<Integer,String>();

	public SVMParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssemblyContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SVMParser.EOF, 0); }
		public List<InstructionContext> instruction() {
			return getRuleContexts(InstructionContext.class);
		}
		public InstructionContext instruction(int i) {
			return getRuleContext(InstructionContext.class,i);
		}
		public AssemblyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assembly; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SVMListener ) ((SVMListener)listener).enterAssembly(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SVMListener ) ((SVMListener)listener).exitAssembly(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SVMVisitor ) return ((SVMVisitor<? extends T>)visitor).visitAssembly(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssemblyContext assembly() throws RecognitionException {
		AssemblyContext _localctx = new AssemblyContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_assembly);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(7);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 50331646L) != 0)) {
				{
				{
				setState(4);
				instruction();
				}
				}
				setState(9);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(10);
			match(EOF);
			 for (Integer j: labelRef.keySet()) 
				  	                        code[j]= labelDef.get(labelRef.get(j));
				                       
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InstructionContext extends ParserRuleContext {
		public Token n;
		public Token l;
		public TerminalNode PUSH() { return getToken(SVMParser.PUSH, 0); }
		public TerminalNode NUMBER() { return getToken(SVMParser.NUMBER, 0); }
		public TerminalNode LABEL() { return getToken(SVMParser.LABEL, 0); }
		public TerminalNode POP() { return getToken(SVMParser.POP, 0); }
		public TerminalNode ADD() { return getToken(SVMParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(SVMParser.SUB, 0); }
		public TerminalNode MULT() { return getToken(SVMParser.MULT, 0); }
		public TerminalNode DIV() { return getToken(SVMParser.DIV, 0); }
		public TerminalNode STOREW() { return getToken(SVMParser.STOREW, 0); }
		public TerminalNode LOADW() { return getToken(SVMParser.LOADW, 0); }
		public TerminalNode COL() { return getToken(SVMParser.COL, 0); }
		public TerminalNode BRANCH() { return getToken(SVMParser.BRANCH, 0); }
		public TerminalNode BRANCHEQ() { return getToken(SVMParser.BRANCHEQ, 0); }
		public TerminalNode BRANCHLESSEQ() { return getToken(SVMParser.BRANCHLESSEQ, 0); }
		public TerminalNode JS() { return getToken(SVMParser.JS, 0); }
		public TerminalNode LOADRA() { return getToken(SVMParser.LOADRA, 0); }
		public TerminalNode STORERA() { return getToken(SVMParser.STORERA, 0); }
		public TerminalNode LOADTM() { return getToken(SVMParser.LOADTM, 0); }
		public TerminalNode STORETM() { return getToken(SVMParser.STORETM, 0); }
		public TerminalNode LOADFP() { return getToken(SVMParser.LOADFP, 0); }
		public TerminalNode STOREFP() { return getToken(SVMParser.STOREFP, 0); }
		public TerminalNode COPYFP() { return getToken(SVMParser.COPYFP, 0); }
		public TerminalNode LOADHP() { return getToken(SVMParser.LOADHP, 0); }
		public TerminalNode STOREHP() { return getToken(SVMParser.STOREHP, 0); }
		public TerminalNode PRINT() { return getToken(SVMParser.PRINT, 0); }
		public TerminalNode HALT() { return getToken(SVMParser.HALT, 0); }
		public InstructionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instruction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SVMListener ) ((SVMListener)listener).enterInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SVMListener ) ((SVMListener)listener).exitInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SVMVisitor ) return ((SVMVisitor<? extends T>)visitor).visitInstruction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstructionContext instruction() throws RecognitionException {
		InstructionContext _localctx = new InstructionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_instruction);
		try {
			setState(61);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(13);
				match(PUSH);
				setState(14);
				((InstructionContext)_localctx).n = match(NUMBER);
				code[i++] = PUSH; 
				        	                  code[i++] = Integer.parseInt((((InstructionContext)_localctx).n!=null?((InstructionContext)_localctx).n.getText():null));
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(16);
				match(PUSH);
				setState(17);
				((InstructionContext)_localctx).l = match(LABEL);
				code[i++] = PUSH; 
					  	                      labelRef.put(i++,(((InstructionContext)_localctx).l!=null?((InstructionContext)_localctx).l.getText():null));
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(19);
				match(POP);
				code[i++] = POP;
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(21);
				match(ADD);
				code[i++] = ADD;
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(23);
				match(SUB);
				code[i++] = SUB;
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(25);
				match(MULT);
				code[i++] = MULT;
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(27);
				match(DIV);
				code[i++] = DIV;
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(29);
				match(STOREW);
				code[i++] = STOREW;
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(31);
				match(LOADW);
				code[i++] = LOADW;
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(33);
				((InstructionContext)_localctx).l = match(LABEL);
				setState(34);
				match(COL);
				labelDef.put((((InstructionContext)_localctx).l!=null?((InstructionContext)_localctx).l.getText():null),i);
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(36);
				match(BRANCH);
				setState(37);
				((InstructionContext)_localctx).l = match(LABEL);
				code[i++] = BRANCH; 
					  	                      labelRef.put(i++,(((InstructionContext)_localctx).l!=null?((InstructionContext)_localctx).l.getText():null));
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(39);
				match(BRANCHEQ);
				setState(40);
				((InstructionContext)_localctx).l = match(LABEL);
				code[i++] = BRANCHEQ; 
					  	                      labelRef.put(i++,(((InstructionContext)_localctx).l!=null?((InstructionContext)_localctx).l.getText():null));
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(42);
				match(BRANCHLESSEQ);
				setState(43);
				((InstructionContext)_localctx).l = match(LABEL);
				code[i++] = BRANCHLESSEQ; 
					  	                      labelRef.put(i++,(((InstructionContext)_localctx).l!=null?((InstructionContext)_localctx).l.getText():null));
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(45);
				match(JS);
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(46);
				match(LOADRA);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(47);
				match(STORERA);
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(48);
				match(LOADTM);
				code[i++] = LOADTM;
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(50);
				match(STORETM);
				code[i++] = STORETM;
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(52);
				match(LOADFP);
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(53);
				match(STOREFP);
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(54);
				match(COPYFP);
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(55);
				match(LOADHP);
				}
				break;
			case 23:
				enterOuterAlt(_localctx, 23);
				{
				setState(56);
				match(STOREHP);
				}
				break;
			case 24:
				enterOuterAlt(_localctx, 24);
				{
				setState(57);
				match(PRINT);
				code[i++] = PRINT;
				}
				break;
			case 25:
				enterOuterAlt(_localctx, 25);
				{
				setState(59);
				match(HALT);
				code[i++] = HALT;
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u001c@\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0001"+
		"\u0000\u0005\u0000\u0006\b\u0000\n\u0000\f\u0000\t\t\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0003\u0001>\b\u0001\u0001\u0001\u0000\u0000"+
		"\u0002\u0000\u0002\u0000\u0000V\u0000\u0007\u0001\u0000\u0000\u0000\u0002"+
		"=\u0001\u0000\u0000\u0000\u0004\u0006\u0003\u0002\u0001\u0000\u0005\u0004"+
		"\u0001\u0000\u0000\u0000\u0006\t\u0001\u0000\u0000\u0000\u0007\u0005\u0001"+
		"\u0000\u0000\u0000\u0007\b\u0001\u0000\u0000\u0000\b\n\u0001\u0000\u0000"+
		"\u0000\t\u0007\u0001\u0000\u0000\u0000\n\u000b\u0005\u0000\u0000\u0001"+
		"\u000b\f\u0006\u0000\uffff\uffff\u0000\f\u0001\u0001\u0000\u0000\u0000"+
		"\r\u000e\u0005\u0001\u0000\u0000\u000e\u000f\u0005\u001a\u0000\u0000\u000f"+
		">\u0006\u0001\uffff\uffff\u0000\u0010\u0011\u0005\u0001\u0000\u0000\u0011"+
		"\u0012\u0005\u0019\u0000\u0000\u0012>\u0006\u0001\uffff\uffff\u0000\u0013"+
		"\u0014\u0005\u0002\u0000\u0000\u0014>\u0006\u0001\uffff\uffff\u0000\u0015"+
		"\u0016\u0005\u0003\u0000\u0000\u0016>\u0006\u0001\uffff\uffff\u0000\u0017"+
		"\u0018\u0005\u0004\u0000\u0000\u0018>\u0006\u0001\uffff\uffff\u0000\u0019"+
		"\u001a\u0005\u0005\u0000\u0000\u001a>\u0006\u0001\uffff\uffff\u0000\u001b"+
		"\u001c\u0005\u0006\u0000\u0000\u001c>\u0006\u0001\uffff\uffff\u0000\u001d"+
		"\u001e\u0005\u0007\u0000\u0000\u001e>\u0006\u0001\uffff\uffff\u0000\u001f"+
		" \u0005\b\u0000\u0000 >\u0006\u0001\uffff\uffff\u0000!\"\u0005\u0019\u0000"+
		"\u0000\"#\u0005\u0018\u0000\u0000#>\u0006\u0001\uffff\uffff\u0000$%\u0005"+
		"\t\u0000\u0000%&\u0005\u0019\u0000\u0000&>\u0006\u0001\uffff\uffff\u0000"+
		"\'(\u0005\n\u0000\u0000()\u0005\u0019\u0000\u0000)>\u0006\u0001\uffff"+
		"\uffff\u0000*+\u0005\u000b\u0000\u0000+,\u0005\u0019\u0000\u0000,>\u0006"+
		"\u0001\uffff\uffff\u0000->\u0005\f\u0000\u0000.>\u0005\r\u0000\u0000/"+
		">\u0005\u000e\u0000\u000001\u0005\u000f\u0000\u00001>\u0006\u0001\uffff"+
		"\uffff\u000023\u0005\u0010\u0000\u00003>\u0006\u0001\uffff\uffff\u0000"+
		"4>\u0005\u0011\u0000\u00005>\u0005\u0012\u0000\u00006>\u0005\u0013\u0000"+
		"\u00007>\u0005\u0014\u0000\u00008>\u0005\u0015\u0000\u00009:\u0005\u0016"+
		"\u0000\u0000:>\u0006\u0001\uffff\uffff\u0000;<\u0005\u0017\u0000\u0000"+
		"<>\u0006\u0001\uffff\uffff\u0000=\r\u0001\u0000\u0000\u0000=\u0010\u0001"+
		"\u0000\u0000\u0000=\u0013\u0001\u0000\u0000\u0000=\u0015\u0001\u0000\u0000"+
		"\u0000=\u0017\u0001\u0000\u0000\u0000=\u0019\u0001\u0000\u0000\u0000="+
		"\u001b\u0001\u0000\u0000\u0000=\u001d\u0001\u0000\u0000\u0000=\u001f\u0001"+
		"\u0000\u0000\u0000=!\u0001\u0000\u0000\u0000=$\u0001\u0000\u0000\u0000"+
		"=\'\u0001\u0000\u0000\u0000=*\u0001\u0000\u0000\u0000=-\u0001\u0000\u0000"+
		"\u0000=.\u0001\u0000\u0000\u0000=/\u0001\u0000\u0000\u0000=0\u0001\u0000"+
		"\u0000\u0000=2\u0001\u0000\u0000\u0000=4\u0001\u0000\u0000\u0000=5\u0001"+
		"\u0000\u0000\u0000=6\u0001\u0000\u0000\u0000=7\u0001\u0000\u0000\u0000"+
		"=8\u0001\u0000\u0000\u0000=9\u0001\u0000\u0000\u0000=;\u0001\u0000\u0000"+
		"\u0000>\u0003\u0001\u0000\u0000\u0000\u0002\u0007=";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}