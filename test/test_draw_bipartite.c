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

    int i;
    for(i = 0; i < 7; i++) { 
        jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    }

    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 3, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 3, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 4, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 6, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 6, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 2, 4, NULL);

    // create model
    void *model;
    jgrapht_capi_xx_drawing_layout_model_2d_create(thread, 0, 0, 10.0, 10.0, &model);

    jgrapht_capi_ix_drawing_exec_two_layered_bipartite_layout_2d(thread, g, model, 0, 0, 1);
 
    double vx, vy;
    for(int i = 0; i < 7; i++) { 
        jgrapht_capi_ix_drawing_layout_model_2d_get_vertex(thread, model, i, &vx, &vy);
        assert(vx >= 0.0);
        assert(vx <= 10.0);
        assert(vy >= 0.0);
        assert(vy <= 10.0);
        //printf("(%lf,%lf)\n", vx, vy);
    }

    jgrapht_capi_handles_destroy(thread, model);

    // test with given partition 
    void *set;
    jgrapht_capi_set_linked_create(thread, &set);
    jgrapht_capi_set_int_add(thread, set, 0, NULL);
    jgrapht_capi_set_int_add(thread, set, 1, NULL);
    jgrapht_capi_set_int_add(thread, set, 2, NULL);
    jgrapht_capi_xx_drawing_layout_model_2d_create(thread, 0, 0, 10.0, 10.0, &model);

    jgrapht_capi_ix_drawing_exec_barycenter_greedy_two_layered_bipartite_layout_2d(thread, g, model, set, 0, 1);
 
    for(int i = 0; i < 7; i++) { 
        jgrapht_capi_ix_drawing_layout_model_2d_get_vertex(thread, model, i, &vx, &vy);
        assert(vx >= 0.0);
        assert(vx <= 10.0);
        assert(vy >= 0.0);
        assert(vy <= 10.0);
        //printf("(%lf,%lf)\n", vx, vy);
    }

    jgrapht_capi_handles_destroy(thread, set);
    jgrapht_capi_handles_destroy(thread, model);


    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
