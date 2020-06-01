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

    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 0, 20);
    jgrapht_capi_graph_add_edge(thread, g, 0, 2, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 1, 10);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 2, 30);
    jgrapht_capi_graph_add_edge(thread, g, 1, 3, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 3, 10);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 4, 20);

    assert(jgrapht_capi_error_get_errno(thread) == 0);


    // gomory-hu
    void *hu;
    jgrapht_capi_cut_gomoryhu_exec_gusfield(thread, g, &hu);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // min s-t cut
    void *source_partition; 
    double value;
    jgrapht_capi_cut_gomoryhu_min_st_cut(thread, hu, 0, 3, &value, &source_partition);
    assert(value == 30.0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int contains = 0;
    jgrapht_capi_set_int_contains(thread, source_partition, 0, &contains);
    assert(contains == 1);
    jgrapht_capi_set_int_contains(thread, source_partition, 1, &contains);
    assert(contains == 1);
    jgrapht_capi_set_int_contains(thread, source_partition, 2, &contains);
    assert(contains == 1);
    jgrapht_capi_set_int_contains(thread, source_partition, 3, &contains);
    assert(contains == 0);
    jgrapht_capi_handles_destroy(thread, source_partition);


    // min cut
    jgrapht_capi_cut_gomoryhu_min_cut(thread, hu, &value, &source_partition);
    assert(value == 30.0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    contains = 0;
    jgrapht_capi_set_int_contains(thread, source_partition, 0, &contains);
    assert(contains == 0);
    jgrapht_capi_set_int_contains(thread, source_partition, 1, &contains);
    assert(contains == 1);
    jgrapht_capi_set_int_contains(thread, source_partition, 2, &contains);
    assert(contains == 1);
    jgrapht_capi_set_int_contains(thread, source_partition, 3, &contains);
    assert(contains == 1);
    jgrapht_capi_handles_destroy(thread, source_partition);


    // tree
    void *tree;
    jgrapht_capi_cut_gomoryhu_tree(thread, hu, &tree);
    int count;
    jgrapht_capi_graph_vertices_count(thread, tree, &count);
    assert (count == 4);
    jgrapht_capi_graph_edges_count(thread, tree, &count);
    assert (count == 3);
    jgrapht_capi_handles_destroy(thread, tree);

    // cleanup
    jgrapht_capi_handles_destroy(thread, hu);
    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
