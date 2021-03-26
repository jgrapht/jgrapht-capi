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

    void *edgelist;
    jgrapht_capi_list_create(thread, &edgelist);

    int added;
    jgrapht_capi_ii_list_edge_pair_add(thread, edgelist, 0, 1, &added);
    jgrapht_capi_ii_list_edge_pair_add(thread, edgelist, 1, 2, &added);
    jgrapht_capi_ii_list_edge_pair_add(thread, edgelist, 2, 3, &added);
    jgrapht_capi_ii_list_edge_pair_add(thread, edgelist, 3, 4, &added);
    jgrapht_capi_ii_list_edge_pair_add(thread, edgelist, 4, 5, &added);
    jgrapht_capi_ii_list_edge_pair_add(thread, edgelist, 0, 2, &added);
    jgrapht_capi_ii_list_edge_pair_add(thread, edgelist, 0, 3, &added);

    void *g;
    jgrapht_capi_ii_graph_sparse_create(thread, 1, 0, 6, edgelist, INCOMING_EDGES_SUPPORT_LAZY_INCOMING_EDGES, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, edgelist);

    int flag;
    assert(jgrapht_capi_xx_graph_is_directed(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_xx_graph_is_undirected(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_xx_graph_is_weighted(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_xx_graph_is_allowing_selfloops(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_xx_graph_is_allowing_multipleedges(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_xx_graph_is_allowing_cycles(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_xx_graph_is_modifiable(thread, g, &flag) == 0);
    assert(flag == 0);

    int count;
    jgrapht_capi_ix_graph_vertices_count(thread, g, &count);
    assert(count == 6);
    jgrapht_capi_ix_graph_edges_count(thread, g, &count);
    assert(count == 7);

    // incoming
    int d;
    assert(jgrapht_capi_ix_graph_indegree_of(thread, g, 2, &d) == 0);
    assert(d == 2);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
