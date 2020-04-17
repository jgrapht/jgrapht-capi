
add_custom_command(
    OUTPUT jgrapht-nlib/target/jgrapht-nlib-1.4.0.jar
    COMMAND cp ${CMAKE_CURRENT_SOURCE_DIR}/pom.xml ${CMAKE_CURRENT_BINARY_DIR}/
    COMMAND cp -r ${CMAKE_CURRENT_SOURCE_DIR}/src ${CMAKE_CURRENT_BINARY_DIR}/
    COMMAND mvn -f ${CMAKE_CURRENT_BINARY_DIR} package
    COMMENT "Building jar file with C native scopes"
)

add_custom_command(
    OUTPUT libjgrapht_nlib.so jgrapht_nlib.h jgrapht_nlib_dynamic.h graal_isolate.h graal_isolate_dynamic.h
    COMMAND native-image -cp ${CMAKE_CURRENT_BINARY_DIR}/target/jgrapht-nlib-1.4.0.jar --no-server --shared
    COMMAND cp ${CMAKE_CURRENT_BINARY_DIR}/jgrapht_nlib.so ${CMAKE_CURRENT_BINARY_DIR}/libjgrapht_nlib.so 
    DEPENDS jgrapht-nlib/target/jgrapht-nlib-1.4.0.jar
    COMMENT "Producing shared library from jar file"
)

add_custom_target(
    buildjar 
    SOURCES jgrapht-nlib/target/jgrapht-nlib-1.4.0.jar
)

add_custom_target(
    jgraphtsharedlib
    SOURCES libjgrapht_nlib.so
)
