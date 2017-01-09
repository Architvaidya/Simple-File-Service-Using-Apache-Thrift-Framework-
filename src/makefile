JFLAGS = -g
JC = javac
JVM= java
JAR=".:/home/yaoliu/src_code/local/libthrift-1.0.0.jar:/home/yaoliu/src_code/local/slf4j-log4j12-1.5.8.jar:/home/yaoliu/src_code/local/slf4j-api-1.5.8.jar:/home/yaoliu/src_code/local/log4j-1.2.14.jar" 
FILE=
.SUFFIXES: .java .class
.java.class:
	$(JC) -classpath $(JAR) $(JFLAGS) $*.java
CLASSES = \
	FileStore.java \
	Handler.java \
	Client.java \
	Server.java \
	RFile.java \
	RFileMetadata.java \
	Status.java \
	StatusReport.java \
	SystemException.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
