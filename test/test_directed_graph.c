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

    void *g = jgrapht_capi_graph_create(thread, 1, 1, 1, 1);
    assert(jgrapht_capi_get_errno(thread) == 0);

    long v1 = jgrapht_capi_graph_add_vertex(thread, g);
    long v2 = jgrapht_capi_graph_add_vertex(thread, g);
    long v3 = jgrapht_capi_graph_add_vertex(thread, g);
    long v4 = jgrapht_capi_graph_add_vertex(thread, g);
    long v5 = jgrapht_capi_graph_add_vertex(thread, g);

    assert(jgrapht_capi_graph_vertices_count(thread, g) == 5);

    long e12 = jgrapht_capi_graph_add_edge(thread, g, v1, v2);
    long e23_1 = jgrapht_capi_graph_add_edge(thread, g, v2, v3);
    long e23_2 = jgrapht_capi_graph_add_edge(thread, g, v2, v3);
    long e24 = jgrapht_capi_graph_add_edge(thread, g, v2, v4);
    long e44 = jgrapht_capi_graph_add_edge(thread, g, v4, v4);
    long e55_1 = jgrapht_capi_graph_add_edge(thread, g, v5, v5);
    long e52 = jgrapht_capi_graph_add_edge(thread, g, v5, v2);
    long e55_2 = jgrapht_capi_graph_add_edge(thread, g, v5, v5);

    // inout
    assert(jgrapht_capi_graph_degree_of(thread, g, v1) == 1);
    assert(jgrapht_capi_graph_degree_of(thread, g, v2) == 5);
    assert(jgrapht_capi_graph_degree_of(thread, g, v3) == 2);
    assert(jgrapht_capi_graph_degree_of(thread, g, v4) == 3);
    assert(jgrapht_capi_graph_degree_of(thread, g, v5) == 5);

    void *eit = jgrapht_capi_graph_vertex_create_eit(thread, g, v1);
    assert(jgrapht_capi_it_next_long(thread, eit) == e12);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_eit(thread, g, v2);
    assert(jgrapht_capi_it_next_long(thread, eit) == e12);
    assert(jgrapht_capi_it_next_long(thread, eit) == e52);
    assert(jgrapht_capi_it_next_long(thread, eit) == e23_1);
    assert(jgrapht_capi_it_next_long(thread, eit) == e23_2);
    assert(jgrapht_capi_it_next_long(thread, eit) == e24);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_eit(thread, g, v3);
    assert(jgrapht_capi_it_next_long(thread, eit) == e23_1);
    assert(jgrapht_capi_it_next_long(thread, eit) == e23_2);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_eit(thread, g, v4);
    assert(jgrapht_capi_it_next_long(thread, eit) == e24);
    assert(jgrapht_capi_it_next_long(thread, eit) == e44);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_eit(thread, g, v5);
    assert(jgrapht_capi_it_next_long(thread, eit) == e55_1);
    assert(jgrapht_capi_it_next_long(thread, eit) == e55_2);
    assert(jgrapht_capi_it_next_long(thread, eit) == e52);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    // incoming
    assert(jgrapht_capi_graph_indegree_of(thread, g, v1) == 0);
    assert(jgrapht_capi_graph_indegree_of(thread, g, v2) == 2);
    assert(jgrapht_capi_graph_indegree_of(thread, g, v3) == 2);
    assert(jgrapht_capi_graph_indegree_of(thread, g, v4) == 2);
    assert(jgrapht_capi_graph_indegree_of(thread, g, v5) == 2);

    eit = jgrapht_capi_graph_vertex_create_in_eit(thread, g, v1);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_in_eit(thread, g, v2);
    assert(jgrapht_capi_it_next_long(thread, eit) == e12);
    assert(jgrapht_capi_it_next_long(thread, eit) == e52);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_in_eit(thread, g, v3);
    assert(jgrapht_capi_it_next_long(thread, eit) == e23_1);
    assert(jgrapht_capi_it_next_long(thread, eit) == e23_2);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_in_eit(thread, g, v4);
    assert(jgrapht_capi_it_next_long(thread, eit) == e24);
    assert(jgrapht_capi_it_next_long(thread, eit) == e44);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_in_eit(thread, g, v5);
    assert(jgrapht_capi_it_next_long(thread, eit) == e55_1);
    assert(jgrapht_capi_it_next_long(thread, eit) == e55_2);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    // outgoing
    assert(jgrapht_capi_graph_outdegree_of(thread, g, v1) == 1);
    assert(jgrapht_capi_graph_outdegree_of(thread, g, v2) == 3);
    assert(jgrapht_capi_graph_outdegree_of(thread, g, v3) == 0);
    assert(jgrapht_capi_graph_outdegree_of(thread, g, v4) == 1);
    assert(jgrapht_capi_graph_outdegree_of(thread, g, v5) == 3);
    
    eit = jgrapht_capi_graph_vertex_create_out_eit(thread, g, v1);
    assert(jgrapht_capi_it_next_long(thread, eit) == e12);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_out_eit(thread, g, v2);
    assert(jgrapht_capi_it_next_long(thread, eit) == e23_1);
    assert(jgrapht_capi_it_next_long(thread, eit) == e23_2);
    assert(jgrapht_capi_it_next_long(thread, eit) == e24);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_out_eit(thread, g, v3);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_out_eit(thread, g, v4);
    assert(jgrapht_capi_it_next_long(thread, eit) == e44);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    eit = jgrapht_capi_graph_vertex_create_out_eit(thread, g, v5);
    assert(jgrapht_capi_it_next_long(thread, eit) == e55_1);
    assert(jgrapht_capi_it_next_long(thread, eit) == e52);
    assert(jgrapht_capi_it_next_long(thread, eit) == e55_2);
    assert(!jgrapht_capi_it_hasnext(thread, eit));
    jgrapht_capi_destroy(thread, eit);

    jgrapht_capi_destroy(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}