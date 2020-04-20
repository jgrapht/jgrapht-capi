cmake_minimum_required(VERSION 3.10)

project(jgrapht)

list(INSERT CMAKE_MODULE_PATH 0 "${CMAKE_CURRENT_SOURCE_DIR}/etc/cmake")

find_package(Java 11 REQUIRED)
find_package(JNI REQUIRED)
find_program(NativeImage native-image REQUIRED)
find_package(Maven REQUIRED)

message(STATUS "native-image found at ${NativeImage}")

include(GNUInstallDirs)

add_custom_command(
    OUTPUT target/jgrapht-capi-0.1.jar
    COMMAND cp ${CMAKE_SOURCE_DIR}/jgrapht-capi/pom.xml ${CMAKE_BINARY_DIR}/
    COMMAND cp -r ${CMAKE_SOURCE_DIR}/jgrapht-capi/src ${CMAKE_BINARY_DIR}/
    COMMAND mvn -f ${CMAKE_BINARY_DIR} package
    COMMENT "Building jar file with C native scopes"
)

add_custom_command(
    OUTPUT libjgrapht_capi.so jgrapht_capi.h jgrapht_capi_dynamic.h graal_isolate.h graal_isolate_dynamic.h
    COMMAND native-image -cp ${CMAKE_BINARY_DIR}/target/jgrapht-capi-0.1.jar --no-fallback --initialize-at-build-time --no-server --shared
    COMMAND cp ${CMAKE_BINARY_DIR}/jgrapht_capi.so ${CMAKE_BINARY_DIR}/libjgrapht_capi.so 
    COMMAND rm ${CMAKE_BINARY_DIR}/jgrapht_capi.so
    DEPENDS target/jgrapht-capi-0.1.jar
    COMMENT "Producing shared library from jar file"
)

add_custom_target(
    buildjar 
    SOURCES target/jgrapht-capi-0.1.jar
)

add_custom_target(
    jgraphtsharedlib
    SOURCES libjgrapht_capi.so
)

set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-capi/jgrapht_capi.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-capi/jgrapht_capi_dynamic.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-capi/graal_isolate.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-capi/graal_isolate_dynamic.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-capi/libjgrapht_capi.so PROPERTY GENERATED 1)

add_library(jgrapht_capi SHARED IMPORTED)
set_property(TARGET jgrapht_capi PROPERTY IMPORTED_LOCATION ${CMAKE_BINARY_DIR}/libjgrapht_capi.so)

add_dependencies(jgrapht_capi jgraphtsharedlib)

install(
    FILES 
    ${CMAKE_BINARY_DIR}/jgrapht_capi.h 
    ${CMAKE_BINARY_DIR}/jgrapht_capi_dynamic.h
    ${CMAKE_BINARY_DIR}/graal_isolate.h
    ${CMAKE_BINARY_DIR}/graal_isolate_dynamic.h
    DESTINATION 
    ${CMAKE_INSTALL_INCLUDEDIR}/jgrapht_capi
)

install(
    FILES
    ${CMAKE_BINARY_DIR}/libjgrapht_capi.so
    DESTINATION        
    ${CMAKE_INSTALL_LIBDIR}
)

enable_testing()
include(CTest)

set(
    TEST_SOURCES 
    "test_vertices.c"
    "test_edges.c"
    "test_directed_graph.c" 
    "test_undirected_graph.c"
    "test_error.c"
    "test_map.c"
    "test_mst.c"
    "test_vertexcover.c"
    "test_clustering.c"
    "test_coloring.c"
    "test_views.c"
)
foreach(testsourcefile ${TEST_SOURCES})
    string(REPLACE ".c" "" testname ${testsourcefile})
    add_executable(${testname} test/${testsourcefile})
    target_include_directories(${testname} PUBLIC ${CMAKE_BINARY_DIR})
    target_link_libraries(${testname} jgrapht_capi)
    add_test(NAME ${testname} COMMAND ${testname})
endforeach( testsourcefile ${APP_SOURCES} )

