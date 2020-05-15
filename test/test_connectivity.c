#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

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
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 0, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 0, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 4, 5, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 5, 3, NULL);

    int is_connected;
    void *setsit;
    jgrapht_capi_connectivity_weak_exec_bfs(thread, g, &is_connected, &setsit);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    assert(is_connected == 1);

    void *set;
    jgrapht_capi_it_next_object(thread, setsit, &set);

    int size = 0;
    jgrapht_capi_set_size(thread, set, &size);
    assert(size == 6);
    jgrapht_capi_handles_destroy(thread, set);
    jgrapht_capi_handles_destroy(thread, setsit);


    // gabow
    jgrapht_capi_connectivity_strong_exec_gabow(thread, g, &is_connected, NULL);
    assert(!is_connected);

    // kosaraju
    jgrapht_capi_connectivity_strong_exec_kosaraju(thread, g, &is_connected, NULL);
    assert(!is_connected);

    // make strongly connected
    jgrapht_capi_graph_add_edge(thread, g, 0, 3, NULL);

    // gabow
    jgrapht_capi_connectivity_strong_exec_gabow(thread, g, &is_connected, NULL);
    assert(is_connected);

    // kosaraju
    jgrapht_capi_connectivity_strong_exec_kosaraju(thread, g, &is_connected, NULL);
    assert(is_connected);

    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
