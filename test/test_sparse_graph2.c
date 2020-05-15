#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>


int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *edgelist;
    jgrapht_capi_list_create(thread, &edgelist);

    int added;
    jgrapht_capi_list_edge_triple_add(thread, edgelist, 0, 1, 13.0, &added);
    jgrapht_capi_list_edge_triple_add(thread, edgelist, 1, 2, 4, &added);
    jgrapht_capi_list_edge_triple_add(thread, edgelist, 2, 3, 2, &added);
    jgrapht_capi_list_edge_triple_add(thread, edgelist, 3, 4, 5, &added);
    jgrapht_capi_list_edge_triple_add(thread, edgelist, 4, 5, 8, &added);
    jgrapht_capi_list_edge_triple_add(thread, edgelist, 0, 2, 2, &added);
    jgrapht_capi_list_edge_triple_add(thread, edgelist, 0, 3, 3, &added);

    void *g;
    jgrapht_capi_graph_sparse_create(thread, 0, 1, 6, edgelist, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, edgelist);

    int flag;
    assert(jgrapht_capi_graph_is_directed(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_undirected(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_weighted(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_allowing_selfloops(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_allowing_multipleedges(thread, g, &flag) == 0);
    assert(flag == 1);

    int count;
    jgrapht_capi_graph_vertices_count(thread, g, &count);
    assert(count == 6);
    jgrapht_capi_graph_edges_count(thread, g, &count);
    assert(count == 7);

    double w;
    jgrapht_capi_graph_get_edge_weight(thread, g, 0, &w);
    assert(w == 13.0);

    jgrapht_capi_graph_set_edge_weight(thread, g, 2, 33.0);
    jgrapht_capi_graph_get_edge_weight(thread, g, 2, &w);
    assert(w == 33.0);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
