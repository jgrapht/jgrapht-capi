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

    int flag;
    assert(jgrapht_capi_graph_is_directed(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_undirected(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_weighted(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_allowing_selfloops(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_allowing_multipleedges(thread, g, &flag) == 0);
    assert(flag == 0);

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
    void *c;
    assert(jgrapht_capi_clustering_exec_k_spanning_tree(thread, g, k, &c) == 0);
    assert(jgrapht_capi_get_errno(thread) == 0);

    long long num_clusters;
    jgrapht_capi_clustering_get_number_clusters(thread, c, &num_clusters);
    assert(num_clusters == 2);

    void *vit;
    jgrapht_capi_clustering_ith_cluster_vit(thread, c, 0, &vit);
    long long v;
    assert(jgrapht_capi_it_next_long(thread, vit, &v) == 0);
    assert(v == 0);
    assert(jgrapht_capi_it_next_long(thread, vit, &v) == 0);
    assert(v == 1);
    assert(jgrapht_capi_it_next_long(thread, vit, &v) == 0);
    assert(v == 2);
    assert(jgrapht_capi_it_next_long(thread, vit, &v) == 0);
    assert(v == 3);
    int hasnext;
    assert(jgrapht_capi_it_hasnext(thread, vit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_destroy(thread, vit);

    jgrapht_capi_clustering_ith_cluster_vit(thread, c, 1, &vit);
    assert(jgrapht_capi_it_next_long(thread, vit, &v) == 0);
    assert(v == 4);
    assert(jgrapht_capi_it_next_long(thread, vit, &v) == 0);
    assert(v == 5);
    assert(jgrapht_capi_it_next_long(thread, vit, &v) == 0);
    assert(v == 6);
    assert(jgrapht_capi_it_next_long(thread, vit, &v) == 0);
    assert(v == 7);
    assert(!jgrapht_capi_it_hasnext(thread, vit, &hasnext));
    assert(hasnext == 0);
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