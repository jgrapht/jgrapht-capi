#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

#define NUM_VERTICES 1000

int main() { 
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }    

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 1, 1, 1, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int v;
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 0);
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 1);
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 2);

    int e;
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 0, &e) == 0);
    assert(e == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 1, &e) == 0);
    assert(e == 1);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 2, &e) == 0);
    assert(e == 2);

    int added = 0;
    jgrapht_capi_graph_add_given_edge(thread, g, 1, 2, 3, &added);
    assert(added == 1);

    jgrapht_capi_graph_add_given_edge(thread, g, 1, 2, 2, &added);
    assert(added == 0);

    int contains = 0;
    jgrapht_capi_graph_contains_edge(thread, g, 3, &contains);
    assert(contains == 1);

    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2, &e) == 0);
    assert(e == 4);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
