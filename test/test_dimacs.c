#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef _WIN32
#include <crtdbg.h>
#endif 
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

#ifdef _WIN32
char *expected="c\r\nc SOURCE: Generated using the JGraphT library\r\nc\r\np edge 4 4\r\ne 0 1\r\ne 1 2\r\ne 2 3\r\ne 3 0\r\n";
#else 
char *expected="c\nc SOURCE: Generated using the JGraphT library\nc\np edge 4 4\ne 0 1\ne 1 2\ne 2 3\ne 3 0\n";
#endif

int preserveid(int x) { 
    return x;
}

int main() {

#ifdef _WIN32
    _CrtSetReportMode( _CRT_WARN, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_WARN, _CRTDBG_FILE_STDERR);
    _CrtSetReportMode( _CRT_ERROR, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_ERROR, _CRTDBG_FILE_STDERR);
    _CrtSetReportMode( _CRT_ASSERT, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_ASSERT, _CRTDBG_FILE_STDERR);
#endif

    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_ii_graph_create(thread, 0, 0, 0, 0, NULL, NULL, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 3, 0, NULL);

    // write file
    jgrapht_capi_ix_export_file_dimacs(thread, g, "dummy.dimacs.out", DIMACS_FORMAT_COLORING, 0, NULL);
    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // read file
    jgrapht_capi_ii_graph_create(thread, 0, 0, 0, 0, NULL, NULL, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_ii_import_file_dimacs(thread, g, "dummy.dimacs.out", preserveid, NULL, NULL, 0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int count;
    jgrapht_capi_ix_graph_vertices_count(thread, g, &count);
    assert(count == 4);
    jgrapht_capi_ix_graph_edges_count(thread, g, &count);
    assert(count == 4);

    // test output to string

    void *out;
    jgrapht_capi_ix_export_string_dimacs(thread, g, DIMACS_FORMAT_MAX_CLIQUE, 0, NULL, &out);
    char *str;
    jgrapht_capi_handles_get_ccharpointer(thread, out, &str);
    assert(strcmp(str, expected) == 0);
    jgrapht_capi_handles_destroy(thread, out);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
