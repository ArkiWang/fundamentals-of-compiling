package StaticCheck;

import edu.ustc.cs.compile.platform.interfaces.InterRepresent;
import edu.ustc.cs.compile.platform.interfaces.InterpreterException;

/**
 * Created by yueli on 2018/5/24.
 */
public class Lab4Main {
    public static void main(String[] args)throws InterpreterException {
        String srcFileName="E:\\JAVAWORKS\\fundamentals of compiling\\src\\StaticCheck\\expr2_2.txt";
        StaticCheck checker=new StaticCheck();
        InterRepresent ir=checker.doParse(srcFileName);
        ir.showIR();
    }
}
