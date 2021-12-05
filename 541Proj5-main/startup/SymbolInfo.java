/**************************************************
*  class used to hold information associated w/
*  Symbs (which are stored in SymbolTables)
*
****************************************************/
// used for map and list
import java.util.*;

class SymbolInfo extends Symb {
    
    public Kinds kind; 
    public Types type;

    // used to store size
    public int size;
     
    // Using a linked list to store the list's of type and kind.
    // Because java does not have a tuple / pair (it does in java 9).
    // class but its not super useful in this case.

    // formal storage
    public LinkedList<Types> typeList = new LinkedList<Types>();
    public LinkedList<Kinds> kindList = new LinkedList<Kinds>();

    // actuals / args storage
    public LinkedList<Types> actualTypeList = new LinkedList<Types>();
    public LinkedList<Kinds> actualKindList = new LinkedList<Kinds>();

     public SymbolInfo(String id, Types t, Kinds k){
    	super(id);
    	
        type = t;

        kind = k; 

    };

    // overload for size info to be placed into
    public SymbolInfo(String id, Types t, Kinds k, int s){
        super(id);
        
        type = t;

        kind = k; 

        size = s;
       
    };
     
     public SymbolInfo(String id, int t, int k){
    	super(id);
    	
        type = new Types(t);

        kind = new Kinds(k); 
    };

    // Used for CG

    public int varIndex; // from given code
    // I couldn't think of a better name...

    public String packagePath;
    // used to store info about the package for
    // global vars

    public String signature;
    // used to hold the signature of a function

     
    public String toString(){
        
        return "("+name()+": kind=" + kind+ ", type="+  type+")";

    };

    //added to support testing

    public List<Types> getTypeList(){

        return typeList;
    };

    public List<Kinds> getKindList(){

        return kindList;
    };

    public List<Types> getActTypeList(){

        return actualTypeList;
    };

    public List<Kinds> getActKindList(){

        return actualKindList;
    };

    public void setSize(int s){
        size = s;
    };


    // easier method to return the size
    public int returnSize(){
        return size;
    };



}

