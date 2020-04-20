#include <stdio.h>
#include <stdlib.h>
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
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 5);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 6);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 7);

    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 1) == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2) == 1);
    assert(jgrapht_capi_graph_add_edge(thread, g, 2, 3) == 2);
    assert(jgrapht_capi_graph_add_edge(thread, g, 3, 0) == 3);
    assert(jgrapht_capi_graph_add_edge(thread, g, 4, 5) == 4);
    assert(jgrapht_capi_graph_add_edge(thread, g, 5, 6) == 5);
    assert(jgrapht_capi_graph_add_edge(thread, g, 6, 7) == 6);
    assert(jgrapht_capi_graph_add_edge(thread, g, 7, 4) == 7);
    assert(jgrapht_capi_graph_add_edge(thread, g, 3, 4) == 8);

    // set larger weight on bridge
    jgrapht_capi_graph_set_edge_weight(thread, g, 8, 100.0);
    assert(jgrapht_capi_get_errno(thread) == 0);

    int k = 2;
    void *c = jgrapht_capi_clustering_exec_k_spanning_tree(thread, g, k);
    assert(jgrapht_capi_get_errno(thread) == 0);

    int num_clusters = jgrapht_capi_clustering_get_number_clusters(thread, c);
    assert(num_clusters == 2);

    void *vit = jgrapht_capi_clustering_ith_cluster_vit(thread, c, 0);
    assert(jgrapht_capi_it_next_long(thread, vit) == 0);
    assert(jgrapht_capi_it_next_long(thread, vit) == 1);
    assert(jgrapht_capi_it_next_long(thread, vit) == 2);
    assert(jgrapht_capi_it_next_long(thread, vit) == 3);
    assert(!jgrapht_capi_it_hasnext(thread, vit));
    jgrapht_capi_destroy(thread, vit);

    vit = jgrapht_capi_clustering_ith_cluster_vit(thread, c, 1);
    assert(jgrapht_capi_it_next_long(thread, vit) == 4);
    assert(jgrapht_capi_it_next_long(thread, vit) == 5);
    assert(jgrapht_capi_it_next_long(thread, vit) == 6);
    assert(jgrapht_capi_it_next_long(thread, vit) == 7);
    assert(!jgrapht_capi_it_hasnext(thread, vit));
    jgrapht_capi_destroy(thread, vit);

    jgrapht_capi_destroy(thread, c);

    // cleanup
    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}