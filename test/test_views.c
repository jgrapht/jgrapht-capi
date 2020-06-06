#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

double weightfunction(int e) { 
    if (e < 3) { 
        return 3.0;
    } else { 
        return 5.0;
    }
}

int vertex_mask(int v) { 
    if (v == 1) { 
        return 1;
    }
    return 0;
}

int edge_mask(int e) { 
    return 0;
}

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

    int v1; 
    jgrapht_capi_graph_add_vertex(thread, g, &v1);
    int v2;
    jgrapht_capi_graph_add_vertex(thread, g, &v2);
    int v3;
    jgrapht_capi_graph_add_vertex(thread, g, &v3);
    int v4;
    jgrapht_capi_graph_add_vertex(thread, g, &v4);
    int v5;
    jgrapht_capi_graph_add_vertex(thread, g, &v5);

    int vcount;
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 5);

    int e12;
    jgrapht_capi_graph_add_edge(thread, g, v1, v2, &e12);
    int e23_1;
    jgrapht_capi_graph_add_edge(thread, g, v2, v3, &e23_1);
    int e23_2;
    jgrapht_capi_graph_add_edge(thread, g, v2, v3, &e23_2);
    int e24;
    jgrapht_capi_graph_add_edge(thread, g, v2, v4, &e24);
    int e44;
    jgrapht_capi_graph_add_edge(thread, g, v4, v4, &e44);
    int e55_1;
    jgrapht_capi_graph_add_edge(thread, g, v5, v5, &e55_1);
    int e52;
    jgrapht_capi_graph_add_edge(thread, g, v5, v2, &e52);
    int e55_2;
    jgrapht_capi_graph_add_edge(thread, g, v5, v5, &e55_2);

    int flag;
    assert(jgrapht_capi_graph_is_directed(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_undirected(thread, g, &flag) == 0);
    assert(flag == 0);

    void *g1;
    jgrapht_capi_graph_as_undirected(thread, g, &g1);

    assert(jgrapht_capi_graph_is_directed(thread, g1, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_undirected(thread, g1, &flag) == 0);
    assert(flag == 1);

    jgrapht_capi_handles_destroy(thread, g1);

    jgrapht_capi_graph_set_edge_weight(thread, g, e12, 100.0);
    double w;
    jgrapht_capi_graph_get_edge_weight(thread, g, e12, &w);
    assert(w == 100.0);
    jgrapht_capi_graph_as_unweighted(thread, g, &g1);
    jgrapht_capi_graph_get_edge_weight(thread, g1, e12, &w);
    assert(w == 1.0);
    jgrapht_capi_handles_destroy(thread, g1);

    jgrapht_capi_graph_as_edgereversed(thread, g, &g1);
    int v;
    jgrapht_capi_graph_edge_source(thread, g1, e12, &v);
    assert(v == v2);
    jgrapht_capi_graph_edge_target(thread, g1, e12, &v);
    assert(v == v1);
    jgrapht_capi_handles_destroy(thread, g1);

    jgrapht_capi_graph_as_unmodifiable(thread, g, &g1);
    jgrapht_capi_graph_add_edge(thread, g1, v1, v5, NULL);
    assert(jgrapht_capi_error_get_errno(thread) != 0);
    assert(strcmp("this graph is unmodifiable", jgrapht_capi_error_get_errno_msg(thread))==0);
    jgrapht_capi_error_clear_errno(thread);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, g1);

    jgrapht_capi_graph_as_weighted(thread, g, weightfunction, 0, 0, &g1);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_graph_get_edge_weight(thread, g1, e55_1, &w);
    assert(w == 5.0);
    jgrapht_capi_handles_destroy(thread, g1);

    jgrapht_capi_graph_as_masked_subgraph(thread, g, vertex_mask, edge_mask, &g1);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_graph_vertices_count(thread, g1, &vcount);
    assert(vcount == 4);
    jgrapht_capi_handles_destroy(thread, g1);

    // test as_subgraph
    void *vset;
    void *eset;
    jgrapht_capi_set_create(thread, &vset);
    jgrapht_capi_set_int_add(thread, vset, v1, NULL);
    jgrapht_capi_set_int_add(thread, vset, v2, NULL);
    jgrapht_capi_set_int_add(thread, vset, v3, NULL);

    jgrapht_capi_set_create(thread, &eset);
    jgrapht_capi_set_int_add(thread, eset, e12, NULL);
    jgrapht_capi_set_int_add(thread, eset, e23_2, NULL);

    jgrapht_capi_graph_as_subgraph(thread, g, vset, eset, &g1);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_graph_vertices_count(thread, g1, &vcount);
    assert(vcount == 3);
    int ecount = 0;
    jgrapht_capi_graph_edges_count(thread, g1, &ecount);
    assert(ecount == 2);
    int contains=1;
    jgrapht_capi_graph_contains_edge(thread, g1, e23_1, &contains);
    assert(contains == 0);
    jgrapht_capi_handles_destroy(thread, g1);

    jgrapht_capi_handles_destroy(thread, vset);
    jgrapht_capi_handles_destroy(thread, eset);

    jgrapht_capi_handles_destroy(thread, g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}