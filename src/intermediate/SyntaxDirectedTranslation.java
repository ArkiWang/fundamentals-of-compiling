package intermediate;
import edu.ustc.cs.compile.platform.interfaces.InterRepresent;
import edu.ustc.cs.compile.util.ir.HIRPack;
import javafx.beans.binding.BooleanExpression;
import lexical.BlockLexer;
import lexical.Token;
import org.eclipse.jdt.core.dom.*;
import parser.RecursionDescendParser;

import java.util.LinkedList;
import lexical.Token.TokenType;

/**
 * Created by yueli on 2018/5/19.
 */
public class SyntaxDirectedTranslation {
    private AST ast=null;
    private BlockLexer lexer=null;
    private Token lookAhead=null;
    public SyntaxDirectedTranslation(){
        ast=AST.newAST(AST.JLS3);
    }
    public InterRepresent doParse(String filePath){
        lexer=new BlockLexer(filePath);
        Block mainBody=this.parse();
        HIRPack ir=new HIRPack();
        ir.setIR(mainBody);
        return ir;
    }
    public Token matchToken(TokenType type, String functionName){
        if(lookAhead.getType()!=type){
            RecursionDescendParser.parsingError(type.toString(),functionName,lookAhead);
        }
        Token matchedSymbol=lookAhead;
        lookAhead=lexer.nextToken();
        return matchedSymbol;
    }
    public Block parse(){
        lookAhead=lexer.nextToken();
        Block mainBody=simpleBlock();
        System.out.println("Parsing Success!");
        return mainBody;
    }
   private Block simpleBlock(){
       if(lookAhead.getType()== TokenType.LBRACKET){
           matchToken(TokenType.LBRACKET,"simpleBlock");
           LinkedList seq=sequence();
           matchToken(TokenType.RBRACKET,"simpleBlock");

           Block mainBody=ast.newBlock();
           if(seq!=null){
               for(int i=0;i<seq.size();i++){
                   mainBody.statements().add(seq.get(i));
               }
           }
           return mainBody;
       }else {
           RecursionDescendParser.parsingError(TokenType.LBRACKET.toString(),"simpleBlock",lookAhead);
           return null;
       }
   }

