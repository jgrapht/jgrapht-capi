#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <jgrapht_capi.h>

#define ILLEGAL_ARGUMENT 2
#define UNSUPPORTED_OPERATION 3
#define NULL_POINTER 6

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    } 

    assert(jgrapht_capi_get_errno(thread) == 0);

    void *g = jgrapht_capi_graph_create(thread, 1, 0, 0, 0);
    assert(jgrapht_capi_get_errno(thread) == 0);

    long v0 = jgrapht_capi_graph_add_vertex(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);
    long v1 = jgrapht_capi_graph_add_vertex(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);
    long v2 = 2;

    jgrapht_capi_graph_add_edge(thread, g, v0, v2);
    assert(jgrapht_capi_get_errno(thread) != 0);
    assert(strcmp("no such vertex in graph: 2", jgrapht_capi_get_errno_msg(thread)) == 0);
    jgrapht_capi_clear_errno(thread);
    assert(jgrapht_capi_get_errno(thread) == 0);    

    long e01 = jgrapht_capi_graph_add_edge(thread, g, v0, v1);
    assert(jgrapht_capi_get_errno(thread) == 0);

    jgrapht_capi_graph_set_edge_weight(thread, g, e01, 15.0);
    assert(jgrapht_capi_get_errno(thread) == UNSUPPORTED_OPERATION);
    assert(strcmp("Error (UnsupportedOperationException)", jgrapht_capi_get_errno_msg(thread)) == 0);    
    jgrapht_capi_clear_errno(thread);

    jgrapht_capi_destroy(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);

    // test error invalid handle
    int has_next = jgrapht_capi_it_hasnext(thread, g);
    assert(jgrapht_capi_get_errno(thread) == NULL_POINTER);
    assert(!has_next);
    assert(strcmp("Error (NullPointerException)", jgrapht_capi_get_errno_msg(thread)) == 0);    

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}