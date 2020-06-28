package intermediate;

import edu.ustc.cs.compile.platform.interfaces.InterRepresent;
import edu.ustc.cs.compile.platform.interfaces.InterpreterException;
import org.eclipse.jdt.core.dom.InfixExpression;

/**
 * Created by yueli on 2018/5/20.
 */
public class Lab3Main {
    public static void main(String[] args)throws InterpreterException {
        String srcFileName="E:\\JAVAWORKS\\fundamentals of compiling\\src\\parser\\test1.txt";
        SyntaxDirectedTranslation parser=new SyntaxDirectedTranslation();
        InterRepresent ir=parser.doParse(srcFileName);
        ir.showIR();
    }
}
