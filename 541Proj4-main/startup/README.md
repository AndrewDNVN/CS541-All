# 541Prod4
Housing for CS 541 Project 4 for Andrew Donovan

This project implements the provided code found here: http://www.cs.uky.edu/~raphael/courses/CS541/public.tar.gz for project 4.

    https://jflex.de/manual.html <- information I looked at in building
    http://www2.cs.tum.edu/projects/cup/docs.php <- information I looked at in building
    https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html <- information I looked at in building
    https://docs.oracle.com/javase/8/docs/api/java/util/List.html <- information I looked at in building
    https://docs.oracle.com/javase/9/docs/api/javafx/util/Pair.html <- information I looked at in building
    https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html <- information I looked at in building 
    https://docs.oracle.com/javase/tutorial/java/javaOO/index.html <- Very useful read on how java objects work

# How to run (main test):

    cd /startup
    make test : Runs all tests included

# Code Included in zip:

    P4.java <= runner code
    csx_go.cup <= Parser definitions
    csx_go.jlex <= Lexer definitions
    Kinds.java <= Kind definitions
    Types.java <= Type definitions
    Makefile <= make file
    README.md <= README
    test.csx_go <= Test data
    out <= output from running normal test

# Implementation:

    Currently, the scanner is built from csx_go.jlex, which is then used to generate sym.java. 
    Which is then used in conjunction with csx_go.cup to construct the parser. 
    Java cup builds the parser over the provided grammar.
    And then, it is ready to parse the provided input.
    And using P4.java, it checks for semantic and scoping errors recursively.
    -- Break, Continue and Label are not implemented

    This is done the way of the book, with parent and child nodes. I was not able to figure out how to implement the lists for the formals.

# Implementation of Errors:

    Print where the error is currently with line and column numbers.
    - Break and continue can be parsed and unparsed. 
        - Albeit, in a straightforward form


# Known Issues
    - When there is an empty function, the brackets print with value (-1)
        - I believe this is because the Unparse inherits the 
          initial class from AST, which sets linenum to -1.

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

# Failed Tests

    - test number:
        - 06 : (Parse Error: Unknown reason)
        - 35
        - 36
        - 38
        - 43

## Carryover from previous Projects:

I used code from: https://jflex.de/manual.html#Example near the bottom of the page under the line "The corresponding JFlex code (MiniScanner.jflex) could be" and above "This small example reads an input like."

code used: 'colon = ":" ' <= modified to => 'COLON = ":" '

The make file has been modified to work with the given filenames and the local version of PMD I am using.

CMD used to Generate below:

    ./run.sh pmd -d {PATH-TO}/CS541Proj4/startup -auxclasspath CS541Proj4/startup/classes -f text -R category/java/bestpractices.xml
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
        - To avoid confusion, the column is printed as well as the standard line number. 