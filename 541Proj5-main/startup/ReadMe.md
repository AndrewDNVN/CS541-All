# 541Proj5
Housing for CS 541 Project 5 for Andrew Donovan

This project implements the provided code found here: http://www.cs.uky.edu/~raphael/courses/CS541/public.tar.gz for project 5.

    https://jflex.de/manual.html <- information I looked at in building
    http://www2.cs.tum.edu/projects/cup/docs.php <- information I looked at in building
    https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html <- information I looked at in building
    https://docs.oracle.com/javase/8/docs/api/java/util/List.html <- information I looked at in building
    https://docs.oracle.com/javase/9/docs/api/javafx/util/Pair.html <- information I looked at in building
    https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html <- information I looked at in building 
    https://docs.oracle.com/javase/tutorial/java/javaOO/index.html <- Beneficial read on how java objects work
    https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html <- Very useful in defining
    http://jasmin.sourceforge.net/guide.html <- Useful in designing the Code Generator
    www.ist.tugraz.at/_attach/Publish/Cb14/Jasmin.pdf <- Used for guidance
    https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javap.html <- helpful in building the Java Comparable Code

# How to run (main test):

    cd /startup
    make test: Runs all tests included

# Code Included in the zip:

    P5.java <= runner code
    csx_go.cup <= Parser definitions
    csx_go.jlex <= Lexer definitions
    Kinds.java <= Kind definitions
    Types.java <= Type definitions
    Makefile <= make file
    README.md <= README
    test.csx_go <= Test data
    out <= output from running normal test
    /Java Comparable Code
        - This code is used to compare how the given simple programs are 
        built-in the java virtual machine and their outputs as well.
        - run via the following:
            - 'javac' command on the file
            - 'javap -c -p > tmp' command to save the output

# Implementation:

    Currently, the scanner is built from csx_go.jlex, which is then used to generate sym.java. 
    Which is then used in conjunction with csx_go.cup to construct the parser. 
    Java cup builds the parser over the provided grammar.
    And then, it is ready to parse the provided input.
    And using P4.java, it checks for semantic and scoping errors recursively.
    Finally in P5.java, the asm code is generated.
    -- Break, Continue, and Label are not implemented
    It then generates code into the test.j file which Jasmin interprets 
    and creates a java class which is then run.

    This is done the way of the book, with parent and child nodes. I could not figure out how to implement the lists for the formals.

# Implementation of Errors:

    Print where the error is currently with line and column numbers.
    - Break and continue can be parsed and unparsed. 
        - Albeit, in a straightforward form


# Cup Version
    - Tested on .11b    {no issues found}[Is what is currently set in 'Makefile']
    - Tested on .10k    {no issues found}

# Style Issues Sustained:

    - SystemPrintln: <- Main issue mostly in ast.java.
    - COLON is not used in the parser it has been commented out due to its function not being
        implemented.
    - Style states that there are unneeded imports (P3 and Scanner .java): 
    - "AbstractClassWithoutAbstractMethod"
        - is in reference to the ASTNode => Ignored.
    [Personal] I find it more readable in code with space between the lines.

# Failed Tests (for parsing)

    - test number:
        - 06 : (Parse Error: Unknown reason)
        - 35
        - 36
        - 38
        - 43
# Known Issues
    For Loops: Unsure of issue
## Carryover from previous Projects:

I used code from: https://jflex.de/manual.html#Example near the bottom of the page under the line "The corresponding JFlex code (MiniScanner.jflex) could be" and above "This small example reads an input like."

code used: 'colon = ":" ' <= modified to => 'COLON = ":" '

The make file has been modified to work with the given filenames and the local version of PMD I am using.

CMD used to Generate below:

    ./run.sh pmd -d {PATH-TO}/CS541Proj5/startup -auxclasspath CS541Proj5/startup/classes -f text -R category/java/bestpractices.xml
    make style [dumps output to file: out_style]
    The output from the style command will say error; This is owing to how PMD returns. There is no error.

Added Spell-checker function from the make file checks all user-modified files.

    make spell
        ispell $(file_name) -x

# Interesting Unparesings
    - The style was heavily based on the style given in the provided code.
    - The Braces have their value saved on the line where the internals of the block start.
    - There are extra ()'s. (lots of them)
    - Because the Semi-Colons are optional, they are not printed in the 'Unparsing' for block nodes.
    - If read and print states have more than one parameter, they are printed on different lines.
        - To avoid confusion, the column is printed and the standard line number. 