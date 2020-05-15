#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

#define NUM_VERTICES 1000

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 1, 1, 1, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    int vcount;
    assert(jgrapht_capi_graph_vertices_count(thread,  g, &vcount) == 0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    int ecount;
    assert(jgrapht_capi_graph_edges_count(thread,  g, &ecount) == 0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int v;
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    assert(v == 0);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    assert(v == 1);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    assert(v == 2);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    assert(v == 3);

    int added = 0;
    jgrapht_capi_graph_add_given_vertex(thread, g, 100, &added);
    assert (added == 1);

    jgrapht_capi_graph_add_given_vertex(thread, g, 2, &added);
    assert (added == 0);

    jgrapht_capi_graph_vertices_count(thread,  g, &vcount);
    assert(vcount == 5);
    assert(jgrapht_capi_error_get_errno(thread) == 0);


    // test safe supplier
    jgrapht_capi_graph_add_given_vertex(thread, g, 4, &added);
    assert (added==1);
    jgrapht_capi_graph_add_given_vertex(thread, g, 5, &added);
    assert (added==1);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    assert (v == 6);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
