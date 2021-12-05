import java.io.*;
import java.util.*;
class SymbolTable {

   private LinkedList<HashMap<String, Symb>> symbol_table_global;
   // used to house the list of scopes 

   SymbolTable() {

      symbol_table_global = new LinkedList<HashMap<String, Symb>>();
      // Creating linked list
   }

   public void openScope() {

      symbol_table_global.addFirst(new HashMap<String, Symb>());

      // add new scope to the top of the linked list
      // this allows for the rest to follow
      
   }

   public void closeScope() throws EmptySTException {

      if (symbol_table_global.isEmpty()) {
         throw new EmptySTException();
         // Check empty first
      }

      else{

         symbol_table_global.removeFirst();
         // Close the top scope
         // Value of the linkedlist

      }
      
   }

   public void insert(String name, Symb s) throws EmptySTException, DuplicateException {

      if (symbol_table_global.isEmpty()) {
         throw new EmptySTException();
         // Check empty first
      }

      else{ // not empty checking next exception

         if ( symbol_table_global.getFirst().containsKey(name)){
            throw new DuplicateException();
            // using the getFirst() check if the top
            // HashMap via containsKey() is true if so
            // Throw Exception
         }

         else{

            symbol_table_global.getFirst().put(name, s);

            System.out.print("(" + name + ":" + s.toString() + ")"+ " was added to the Top Scope. \n");

            // put s object into the top scope
            // this is done via getFirst method
            // loads whole symbol into map as the value 
         }

      }
      
   }

   public Symb localLookup(String s) throws EmptySTException {

     if (symbol_table_global.isEmpty()) {
      throw new EmptySTException();
         // Check empty first
   }


   else {

      if ( symbol_table_global.getFirst().containsKey(s)){

         return symbol_table_global.getFirst().get(s);

         // Check Top Scope for symbol using the .getFirst()
         // Then on that return via the containsKey()
         // Check if the key is there
         // if so return

      }

   }

   return null; 
   // else nothing is found return null
}

public Symb globalLookup(String s) throws EmptySTException {

  if (symbol_table_global.isEmpty()) {
   throw new EmptySTException();
               // Check empty first
}

else{

   for(HashMap<String, Symb> tmp : symbol_table_global){
         //^ was replaced via the pmd style guide
         // using the foreach method load each map needed into
         // the tmp HashMap and then check that from the given name
      if(tmp.containsKey(s)){
         return tmp.get(s);
            // Check if tmp currently cotains the key
            // if so return
      }
   }
}

return null; 
    // the fall through

}

@Override
public String toString() {
   String output = "\nContents of the Symbol Table:\n";

   for(int i = 0; i < symbol_table_global.size(); i++){
      // the foreach method does not work well 
      // owing to the get requiring an interger 
      output += symbol_table_global.get(i);
      output += "\n"; // for each new scope added
   }
   output += "\nEnd of Table \n";

   // Quick and dirty method of getting all info out of the table
   // Formatting added to aid in readability of the Dump Command

   return output; 
}

void dump(PrintStream ps) {
   ps.print(toString());
   //Just calls to string 
}


} // class SymbolTable
