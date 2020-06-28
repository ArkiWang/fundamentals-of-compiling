package executeCheck;

import edu.ustc.cs.compile.platform.interfaces.InterRepresent;
import edu.ustc.cs.compile.platform.interfaces.InterpreterException;
import edu.ustc.cs.compile.platform.interfaces.InterpreterInterface;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.internal.compiler.*;



import java.util.Hashtable;
import java.util.Stack;

/**
 * Created by yueli on 2018/5/25.
 */
public class Interpreter implements InterpreterInterface {

    @Override
    public void interpret(InterRepresent interRepresent) throws InterpreterException {
        InterpVisitor visitor=new InterpVisitor();
        try {
            ((Block)interRepresent.getIR()).accept(visitor);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            throw new InterpreterException();
        }
    }
}
class InterpVisitor extends ASTVisitor {
    Hashtable<String,Integer>symValue=new Hashtable<>();
    Stack<Integer>stack=new Stack<>();

    public boolean visit(IfStatement ifst){
        ifst.getExpression().accept(this);
        Statement thenStatement=ifst.getThenStatement();
        Statement elseStatement=ifst.getElseStatement();
        int result=stack.pop();
        if(result==1){
            thenStatement.accept(this);
        }else {
            if(elseStatement!=null){
                elseStatement.accept(this);
            }
        }
        return  false;
    }
    public boolean visit(WhileStatement ws){
        ws.getExpression().accept(this);
        int result=stack.pop();
        while (result==1){
            ws.getBody().accept(this);
            ws.getExpression().accept(this);
            result=stack.pop();
        }
        return false;
    }
    public void endVisit(Assignment assign){
        Assignment.Operator operator=assign.getOperator();
        String varName=assign.getLeftHandSide().toString();
        int value=stack.pop();
        stack.pop();
        if(operator==Assignment.Operator.ASSIGN){
            symValue.put(varName,value);
        }
        System.out.println(varName+"="+value);
    }
    public void endVisit(InfixExpression infix){
        InfixExpression.Operator operator=infix.getOperator();
        int leftValue,rightValue;
        rightValue=stack.pop();
        leftValue=stack.pop();
        if(operator==InfixExpression.Operator.PLUS){
            stack.push(leftValue+rightValue);
        }else if(operator==InfixExpression.Operator.MINUS){
            stack.push(leftValue-rightValue);
        }else if(operator==InfixExpression.Operator.TIMES){
            stack.push(leftValue*rightValue);
        }else if(operator==InfixExpression.Operator.DIVIDE){
            if (rightValue==0){
                System.out.println("divided by zero!");
                System.exit(1);
            }else {
                stack.push(leftValue/rightValue);
            }
        }else if (operator==InfixExpression.Operator.REMAINDER){
            if (rightValue==0){
                System.out.println("divided by zero!");
                System.exit(1);
            }else {
                stack.push(leftValue%rightValue);
            }
        } else if(operator==InfixExpression.Operator.GREATER){
            stack.push(leftValue>rightValue?1:0);
        }else if(operator==InfixExpression.Operator.LESS){
            stack.push(leftValue<rightValue?1:0);
        }else if(operator==InfixExpression.Operator.GREATER_EQUALS){
            stack.push(leftValue>=rightValue?1:0);
        }else if(operator==InfixExpression.Operator.LESS_EQUALS){
            stack.push(leftValue<=rightValue?1:0);
        }else if(operator==InfixExpression.Operator.EQUALS){
            stack.push(leftValue==rightValue?1:0);
        }else if(operator==InfixExpression.Operator.NOT_EQUALS){
            stack.push(leftValue!=rightValue?1:0);
        }else if(operator==InfixExpression.Operator.CONDITIONAL_AND){
            stack.push((leftValue&rightValue)==1?1:0);
        }else if(operator==InfixExpression.Operator.CONDITIONAL_OR){
            stack.push((leftValue|rightValue)==1?1:0);
        }
    }
    public void endVisit(SimpleName sn){
        String name=sn.getIdentifier();
        if(!symValue.containsKey(name)){
            symValue.put(name,0);
        }
        stack.push(symValue.get(name));
    }
    public void endVisit(NumberLiteral nl){
        stack.push(Integer.parseInt(nl.getToken()));
    }


}
