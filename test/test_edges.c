#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht.h>

#define NUM_VERTICES 1000
#define ITERATOR_NO_SUCH_ELEMENT 100

int main() { 
    jgrapht_thread_create();
    assert(jgrapht_is_thread_attached());

    assert(jgrapht_get_errno() == 0);

    void *g = jgrapht_graph_create(1, 1, 1, 1);
    assert(jgrapht_get_errno() == 0);

    assert(jgrapht_graph_is_directed(g));
    assert(!jgrapht_graph_is_undirected(g));
    assert(jgrapht_graph_is_weighted(g));
    assert(jgrapht_graph_is_allowing_selfloops(g));
    assert(jgrapht_graph_is_allowing_multipleedges(g));

    assert(jgrapht_graph_add_vertex(g) == 0);
    assert(jgrapht_graph_add_vertex(g) == 1);
    assert(jgrapht_graph_add_vertex(g) == 2);

    assert(jgrapht_graph_add_edge(g, 0, 0) == 0);
    assert(jgrapht_graph_add_edge(g, 0, 1) == 1);
    assert(jgrapht_graph_add_edge(g, 0, 2) == 2);
    assert(jgrapht_graph_add_edge(g, 1, 2) == 3);
    assert(jgrapht_graph_add_edge(g, 1, 2) == 4);
    assert(jgrapht_graph_add_edge(g, 1, 2) == 5);

    assert(jgrapht_graph_edge_source(g, 0) == 0);
    assert(jgrapht_graph_edge_target(g, 0) == 0);
    assert(jgrapht_graph_edge_source(g, 1) == 0);
    assert(jgrapht_graph_edge_target(g, 1) == 1);
    assert(jgrapht_graph_edge_source(g, 2) == 0);
    assert(jgrapht_graph_edge_target(g, 2) == 2);
    assert(jgrapht_graph_edge_source(g, 3) == 1);
    assert(jgrapht_graph_edge_target(g, 3) == 2);
    assert(jgrapht_graph_edge_source(g, 4) == 1);
    assert(jgrapht_graph_edge_target(g, 4) == 2);
    assert(jgrapht_graph_edge_source(g, 5) == 1);
    assert(jgrapht_graph_edge_target(g, 5) == 2);

    assert(jgrapht_graph_get_edge_weight(g, 0) == 1.0);
    assert(jgrapht_graph_get_edge_weight(g, 1) == 1.0);
    assert(jgrapht_graph_get_edge_weight(g, 2) == 1.0);
    assert(jgrapht_graph_get_edge_weight(g, 3) == 1.0);
    assert(jgrapht_graph_get_edge_weight(g, 4) == 1.0);
    assert(jgrapht_graph_get_edge_weight(g, 5) == 1.0);

    jgrapht_graph_set_edge_weight(g, 0, 5.0);
    assert(jgrapht_get_errno() == 0 && jgrapht_graph_get_edge_weight(g, 0) == 5.0);

    void *eit = jgrapht_graph_create_all_eit(g);
    assert(jgrapht_it_next(eit) == 0);
    assert(jgrapht_it_next(eit) == 1);
    assert(jgrapht_it_next(eit) == 2);
    assert(jgrapht_it_next(eit) == 3);
    assert(jgrapht_it_next(eit) == 4);
    assert(jgrapht_it_next(eit) == 5);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    assert(jgrapht_graph_contains_edge_between(g, 0, 0));
    assert(jgrapht_graph_contains_edge_between(g, 0, 1));
    assert(jgrapht_graph_contains_edge_between(g, 0, 2));
    assert(!jgrapht_graph_contains_edge_between(g, 1, 0));
    assert(!jgrapht_graph_contains_edge_between(g, 1, 1));
    assert(jgrapht_graph_contains_edge_between(g, 1, 2));
    assert(!jgrapht_graph_contains_edge_between(g, 2, 0));
    assert(!jgrapht_graph_contains_edge_between(g, 2, 1));
    assert(!jgrapht_graph_contains_edge_between(g, 2, 2));

    eit = jgrapht_graph_create_between_eit(g, 1 , 2);
    assert(jgrapht_it_next(eit) == 3);
    assert(jgrapht_it_next(eit) == 4);
    assert(jgrapht_it_next(eit) == 5);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    jgrapht_destroy(g);

    jgrapht_thread_destroy();
    assert(!jgrapht_is_thread_attached());

}