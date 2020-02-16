
grammar PSL;

program
    :   pslRule+ EOF
    ;

pslRule
    :   logicalRule
    |   arithmeticRule
    ;

// The inner part of a rule (logical or arithmetic).
// This cannot be reached from a default PSL program and must be asked for specifically.
pslRulePartial
    :   logicalRule EOF
    |   arithmeticRule EOF
    |   logicalRuleExpression EOF
    |   arithmeticRuleExpression filterClause* EOF
    ;

//
// Atoms and literals
//

predicate
    :   IDENTIFIER
    ;

atom
    :   predicate LPAREN term (COMMA term)* RPAREN
    |   term termOperator term
    ;

term
    :   variable
    |   constant
    ;

variable
    :   IDENTIFIER
    ;

constant
    :   SINGLE_QUOTE IDENTIFIER SINGLE_QUOTE
    |   DOUBLE_QUOTE IDENTIFIER DOUBLE_QUOTE
    ;

//
// Logical rules
//

logicalRule
    :   weightedLogicalRule
    |   unweightedLogicalRule
    ;

weightedLogicalRule
    :   weightExpression logicalRuleExpression EXPONENT_EXPRESSION?
    ;

unweightedLogicalRule
    :   logicalRuleExpression PERIOD
    ;

logicalValue
    :   atom
    |   not atom
    ;

logicalConjunctiveValue
    :   logicalValue
    |   LPAREN logicalConjunctiveExpression RPAREN
    ;

logicalDisjunctiveValue
    :   logicalValue
    |   LPAREN logicalDisjunctiveExpression RPAREN
    ;

logicalConjunctiveExpression
    :   logicalConjunctiveValue
    |   logicalConjunctiveExpression and logicalConjunctiveValue
    ;

logicalDisjunctiveExpression
    :   logicalDisjunctiveValue
    |   logicalDisjunctiveExpression or logicalDisjunctiveValue
    ;

// Note that we are not supporting mixing disjunctions and conjunction.
// You can onyl have one type on each side of the arrow.
logicalImplicationExpression
    :   logicalDisjunctiveExpression impliedBy logicalConjunctiveExpression
    |   logicalConjunctiveExpression then logicalDisjunctiveExpression
    ;

logicalRuleExpression
    :   logicalDisjunctiveExpression
    |   logicalImplicationExpression
    ;

//
// Arithmetic rules
//

arithmeticRule
    :   weightedArithmeticRule
    |   unweightedArithmeticRule
    ;

weightedArithmeticRule
    :   weightExpression arithmeticRuleExpression EXPONENT_EXPRESSION? filterClause*
    ;

unweightedArithmeticRule
    :   arithmeticRuleExpression PERIOD filterClause*
    ;

arithmeticRuleExpression
    :   linearArithmeticExpression arithmeticRuleRelation linearArithmeticExpression
    ;

linearArithmeticExpression
    :   linearArithmeticOperand
    |   linearArithmeticExpression linearOperator linearArithmeticOperand
    ;

linearArithmeticOperand
    :   arithmeticCoefficientOperand
    |   LPAREN linearArithmeticExpression RPAREN
    ;

arithmeticCoefficientOperand
    :   (coefficientExpression MULT?)? (summationAtom | atom) (DIV coefficientExpression)?
    |   coefficientExpression
    ;

summationAtom
    :   predicate LPAREN (summationVariable | term) (COMMA (summationVariable | term))* RPAREN
    ;

summationVariable
    :   PLUS IDENTIFIER
    ;

coefficient
    :   number
    |   coefficientOperator
    |   LPAREN coefficientExpression RPAREN
    ;

coefficientMultiplicativeExpression
    :   coefficient
    |   coefficientMultiplicativeExpression MULT coefficient
    |   coefficientMultiplicativeExpression DIV coefficient
    ;

coefficientAdditiveExpression
    :   coefficientMultiplicativeExpression
    |   coefficientAdditiveExpression PLUS coefficientMultiplicativeExpression
    |   coefficientAdditiveExpression MINUS coefficientMultiplicativeExpression
    ;

coefficientExpression
    :   coefficientAdditiveExpression
    ;

// Note: The currently supported (non-cardinality) operators support exactly two arguments.
// To support future operators, we would have to change the syntax a bit.
coefficientOperator
    :   PIPE variable PIPE
    |   coefficientFunction
    ;

