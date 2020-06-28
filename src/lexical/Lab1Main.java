package lexical;

/**
 * Created by yueli on 2018/5/17.
 */
public class Lab1Main {
    public static void main(String args[]){
        BlockLexer I=new BlockLexer("E:\\JAVAWORKS\\fundamentals of compiling\\src\\parser\\expr2_1.txt");
        Token s=I.nextToken();
        while(s.getType()!= Token.TokenType.EOF){
            System.out.println(s);
            s=I.nextToken();
        }
    }
}