#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <jgrapht_capi.h>

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

    int flag;
    assert(jgrapht_capi_graph_is_directed(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_undirected(thread, g, &flag) == 0);
    assert(flag == 1);


    void *g1 = jgrapht_capi_graph_as_undirected(thread, g);

    assert(jgrapht_capi_graph_is_directed(thread, g1, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_undirected(thread, g1, &flag) == 0);
    assert(flag == 0);

    jgrapht_capi_destroy(thread, g1);

    jgrapht_capi_graph_set_edge_weight(thread, g, e12, 100.0);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g, e12) == 100.0);
    g1 = jgrapht_capi_graph_as_unweighted(thread, g);
    assert(jgrapht_capi_graph_get_edge_weight(thread, g1, e12) == 1.0);
    jgrapht_capi_destroy(thread, g1);

    g1 = jgrapht_capi_graph_as_edgereversed(thread, g);
    assert(jgrapht_capi_graph_edge_source(thread, g1, e12) == v2);
    assert(jgrapht_capi_graph_edge_target(thread, g1, e12) == v1);
    jgrapht_capi_destroy(thread, g1);

    g1 = jgrapht_capi_graph_as_unmodifiable(thread, g);
    jgrapht_capi_graph_add_edge(thread, g1, v1, v5);
    assert(jgrapht_capi_get_errno(thread) != 0);
    assert(strcmp("this graph is unmodifiable", jgrapht_capi_get_errno_msg(thread))==0);
    jgrapht_capi_clear_errno(thread);
    jgrapht_capi_destroy(thread, g);

    jgrapht_capi_destroy(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}