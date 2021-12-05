##########################################
####
# Makefile for project 3
# Modified 2/2011 Raphael Finkel
#
# All classes are compiled into the ./classes directory
#
# The default is to make all .class files.
#
#    clean    -- removes all generated files
#
#    test     -- Tests proj4 by running P4.class with inputs test[123].lite
# 	
#	COURSEDIR = /homes/raphael/courses/cs541/public
#	CUPPATH = $(COURSEDIR)/cup
#	CLASSPATH = $(DESTPATH):$(CUPPATH)/java-cup-10k.jar
#
###

DESTPATH = ./classes
CUPPATH = /home/andrew/Documents/cup
CLASSPATH = $(DESTPATH):$(CUPPATH)/java-cup-10k.jar
VPATH = $(DESTPATH)
JCFLAGS = -Xlint:all,-auxiliaryclass,-rawtypes -deprecation -classpath $(CLASSPATH) -d $(DESTPATH)
JFLAGS = -classpath $(CLASSPATH)

%.class: %.java
	javac $(JCFLAGS) $<

.PHONY: all 
all: P4.class Yylex.class parser.class 

# don't use CUP 0.11a; I couldn't get it to work right.  -- Raphael 2/2011
sym.java parser.java: csx_go.cup 
	java $(JFLAGS) java_cup/Main < csx_go.cup
Yylex.java: csx_go.jlex
	jflex csx_go.jlex
parser.class: ASTNode.class Scanner.class SyntaxErrorException.class
Yylex.class: sym.class 
SymbolTable.class: Symb.class EmptySTException.class DuplicateException.class
Scanner.class: Yylex.class Types.class
SymbolInfo.class: Kinds.class
ASTNode.class: ast.java sym.class SymbolTable.class SymbolInfo.class
	javac $(JCFLAGS) ast.java
P4.class: Yylex.class Scanner.class parser.class ASTNode.class

test: all
	java $(JFLAGS) P4 ../tests/test-00.csx_go 0
	java $(JFLAGS) P4 ../tests/test-01.csx_go 0
	java $(JFLAGS) P4 ../tests/test-02.csx_go 0
	java $(JFLAGS) P4 ../tests/test-03.csx_go 0
	java $(JFLAGS) P4 ../tests/test-04.csx_go 0
	java $(JFLAGS) P4 ../tests/test-05.csx_go 0

	java $(JFLAGS) P4 ../tests/test-07.csx_go 0
	java $(JFLAGS) P4 ../tests/test-08.csx_go 0
	java $(JFLAGS) P4 ../tests/test-09.csx_go 0
	java $(JFLAGS) P4 ../tests/test-10.csx_go 0
	java $(JFLAGS) P4 ../tests/test-11.csx_go 0
	java $(JFLAGS) P4 ../tests/test-12.csx_go 0
	java $(JFLAGS) P4 ../tests/test-13.csx_go 0
	java $(JFLAGS) P4 ../tests/test-14.csx_go 0
	java $(JFLAGS) P4 ../tests/test-15.csx_go 0
	java $(JFLAGS) P4 ../tests/test-16.csx_go 0
	java $(JFLAGS) P4 ../tests/test-18.csx_go 0
	java $(JFLAGS) P4 ../tests/test-19.csx_go 0
	java $(JFLAGS) P4 ../tests/test-20.csx_go 0
	java $(JFLAGS) P4 ../tests/test-21.csx_go 0
	java $(JFLAGS) P4 ../tests/test-22.csx_go 0
	java $(JFLAGS) P4 ../tests/test-23.csx_go 0
	java $(JFLAGS) P4 ../tests/test-24.csx_go 0
	java $(JFLAGS) P4 ../tests/test-25.csx_go 0
	java $(JFLAGS) P4 ../tests/test-26.csx_go 0
	java $(JFLAGS) P4 ../tests/test-27.csx_go 0
	java $(JFLAGS) P4 ../tests/test-28.csx_go 0
	java $(JFLAGS) P4 ../tests/test-29.csx_go 0
	java $(JFLAGS) P4 ../tests/test-30.csx_go 0
	java $(JFLAGS) P4 ../tests/test-31.csx_go 0
	java $(JFLAGS) P4 ../tests/test-32.csx_go 0
	java $(JFLAGS) P4 ../tests/test-33.csx_go 0
	java $(JFLAGS) P4 ../tests/test-34.csx_go 0
	java $(JFLAGS) P4 ../tests/test-35.csx_go 0
	java $(JFLAGS) P4 ../tests/test-36.csx_go 0
	java $(JFLAGS) P4 ../tests/test-37.csx_go 0
	java $(JFLAGS) P4 ../tests/test-38.csx_go 0
	java $(JFLAGS) P4 ../tests/test-39.csx_go 0
	java $(JFLAGS) P4 ../tests/test-40.csx_go 0
	java $(JFLAGS) P4 ../tests/test-41.csx_go 0
	java $(JFLAGS) P4 ../tests/test-42.csx_go 0
	java $(JFLAGS) P4 ../tests/test-43.csx_go 0
	java $(JFLAGS) P4 ../tests/test-46.csx_go 0
	

test_Y: all
	java $(JFLAGS) P4 test.csx_go y

test_I: all
		
		java $(JFLAGS) P4 ../tests/test-35.csx_go 0
		java $(JFLAGS) P4 ../tests/test-36.csx_go 0
		java $(JFLAGS) P4 ../tests/test-38.csx_go 0
		java $(JFLAGS) P4 ../tests/test-43.csx_go 0
###
# style check
###

HOW = basic,braces,clone,design,finalizers,migrating,unusedcode,imports,optimizations,strictexception,strings,sunsecure,typeresolution

style: 
	t=`pwd`; cd ~/pmd-bin-6.38.0/bin; \
		./run.sh pmd -d $$t -f text -R category/java/bestpractices.xml 

###
# lint check
###

lint: all
	jlint +all classes
###
# Spell check
###
spell:
	ispell csx_go.cup
	ispell csx_go.jlex
	ispell P4.java
	ispell ast.java
	ispell README.md
	ispell SymbolInfo.java
	ispell Kinds.java
	ispell Types.java


###
# clean
###

clean: 
	rm -f $(DESTPATH)/*.class Yylex.java* sym.java parser.java
