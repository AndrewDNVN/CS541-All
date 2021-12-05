####
# Makefile for project 2
# Modified 1/2011 Raphael Finkel
#
#
# All classes are compiled into the ./classes directory
#
# The default is to make all .class files.
#
#    clean    -- removes all generated files
#
#    test     -- Tests proj2 by running P2.class with input tiny.csx
#
###

DESTPATH = ./classes
CUPPATH = /home/andrew/Documents/cup/java-cup-10k.jar
CLASSPATH = $(DESTPATH):$(CUPPATH)
VPATH = $(DESTPATH)
JCFLAGS = -Xlint:all,-auxiliaryclass,-rawtypes -deprecation -classpath $(CLASSPATH) -d $(DESTPATH)
JFLAGS = -classpath $(CLASSPATH)

%.class: %.java
	javac $(JCFLAGS) $<

all : P2.class sym.class Yylex.class 

P2.class : P2.java Yylex.class
sym.class: sym.java
Yylex.class: sym.class
Yylex.java: csx_go.jlex
	rm -f Yylex.java
	jflex csx_go.jlex

###
# test
###

test:   P2.class Yylex.class csx_lite
	java $(JFLAGS) P2 csx_lite

testLong: P2.class Yylex.class csx_long
	java $(JFLAGS) P2 csx_long

###
# style check
###

HOW = basic,braces,clone,design,finalizers,migrating,unusedcode,imports,optimizations,strictexception,strings,sunsecure,typeresolution

style: 
	t=`pwd`; cd ~/pmd-bin-6.38.0/bin; \
		./run.sh pmd -d $$t -f text -R category/java/bestpractices.xml -showsuppressed

###
# spelling
###

spell:
	ispell P2.java -x
	ispell sym.java -x
	ispell csx_go.jlex -x

###
# lint check
###

lint: all
	jlint +all classes

###
# clean
###

clean: 
	rm -f $(DESTPATH)/*.class Yylex.java

