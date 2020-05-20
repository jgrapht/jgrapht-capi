#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

double costs [] = {
    1.0, 5.0,
    4.0, 2.0,
    4.0, 4.0,
    1.0, 2.0,
    2.0, 5.0,
    2.0, 3.0,
    6.0, 1.0,
    3.0, 3.0
};

double* cost_function(int edge) { 
    return costs + 2 * edge;
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
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 4, NULL);    
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, NULL);    
    
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *paths_it;
    double weight; 
    int start_vertex, end_vertex;
    jgrapht_capi_multisp_exec_martin_get_paths_between_vertices(thread, g, 0, 4, cost_function, 2, &paths_it);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int paths = 0;
    void *gp;
    int hasnext; 
    while(1) { 
        jgrapht_capi_it_hasnext(thread, paths_it, &hasnext);
        if (!hasnext) { 
            break;
        }
        paths++;
        jgrapht_capi_it_next_object(thread, paths_it, &gp);
        jgrapht_capi_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
        jgrapht_capi_handles_destroy(thread, gp);
    }
    assert (paths == 3);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, paths_it);


    // one more 
    jgrapht_capi_multisp_exec_martin_get_paths_between_vertices(thread, g, 0, 3, cost_function, 2, &paths_it);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    paths = 0;
    while(1) { 
        jgrapht_capi_it_hasnext(thread, paths_it, &hasnext);
        if (!hasnext) { 
            break;
        }
        paths++;
        jgrapht_capi_it_next_object(thread, paths_it, &gp);
        jgrapht_capi_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
        jgrapht_capi_handles_destroy(thread, gp);
    }
    assert (paths == 2);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, paths_it);


    // single source
    void *multi;
    jgrapht_capi_multisp_exec_martin_get_multiobjectivesinglesource_from_vertex(thread, g, 0, cost_function, 2, &multi);

    jgrapht_capi_multisp_multiobjectivesinglesource_get_paths_to_vertex(thread, multi, 4, &paths_it);
    paths = 0;
    while(1) { 
        jgrapht_capi_it_hasnext(thread, paths_it, &hasnext);
        if (!hasnext) { 
            break;
        }
        paths++;
        jgrapht_capi_it_next_object(thread, paths_it, &gp);
        jgrapht_capi_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
        jgrapht_capi_handles_destroy(thread, gp);
    }
    assert (paths == 3);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, paths_it);
    jgrapht_capi_handles_destroy(thread, multi);
    assert(jgrapht_capi_error_get_errno(thread) == 0);


    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
