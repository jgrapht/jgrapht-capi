
all:
	mvn clean package
	native-image -cp target/jgrapht-nlib-*.jar --no-server --shared

.PHONY: clean

clean: 
	rm -rf *.h *.so dependency-reduced-pom.xml target/
