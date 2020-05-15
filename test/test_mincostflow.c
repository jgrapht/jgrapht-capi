#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

int edge_lower_bound(int e) { 
    return 0;
}

int edge_upper_bound(int e) { 
    switch(e) { 
    case 0:
        return 20;
    case 1:
        return 10;
    case 2:
        return 30;
    case 3:
        return 10;
    case 4:
        return 20;
    }
    return 0;
}

int vertex_supply(int v) { 
    switch(v) { 
    case 0:
        return 30;
    case 3:
        return -30;
    }
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


    void *dual, *flow; 
    double flow_cost;
    jgrapht_capi_mincostflow_exec_capacity_scaling(thread, g, vertex_supply, edge_lower_bound, edge_upper_bound, 8, &flow_cost, &flow, &dual);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    assert(flow_cost == 1300.0);

    jgrapht_capi_handles_destroy(thread, flow);
    jgrapht_capi_handles_destroy(thread, dual);

    // cleanup
    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
