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

    int i;
    for(i = 0; i < 7; i++) { 
        jgrapht_capi_graph_add_vertex(thread, g, NULL);
    }

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 5, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 6, NULL);

    // create model
    void *model;
    jgrapht_capi_drawing_layout_model_2d_create(thread, 0, 0, 10.0, 10.0, &model);

    jgrapht_capi_drawing_exec_fr_layout_2d(thread, g, model, 50, 1.0, 17);

    double vx, vy;
    for(int i = 0; i < 7; i++) { 
        jgrapht_capi_drawing_layout_model_2d_get_vertex(thread, model, i, &vx, &vy);
        assert(vx >= 0.0);
        assert(vx <= 10.0);
        assert(vy >= 0.0);
        assert(vy <= 10.0);
        //printf("(%lf,%lf)\n", vx, vy);
    }

    jgrapht_capi_handles_destroy(thread, model);


    // now with index
    jgrapht_capi_drawing_layout_model_2d_create(thread, 0, 0, 10.0, 10.0, &model);
    jgrapht_capi_drawing_exec_indexed_fr_layout_2d(thread, g, model, 50, 1.0, 17, 0.5, 0.000000001);

    for(int i = 0; i < 7; i++) { 
        jgrapht_capi_drawing_layout_model_2d_get_vertex(thread, model, i, &vx, &vy);
        assert(vx >= 0.0);
        assert(vx <= 10.0);
        assert(vy >= 0.0);
        assert(vy <= 10.0);
        //printf("(%lf,%lf)\n", vx, vy);
    }

    jgrapht_capi_handles_destroy(thread, model);


    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
