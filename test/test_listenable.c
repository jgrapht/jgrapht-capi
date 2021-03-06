#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef _WIN32
#include <crtdbg.h>
#endif 
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

void event(int v, int type) { 
    assert(v == 0 && type == GRAPH_EVENT_VERTEX_ADDED);
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
    jgrapht_capi_ii_graph_create(thread, 1, 1, 1, 1, NULL, NULL, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *gl;
    jgrapht_capi_xx_listenable_as_listenable(thread, g, &gl);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *listener;
    jgrapht_capi_ii_listenable_create_graph_listener(thread, event, &listener);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_ii_listenable_add_graph_listener(thread, gl, listener);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int v; 
    jgrapht_capi_ix_graph_add_vertex(thread, gl, &v);
    assert( v == 0);

    jgrapht_capi_ii_listenable_remove_graph_listener(thread, gl, listener);
    jgrapht_capi_handles_destroy(thread, gl);
    jgrapht_capi_handles_destroy(thread, g);


    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
