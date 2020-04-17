cmake_minimum_required(VERSION 3.10)

project(jgrapht)

list(INSERT CMAKE_MODULE_PATH 0 "${CMAKE_CURRENT_SOURCE_DIR}/CMake")

find_package(Java 11 REQUIRED)
find_package(JNI REQUIRED)
find_program(NativeImage native-image REQUIRED)
find_package(Maven REQUIRED)

message(STATUS "native-image found at ${NativeImage}")

include(GNUInstallDirs)

execute_process(
    COMMAND(${Maven} package)
    WORKING_DIRECTORY ${CMAKE_BINARY_DIR}
)

add_subdirectory(jgrapht-nlib)

set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-nlib/jgrapht_nlib.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-nlib/jgrapht_nlib_dynamic.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-nlib/graal_isolate.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-nlib/graal_isolate_dynamic.h PROPERTY GENERATED 1)


add_library(
    jgrapht SHARED 
    jgrapht/jgrapht.c
    ${CMAKE_BINARY_DIR}/jgrapht-nlib/jgrapht_nlib.h 
    ${CMAKE_BINARY_DIR}/jgrapht-nlib/jgrapht_nlib_dynamic.h
    ${CMAKE_BINARY_DIR}/jgrapht-nlib/graal_isolate.h
    ${CMAKE_BINARY_DIR}/jgrapht-nlib/graal_isolate_dynamic.h
)

target_include_directories(jgrapht PUBLIC ${CMAKE_BINARY_DIR}/jgrapht-nlib)

add_dependencies(jgrapht jgraphtsharedlib)

