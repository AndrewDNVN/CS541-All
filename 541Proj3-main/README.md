# 541Proj3
Housing for CS 541 Project 3 for Andrew Donovan

This project implements the provided code found here: http://www.cs.uky.edu/~raphael/courses/CS541/public.tar.gz for project 3.

    https://jflex.de/manual.html <- information I looked at in building
    http://www2.cs.tum.edu/projects/cup/docs.php <- information I looked at in building

# How to run (main test):

    cd /startup
    make test > out

    - make test_Bad &> out.bad
        - Shows output when parser fails

# Code Included in zip:

    P3.java <= runner code
    csx_go.cup <= parser definitions
    csx_go.jlex <= Lexer definitions
    Makefile <= make file
    README.md <= README
    test.csx_go <= Test data with comments with the outcome / tests
    out <= output from running normal test
    test.bad <= shows how the parser errors.
    out.bad <= output from bad run

# Implementation:

    Currently, the scanner is built from csx_go.jlex, which is then used to generate sym.java. 
    Which is then used in conjunction with csx_go.cup to construct the parser. 
    Java cup builds the parser over the provided grammar.
    And then, it is ready to parse the provided input.
    And using P3.java, it prints out the AST to the terminal.
    -- Break and Continue are implemented

# Implementation of Errors:

    Print where the error is currently with line and column numbers.
    - Break and continue can be parsed and unparsed. 
        - Albeit, in a straightforward form

# Work in Progress
    -Labeled for loops
        - Outline is presented not implemented due to time constraints.
            {Wanted to get commentary on this before starting Proj 4.}


# Intresting Unparesings
    - The style was heavily based on the style given in the provided code.
    - The Braces have their value saved on the line where the internals of the block start.
    - There are extra ()'s. (lots of them)
    - Because the Semi-Colons are optional, they are not printed in the 'Unparsing' for block nodes.
    - If read and print states have more than one parameter, they are printed on different lines.
        - To avoid confusion, the column is printed as well as the standard line number. 


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
    - Style states that there are uneeded imports (P3 and Scanner .java): 
        - "UnusedImports:   Unused import 'java_cup.runtime.*'"
        - "UnusedImports:  Unused import 'java.io.Reader'"
        - But upon removing the code did not complie. So they are needed in some form.
            - I do not exactly know why though.
    - "AbstractClassWithoutAbstractMethod"
        - is in refrence to the ASTNode => Ignored.
    [Personal] I find it more readable in code with space between the lines.

## Carryover from previous Projects:

I used code from: https://jflex.de/manual.html#Example near the bottom of the page under the line "The corresponding JFlex code (MiniScanner.jflex) could be" and above "This small example reads an input like."

code used: 'colon = ":" ' <= modified to => 'COLON = ":" '

The make file has been modified to work with the given filenames and the local version of PMD I am using.

CMD used to Generate below:

    ./run.sh pmd -d {PATH-TO}/CS541Proj3/proj3/startup -auxclasspath proj2/startup/classes -f text -R category/java/bestpractices.xml
    make style [dumps output to file: out_style]
    The output from the style command will say error; This is owing to how PMD returns. There is no error.

Added Spell-checker function from the make file checks all user-modified files.

    make spell
        ispell $(file_name) -x
