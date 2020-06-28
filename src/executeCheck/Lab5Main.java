package executeCheck;

import StaticCheck.StaticCheck;
import edu.ustc.cs.compile.platform.interfaces.InterRepresent;
import edu.ustc.cs.compile.platform.interfaces.InterpreterException;

/**
 * Created by yueli on 2018/5/25.
 */
public class Lab5Main {
    public static void main(String[] args) throws InterpreterException{
        String srcFileName="E:\\JAVAWORKS\\fundamentals of compiling\\src\\StaticCheck\\test2.txt";
        StaticCheck checker=new StaticCheck();
        InterRepresent ir=checker.doParse(srcFileName);
        ir.showIR();
        Interpreter it=new Interpreter();
        it.interpret(ir);
    }
}
