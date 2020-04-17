
# JGraphT Native Library

This is a native shared library build of the JGraphT library. The interface to this native 
build follows C-style.

## Why? 

This shared library is used in the development of a python library for JGraphT.

# Compilation

You need to have GraalVM and its native-image tool installed.

```
mvn clean package
native-image -cp target/jgrapht-nlib-1.4.0.jar --no-server --shared
```

The command will generate the following header files: 

```
graal_isolate.h
graal_isolate_dynamic.h
jgrapht_nlib.h
jgrapht_nlib_dynamic.h
```

and the shared library `jgrapht_nlib.so`. 
```

For debugging purposes during the build you can add the `-H:Log=InvokeCC:` flag on the `native-image` 
invocation. 
