package parser;

/**
 * Created by yueli on 2018/5/18.
 */
public class Lab2Main {
    public static void main(String[] args) {
        String srcFileName="E:\\JAVAWORKS\\fundamentals of compiling\\src\\parser\\test1.txt";
        RecursionDescendParser parser=new RecursionDescendParser();
        parser.doParse(srcFileName);
    }
}
