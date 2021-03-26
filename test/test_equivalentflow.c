#include <stdio.h>
#include <stdlib.h>

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
    jgrapht_capi_ii_graph_create(thread, 0, 0, 0, 1, NULL, NULL, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 0, 20);
    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 2, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 1, 10);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 2, 30);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 3, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 3, 10);
    jgrapht_capi_ii_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_xi_graph_set_edge_weight(thread, g, 4, 20);

    assert(jgrapht_capi_error_get_errno(thread) == 0);


    // eft
    void *eft;
    jgrapht_capi_xx_equivalentflowtree_exec_gusfield(thread, g, &eft);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // max s-t flow
    double value;
    jgrapht_capi_ix_equivalentflowtree_max_st_flow(thread, eft, 0, 3, &value);
    assert(value == 30.0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // tree
    void *tree;
    jgrapht_capi_ii_equivalentflowtree_tree(thread, eft, &tree);
    int count;
    jgrapht_capi_ix_graph_vertices_count(thread, tree, &count);
    assert (count == 4);
    jgrapht_capi_ix_graph_edges_count(thread, tree, &count);
    assert (count == 3);
    jgrapht_capi_handles_destroy(thread, tree);

    // cleanup
    jgrapht_capi_handles_destroy(thread, eft);
    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
