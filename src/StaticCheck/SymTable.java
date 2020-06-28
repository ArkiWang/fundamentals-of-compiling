package StaticCheck;
import lexical.Token;

import java.util.HashMap;
import java.util.LinkedList;
import lexical.Token.*;


/**
 * Created by yueli on 2018/5/24.
 */
public class SymTable {
    private HashMap<String,LinkedList<Symbol>>blockSymTables;
    public SymTable(){
        blockSymTables=new HashMap<>();
    }
    public void insert(String name, TokenType type,int level){
         Kind kind=null;
        if(type==TokenType.KEY_INT){
            kind=Kind.INT;
        }else if(type==TokenType.KEY_BOOLEAN){
            kind=Kind.BOOLEAN;
        }else {
            System.out.println("error insert into symTable, found non supported variable type.");
            System.out.println("name="+name);
            System.out.println("type="+type);
            System.out.println("level="+level);
            System.exit(1);
        }
        LinkedList<Symbol>entry=blockSymTables.get(name);
        if(entry==null){
            /*variables not declared before*/
            entry=new LinkedList<>();
        }else if(entry.getFirst().getLevel()==level){
            /*duplicate variable declaration in same level*/
            System.out.println("error insert into symTable, found duplicate variables.");
            System.out.println("name="+name);
            System.out.println("type="+type);
            System.out.println("level="+level);
            System.exit(1);
        }
        Symbol symbol=new Symbol(name,kind,level);
        entry.addFirst(symbol);
        blockSymTables.put(name,entry);
    }
    public Symbol lookup(String name,int level){
        LinkedList<Symbol>entry=blockSymTables.get(name);
        if(entry!=null){
            return entry.getFirst();
        }
        return null;
    }
    public void delete(String name){
        LinkedList<Symbol>entry=blockSymTables.get(name);
        if(entry!=null){
            entry.removeFirst();
            if(entry.isEmpty()){
                blockSymTables.remove(name);
            }
        }
    }
    public void display(){
        if(blockSymTables.isEmpty()){
            System.out.println("symTables is empty");
            return;
        }
        String ret="";
        for(String key:blockSymTables.keySet()){
            LinkedList<Symbol>list=blockSymTables.get(key);
            for(int i=0;i<list.size();i++){
                Symbol symbol=list.get(i);
                ret+=symbol.toString()+"\n";
            }
            System.out.println(ret);
        }
    }
    class Symbol{
        private String name;
        private Kind kind;
        private int level;
        public Symbol(String name,Kind kind,int level){
            this.name=name;
            this.kind=kind;
            this.level=level;
        }
        public String getName(){return name;}
        public Kind getKind(){return kind;}
        public int getLevel(){return level;}
        public String toString(){
            String ret="";
            ret+="name="+name;
            ret+=",kind"+kind;
            ret+=",level"+level;
            return ret;
        }
    }
    enum Kind{
        INT,BOOLEAN
    }
}
