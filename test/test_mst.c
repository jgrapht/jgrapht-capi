#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi.h>

#define ITERATOR_NO_SUCH_ELEMENT 100

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_get_errno(thread) == 0);

    void *g = jgrapht_capi_graph_create(thread, 0, 0, 0, 1);
    assert(jgrapht_capi_get_errno(thread) == 0);

    assert(!jgrapht_capi_graph_is_directed(thread, g));
    assert(jgrapht_capi_graph_is_undirected(thread, g));
    assert(jgrapht_capi_graph_is_weighted(thread, g));
    assert(!jgrapht_capi_graph_is_allowing_selfloops(thread, g));
    assert(!jgrapht_capi_graph_is_allowing_multipleedges(thread, g));

    assert(jgrapht_capi_graph_add_vertex(thread, g) == 0);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 1);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 2);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 3);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 4);

    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 1) == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2) == 1);
    assert(jgrapht_capi_graph_add_edge(thread, g, 2, 3) == 2);
    assert(jgrapht_capi_graph_add_edge(thread, g, 3, 4) == 3);
    assert(jgrapht_capi_graph_add_edge(thread, g, 4, 0) == 4);

    jgrapht_capi_graph_set_edge_weight(thread, g, 0, 5.0);
    jgrapht_capi_graph_set_edge_weight(thread, g, 1, 4.0);
    jgrapht_capi_graph_set_edge_weight(thread, g, 2, 3.0);
    jgrapht_capi_graph_set_edge_weight(thread, g, 3, 2.0);
    jgrapht_capi_graph_set_edge_weight(thread, g, 4, 1.0);

    // run kruskal
    void *mst = jgrapht_capi_mst_exec_kruskal(thread, g);
    assert(jgrapht_capi_mst_get_weight(thread,  mst) == 10.0);
    void *eit = jgrapht_capi_mst_create_eit(thread,  mst);
    assert(jgrapht_capi_it_next_long(thread,  eit) == 1);
    assert(jgrapht_capi_it_next_long(thread,  eit) == 2);
    assert(jgrapht_capi_it_next_long(thread,  eit) == 3);
    assert(jgrapht_capi_it_next_long(thread,  eit) == 4);
    assert(!jgrapht_capi_it_hasnext(thread,  eit));
    jgrapht_capi_destroy(thread,  eit);
    jgrapht_capi_destroy(thread,  mst);

    // run prim
    mst = jgrapht_capi_mst_exec_prim(thread, g);
    assert(jgrapht_capi_mst_get_weight(thread,  mst) == 10.0);
    eit = jgrapht_capi_mst_create_eit(thread,  mst);
    assert(jgrapht_capi_it_next_long(thread,  eit) == 1);
    assert(jgrapht_capi_it_next_long(thread,  eit) == 2);
    assert(jgrapht_capi_it_next_long(thread,  eit) == 3);
    assert(jgrapht_capi_it_next_long(thread,  eit) == 4);
    assert(!jgrapht_capi_it_hasnext(thread,  eit));
    jgrapht_capi_destroy(thread,  eit);
    jgrapht_capi_destroy(thread,  mst);

    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}