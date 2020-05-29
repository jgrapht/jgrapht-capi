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

    double x, y, w, h;
    jgrapht_capi_drawing_layout_model_2d_get_drawable_area(thread, model, &x, &y, &w, &h);
    assert(x == 0);
    assert(y == 0);
    assert(w == 10.0);
    assert(h == 10.0);

    jgrapht_capi_drawing_exec_random_layout_2d(thread, g, model, 17);

    double vx, vy;
    for(int i = 0; i < 7; i++) { 
        jgrapht_capi_drawing_layout_model_2d_get_vertex(thread, model, i, &vx, &vy);
        assert(vx >= 0.0);
        assert(vx <= 10.0);
        assert(vy >= 0.0);
        assert(vy <= 10.0);
        // printf("(%lf,%lf)\n", vx, vy);
    }

    jgrapht_capi_drawing_layout_model_2d_put_vertex(thread, model, 3, 2.5, 3.5);
    jgrapht_capi_drawing_layout_model_2d_get_vertex(thread, model, 3, &vx, &vy);
    assert(vx == 2.5);
    assert(vy == 3.5);

    int fixed;
    jgrapht_capi_drawing_layout_model_2d_get_fixed(thread, model, 2, &fixed);
    assert(fixed==0);
    jgrapht_capi_drawing_layout_model_2d_set_fixed(thread, model, 2, 1);
    jgrapht_capi_drawing_layout_model_2d_get_fixed(thread, model, 2, &fixed);
    assert(fixed==1);
    jgrapht_capi_drawing_layout_model_2d_set_fixed(thread, model, 2, 0);
    jgrapht_capi_drawing_layout_model_2d_get_fixed(thread, model, 2, &fixed);
    assert(fixed==0);

    jgrapht_capi_handles_destroy(thread, model);
    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
