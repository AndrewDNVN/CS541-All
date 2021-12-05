import java.io.*;
import java.util.*;
class P1 {
 public static void main(String args[]){
   System.out.println(
     "Project 1 test driver. Enter any of the following commands:\n"+
     "  (Command prefixes are allowed)\n"+
     "\tOpen (a new scope)\n"+
     "\tClose (innermost current scope)\n"+
     "\tDump (contents of symbol table)\n"+
     "\tInsert (symbol,integer pair into symbol table)\n"+
     "\tLookup (lookup symbol in top scope)\n"+
     "\tGlobal (global lookup of symbol in symbol table)\n"+
     "\tQuit (test driver)\n"+
     ""); // Given in Startup Tar
          // Reordered to match the internal driver code


   SymbolTable internalST = new SymbolTable();

   // New Symbol Table object

   Scanner sc = new Scanner(System.in);

   // Built in Scanner from java.util

   String userCMD;
   // used to take in User input

   while (sc.hasNext()){
    // if there is somethign there
    // Keep looping

    userCMD = sc.next();
    // take in the next object

    // and then run through the tree of if and if-else to determine the command
    
    // determine User CMD allows all cases and single letters of all cases as well

    // exceptions are numbered 0-4 to help with debugging

    if ("open".equalsIgnoreCase(userCMD) || "o".equalsIgnoreCase(userCMD)){

            // Does not throw exceptions

      internalST.openScope();
      // open a new top scope

      System.out.println("Opened new Scope.\n");
      // print out of the command

    }

    else if ("close".equalsIgnoreCase(userCMD) || "c".equalsIgnoreCase(userCMD)){

            // Throws Empty Stack exception

      try{

        internalST.closeScope();

        // inside of a try catch block to catch exceptions
        // no return

      }

      catch(EmptySTException a){
        System.out.println("Empty Stack Exception Thrown and caught. 0\n");

      }

      System.out.println("Closed the Top Scope.\n");
      // print out what was done


    }

    else if ("dump".equalsIgnoreCase(userCMD) || "d".equalsIgnoreCase(userCMD)){

            // Does not throw exceptions
     internalST.dump(System.out);
     // Print out the current symbol table

   }

   else if ("insert".equalsIgnoreCase(userCMD) || "i".equalsIgnoreCase(userCMD)){
          //Throws Empty Stack exception
          //Throws Duplicate exception
          // no return

    System.out.println("Enter Symbol: ");
    String symbolInternal = sc.next(); 
          // take in the symbol name

    System.out.println("Enter value: ");
    String valueInternal = sc.next(); 
          // take in the symbol value

          //create an item of the Symb class

    Symb testSymb = new Symb(valueInternal); 
         // create a value to place in to the map

    try{

      internalST.insert(symbolInternal, testSymb); 
            // Insert into the table

    } catch(EmptySTException a){

      System.out.println( "Empty Symbol Table Exception Thrown and caught. 1\n");

    }

    catch(DuplicateException b){

      System.out.println(testSymb.name() +" : Duplicate Exception Thrown and caught. Found in the curent scope. 2\n");

    }

  }

  else if ("lookup".equalsIgnoreCase(userCMD) || "l".equalsIgnoreCase(userCMD)){

          //Returns Symb
          //Throws Empty Stack exception

    System.out.println("Enter Symbol: ");
          String symbolInternal = sc.next(); // take in the symbol name


          try{

            Symb localLookupTMP = internalST.localLookup(symbolInternal); // Looking up symbol in top scope and returning the full symbol if found
            // create tmp Symb object to handle the return

            if (localLookupTMP != null){

              System.out.print("(" + symbolInternal + ":" + localLookupTMP.toString() + ")"+ " Was found in the top Scope. \n");
              // print out command
            }

            else{

              System.out.print(symbolInternal + ": Was not found in the top scope.\n");
              // print out failure

            }

          }

          catch(EmptySTException c){

            System.out.println( "Empty Symbol Table Exception Thrown and caught. 3\n");
            break;

          }


        }

        else if ("global".equalsIgnoreCase(userCMD) || "g".equalsIgnoreCase(userCMD)){

          //Returns Symb
          //Throws Empty Stack exception

          System.out.println("Enter Symbol: ");
          String symbolInternal = sc.next(); // take in the symbol name


          try{

           Symb globalLookupTMP = internalST.globalLookup(symbolInternal); // Looking up symbol in all scopes and returning the full symbol if found
           // create tmp Symb object to handle the return

           if (globalLookupTMP != null){

            System.out.print("(" + symbolInternal + ":" + globalLookupTMP.toString() + ")"+ " Was found in the table. \n");
            // print out command
          }

          else{

            System.out.print(symbolInternal + ": Was not found in the table.\n");
            // print out failure

          }

        }

        catch(EmptySTException d){

          System.out.println( "Empty Symbol Table Exception Thrown and caught. 4\n");

        }


      }

      else if ("quit".equalsIgnoreCase(userCMD) || "q".equalsIgnoreCase(userCMD)){

          //Does not throw Exceptions
        System.out.println("Quitting.\n");
        break;


      }

        else{ // make sure user did put in an actual command

          System.out.println("User entered not a Command.\n");

        }


      } // end of while loop

      sc.close();
      // closing the scanner object

      // Complete this
   } // main
} // class P1