   private LinkedList sequence(){//
       if(lookAhead.getType()== TokenType.IDENTIFIER) {
           ExpressionStatement es = assignmentStatement();
           LinkedList seq = sequence();
           if (seq == null) {
               seq = new LinkedList();
           }
           seq.addFirst(es);
           return seq;
       }else if(lookAhead.getType()==TokenType.RBRACKET){
           //match epsilon
           return null;
       }else if(lookAhead.getType()==TokenType.KEY_IF){/**/
           IfStatement ifst=ifStatement();
           LinkedList seq=sequence();
           if (seq == null) {
               seq = new LinkedList();
           }
           seq.addFirst(ifst);
           return seq;
       }else if(lookAhead.getType()==TokenType.KEY_WHILE){/**/
           WhileStatement ws=whileStatement();
           LinkedList seq=sequence();
           if (seq == null) {
               seq = new LinkedList();
           }
           seq.addFirst(ws);
           return seq;
       }else {
           String errorTypes=TokenType.IDENTIFIER.toString()+","
                   +TokenType.RBRACKET.toString();
           RecursionDescendParser.parsingError(errorTypes,"sequence",lookAhead);
           return null;
       }
   }
   private IfStatement ifStatement(){
       IfStatement ifst=ast.newIfStatement();
       if(lookAhead.getType()==TokenType.KEY_IF){
           matchToken(TokenType.KEY_IF,"ifStatement");
           matchToken(TokenType.LPAREN,"ifStatement");
           Expression e= boolExpression();
           ifst.setExpression(e);
           matchToken(TokenType.RPAREN,"ifStatement");
           matchToken(TokenType.LBRACKET,"ifStatement");
           Block ifBlock=ast.newBlock();
           LinkedList seq=sequence();
           while (!seq.isEmpty()){
                 ifBlock.statements().add(seq.pop());
           }
           ifst.setThenStatement(ifBlock);
           matchToken(TokenType.RBRACKET,"ifStatement");
           optionalElse(ifst);
           return ifst;
       }else {
           EmptyStatement es=ast.newEmptyStatement();
           ifst.setThenStatement(es);
           return null;
       }
   }
   private WhileStatement whileStatement() {
       WhileStatement ws = ast.newWhileStatement();
       if (lookAhead.getType() == TokenType.KEY_WHILE) {
           matchToken(TokenType.KEY_WHILE, "whileStatement");
           matchToken(TokenType.LPAREN, "whileStatement");
           Expression e = boolExpression();
           ws.setExpression(e);
           matchToken(TokenType.RPAREN, "whileStatement");
           matchToken(TokenType.LBRACKET, "whileStatement");
           LinkedList seq=sequence();
           Block whileBlock=ast.newBlock();
           while (!seq.isEmpty()){
               whileBlock.statements().add(seq.pop());
           }
           ws.setBody(whileBlock);
           matchToken(TokenType.RBRACKET, "whileStatement");
           return ws;
       } else {
           EmptyStatement es=ast.newEmptyStatement();
           ws.setBody(es);
           return null;
       }
   }
   private void optionalElse(IfStatement ifst){
       if(lookAhead.getType()==TokenType.KEY_ELSE){
           matchToken(TokenType.KEY_ELSE,"optionalElse");
           matchToken(TokenType.LBRACKET,"optionalElse");
           Block elseBlock=ast.newBlock();
           LinkedList seq=sequence();
           while(!seq.isEmpty()){
               elseBlock.statements().add(seq.pop());
           }
           ifst.setElseStatement(elseBlock);
           matchToken(TokenType.RBRACKET,"optionalElse");
       }else if(lookAhead.getType()==TokenType.IDENTIFIER||
               lookAhead.getType()==TokenType.KEY_IF||
               lookAhead.getType()==TokenType.KEY_WHILE||
               lookAhead.getType()==TokenType.RPAREN){
           //EmptyStatement es=ast.newEmptyStatement();
           //ifst.setElseStatement(es);
       }else {

       }
   }
   private ExpressionStatement assignmentStatement(){
       if (lookAhead.getType()==TokenType.IDENTIFIER){
           Token id=matchToken(TokenType.IDENTIFIER,"assignmentStatement");
           matchToken(TokenType.ASSIGN,"assignmentStatement");
           Expression e=expression();
           matchToken(TokenType.SEMICOLON,"assignmentStatement");

           SimpleName sn=ast.newSimpleName(id.getLexeme());
           Assignment assign=ast.newAssignment();
           assign.setLeftHandSide(sn);
           assign.setOperator(Assignment.Operator.ASSIGN);
           assign.setRightHandSide(e);
           ExpressionStatement es=ast.newExpressionStatement(assign);
           return es;
       }else {
           String errorTypes=TokenType.IDENTIFIER.toString();
           RecursionDescendParser.parsingError(errorTypes,"assignmentStatement",lookAhead);
           return  null;
       }
   }
   private Expression boolExpression(){
       if (lookAhead.getType() == TokenType.BOOL_TURE ||
               lookAhead.getType() == TokenType.BOOL_FALSE ||
               lookAhead.getType() == TokenType.LPAREN ||
               lookAhead.getType() == TokenType.IDENTIFIER||
               lookAhead.getType() == TokenType.INTEGER_LITERAL ){
          Expression left=boolTerm();
          Expression right=boolExpression_1(left);
          return right;
       }else {
           return null;
       }
   }
   private Expression boolExpression_1(Expression left){
       if(lookAhead.getType()==TokenType.LOGICAL_OR){
           matchToken(TokenType.LOGICAL_OR,"boolExpression_1");
           Expression right=boolTerm();


           InfixExpression infix=ast.newInfixExpression();
           infix.setOperator(InfixExpression.Operator.CONDITIONAL_OR);
           infix.setLeftOperand(left);
           infix.setRightOperand(right);
           Expression e=boolExpression_1(infix);
           return e;
       }else if(lookAhead.getType()==TokenType.RPAREN) {
           return left;
       }else{
           return null;

       }
   }
   private Expression expression(){
       if(lookAhead.getType()==TokenType.IDENTIFIER
               ||lookAhead.getType()==TokenType.LPAREN
               ||lookAhead.getType()==TokenType.INTEGER_LITERAL){
           Expression left=term();
           Expression right=expression_1(left);
           return right;
       }else {
           String errorTypes=TokenType.IDENTIFIER.toString()
                   +","+TokenType.INTEGER_LITERAL.toString()
                   +","+TokenType.LPAREN.toString();
           RecursionDescendParser.parsingError(errorTypes,"expression",lookAhead);
           return null;
       }
   }
   private Expression expression_1(Expression left){
       if(lookAhead.getType()==TokenType.PLUS){
           matchToken(TokenType.PLUS,"expression_1");
           Expression right=term();

           InfixExpression infix=ast.newInfixExpression();
           infix.setOperator(InfixExpression.Operator.PLUS);
           infix.setLeftOperand(left);
           infix.setRightOperand(right);

           Expression e=expression_1(infix);
           return e;
       }else if(lookAhead.getType()==TokenType.MINUS){
           matchToken(TokenType.MINUS,"expression_1");
           Expression right=term();

           InfixExpression infix=ast.newInfixExpression();
           infix.setOperator(InfixExpression.Operator.MINUS);
           infix.setLeftOperand(left);
           infix.setRightOperand(right);

           Expression e=expression_1(infix);
           return e;
       }else if(lookAhead.getType()==TokenType.LOGICAL_AND||
               lookAhead.getType()==TokenType.LOGICAL_OR){
           return left;
       }/*else if (lookAhead.getType()==TokenType.RPAREN
                ||lookAhead.getType()==TokenType.SEMICOLON){*/
       else if(lookAhead.getType()==TokenType.SEMICOLON||
               lookAhead.getType()==TokenType.LESS||
               lookAhead.getType()==TokenType.GREATER||
               lookAhead.getType()==TokenType.LESS_EQUAL||
               lookAhead.getType()==TokenType.GREATER_EQUAL||
               lookAhead.getType()==TokenType.EQUAL||
               lookAhead.getType()==TokenType.NOT_EQUAL||
               lookAhead.getType()==TokenType.RPAREN){//add
           //match epsilon
           //follow(E')={')',';'
           return left;
          }else {
           String errorTypes=TokenType.PLUS.toString()
                   +","+TokenType.MINUS.toString()
                   +","+TokenType.RPAREN.toString()
                   +","+TokenType.SEMICOLON.toString();
           RecursionDescendParser.parsingError(errorTypes,"expression_1",lookAhead);
           return null;
       }
   }
   private Expression boolTerm(){
       if (lookAhead.getType() == TokenType.BOOL_TURE ||
               lookAhead.getType() == TokenType.BOOL_FALSE ||
               lookAhead.getType() == TokenType.LPAREN ||
               lookAhead.getType() == TokenType.IDENTIFIER||
               lookAhead.getType() == TokenType.INTEGER_LITERAL ) {
           Expression f=boolFactor();
           Expression t=boolTerm_1(f);
           return t;
       }else {
           return null;

       }
   }
   private Expression term(){
       if (lookAhead.getType()==TokenType.IDENTIFIER
           ||lookAhead.getType()==TokenType.LPAREN
           ||lookAhead.getType()==TokenType.INTEGER_LITERAL){
           Expression f=factor();
           Expression t=term_1(f);
           return t;
       }else {
           String errorTypes=TokenType.IDENTIFIER.toString()
                   +","+TokenType.INTEGER_LITERAL.toString()
                   +","+TokenType.LPAREN.toString();
           RecursionDescendParser.parsingError(errorTypes,"term",lookAhead);
           return null;
       }
   }
   private Expression boolTerm_1(Expression left){
       if(lookAhead.getType()==TokenType.LOGICAL_AND){
           matchToken(TokenType.LOGICAL_AND,"boolTerm_1");
           Expression right=boolFactor();
           InfixExpression infix=ast.newInfixExpression();
           infix.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
           infix.setLeftOperand(left);
           infix.setRightOperand(right);
           Expression t=boolTerm_1(infix);
           return t;
       }else if(lookAhead.getType()==TokenType.RPAREN||
               lookAhead.getType()==TokenType.LOGICAL_OR){
           return  left;
       }else {
           return null;
       }
   }
   private Expression term_1(Expression left){
       if(lookAhead.getType()==TokenType.TIMES){
           matchToken(TokenType.TIMES,"term_1");
           Expression right=factor();
           InfixExpression infix=ast.newInfixExpression();
           infix.setOperator(InfixExpression.Operator.TIMES);
           infix.setLeftOperand(left);
           infix.setRightOperand(right);
           Expression t=term_1(infix);
           return t;
       }else if(lookAhead.getType()==TokenType.DIVIDE){
           matchToken(TokenType.DIVIDE,"term_1");
           Expression right=factor();
           InfixExpression infix=ast.newInfixExpression();
           infix.setOperator(InfixExpression.Operator.DIVIDE);
           infix.setLeftOperand(left);
           infix.setRightOperand(right);

           Expression t=term_1(infix);
           return t;
       }else if(lookAhead.getType()==TokenType.REMAINDER){
           matchToken(TokenType.REMAINDER,"term_1");
           Expression right=factor();

           InfixExpression infix=ast.newInfixExpression();
           infix.setOperator(InfixExpression.Operator.REMAINDER);
           infix.setLeftOperand(left);
           infix.setRightOperand(right);

           Expression t=term_1(infix);
           return t;
       }else if /*(lookAhead.getType()==TokenType.PLUS
               ||lookAhead.getType()==TokenType.MINUS
               ||lookAhead.getType()==TokenType.RPAREN
               ||lookAhead.getType()==TokenType.SEMICOLON){
           //match epsilon
           //follow(T')={'+','-',')',';}*/
               (lookAhead.getType()==TokenType.PLUS||
                       lookAhead.getType()==TokenType.MINUS||
                       lookAhead.getType()==TokenType.SEMICOLON||
                       lookAhead.getType()==TokenType.LESS||
                       lookAhead.getType()==TokenType.GREATER||
                       lookAhead.getType()==TokenType.LESS_EQUAL||
                       lookAhead.getType()==TokenType.GREATER_EQUAL||
                       lookAhead.getType()==TokenType.EQUAL||
                       lookAhead.getType()==TokenType.NOT_EQUAL||
                       lookAhead.getType()==TokenType.RPAREN//add
               ) {
           return left;
       }else if(lookAhead.getType()==TokenType.LOGICAL_AND||
               lookAhead.getType()==TokenType.LOGICAL_OR){
                   return left;
       }else {
           String errorTypes=TokenType.TIMES.toString()
                   +","+TokenType.DIVIDE.toString()
                   +","+TokenType.REMAINDER.toString()
                   +","+TokenType.PLUS.toString()
                   +","+TokenType.RPAREN.toString()
                   +","+TokenType.SEMICOLON.toString();
           RecursionDescendParser.parsingError(errorTypes,"term_1",lookAhead);
           return null;
       }

   }
   private Expression boolFactor(){
       if(lookAhead.getType()==TokenType.BOOL_TURE){
          matchToken(TokenType.BOOL_TURE,"boolFactor");
          BooleanLiteral bl=ast.newBooleanLiteral(true);
          return bl;
       }else if(lookAhead.getType()==TokenType.BOOL_FALSE){
          matchToken(TokenType.BOOL_FALSE,"boolFactor");
          BooleanLiteral bl=ast.newBooleanLiteral(false);
          return bl;
       }else if(lookAhead.getType()==TokenType.LPAREN||
               lookAhead.getType()==TokenType.IDENTIFIER||
               lookAhead.getType()==TokenType.INTEGER_LITERAL){
           Expression re=relationalExpression();
           return re;
       }else {
           return null;

       }
   }
   private Expression relationalExpression(){
       if(lookAhead.getType()==TokenType.LPAREN||
               lookAhead.getType()==TokenType.IDENTIFIER||
               lookAhead.getType()==TokenType.INTEGER_LITERAL){
           InfixExpression infix=ast.newInfixExpression();
           Expression left=expression();
           infix.setOperator(relationalOperator());
           Expression right=expression();
           infix.setLeftOperand(left);
           infix.setRightOperand(right);
           return infix;
       }else {
           return null;
       }
   }
   private InfixExpression.Operator relationalOperator(){
       if(lookAhead.getType()==TokenType.LESS){
          matchToken(TokenType.LESS,"relationalExpression");
          return InfixExpression.Operator.LESS;
       }else if(lookAhead.getType()==TokenType.GREATER){
           matchToken(TokenType.GREATER,"relationalExpression");
           return InfixExpression.Operator.GREATER;
       }else if(lookAhead.getType()==TokenType.LESS_EQUAL){
           matchToken(TokenType.LESS_EQUAL,"relationalExpression");
           return InfixExpression.Operator.LESS_EQUALS;
       }else if(lookAhead.getType()==TokenType.GREATER_EQUAL){
           matchToken(TokenType.GREATER_EQUAL,"relationalExpression");
           return InfixExpression.Operator.GREATER_EQUALS;
       }else if(lookAhead.getType()==TokenType.EQUAL){
           matchToken(TokenType.EQUAL,"relationalExpression");
           return InfixExpression.Operator.EQUALS;
       }else if(lookAhead.getType()==TokenType.NOT_EQUAL){
           matchToken(TokenType.NOT_EQUAL,"relationalExpression");
           return InfixExpression.Operator.NOT_EQUALS;
       }else {
           return null;

       }
   }
   private Expression factor(){
       if (lookAhead.getType()==TokenType.LPAREN){
           matchToken(TokenType.LPAREN,"factor");
           Expression e=expression();
           matchToken(TokenType.RPAREN,"factor");
           ParenthesizedExpression pe=ast.newParenthesizedExpression();
           pe.setExpression(e);
           return pe;
       }else if(lookAhead.getType()==TokenType.IDENTIFIER){
           Token id=matchToken(TokenType.IDENTIFIER,"factor");
           SimpleName sn=ast.newSimpleName(id.getLexeme());
           return  sn;
       }else if(lookAhead.getType()==TokenType.INTEGER_LITERAL){
           Token num=matchToken(TokenType.INTEGER_LITERAL,"factor");
           NumberLiteral nl=ast.newNumberLiteral(num.getLexeme());
           return nl;
       }else{
           String errorTypes=TokenType.LPAREN.toString()
                   +","+TokenType.IDENTIFIER.toString()
                   +","+TokenType.INTEGER_LITERAL.toString();
           RecursionDescendParser.parsingError(errorTypes,"factor",lookAhead);
           return null;
       }
   }
}
