This project is by Andrew Donovan for CS 541.

This project implements the provided code found here: http://www.cs.uky.edu/~raphael/courses/CS541/public.tar.gz for project 1.

It also uses the standard Java utility classes Hashmap and Scanner.
- https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html <- information I looked at in building
- https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html <- information I looked at in building
- https://docs.oracle.com/javase/8/docs/api/java/util/List.html <- information I looked at in building
- https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html <- information I looked at in building

It also uses the standard Java utility classes Hashmap and Scanner.

I used https://docs.oracle.com/javase/tutorial/java/javaOO/variables.html in researching how to program in Java.

Expected outcome explained in testinput_expected_outcome.txt.

Output captured via make test > output.

Code included in .zip :

	- DuplicateException.java : No modifcation
	- EmptySTException.java : No modifcation
	- Makefile : Style Modification to allow for PMD to be run locally
	- output : the output from running 'make test' with the provided input
	- P1.java : Driver code, slightly changed
	- Symb.java : No modifcation
	- SymbolTable.java : Uses Linked List as the Global table of Type HashMap<String, Symb> internally. Accomplishes all outcomes stated in Doc.
	- tags : No modification
	- testInput : tests most cases
	- testInput_expceted_outcome.txt : explainznes what the output should be / why it was tested
	- TestSym.java : Modified to have both fields be of type string
	- .gitignore : to make sure no .class files where included
	- README.md : Explanations

Current Style Issues(Reasons):

	- SystemPrintln: desired output is the terminal no need to log elsewhere
	- Linked List Loose Coupling: from the pmd Description, this is a non-issue; this code is designed with the linked list at it's core. And currently, there is no need to reference via its interface type.
	- ForLoopCanBeForeach: This implementation of the for loop is made for ease of readability; As opposed to doing a forEachLoop. 

CMD used to Genreate above: 
	
	- ./run.sh pmd -d 541Proj1/startup -auxclasspath startup/classes  -f text -R category/java/bestpractices.xml
{This was run using version 6.38.0 of PMD.}