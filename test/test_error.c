#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    } 

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 1, 0, 0, 0, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int v0;
    jgrapht_capi_graph_add_vertex(thread, g, &v0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    int v1;
    jgrapht_capi_graph_add_vertex(thread, g, &v1);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    int v2 = 2;

    jgrapht_capi_graph_add_edge(thread, g, v0, v2, NULL);
    assert(jgrapht_capi_error_get_errno(thread) != 0);
    assert(strcmp("no such vertex in graph: 2", jgrapht_capi_error_get_errno_msg(thread)) == 0);
    jgrapht_capi_error_clear_errno(thread);
    assert(jgrapht_capi_error_get_errno(thread) == 0);    

    int e01;
    jgrapht_capi_graph_add_edge(thread, g, v0, v1, &e01);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_set_edge_weight(thread, g, e01, 15.0);
    assert(jgrapht_capi_error_get_errno(thread) == STATUS_UNSUPPORTED_OPERATION);
    assert(strcmp("Error (UnsupportedOperationException)", jgrapht_capi_error_get_errno_msg(thread)) == 0);    
    jgrapht_capi_error_clear_errno(thread);

    jgrapht_capi_handles_destroy(thread, g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test error invalid handle
    int has_next = 50; 
    jgrapht_capi_it_hasnext(thread, g, &has_next);
    assert(has_next == 50); // no write due to exception
    assert(jgrapht_capi_error_get_errno(thread) == STATUS_NULL_POINTER);
    assert(strcmp("Error (NullPointerException)", jgrapht_capi_error_get_errno_msg(thread)) == 0);    

    // test error message after clear
    jgrapht_capi_error_clear_errno(thread);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(strcmp(jgrapht_capi_error_get_errno_msg(thread), "") == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
