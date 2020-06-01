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

    void *odd_vertices;
    jgrapht_capi_set_create(thread, &odd_vertices);
    jgrapht_capi_set_int_add(thread, odd_vertices, 0, NULL);
    jgrapht_capi_set_int_add(thread, odd_vertices, 1, NULL);

    assert(jgrapht_capi_error_get_errno(thread) == 0);


    void *cutSourcePartition; 
    double cut_value;

    jgrapht_capi_cut_oddmincutset_exec_padberg_rao(thread, g, odd_vertices, 1, &cut_value, &cutSourcePartition);
    assert(cut_value == 30.0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int contains = 0;
    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 0, &contains);
    assert(contains == 1);
    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 1, &contains);
    assert(contains == 0);
    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 2, &contains);
    assert(contains == 0);
    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 3, &contains);
    assert(contains == 0);

    jgrapht_capi_handles_destroy(thread, cutSourcePartition);
    jgrapht_capi_handles_destroy(thread, odd_vertices);

    // cleanup
    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);    

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
