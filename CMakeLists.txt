cmake_minimum_required(VERSION 2.8)

project(jgrapht)

list(INSERT CMAKE_MODULE_PATH 0 "${CMAKE_CURRENT_SOURCE_DIR}/etc/cmake")

find_package(Java 11)
find_package(JNI)
find_program(NativeImage native-image REQUIRED)
find_package(Maven REQUIRED)

message(STATUS "native-image found at ${NativeImage}")

include(GNUInstallDirs)

SET(JGRAPHT_LIBRARY "${CMAKE_SHARED_LIBRARY_PREFIX}jgrapht_capi${CMAKE_SHARED_LIBRARY_SUFFIX}")

add_custom_command(
    OUTPUT target/jgrapht-capi-0.1.jar
    COMMAND ${CMAKE_COMMAND} -E copy ${CMAKE_SOURCE_DIR}/jgrapht-capi/pom.xml ${CMAKE_BINARY_DIR}/
    COMMAND ${CMAKE_COMMAND} -E copy_directory ${CMAKE_SOURCE_DIR}/jgrapht-capi/src ${CMAKE_BINARY_DIR}/src
    COMMAND mvn -B -f ${CMAKE_BINARY_DIR} package
    COMMENT "Building jar file with C native scopes"
)

add_custom_command(
    OUTPUT ${JGRAPHT_LIBRARY} jgrapht_capi.h jgrapht_capi_dynamic.h graal_isolate.h graal_isolate_dynamic.h
    COMMAND native-image -cp ${CMAKE_BINARY_DIR}/target/jgrapht-capi-0.1.jar --no-fallback --initialize-at-build-time --no-server --shared
    COMMAND ${CMAKE_COMMAND} -E rename ${CMAKE_BINARY_DIR}/jgrapht_capi${CMAKE_SHARED_LIBRARY_SUFFIX} ${CMAKE_BINARY_DIR}/${JGRAPHT_LIBRARY}
    DEPENDS target/jgrapht-capi-0.1.jar
    COMMENT "Producing shared library from jar file"
)

if(APPLE)
  add_custom_command(
    OUTPUT ${JGRAPHT_LIBRARY} APPEND
    COMMAND install_name_tool -id "@rpath/${JGRAPHT_LIBRARY}" ${CMAKE_BINARY_DIR}/${JGRAPHT_LIBRARY}
  )
endif(APPLE)

add_custom_target(
    buildjar 
    SOURCES target/jgrapht-capi-0.1.jar
)

add_custom_target(
    jgraphtsharedlib
    SOURCES ${JGRAPHT_LIBRARY}
)

set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-capi/jgrapht_capi.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-capi/jgrapht_capi_dynamic.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-capi/graal_isolate.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-capi/graal_isolate_dynamic.h PROPERTY GENERATED 1)
set_property(SOURCE ${CMAKE_BINARY_DIR}/jgrapht-capi/${JGRAPHT_LIBRARY} PROPERTY GENERATED 1)

add_library(jgrapht_capi SHARED IMPORTED)
set_property(TARGET jgrapht_capi PROPERTY IMPORTED_LOCATION ${CMAKE_BINARY_DIR}/${JGRAPHT_LIBRARY})
IF(WIN32)
    set_property(TARGET jgrapht_capi PROPERTY IMPORTED_IMPLIB ${CMAKE_BINARY_DIR}/jgrapht_capi.lib)
ENDIF(WIN32)

add_dependencies(jgrapht_capi jgraphtsharedlib)

install(
    FILES
    ${CMAKE_BINARY_DIR}/jgrapht_capi.h 
    ${CMAKE_BINARY_DIR}/jgrapht_capi_dynamic.h
    ${CMAKE_BINARY_DIR}/graal_isolate.h
    ${CMAKE_BINARY_DIR}/graal_isolate_dynamic.h
    ${CMAKE_SOURCE_DIR}/jgrapht-capi/src/main/native/jgrapht_capi_types.h
    DESTINATION 
    ${CMAKE_INSTALL_INCLUDEDIR}/jgrapht_capi
)

install(
    FILES
    ${CMAKE_BINARY_DIR}/${JGRAPHT_LIBRARY}
    DESTINATION        
    ${CMAKE_INSTALL_LIBDIR}
)

enable_testing()
include(CTest)

set(
    TEST_SOURCES 
    "test_vertices.c"
    "test_addvertex.c"
    "test_edges.c"
    "test_directed_graph.c" 
    "test_undirected_graph.c"
    "test_error.c"
    "test_map.c"
    "test_set.c"
    "test_list.c"
    "test_mst.c"
    "test_vertexcover.c"
    "test_clustering.c"
    "test_coloring.c"
    "test_views.c"
    "test_graphtests.c"
    "test_graphmetrics.c"
    "test_partition.c"
    "test_matching.c"
    "test_generate.c"
    "test_scoring.c"
    "test_traverse.c"
    "test_spanner.c"
    "test_tour.c"
    "test_shortestpaths.c"
    "test_k_shortestpaths.c"
    "test_clique.c"
    "test_dimacs.c"
    "test_gml.c"
    "test_json.c"
    "test_json2.c"
    "test_lemon.c"
    "test_flow.c"
    "test_planar.c"
    "test_gexf.c"
    "test_graph6.c"
    "test_sparse6.c"
    "test_csv.c"
    "test_graphml.c"
    "test_graphml_simple.c"
    "test_dot.c"
    "test_mincut.c"
    "test_iso.c"
    "test_iso_subgraph.c"
    "test_edgesupplier.c"
    "test_mincostflow.c"
    "test_eulerian.c"
    "test_chinese_postman.c"
    "test_fundamental_basis_paton.c"
    "test_fundamental_basis_bfs.c"
    "test_fundamental_basis_stack.c"
    "test_simple_cycles_hawick_james.c"
    "test_simple_cycles_tarjan.c"
    "test_simple_cycles_tiernan.c"
    "test_simple_cycles_szwarcfiter_lauer.c"
    "test_simple_cycles_johnson.c"
    "test_connectivity.c"
    "test_sparse_graph.c"
    "test_sparse_graph2.c"
)
foreach(testsourcefile ${TEST_SOURCES})
    string(REPLACE ".c" "" testname ${testsourcefile})
    add_executable(${testname} test/${testsourcefile})
    target_include_directories(${testname} PUBLIC ${CMAKE_BINARY_DIR} ${CMAKE_SOURCE_DIR}/jgrapht-capi/src/main/native)
    target_link_libraries(${testname} jgrapht_capi)
    if(UNIX)
      target_link_libraries(${testname} m)
    endif(UNIX)
    add_test(NAME ${testname} COMMAND ${testname})
endforeach(testsourcefile ${TEST_SOURCES})

