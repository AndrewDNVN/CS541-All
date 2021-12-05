/** CUP generated class containing symbol constants. */
/* you will need to modify by hand for CSX_go */

//public static final int            =  ;              // 

public class sym {
  /* terminals */

  // Current number of terminals: 54

  // I used this to help build out csx_go.jlex
  // that is why the spacing is the way that it is
  // I find it much easier to read and understand.
  // Also, the values are in numerical order, close to the order given
  // in the documentation.

  // special

  public static final int EOF         = 0;     // End of File
  public static final int error       = 1;     // Generic error state

  // normal

  public static final int IDENTIFIER  = 2;     // ID
  
  // single position special

  public static final int SEMI        = 3;     // ;
  public static final int L_PAREN     = 4;     // (
  public static final int R_PAREN     = 5;     // )
  public static final int L_BRACE     = 6;     // {
  public static final int R_BRACE     = 7;     // }
  public static final int PLUS        = 8;     // +
  public static final int MINUS       = 9;     // - 
  public static final int EQUALS      = 10;    // =
  public static final int STAR        = 11;    // *
  public static final int COMMA       = 12;    // ,
  public static final int NOT         = 13;    // !
  public static final int L_BRACK     = 14;    // [  
  public static final int R_BRACK     = 15;    // ]    
  public static final int COLON       = 16;    // :
  public static final int L_THAN      = 17;    // <
  public static final int G_THAN      = 18;    // > 
  public static final int SLASH       = 19;    // / 

  // two position special

  public static final int D_EQUAL     = 20;    // ==
  public static final int NOT_EQUAL   = 21;    // !=
  public static final int D_AND       = 22;    // &&
  public static final int OR          = 23;    // ||
  public static final int L_THAN_EQ   = 24;    // <=
  public static final int G_THAN_EQ   = 25;    // >=

  // Reserved Words

  public static final int BOOL        = 26;    // BOOL 
  public static final int BREAK       = 27;    // BREAK
  public static final int CHAR        = 28;    // CHAR
  public static final int CONST       = 29;    // CONST
  public static final int CONTINUE    = 30;    // CONTINUE
  public static final int ELSE        = 31;    // ELSE
  public static final int FOR         = 32;    // FOR
  public static final int FUNC        = 33;    // FUNC
  public static final int IF          = 34;    // IF
  public static final int INT         = 35;    // INT Decl
  public static final int PACKAGE     = 36;    // PACKAGE
  public static final int PRINT       = 37;    // PRINT
  public static final int RETURN      = 38;    // RETURN
  public static final int VAR         = 39;    // VAR
  public static final int READ        = 40;    // READ
    
  // literals

  public static final int INT_LIT     = 41;    // Integer literal
  public static final int CHAR_LIT    = 42;    // Char literal
  public static final int STRING_LIT  = 43;    // String literal
  public static final int BOOL_LIT_T  = 44;    // Boolean literal True
  public static final int BOOL_LIT_F  = 45;    // Boolean literal False


  // Other States

    // Integer Errors

  public static final int INT_LIT_MAX_SIZE    = 46;   // Integer literal Max Size error
  public static final int INT_LIT_MIN_SIZE    = 47;   // Integer literal Min Size error

    // Char Errors
      // stored as a string to keep all information
  
  public static final int CHAR_LIT_ERR_ILL    = 48;   // Char Illegal 
  public static final int CHAR_LIT_ERR_UNC    = 49;   // Char Unclosed
  
  
    // String Errors

  public static final int STRING_LIT_ERR_ILL  = 50;   // String Illegal
  public static final int STRING_LIT_ERR_UNC  = 51;   // String unclosed

  // Special print states for Char

  public static final int CHAR_LIT_TAB        = 52;    // Char literal for tab to print
  public static final int CHAR_LIT_NEWLINE    = 53;    // Char literal for tab to print
  

}
