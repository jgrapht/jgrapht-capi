#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht.h>

#define ITERATOR_NO_SUCH_ELEMENT 100

int main() { 
    jgrapht_thread_create();
    assert(jgrapht_is_thread_attached());

    assert(jgrapht_get_errno() == 0);

    void *g = jgrapht_graph_create(0, 0, 0, 1);
    assert(jgrapht_get_errno() == 0);

    assert(!jgrapht_graph_is_directed(g));
    assert(jgrapht_graph_is_undirected(g));
    assert(jgrapht_graph_is_weighted(g));
    assert(!jgrapht_graph_is_allowing_selfloops(g));
    assert(!jgrapht_graph_is_allowing_multipleedges(g));

    assert(jgrapht_graph_add_vertex(g) == 0);
    assert(jgrapht_graph_add_vertex(g) == 1);
    assert(jgrapht_graph_add_vertex(g) == 2);
    assert(jgrapht_graph_add_vertex(g) == 3);
    assert(jgrapht_graph_add_vertex(g) == 4);

    assert(jgrapht_graph_add_edge(g, 0, 1) == 0);
    assert(jgrapht_graph_add_edge(g, 1, 2) == 1);
    assert(jgrapht_graph_add_edge(g, 2, 3) == 2);
    assert(jgrapht_graph_add_edge(g, 3, 4) == 3);
    assert(jgrapht_graph_add_edge(g, 4, 0) == 4);

    jgrapht_graph_set_edge_weight(g, 0, 5.0);
    jgrapht_graph_set_edge_weight(g, 1, 4.0);
    jgrapht_graph_set_edge_weight(g, 2, 3.0);
    jgrapht_graph_set_edge_weight(g, 3, 2.0);
    jgrapht_graph_set_edge_weight(g, 4, 1.0);

    // run kruskal
    void *mst = jgrapht_mst_exec_kruskal(g);
    assert(jgrapht_mst_get_weight(mst) == 10.0);
    void *eit = jgrapht_mst_create_eit(mst);
    assert(jgrapht_it_next(eit) == 1);
    assert(jgrapht_it_next(eit) == 2);
    assert(jgrapht_it_next(eit) == 3);
    assert(jgrapht_it_next(eit) == 4);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);
    jgrapht_destroy(mst);

    // run prim
    mst = jgrapht_mst_exec_prim(g);
    assert(jgrapht_mst_get_weight(mst) == 10.0);
    eit = jgrapht_mst_create_eit(mst);
    assert(jgrapht_it_next(eit) == 1);
    assert(jgrapht_it_next(eit) == 2);
    assert(jgrapht_it_next(eit) == 3);
    assert(jgrapht_it_next(eit) == 4);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);
    jgrapht_destroy(mst);

    jgrapht_destroy(g);

    jgrapht_thread_destroy();
    assert(!jgrapht_is_thread_attached());

}