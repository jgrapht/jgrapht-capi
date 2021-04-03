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
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 3, 0, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 4, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 4, 5, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 5, 2, NULL);

    double weight;
    void *gpit;
    jgrapht_capi_xx_cycles_fundamental_basis_exec_paton(thread, g, &weight, &gpit);

    assert(weight == 8.0);

    void *gp;
    int hasnext;
    jgrapht_capi_x_it_hasnext(thread, gpit, &hasnext);
    assert(hasnext==1);
    jgrapht_capi_x_it_next(thread, gpit, &gp);
    assert(jgrapht_capi_error_get_errno(thread) == 0);    

    int start_vertex; 
    int end_vertex; 
    jgrapht_capi_ix_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 4.0);
    jgrapht_capi_handles_destroy(thread, gp);

    // 2nd path
    jgrapht_capi_x_it_hasnext(thread, gpit, &hasnext);
    assert(hasnext==1);
    jgrapht_capi_x_it_next(thread, gpit, &gp);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_ix_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 4.0);
    jgrapht_capi_handles_destroy(thread, gp);    

    jgrapht_capi_x_it_hasnext(thread, gpit, &hasnext);
    assert(hasnext==0);

    jgrapht_capi_handles_destroy(thread, gpit);
    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
