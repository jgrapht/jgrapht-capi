
# JGraphT Library C-API

This is a native shared library build of the JGraphT library. The interface to this native 
build follows C-style.

# Compilation

You need to have GraalVM and its native-image tool installed.

```
mvn clean package
native-image -cp target/jgrapht-capi-*.jar --no-server --shared --no-fallback --initialize-at-build-time
```

The command will generate the following header files: 

```
graal_isolate.h
graal_isolate_dynamic.h
jgrapht_capi.h
jgrapht_capi_dynamic.h
```

and the shared library `jgrapht_capi.so`. 
```

For debugging purposes during the build you can add the `-H:Log=InvokeCC:` flag on the `native-image` 
invocation. 
