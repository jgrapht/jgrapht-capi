cmake_minimum_required(VERSION 3.10)

project(jgrapht)

list(INSERT CMAKE_MODULE_PATH 0 "${CMAKE_CURRENT_SOURCE_DIR}/CMake")

find_package(Java 11 REQUIRED)
find_package(JNI REQUIRED)
find_program(NativeImage native-image REQUIRED)
find_package(Maven REQUIRED)

message(STATUS "native-image found at ${NativeImage}")

include(GNUInstallDirs)

add_custom_command(
    OUTPUT target/jgrapht-nlib-0.1.jar
    COMMAND cp ${CMAKE_SOURCE_DIR}/jgrapht-nlib/pom.xml ${CMAKE_BINARY_DIR}/
    COMMAND cp -r ${CMAKE_SOURCE_DIR}/jgrapht-nlib/src ${CMAKE_BINARY_DIR}/
    COMMAND mvn -f ${CMAKE_BINARY_DIR} package
    COMMENT "Building jar file with C native scopes"
)

add_custom_command(
    OUTPUT libjgrapht_nlib.so jgrapht_nlib.h jgrapht_nlib_dynamic.h graal_isolate.h graal_isolate_dynamic.h
    COMMAND native-image -cp ${CMAKE_BINARY_DIR}/target/jgrapht-nlib-0.1.jar --no-server --shared
    COMMAND cp ${CMAKE_BINARY_DIR}/jgrapht_nlib.so ${CMAKE_BINARY_DIR}/libjgrapht_nlib.so 
    DEPENDS target/jgrapht-nlib-0.1.jar
    COMMENT "Producing shared library from jar file"
)

add_custom_target(
    buildjar 
    SOURCES target/jgrapht-nlib-0.1.jar
)

add_custom_target(
    jgraphtsharedlib
    SOURCES libjgrapht_nlib.so
)

set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-nlib/jgrapht_nlib.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-nlib/jgrapht_nlib_dynamic.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-nlib/graal_isolate.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-nlib/graal_isolate_dynamic.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-nlib/libjgrapht_nlib.so PROPERTY GENERATED 1)

add_library(
    jgrapht SHARED 
    jgrapht/jgrapht.c
    jgrapht/jgrapht.h
    ${CMAKE_BINARY_DIR}/jgrapht_nlib.h 
    ${CMAKE_BINARY_DIR}/jgrapht_nlib_dynamic.h
    ${CMAKE_BINARY_DIR}/graal_isolate.h
    ${CMAKE_BINARY_DIR}/graal_isolate_dynamic.h
)

target_include_directories(jgrapht PUBLIC ${CMAKE_BINARY_DIR})

add_library(jgrapht_nlib SHARED IMPORTED)
set_property(TARGET jgrapht_nlib PROPERTY IMPORTED_LOCATION ${CMAKE_BINARY_DIR}/libjgrapht_nlib.so)
target_link_libraries(jgrapht PUBLIC jgrapht_nlib)

add_dependencies(jgrapht jgraphtsharedlib)

install(
    TARGETS jgrapht 
    ARCHIVE DESTINATION ${CMAKE_INSTALL_LIBDIR}
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)

install(
    FILES 
    jgrapht/jgrapht.h 
    ${CMAKE_BINARY_DIR}/jgrapht_nlib.h 
    ${CMAKE_BINARY_DIR}/jgrapht_nlib_dynamic.h
    ${CMAKE_BINARY_DIR}/graal_isolate.h
    ${CMAKE_BINARY_DIR}/graal_isolate_dynamic.h
    DESTINATION 
    ${CMAKE_INSTALL_INCLUDEDIR}/jgrapht
)

install(
    FILES
    ${CMAKE_BINARY_DIR}/libjgrapht_nlib.so
    DESTINATION        
    ${CMAKE_INSTALL_LIBDIR}
)

