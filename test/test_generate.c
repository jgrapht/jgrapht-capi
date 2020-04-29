#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

#define ITERATOR_NO_SUCH_ELEMENT 100

int main() { 
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    void *g;

    // barabasi albert
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_barabasi_albert(thread, g, 10, 5, 100, 17);
    long long vcount;
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 100);
    jgrapht_capi_destroy(thread, g);

    // barabasi albert forest
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_barabasi_albert_forest(thread, g, 5, 100, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 100);
    jgrapht_capi_destroy(thread, g);

    // complete
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_complete(thread, g, 10);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_destroy(thread, g);

    // bipartite complete
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_bipartite_complete(thread, g, 10, 10);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 20);
    jgrapht_capi_destroy(thread, g);

    // empty
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_empty(thread, g, 10);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_destroy(thread, g);

    // gnm random 
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_gnm_random(thread, g, 10, 30, 0, 0, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_destroy(thread, g);

    // gnp random
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_gnp_random(thread, g, 10, 0.1, 0, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_destroy(thread, g);

    // ring
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_ring(thread, g, 10);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_destroy(thread, g);

    // scalefree
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_scalefree(thread, g, 10, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_destroy(thread, g);

    // watts strogatz
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_watts_strogatz(thread, g, 10, 2, 0.1, 0, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_destroy(thread, g);

    // kleinberg
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_kleinberg_smallworld(thread, g, 10, 2, 1, 0.2, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 100);
    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
