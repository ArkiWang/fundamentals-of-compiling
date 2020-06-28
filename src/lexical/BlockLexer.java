package lexical;

import lexical.Token.TokenType;

import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;






public class BlockLexer {
    private PushbackReader in=null;
    private int state=0;
    private int start=0;
    private StringBuffer lexeme=new StringBuffer();
    private char c;
    private int line=0;
    private int column=0;

    public BlockLexer(String infile){
        PushbackReader reader=null;
        try{
            reader=new PushbackReader(new FileReader(infile));
        }catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        in=reader;
    }
    private void nextChar(){
        try{
            c=(char)in.read();
            lexeme.append(c);
            column++;
        }catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
    private void pushbackChar(){
        try{
            in.unread(lexeme.charAt(lexeme.length()-1));
            lexeme.deleteCharAt(lexeme.length()-1);
            column--;
        }catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
    private Token getToken(TokenType type){
        state=0;
        start=0;
        String t=lexeme.toString();
        lexeme.setLength(0);
        return new Token(type,t,line+1,column-t.length()+1);

    }
    private void dropChar(){
        lexeme.setLength(0);
    }
    public Token nextToken(){
        while(true){
            switch(state){
                case 0:
                    nextChar();
                    if(Character.isWhitespace(c)){//识别空格 多个没用
                        if(c=='\n'){
                            line++;
                            column=0;
                        }
                        dropChar();
                    }else if(Character.isDigit(c)){
                        state=1;
                    }else if(Character.isLetter(c)){
                        state=3;
                    }else if(c=='+'){
                        return getToken(TokenType.PLUS);
                    }else if(c=='-'){
                        return getToken(TokenType.MINUS);
                    }else if(c=='*'){
                        return getToken(TokenType.TIMES);
                    }else if(c=='/'){
                        return getToken(TokenType.DIVIDE);
                    }else if(c=='%'){
                        return getToken(TokenType.REMAINDER);
                    }else if(c=='('){
                        return getToken(TokenType.LPAREN);
                    }else if(c==')'){
                        return getToken(TokenType.RPAREN);
                    }else if(c=='{'){
                        return getToken(TokenType.LBRACKET);
                    }else if(c=='}'){
                        return getToken(TokenType.RBRACKET);
                    }else if(c==';'){
                        return getToken(TokenType.SEMICOLON);
                    }/*else if(c=='='){
                        return getToken(TokenType.EQUAL);
                    }*/else if(c==','){
                        return getToken(TokenType.COMMA);
                    }else if((c&0xff)==0xff){
                        return getToken(TokenType.EOF);
                    }else if(c=='=') {
                        state=5;
                    }else if(c=='!') {
                        state=6;
                    }else if(c=='<') {
                        state = 7;
                    }else if(c=='>') {
                        state = 8;
                    }else if(c=='&') {
                        state = 9;
                    }
                    else if(c=='|') {
                        state = 10;
                    }else{
                        System.out.println("get nextToken error");
                        System.out.println("find illegal character"+c);
                        System.out.println("at line"+line+",column"+column);
                        System.exit(-1);
                    }
                    break;
                case 1:
                    nextChar();
                    if(Character.isDigit(c)){
                        state=1;
                    }else{
                        state=2;
                    }
                    break;
                case 2:
                    pushbackChar();
                    return getToken(TokenType.INTEGER_LITERAL);
                case 3:
                    nextChar();
                    if(Character.isLetterOrDigit(c)){
                        state=3;
                    }else{
                        state=4;
                    }
                    break;
                case 4:
                    pushbackChar();
                    String t=lexeme.toString();
                    if(t.equalsIgnoreCase("int")){
                        return getToken(TokenType.KEY_INT);
                        //add here
                    }else if(t.equalsIgnoreCase("boolean")) {
                        return getToken(TokenType.KEY_BOOLEAN);
                    }else if(t.equalsIgnoreCase("while")) {
                        return getToken(TokenType.KEY_WHILE);
                    }else if(t.equalsIgnoreCase("if")) {
                        return getToken(TokenType.KEY_IF);
                    }else if(t.equalsIgnoreCase("else")) {
                        return getToken(TokenType.KEY_ELSE);
                    }else if(t.equalsIgnoreCase("true")) {
                        return getToken(TokenType.BOOL_TURE);
                    }else if(t.equalsIgnoreCase("false")) {
                        return getToken(TokenType.BOOL_FALSE);
                    /*}else if(t.equalsIgnoreCase("&&")) {
                        return getToken(TokenType.LOGICAL_AND);
                    }else if(t.equalsIgnoreCase("||")) {
                        return getToken(TokenType.LOGICAL_OR);*/
                    }else {
                        return getToken(TokenType.IDENTIFIER);
                    }
                case 5:
                    nextChar();
                    if(c=='='){
                        return getToken(TokenType.EQUAL);
                    }else {
                        pushbackChar();
                        return getToken(TokenType.ASSIGN);
                    }
                case 6:
                    nextChar();
                    if(c=='='){
                        return getToken(TokenType.NOT_EQUAL);
                    }else {
                        pushbackChar();
                        return getToken(TokenType.LOGICAL_NOT);
                    }
                case 7:
                    nextChar();
                    if(c=='='){
                        return getToken(TokenType.LESS_EQUAL);
                    }else {
                        pushbackChar();
                        return getToken(TokenType.LESS);
                    }
                case 8:
                    nextChar();
                    if(c=='='){
                        return getToken(TokenType.GREATER_EQUAL);
                    }else {
                        pushbackChar();
                        return getToken(TokenType.GREATER);
                    }
                case 9:
                    nextChar();
                    if(c=='&'){
                        return getToken(TokenType.LOGICAL_AND);
                    }else {
                        pushbackChar();
                        return getToken(TokenType.SBS_AND);
                    }
                case 10:
                    nextChar();
                    if(c=='|'){
                        return getToken(TokenType.LOGICAL_OR);
                    }else {
                        pushbackChar();
                        return getToken(TokenType.SBS_OR);
                    }
                default:
                    System.out.println("get nextToken error!");
                    System.out.println("find illegal state:"+state);
                    System.exit(-1);
            }

        }
    }
}
