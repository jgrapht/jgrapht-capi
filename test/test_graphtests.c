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

    void *g;
    assert(jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g) == 0);

    int v;
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    jgrapht_capi_graph_add_vertex(thread, g, &v);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, NULL);

    int flag;
    jgrapht_capi_graph_test_is_empty(thread, g, &flag);
    assert(flag == 0);
    jgrapht_capi_graph_test_is_simple(thread, g, &flag);
    assert(flag == 1);
    jgrapht_capi_graph_test_has_selfloops(thread, g, &flag);
    assert(flag == 0);
    jgrapht_capi_graph_test_has_multipleedges(thread, g, &flag);
    assert(flag == 0);
    jgrapht_capi_graph_test_is_complete(thread, g, &flag);
    assert(flag == 0);
    jgrapht_capi_graph_test_is_weakly_connected(thread, g, &flag);
    assert(flag == 1);
    jgrapht_capi_graph_test_is_strongly_connected(thread, g, &flag);
    assert(flag == 1);
    jgrapht_capi_graph_test_is_tree(thread, g, &flag);
    assert(flag == 1);
    jgrapht_capi_graph_test_is_forest(thread, g, &flag);
    assert(flag == 1);
    jgrapht_capi_graph_test_is_overfull(thread, g, &flag);
    assert(flag == 0);
    jgrapht_capi_graph_test_is_split(thread, g, &flag);
    assert(flag == 0);
    jgrapht_capi_graph_test_is_bipartite(thread, g, &flag);
    assert(flag == 1);
    jgrapht_capi_graph_test_is_cubic(thread, g, &flag);
    assert(flag == 0);
    jgrapht_capi_graph_test_is_eulerian(thread, g, &flag);
    assert(flag == 0);
    jgrapht_capi_graph_test_is_chordal(thread, g, &flag);
    assert(flag == 1);
    jgrapht_capi_graph_test_is_weakly_chordal(thread, g, &flag);
    assert(flag == 1);
    jgrapht_capi_graph_test_has_ore(thread, g, &flag);
    assert(flag == 0);
    jgrapht_capi_graph_test_is_trianglefree(thread, g, &flag);
    assert(flag == 1);
    jgrapht_capi_graph_test_is_perfect(thread, g, &flag);
    assert(flag == 1);
    jgrapht_capi_graph_test_is_planar(thread, g, &flag);
    assert(flag == 1);
    jgrapht_capi_graph_test_is_kuratowski_subdivision(thread, g, &flag);
    assert(flag == 0);
    jgrapht_capi_graph_test_is_k33_subdivision(thread, g, &flag);
    assert(flag == 0);
    jgrapht_capi_graph_test_is_k5_subdivision(thread, g, &flag);
    assert(flag == 0);

    assert(jgrapht_capi_handles_destroy(thread, g) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}