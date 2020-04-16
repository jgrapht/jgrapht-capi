
# Build

```
mvn clean package
native-image -cp target/jgrapht-nlib-1.4.0.jar --no-server --shared
```

For debugging purposes also add the `-H:Log=InvokeCC:` flag.
