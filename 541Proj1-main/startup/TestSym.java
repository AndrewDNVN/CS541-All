class TestSym extends Symb {
   private String value;
   TestSym(String n, String i) {super(n); value = i;}
   public String value() {return value;}
   @Override
   public String toString() {return "("+name()+":"+String.valueOf(value)+")";}
} // class TestSym

// converted to only take in strings
// can be modifed later to handle other values
