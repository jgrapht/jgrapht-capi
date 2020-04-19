#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht.h>

#define ITERATOR_NO_SUCH_ELEMENT 100

int main() { 
    jgrapht_thread_create();
    assert(jgrapht_is_thread_attached());

    assert(jgrapht_get_errno() == 0);

    void *g = jgrapht_graph_create(0, 1, 1, 1);
    assert(jgrapht_get_errno() == 0);

    long v1 = jgrapht_graph_add_vertex(g);
    long v2 = jgrapht_graph_add_vertex(g);
    long v3 = jgrapht_graph_add_vertex(g);
    long v4 = jgrapht_graph_add_vertex(g);
    long v5 = jgrapht_graph_add_vertex(g);

    assert(jgrapht_graph_vertices_count(g) == 5);

    long e12 = jgrapht_graph_add_edge(g, v1, v2);
    long e23_1 = jgrapht_graph_add_edge(g, v2, v3);
    long e23_2 = jgrapht_graph_add_edge(g, v2, v3);
    long e24 = jgrapht_graph_add_edge(g, v2, v4);
    long e44 = jgrapht_graph_add_edge(g, v4, v4);
    long e55_1 = jgrapht_graph_add_edge(g, v5, v5);
    long e52 = jgrapht_graph_add_edge(g, v5, v2);
    long e55_2 = jgrapht_graph_add_edge(g, v5, v5);

    // inout
    assert(jgrapht_graph_degree_of(g, v1) == 1);
    assert(jgrapht_graph_degree_of(g, v2) == 5);
    assert(jgrapht_graph_degree_of(g, v3) == 2);
    assert(jgrapht_graph_degree_of(g, v4) == 3);
    assert(jgrapht_graph_degree_of(g, v5) == 5);

    void *eit = jgrapht_graph_vertex_create_eit(g, v1);
    assert(jgrapht_it_next(eit) == e12);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_eit(g, v2);
    assert(jgrapht_it_next(eit) == e12);
    assert(jgrapht_it_next(eit) == e23_1);
    assert(jgrapht_it_next(eit) == e23_2);
    assert(jgrapht_it_next(eit) == e24);
    assert(jgrapht_it_next(eit) == e52);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_eit(g, v3);
    assert(jgrapht_it_next(eit) == e23_1);
    assert(jgrapht_it_next(eit) == e23_2);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_eit(g, v4);
    assert(jgrapht_it_next(eit) == e24);
    assert(jgrapht_it_next(eit) == e44);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_eit(g, v5);
    assert(jgrapht_it_next(eit) == e55_1);    
    assert(jgrapht_it_next(eit) == e52);        
    assert(jgrapht_it_next(eit) == e55_2);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    // incoming
    assert(jgrapht_graph_indegree_of(g, v1) == 1);
    assert(jgrapht_graph_indegree_of(g, v2) == 5);
    assert(jgrapht_graph_indegree_of(g, v3) == 2);
    assert(jgrapht_graph_indegree_of(g, v4) == 3);
    assert(jgrapht_graph_indegree_of(g, v5) == 5);

    eit = jgrapht_graph_vertex_create_in_eit(g, v1);
    assert(jgrapht_it_next(eit) == e12);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_in_eit(g, v2);
    assert(jgrapht_it_next(eit) == e12);
    assert(jgrapht_it_next(eit) == e23_1);
    assert(jgrapht_it_next(eit) == e23_2);
    assert(jgrapht_it_next(eit) == e24);
    assert(jgrapht_it_next(eit) == e52);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_in_eit(g, v3);
    assert(jgrapht_it_next(eit) == e23_1);
    assert(jgrapht_it_next(eit) == e23_2);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_in_eit(g, v4);
    assert(jgrapht_it_next(eit) == e24);
    assert(jgrapht_it_next(eit) == e44);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_in_eit(g, v5);
    assert(jgrapht_it_next(eit) == e55_1);
    assert(jgrapht_it_next(eit) == e52);
    assert(jgrapht_it_next(eit) == e55_2);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    // outgoing
    assert(jgrapht_graph_outdegree_of(g, v1) == 1);
    assert(jgrapht_graph_outdegree_of(g, v2) == 5);
    assert(jgrapht_graph_outdegree_of(g, v3) == 2);
    assert(jgrapht_graph_outdegree_of(g, v4) == 3);
    assert(jgrapht_graph_outdegree_of(g, v5) == 5);
    
    eit = jgrapht_graph_vertex_create_out_eit(g, v1);
    assert(jgrapht_it_next(eit) == e12);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_out_eit(g, v2);
    assert(jgrapht_it_next(eit) == e12);
    assert(jgrapht_it_next(eit) == e23_1);
    assert(jgrapht_it_next(eit) == e23_2);
    assert(jgrapht_it_next(eit) == e24);
    assert(jgrapht_it_next(eit) == e52);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_out_eit(g, v3);
    assert(jgrapht_it_next(eit) == e23_1);
    assert(jgrapht_it_next(eit) == e23_2);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_out_eit(g, v4);
    assert(jgrapht_it_next(eit) == e24);
    assert(jgrapht_it_next(eit) == e44);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    eit = jgrapht_graph_vertex_create_out_eit(g, v5);
    assert(jgrapht_it_next(eit) == e55_1);
    assert(jgrapht_it_next(eit) == e52);
    assert(jgrapht_it_next(eit) == e55_2);
    assert(!jgrapht_it_hasnext(eit));
    jgrapht_destroy(eit);

    jgrapht_destroy(g);
    assert(jgrapht_get_errno() == 0);

    jgrapht_thread_destroy();
    assert(!jgrapht_is_thread_attached());

}