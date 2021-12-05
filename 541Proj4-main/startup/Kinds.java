class Kinds{
 public static final int Var          = 0;
 public static final int Value        = 1;
 public static final int Other        = 2;
 public static final int Const        = 3;
 public static final int Array        = 4;
 public static final int ScalarParam  = 5;
 public static final int ArrayParam   = 6;
 public static final int Function     = 7;


 Kinds(int i){val = i;}
 Kinds(){val = Other;}

 public String toString() {
        switch(val){
          case 0: return "Var";
          case 1: return "Value";
          case 2: return "Other";
          case 3: return "Const";
          case 4: return "Array";
          case 5: return "ScalarParam";
          case 6: return "ArrayParam";
          case 7: return "Function";
          default: throw new RuntimeException();
        }
 }

 int val;
}
