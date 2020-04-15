
# Build

```
mvn clean package
native-image -cp target/jgrapht-nlib-1.4.1-SNAPSHOT.jar --no-server --shared
```

For debugging purposes also add the `-H:Log=InvokeCC:` flag.
