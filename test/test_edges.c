#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi.h>

#define NUM_VERTICES 1000
#define ITERATOR_NO_SUCH_ELEMENT 100

int main() { 
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }    

    assert(jgrapht_capi_get_errno(thread) == 0);

    void *g = jgrapht_capi_graph_create(thread, 1, 1, 1, 1);
    assert(jgrapht_capi_get_errno(thread) == 0);

    assert(jgrapht_capi_graph_is_directed(thread, g));
    assert(!jgrapht_capi_graph_is_undirected(thread, g));
    assert(jgrapht_capi_graph_is_weighted(thread, g));
    assert(jgrapht_capi_graph_is_allowing_selfloops(thread, g));
    assert(jgrapht_capi_graph_is_allowing_multipleedges(thread, g));

    assert(jgrapht_capi_graph_add_vertex(thread, g) == 0);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 1);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 2);

    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 0) == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 1) == 1);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 2) == 2);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2) == 3);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2) == 4);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2) == 5);

    assert(jgrapht_capi_graph_edge_source(thread, g, 0) == 0);
    assert(jgrapht_capi_graph_edge_target(thread, g, 0) == 0);
    assert(jgrapht_capi_graph_edge_source(thread, g, 1) == 0);
    assert(jgrapht_capi_graph_edge_target(thread, g, 1) == 1);
    assert(jgrapht_capi_graph_edge_source(thread, g, 2) == 0);
    assert(jgrapht_capi_graph_edge_target(thread, g, 2) == 2);
    assert(jgrapht_capi_graph_edge_source(thread, g, 3) == 1);
    assert(jgrapht_capi_graph_edge_target(thread, g, 3) == 2);
    assert(jgrapht_capi_graph_edge_source(thread, g, 4) == 1);
    assert(jgrapht_capi_graph_edge_target(thread, g, 4) == 2);
    assert(jgrapht_capi_graph_edge_source(thread, g, 5) == 1);
    assert(jgrapht_capi_graph_edge_target(thread, g, 5) == 2);

    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 0) == 1.0);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 1) == 1.0);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 2) == 1.0);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 3) == 1.0);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 4) == 1.0);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, 5) == 1.0);

    jgrapht_capi_graph_set_edge_weight(thread, g, 0, 5.0);
    assert(jgrapht_capi_get_errno(thread) == 0 && jgrapht_capi_graph_get_edge_weight(thread, g, 0) == 5.0);

    void *eit = jgrapht_capi_graph_create_all_eit(thread, g);
    assert(jgrapht_capi_it_next(thread, eit) == 0);
    assert(jgrapht_capi_it_next(thread, eit) == 1);
    assert(jgrapht_capi_it_next(thread, eit) == 2);
    assert(jgrapht_capi_it_next(thread, eit) == 3);
    assert(jgrapht_capi_it_next(thread, eit) == 4);
    assert(jgrapht_capi_it_next(thread, eit) == 5);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 0, 0));
    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 0, 1));
    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 0, 2));
    assert(!jgrapht_capi_graph_contains_edge_between(thread, g, 1, 0));
    assert(!jgrapht_capi_graph_contains_edge_between(thread, g, 1, 1));
    assert(jgrapht_capi_graph_contains_edge_between(thread, g, 1, 2));
    assert(!jgrapht_capi_graph_contains_edge_between(thread, g, 2, 0));
    assert(!jgrapht_capi_graph_contains_edge_between(thread, g, 2, 1));
    assert(!jgrapht_capi_graph_contains_edge_between(thread, g, 2, 2));

    eit = jgrapht_capi_graph_create_between_eit(thread, g, 1 , 2);
    assert(jgrapht_capi_it_next(thread, eit) == 3);
    assert(jgrapht_capi_it_next(thread, eit) == 4);
    assert(jgrapht_capi_it_next(thread, eit) == 5);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}