package parser;

import lexical.BlockLexer;
import lexical.Token;
import lexical.Token.*;

/**
 * Created by yueli on 2018/5/17.
 */
public class RecursionDescendParser {
    private BlockLexer lexer=null;
    private Token lookAhead=null;
    public RecursionDescendParser(){

    }
    public void doParse(String filePath){
        lexer=new BlockLexer(filePath);
        this.parse();
    }
    public void matchToken(TokenType type,String functionName){
        if(lookAhead.getType()!=type){
            parsingError(type.toString(),functionName,lookAhead);
        }
        lookAhead=lexer.nextToken();
    }
    public static void parsingError(String types,String functionName,Token lookAhead){
        System.out.println("Parsing Error!in "+functionName);
        System.out.println("encounter "+lookAhead.getLexeme());
        System.out.println("at line "+lookAhead.getLine()+",column "+lookAhead.getColumn());
        System.out.println("while expecting "+types);
        System.exit(1);
    }
    public void parse(){
        lookAhead=lexer.nextToken();
        simpleBlock();
        System.out.println("Parsing Success!");

    }
    private void simpleBlock(){
        if(lookAhead.getType()==TokenType.LBRACKET){
            matchToken(TokenType.LBRACKET,"simpleBlock");
            sequence();
            matchToken(TokenType.RBRACKET,"simpleBlock");
        }else {
            parsingError(TokenType.LBRACKET.toString(),"simpleBlock",lookAhead);
        }
    }
    /*
    * sequence=assignmentStatement sequence | ifStatement sequence | whileStatement sequence | epsilon
    * S->AS|IS|WS|e
    * */
    private void sequence(){
        if(lookAhead.getType()==TokenType.IDENTIFIER){
              assignmentStatement();
              sequence();
        }else if (lookAhead.getType()==TokenType.KEY_IF) {/*add*/
            ifStatement();
            sequence();
        }else if(lookAhead.getType()==TokenType.KEY_WHILE) {/*add*/
            whileStatement();
            sequence();
        }else if(lookAhead.getType()==TokenType.RBRACKET){
            //match epsilon
        }else {
            String errorTypes=TokenType.IDENTIFIER.toString()+","+
                    TokenType.RBRACKET.toString();
            parsingError(errorTypes,"sequence",lookAhead);
        }
    }
    private void assignmentStatement(){//赋值
        if(lookAhead.getType()==TokenType.IDENTIFIER){
            matchToken(TokenType.IDENTIFIER,"assignmentStatement");
            matchToken(TokenType.ASSIGN,"assignmentStatement");
            expression();
            matchToken(TokenType.SEMICOLON,"assignmentStatement");
        }else{
            String errorTypes=TokenType.IDENTIFIER.toString();
            parsingError(errorTypes,"assignmentStatement",lookAhead);
        }
    }
    private void whileStatement(){
        if(lookAhead.getType()==TokenType.KEY_WHILE){
            matchToken(TokenType.KEY_WHILE,"whileStatement");
            matchToken(TokenType.LPAREN,"whileStatement");
            boolExpression();
            matchToken(TokenType.RPAREN,"whileStatement");
            matchToken(TokenType.LBRACKET,"whileStatement");
            sequence();
            matchToken(TokenType.RBRACKET,"whileStatement");
        }else {

        }
    }
    /*
    * */
    private void ifStatement(){
        if(lookAhead.getType()==TokenType.KEY_IF){
            matchToken(TokenType.KEY_IF,"ifStatement");
            matchToken(TokenType.LPAREN,"ifStatement");
            boolExpression();

            matchToken(TokenType.RPAREN,"ifStatement");
            matchToken(TokenType.LBRACKET,"ifStatement");
            sequence();
            matchToken(TokenType.RBRACKET,"ifStatement");
            optionalElse();
        }else {

        }
    }
    private void optionalElse(){
        if(lookAhead.getType()==TokenType.KEY_ELSE){
            matchToken(TokenType.KEY_ELSE,"optionalElse");
            matchToken(TokenType.LBRACKET,"optionalElse");
            sequence();
            matchToken(TokenType.RBRACKET,"optionalElse");
        }else if(lookAhead.getType()==TokenType.IDENTIFIER||
                lookAhead.getType()==TokenType.KEY_IF||
                lookAhead.getType()==TokenType.KEY_WHILE||
                lookAhead.getType()==TokenType.RPAREN){

        }else {

        }
    }
    private void expression(){
        if(lookAhead.getType()==TokenType.IDENTIFIER||
                lookAhead.getType()==TokenType.LPAREN||
                lookAhead.getType()==TokenType.INTEGER_LITERAL){
            term();
            expression_1();
        }else {
            String errorTypes=TokenType.PLUS.toString()
                    +","+TokenType.MINUS.toString()
                    +","+TokenType.RPAREN.toString()
                    +","+TokenType.SEMICOLON.toString();
            parsingError(errorTypes,"expression",lookAhead);
        }
    }

