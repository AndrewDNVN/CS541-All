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
#	
###

DESTPATH = ./classes
COURSEDIR = /homes/raphael/courses/cs541/public
CUPPATH = $(COURSEDIR)/cup
CLASSPATH = $(DESTPATH):$(CUPPATH)/java-cup-10k.jar
JASMINPATH = $(CUPPATH)/cup.jar:$(CUPPATH)/jasmin-sable.jar
VPATH = $(DESTPATH)
JCFLAGS = -Xlint:all,-auxiliaryclass,-rawtypes -deprecation -classpath $(CLASSPATH) -d $(DESTPATH)
JFLAGS = -classpath $(CLASSPATH)
TESTDIR = .

%.class: %.java
	javac $(JCFLAGS) $<

.PHONY: all 
all: P5.class Yylex.class parser.class 

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
P5.class: Yylex.class Scanner.class parser.class ASTNode.class 


test: all CSXLib.class
	#java $(JFLAGS) P5 $(TESTDIR)/helloWorld.csx_go
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo
	
	#java $(JFLAGS) P5 $(TESTDIR)/test-00.csx_go
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-01.csx_go
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-02.csx_go
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-03.csx_go
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-04.csx_go
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-05.csx_go
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	###
	#	Still have not figured out why the compiler can't handle test case 06
	###

	#java $(JFLAGS) P5 $(TESTDIR)/test-07.csx_go # has aritmetic issues
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-08.csx_go 
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-09.csx_go 
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-10.csx_go 
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-11.csx_go 
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-12.csx_go 
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-13.csx_go 
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-14.csx_go 
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-15.csx_go # not working
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
	#	rm test.j
	#@echo

	#java $(JFLAGS) P5 $(TESTDIR)/test-16.csx_go 
	#java -classpath $(JASMINPATH) jasmin.Main test.j
	#java -classpath .:./classes test
		#rm test.j
	#@echo

	java $(JFLAGS) P5 $(TESTDIR)/test-19.csx_go 
	java -classpath $(JASMINPATH) jasmin.Main test.j
	java -classpath .:./classes test
		#rm test.j
	@echo

	# other tests removed


###
# style check
###

HOW = basic,braces,clone,design,finalizers,migrating,unusedcode,imports,optimizations,strictexception,strings,sunsecure,typeresolution

style: 
	t=`pwd`; cd ~/pmd-bin-6.41.0/bin; \
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
	#ispell csx_go.cup
	#ispell csx_go.jlex
	#ispell Kinds.java
	#ispell Types.java
	#ispell P5.java
	#ispell SymbolInfo.java
	ispell ast.java
	#ispell ReadMe.md
	



###
# clean
###

clean: 
	rm -f $(DESTPATH)/*.class Yylex.java* sym.java parser.java test.j *.class
