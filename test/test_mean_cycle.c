#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#ifdef _WIN32
#include <crtdbg.h>
#endif 
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

int main() {
    
#ifdef _WIN32
    _CrtSetReportMode( _CRT_WARN, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_WARN, _CRTDBG_FILE_STDERR);
    _CrtSetReportMode( _CRT_ERROR, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_ERROR, _CRTDBG_FILE_STDERR);
    _CrtSetReportMode( _CRT_ASSERT, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_ASSERT, _CRTDBG_FILE_STDERR);
#endif

    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_ii_graph_create(thread, 1, 0, 0, 1, NULL, NULL, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 0, 5.0);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 1, 2.0);
    jgrapht_capi_ii_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 2, 100.0);
    jgrapht_capi_ii_graph_add_edge(thread, g, 3, 4, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 3, 2.0);
    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 5, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 4, 20.0);
    jgrapht_capi_ii_graph_add_edge(thread, g, 5, 3, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 5, 20.0);
    jgrapht_capi_ii_graph_add_edge(thread, g, 4, 5, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 6, 5.0);
    jgrapht_capi_ii_graph_add_edge(thread, g, 3, 0, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 7, 500.0);
    
    void *cycle;
    double mean; 

    jgrapht_capi_xx_cycles_mean_exec_howard(thread, g, 100, 1e-6, &mean, &cycle);
    assert(mean == 9.0);

    int start_vertex, end_vertex;
    double weight;
    jgrapht_capi_ix_handles_get_graphpath(thread, cycle, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 27);
    assert(start_vertex == 5);
    assert(end_vertex == 5);
    jgrapht_capi_handles_destroy(thread, cycle);

    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
