# CS541 Proj2
This project is by Andrew Donovan for CS 541.

This project implements the provided code found here: http://www.cs.uky.edu/~raphael/courses/CS541/public.tar.gz for project 2.


- https://jflex.de/manual.html <- information I looked at in building
- https://www.unix.com/man-page/linux/1/flex/ <- information I looked at in building
- https://www.asciitable.com/ <- information I looked at in building
- https://docs.oracle.com/javase/9/docs/api/java/lang/String.html <- information I looked at in building


How to run (main tests or the long test):
- cd /startup
- make test > out 
- make testLong > out_long


Code Included in zip:

- P2.java 		<= runner code
- sym.java 		<= terminal definitions
- csx_go.jlex 	<= Lexer definitions
- Makefile		<= make file
- README.md 	<= README
- csx_lite		<= Test data with comments with the outcome / tests
- csx_long		<= Long single line of ^ (total: 23040 chars) [approximately 700kB] this tests the single line holding ability
- out			<= output from running normal test
- out_Long		<= output from running long test

###
I used code from: https://jflex.de/manual.html#Example near the bottom of the page under the line "The corresponding JFlex code (MiniScanner.jflex) could be" and above "This small example reads an input like". 

code used: 'colon =  ":" ' <= modified to => 'COLON 			= ":" '
###

Implementation: 
- When an error is found the scanner reports either the generic error message or a predefined message with what the error is. And where it was located.
	- Pre-defined error states
		- Integer Max Size Error
		- Integer Minimum Size Error
		- Illegal Char Error
		- Unclosed Char Error
		- Illegal String Error
		- Unclosed String Error
- There are also two special char prints in P2.java which print the symbol, of the char value stored. They are tab and newline.

The make file has been modified to work with the given filenames and to work with the local version of PMD I am using.

CMD used to Generate below:

- ./run.sh pmd -d {PATH-TO}/CS541Proj2/proj2/startup -auxclasspath proj2/startup/classes -f text -R category/java/bestpractices.xml
- make style [dumps output to file: out_style]
- The output from the style command will say error; This is owing to how PMD returns. There is no error.


Style Issues:
- SystemPrintln: <- the only style issue
- [Personal] I find it more readable in code with space between the lines.


Added Spell checker function from the make file
- make spell
	- ispell $(file_name) -x

Tested:
- Multi line comments and single line comments
- symbols of size one and two 
- RW of multiple cases
- Boolean literals of multiple
- Id's
- Integer Literals Positive and negative
- Max and Minimum size errors
- Strings
- String errors
- Chars
- Char Errors
