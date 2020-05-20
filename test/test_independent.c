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

    
    // check chordal graph
    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 1, &g);

    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 4, 5, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 5, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);

    void *ind;
    jgrapht_capi_independent_set_exec_chordal_max_independent_set(thread, g, &ind);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    void *vit;
    jgrapht_capi_set_it_create(thread, ind, &vit);
    int elem = 0;
    int hasnext;
    int v;
    while(1) { 
        jgrapht_capi_it_hasnext(thread, vit, &hasnext);
        if (!hasnext) 
            break;
        elem++;
        jgrapht_capi_it_next_int(thread, vit, &v);
    }
    jgrapht_capi_handles_destroy(thread, vit);
    jgrapht_capi_handles_destroy(thread, ind);
    assert (elem == 2);

    jgrapht_capi_handles_destroy(thread, g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
