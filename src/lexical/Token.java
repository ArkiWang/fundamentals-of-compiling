package lexical;

/**
 * Created by yueli on 2018/5/17.
 */


public class Token {
    private TokenType type;
    private String token;
    private int line;
    private int column;

    public Token(TokenType type,String token,int line,int column){
        this.type=type;
        this.token=token;
        this.line=line;
        this.column=column;
    }
    public TokenType getType(){
        return type;
    }
    public int getLine(){
        return line;
    }
    public int getColumn(){
        return column;
    }
    public String getLexeme(){
        return token;
    }
    public String toString(){
        return type+" "+token+" ("+line+","+column+")";
    }
    public enum TokenType{
        IGNORE,IDENTIFIER,
        INTEGER_LITERAL,BOOL_TURE,BOOL_FALSE,
        KEY_INT,KEY_BOOLEAN,KEY_WHILE,KEY_IF,KEY_ELSE,
        PLUS,MINUS,TIMES,DIVIDE,REMAINDER,
        LESS,GREATER,LESS_EQUAL,GREATER_EQUAL,NOT_EQUAL,EQUAL,
        LOGICAL_NOT,LOGICAL_AND,LOGICAL_OR,
        SBS_AND,SBS_OR,
        ASSIGN,LPAREN,RPAREN,LBRACKET,RBRACKET,
        COMMA,SEMICOLON,
        EOF}

}
