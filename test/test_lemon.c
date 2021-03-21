#include <stdio.h>
#include <stdlib.h>

#ifdef _WIN32
#include <crtdbg.h>
#endif 
#include <assert.h>

#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

#ifdef _WIN32
#define CRLF "\r\n"
#else
#define CRLF "\n"
#endif

char *expected="\
#Creator: JGraphT Lemon (LGF) Exporter"CRLF"\
#Version: 1"CRLF"\
"CRLF"\
@nodes"CRLF"\
label"CRLF"\
0"CRLF"\
1"CRLF"\
2"CRLF"\
"CRLF"\
@arcs"CRLF"\
		weight"CRLF"\
0	1	1.0"CRLF"\
1	2	1.0"CRLF""CRLF"";

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

    jgrapht_capi_ii_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ii_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ii_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 3, 0, NULL);

    // just test the API with a dummy file
    jgrapht_capi_xx_export_file_lemon(thread, g, "dummy.lemon.out", 1, 0, NULL);

    // test output to string
    void *out;
    jgrapht_capi_xx_export_string_lemon(thread, g, 1, 0, NULL, &out);
    char *str;
    jgrapht_capi_handles_get_ccharpointer(thread, out, &str);
    //printf("%s", str);
    assert(strcmp(str, expected) == 0);
    jgrapht_capi_handles_destroy(thread, out);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
