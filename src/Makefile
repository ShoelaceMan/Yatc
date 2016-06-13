CFLAGS=-g
CC=javac
BINARY=yatc

all: $(BINARY)

$(BINARY):
	$(CC) $(CFLAGS) com/shoelaceman/$(BINARY)/*.java
	$(CC) $(CFLAGS) com/shoelaceman/$(BINARY)/*/*.java

clean:
	rm -fv com/shoelaceman/$(BINARY)/*.class
	rm -fv com/shoelaceman/$(BINARY)/*/*.class
