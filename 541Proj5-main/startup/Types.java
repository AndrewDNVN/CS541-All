class Types{
 public static final int Character 	= 0;
 public static final int Integer 	= 1;
 public static final int Boolean 	= 2;
 public static final int Error 		= 3;
 public static final int Unknown 	= 4;
 public static final int Void 		= 5;
 public static final int String 	= 6;

 Types(int i){val = i;}
 Types(){val = Unknown;}

 public String toString() {
	switch(val){
	  case 0: return "Character";
	  case 1: return "Integer";
	  case 2: return "Boolean";
	  case 3: return "Error";
	  case 4: return "Unknown";
	  case 5: return "Void";
	  case 6: return "String";
	  default: throw new RuntimeException();
	}
 }

 int val;
}

