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
#    test     -- Tests proj3 by running P3.class with input test.csx_go
#/homes/raphael/courses/cs541/public/cup
# 
###

DESTPATH = ./classes
CUPPATH = /homes/raphael/courses/cs541/public/cup
CLASSPATH = $(DESTPATH):$(CUPPATH)/java-cup-11b.jar
VPATH = $(DESTPATH)
JCFLAGS = -Xlint:all,-auxiliaryclass,-rawtypes -deprecation -classpath $(CLASSPATH) -d $(DESTPATH)
JFLAGS = -classpath $(CLASSPATH)

%.class: %.java
	javac $(JCFLAGS) $<

.PHONY: all 
all: P3.class Yylex.class test.csx_go parser.class 

# don't use CUP 0.11a; I couldn't get it to work right.  -- Raphael 2/2011
sym.java parser.java: csx_go.cup 
	java $(JFLAGS) java_cup/Main < csx_go.cup
Yylex.java: csx_go.jlex
	jflex csx_go.jlex
parser.class: ASTNode.class Scanner.class
Yylex.class: sym.class 
Scanner.class: Yylex.class
ASTNode.class: ast.java sym.class
	javac $(JCFLAGS) ast.java
P3.class: Yylex.class Scanner.class parser.class ASTNode.class

test: all
	java $(JFLAGS) P3 test.csx_go

test_bad: all
	java $(JFLAGS) P3 test.bad

###
# style check
###

HOW = basic,braces,clone,design,finalizers,migrating,unusedcode,imports,optimizations,strictexception,strings,sunsecure,typeresolution

style: 
	t=`pwd`; cd ~/pmd-bin-6.38.0/bin; \
		./run.sh pmd -d $$t -f text -R category/java/bestpractices.xml 

###
# spelling
###

spell:
	ispell P3.java -x
	ispell csx_go.jlex -x
	ispell csx_go.cup -x
	ispell ast.java -x
###
# lint check
###

lint: all
	jlint +all classes

###
# clean
###

clean: 
	rm -f $(DESTPATH)/*.class Yylex.java* sym.java parser.java