coefficientFunction
    :   coefficientFunctionOperator LBRACKET coefficientExpression COMMA coefficientExpression RBRACKET
    |   coefficientFunctionOperator LPAREN coefficientExpression COMMA coefficientExpression RPAREN
    ;

coefficientFunctionOperator
    :   MAX
    |   MIN
    ;

filterClause
    :   LBRACE variable COLON booleanExpression RBRACE
    ;

booleanValue
    :   logicalValue
    |   LPAREN booleanExpression RPAREN
    ;

booleanConjunctiveExpression
    :   booleanValue
    |   booleanConjunctiveExpression and booleanValue
    ;

booleanDisjunctiveExpression
    :   booleanConjunctiveExpression
    |   booleanDisjunctiveExpression or booleanConjunctiveExpression
    ;

booleanExpression
    :   booleanDisjunctiveExpression
    ;

//
// Common expressions
//

weightExpression
    :   NONNEGATIVE_NUMBER COLON
    ;

EXPONENT_EXPRESSION
    :   CARROT [12]
    ;

//
// Logical operators
//

not
    :   NEGATION
    ;

and
    :   AMPERSAND
    |   AMPERSAND AMPERSAND
    ;

or
    :   PIPE
    |   PIPE PIPE
    ;

then
    :   '>>'
    |   '->'
    ;

impliedBy
    :   '<<'
    |   '<-'
    ;

//
// Term operators
//

termOperator
    :   termEqual
    |   notEqual
    |   nonSymmetric
    ;

termEqual
    :   EQUAL EQUAL
    ;

notEqual
    :   NEGATION EQUAL
    |   MINUS
    ;

nonSymmetric
    :   MOD
    |   CARROT
    ;

//
// Arithmetic rule relations
//

arithmeticRuleRelation
    :   LESS_THAN_EQUAL
    |   GREATER_THAN_EQUAL
    |   EQUAL
    ;

LESS_THAN_EQUAL
    :   '<='
    ;

GREATER_THAN_EQUAL
    :   '>='
    ;

EQUAL
    :   '='
    ;

//
// Arithmetic operators
//

arithmeticOperator
    :   PLUS
    |   MINUS
    |   MULT
    |   DIV
    ;

linearOperator
    :   PLUS
    |   MINUS
    ;

PLUS
    :   '+'
    ;

MINUS
    :   '-'
    ;

MULT
    :   '*'
    ;

DIV
    :   '/'
    ;

MAX
    :   '@Max'
    ;

MIN
    :   '@Min'
    ;

//
// Identifiers and numbers
//

number
    :   MINUS? NONNEGATIVE_NUMBER
    ;

IDENTIFIER
    :   LETTER (LETTER | DIGIT)*
    ;

NONNEGATIVE_NUMBER
    :   DIGIT+ (PERIOD DIGIT+)? ([eE] MINUS? DIGIT+)?
    ;

fragment
LETTER
    :   [a-zA-Z$_] // these are the "java letters" below 0xFF
    ;

fragment
DIGIT
    :   [0-9]
    ;

//
// Common tokens
//

PERIOD
    :   '.'
    ;

COMMA
    :   ','
    ;

COLON
    :   ':'
    ;

NEGATION
    :   '~'
    |   '!'
    ;

AMPERSAND
    :   '&'
    ;

PIPE
    :   '|'
    ;

LPAREN
    :   '('
    ;

RPAREN
    :   ')'
    ;

LBRACE
    :   '{'
    ;

RBRACE
    :   '}'
    ;

LBRACKET
    :   '['
    ;

RBRACKET
    :   ']'
    ;

SINGLE_QUOTE
    :   '\''
    ;

DOUBLE_QUOTE
    :   '\"'
    ;

MOD
    :   '%'
    ;

CARROT
    :   '^'
    ;

//
// Whitespace and comments
// (from https://github.com/antlr/grammars-v4)
//

// Put the whitespace in a hidden channel instead of skipping so we can preserve space in debugging.
WS
    :   [ \t\r\n\u000C]+ -> channel(HIDDEN)
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;

PYTHON_COMMENT
    :   '#' ~[\r\n]* -> skip
    ;