    private void expression_1(){
        if(lookAhead.getType()==TokenType.PLUS){
            matchToken(TokenType.PLUS,"expression_1");
            term();
            expression_1();
        }else if(lookAhead.getType()==TokenType.MINUS){
            matchToken(TokenType.MINUS,"expression_1");
            term();
            expression_1();
        }else if(lookAhead.getType()==TokenType.LOGICAL_AND||
                lookAhead.getType()==TokenType.LOGICAL_OR){

        }
        /*else if (lookAhead.getType()==TokenType.RPAREN
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
            //follow(E')={')',';'}
        }else {
            String errorTypes=TokenType.PLUS.toString()
                    +","+TokenType.MINUS.toString()
                    +","+TokenType.RPAREN.toString()
                    +","+TokenType.SEMICOLON.toString();
            parsingError(errorTypes,"expression_1",lookAhead);
        }
    }
    private void boolExpression(){
        if (lookAhead.getType() == TokenType.BOOL_TURE ||
                lookAhead.getType() == TokenType.BOOL_FALSE ||
                lookAhead.getType() == TokenType.LPAREN ||
                lookAhead.getType() == TokenType.IDENTIFIER||
                lookAhead.getType() == TokenType.INTEGER_LITERAL ){
            boolTerm();
            boolExpression_1();
        }else {

        }
    }
    private void boolExpression_1(){
        if(lookAhead.getType()==TokenType.LOGICAL_OR){
            matchToken(TokenType.LOGICAL_OR,"boolExpression_1");
            boolTerm();
            boolExpression_1();
        }else if(lookAhead.getType()==TokenType.RPAREN){

        }else{

        }
    }
    private void boolTerm() {
        if (lookAhead.getType() == TokenType.BOOL_TURE ||
                lookAhead.getType() == TokenType.BOOL_FALSE ||
                lookAhead.getType() == TokenType.LPAREN ||
                lookAhead.getType() == TokenType.IDENTIFIER||
                lookAhead.getType() == TokenType.INTEGER_LITERAL ) {
            boolFactor();
            boolTerm_1();
        }else {

        }
    }
    private void boolTerm_1(){
        if(lookAhead.getType()==TokenType.LOGICAL_AND){
            matchToken(TokenType.LOGICAL_AND,"boolTerm_1");
            boolFactor();
            boolTerm_1();
        }else if(lookAhead.getType()==TokenType.RPAREN||
                lookAhead.getType()==TokenType.LOGICAL_OR){

        }else {

        }
    }
    private void boolFactor(){
        /*if(lookAhead.getType()==TokenType.LOGICAL_NOT){
            matchToken(TokenType.LOGICAL_NOT,"boolFactor");
            boolExpression();
        }else */if(lookAhead.getType()==TokenType.BOOL_TURE){
            matchToken(TokenType.BOOL_TURE,"boolFactor");
        }else if(lookAhead.getType()==TokenType.BOOL_FALSE){
            matchToken(TokenType.BOOL_FALSE,"boolFactor");
        }else if(lookAhead.getType()==TokenType.LPAREN||
                lookAhead.getType()==TokenType.IDENTIFIER||
                lookAhead.getType()==TokenType.INTEGER_LITERAL){
            relationalExpression();
        }else {

        }
    }
    private void relationalExpression(){
        if(lookAhead.getType()==TokenType.LPAREN||
                lookAhead.getType()==TokenType.IDENTIFIER||
                lookAhead.getType()==TokenType.INTEGER_LITERAL){
            expression();
            relationalOperator();
            expression();
        }else {

        }
    }
    private void relationalOperator(){
        if(lookAhead.getType()==TokenType.LESS){
            matchToken(TokenType.LESS,"relationalExpression");
        }else if(lookAhead.getType()==TokenType.GREATER){
            matchToken(TokenType.GREATER,"relationalExpression");
        }else if(lookAhead.getType()==TokenType.LESS_EQUAL){
            matchToken(TokenType.LESS_EQUAL,"relationalExpression");
        }else if(lookAhead.getType()==TokenType.GREATER_EQUAL){
            matchToken(TokenType.GREATER_EQUAL,"relationalExpression");
        }else if(lookAhead.getType()==TokenType.EQUAL){
            matchToken(TokenType.EQUAL,"relationalExpression");
        }else if(lookAhead.getType()==TokenType.NOT_EQUAL){
            matchToken(TokenType.NOT_EQUAL,"relationalExpression");
        }else {

        }
    }
    private void term(){
        if(lookAhead.getType()==TokenType.IDENTIFIER
                ||lookAhead.getType()==TokenType.LPAREN
                ||lookAhead.getType()==TokenType.INTEGER_LITERAL) {
            factor();
            term_1();

        }else {
            String errorTypes=TokenType.IDENTIFIER.toString()
                    +","+TokenType.INTEGER_LITERAL.toString()
                    +","+TokenType.LPAREN.toString();
            parsingError(errorTypes,"term",lookAhead);
        }
    }
    private void term_1(){
        if(lookAhead.getType()==TokenType.TIMES){
            matchToken(TokenType.TIMES,"term_1");
            factor();
            term_1();
        }else if (lookAhead.getType()==TokenType.DIVIDE){
            matchToken(TokenType.DIVIDE,"term_1");
            factor();
            term_1();
        }else if (lookAhead.getType()==TokenType.REMAINDER) {
            matchToken(TokenType.REMAINDER, "term_1");
            factor();
            term_1();
        }else if(lookAhead.getType()==TokenType.LOGICAL_AND||
                lookAhead.getType()==TokenType.LOGICAL_OR){

        }else if/* (lookAhead.getType()==TokenType.PLUS||
                lookAhead.getType()==TokenType.MINUS||
                lookAhead.getType()==TokenType.RPAREN||
                lookAhead.getType()==TokenType.SEMICOLON){
            //match epsilon
            //follow(T')={'+','-',')',';'}*/
                (lookAhead.getType()==TokenType.PLUS||
                        lookAhead.getType()==TokenType.MINUS||
                        lookAhead.getType()==TokenType.SEMICOLON||
                        lookAhead.getType()==TokenType.LESS||
                        lookAhead.getType()==TokenType.GREATER||
                        lookAhead.getType()==TokenType.LESS_EQUAL||
                        lookAhead.getType()==TokenType.GREATER_EQUAL||
                        lookAhead.getType()==TokenType.EQUAL||
                        lookAhead.getType()==TokenType.NOT_EQUAL||
                        lookAhead.getType()==TokenType.RPAREN
                ){
        }else {
            String errorTypes=TokenType.TIMES.toString()
                    +","+TokenType.DIVIDE.toString()
                    +","+TokenType.REMAINDER.toString()
                    +","+TokenType.PLUS.toString()
                    +","+TokenType.MINUS.toString()
                    +","+TokenType.RPAREN.toString()
                    +","+TokenType.SEMICOLON.toString();
            parsingError(errorTypes,"term_1",lookAhead);
        }
    }
    private void factor(){
        if(lookAhead.getType()==TokenType.LPAREN){//(
            matchToken(TokenType.LPAREN,"factor");
            expression();
            matchToken(TokenType.RPAREN,"factor");
        }else if(lookAhead.getType()==TokenType.IDENTIFIER){//var
            matchToken(TokenType.IDENTIFIER,"factor");
        }else if (lookAhead.getType()==TokenType.INTEGER_LITERAL){//int
            matchToken(TokenType.INTEGER_LITERAL,"factor");
        }else{
            String errorTypes=TokenType.LPAREN.toString()
                    +","+TokenType.IDENTIFIER.toString()
                    +","+TokenType.INTEGER_LITERAL.toString();
            parsingError(errorTypes,"factor",lookAhead);
        }
    }
}
