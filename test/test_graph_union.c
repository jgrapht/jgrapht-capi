#include <stdio.h>
#include <stdlib.h>

#ifdef _WIN32
#include <crtdbg.h>
#endif 
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

double combiner(double a, double b) { 
    return 10.0*(a+b);
}

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

    void *g1, *g2;
    jgrapht_capi_ii_graph_create(thread, 0, 0, 0, 1, NULL, NULL, &g1);
    jgrapht_capi_ii_graph_create(thread, 0, 0, 0, 1, NULL, NULL, &g2);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_ix_graph_add_vertex(thread, g1, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g1, NULL);

    jgrapht_capi_ix_graph_add_vertex(thread, g2, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g2, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g2, NULL);


    jgrapht_capi_ii_graph_add_edge(thread, g1, 0, 1, NULL);

    jgrapht_capi_ii_graph_add_edge(thread, g2, 0, 1, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g2, 1, 2, NULL);

    jgrapht_capi_xi_graph_set_edge_weight(thread, g1, 0, 5.0);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g2, 0, 4.0);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g2, 1, 3.0);

    void *g;
    jgrapht_capi_xx_graph_as_graph_union(thread, g1, g2, combiner, &g);

    int count;
    jgrapht_capi_ix_graph_vertices_count(thread, g, &count);
    assert (count == 3);
    jgrapht_capi_ix_graph_edges_count(thread, g, &count);
    assert (count == 2);

    double weight;
    jgrapht_capi_xi_graph_get_edge_weight(thread, g, 0, &weight);
    assert (weight == 10*(5.0+4.0));
    jgrapht_capi_xi_graph_get_edge_weight(thread, g, 1, &weight);
    assert (weight == 3.0);

    jgrapht_capi_handles_destroy(thread, g);

    // test with default combiner
    jgrapht_capi_xx_graph_as_graph_union(thread, g1, g2, NULL, &g);
    jgrapht_capi_ix_graph_vertices_count(thread, g, &count);
    assert (count == 3);
    jgrapht_capi_ix_graph_edges_count(thread, g, &count);
    assert (count == 2);
    jgrapht_capi_xi_graph_get_edge_weight(thread, g, 0, &weight);
    assert (weight == 5.0+4.0);
    jgrapht_capi_xi_graph_get_edge_weight(thread, g, 1, &weight);
    assert (weight == 3.0);


    jgrapht_capi_handles_destroy(thread, g1);
    jgrapht_capi_handles_destroy(thread, g2);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
