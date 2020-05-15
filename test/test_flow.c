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
    jgrapht_capi_graph_create(thread, 1, 0, 0, 1, &g);
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

    void *cutSourcePartition, *flow; 
    double flow_value;

    // dinic
    jgrapht_capi_maxflow_exec_dinic(thread, g, 0, 3, &flow_value, &flow, &cutSourcePartition);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(flow_value == 30.0);

    double e2_flow;
    jgrapht_capi_map_int_double_get(thread, flow, 2, &e2_flow);
    assert(e2_flow == 10.0);
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

    jgrapht_capi_handles_destroy(thread, flow);
    jgrapht_capi_handles_destroy(thread, cutSourcePartition);


    // push relabel
    jgrapht_capi_maxflow_exec_push_relabel(thread, g, 0, 3, &flow_value, &flow, &cutSourcePartition);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(flow_value == 30.0);

    jgrapht_capi_map_int_double_get(thread, flow, 2, &e2_flow);
    assert(e2_flow == 10.0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 0, &contains);
    assert(contains == 1);
    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 1, &contains);
    assert(contains == 0);
    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 2, &contains);
    assert(contains == 0);
    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 3, &contains);
    assert(contains == 0);

    jgrapht_capi_handles_destroy(thread, flow);
    jgrapht_capi_handles_destroy(thread, cutSourcePartition);


    // edmonds karp
    jgrapht_capi_maxflow_exec_edmonds_karp(thread, g, 0, 3, &flow_value, &flow, &cutSourcePartition);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(flow_value == 30.0);

    jgrapht_capi_map_int_double_get(thread, flow, 2, &e2_flow);
    assert(e2_flow == 10.0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 0, &contains);
    assert(contains == 1);
    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 1, &contains);
    assert(contains == 0);
    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 2, &contains);
    assert(contains == 0);
    jgrapht_capi_set_int_contains(thread, cutSourcePartition, 3, &contains);
    assert(contains == 0);

    jgrapht_capi_handles_destroy(thread, flow);
    jgrapht_capi_handles_destroy(thread, cutSourcePartition);

    // cleanup
    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
