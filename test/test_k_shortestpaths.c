#include <stdio.h>
#include <stdlib.h>
#include <math.h>
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
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 0, 5.0);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 1, 2.0);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 2, 100.0);
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 3, 2.0);
    jgrapht_capi_graph_add_edge(thread, g, 0, 5, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 4, 20.0);
    jgrapht_capi_graph_add_edge(thread, g, 5, 3, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 5, 20.0);
    
    void *gp;
    double weight; 
    int start_vertex, end_vertex;

    // test yen
    void *gpit;
    jgrapht_capi_sp_exec_yen_get_k_loopless_paths_between_vertices(thread, g, 0, 4, 2, &gpit);
    assert(jgrapht_capi_error_get_errno(thread) == 0);    

    int hasnext = 0;
    jgrapht_capi_it_hasnext(thread, gpit, &hasnext);
    assert(hasnext==1);
    jgrapht_capi_it_next_object(thread, gpit, &gp);
    assert(jgrapht_capi_error_get_errno(thread) == 0);    

    jgrapht_capi_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 42.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_handles_destroy(thread, gp);

    // 2nd path
    jgrapht_capi_it_hasnext(thread, gpit, &hasnext);
    assert(hasnext==1);
    jgrapht_capi_it_next_object(thread, gpit, &gp);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 109.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_handles_destroy(thread, gp);    

    jgrapht_capi_handles_destroy(thread, gpit);


    // test eppstein
    jgrapht_capi_sp_exec_eppstein_get_k_paths_between_vertices(thread, g, 0, 4, 2, &gpit);
    assert(jgrapht_capi_error_get_errno(thread) == 0);    

    jgrapht_capi_it_hasnext(thread, gpit, &hasnext);
    assert(hasnext==1);
    jgrapht_capi_it_next_object(thread, gpit, &gp);
    assert(jgrapht_capi_error_get_errno(thread) == 0);    

    jgrapht_capi_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 42.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_handles_destroy(thread, gp);

    // 2nd path
    jgrapht_capi_it_hasnext(thread, gpit, &hasnext);
    assert(hasnext==1);
    jgrapht_capi_it_next_object(thread, gpit, &gp);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 109.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_handles_destroy(thread, gp);    

    jgrapht_capi_handles_destroy(thread, gpit);

    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
