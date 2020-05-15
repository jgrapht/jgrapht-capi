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
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 4, 0, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 0, NULL);

    void *embedding, *kuratowski_subdivision;
    int is_planar = 0;

    jgrapht_capi_planarity_exec_boyer_myrvold(thread, g, &is_planar, &embedding, &kuratowski_subdivision);
    assert (is_planar);

    void *it;
    jgrapht_capi_planarity_embedding_edges_around_vertex(thread, embedding, 0, &it);

    int e;
    jgrapht_capi_it_next_int(thread, it, &e);
    assert (e == 5);
    jgrapht_capi_it_next_int(thread, it, &e);
    assert (e == 0);
    jgrapht_capi_it_next_int(thread, it, &e);
    assert (e == 4);
    int has_next = 1;
    jgrapht_capi_it_hasnext(thread, it, &has_next);
    assert (has_next == 0);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_handles_destroy(thread, it);
    jgrapht_capi_handles_destroy(thread, embedding);
    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
