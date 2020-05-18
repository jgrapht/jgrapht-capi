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

    // barabasi albert
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_barabasi_albert(thread, g, 10, 5, 100, 17);
    int vcount;
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 100);
    jgrapht_capi_handles_destroy(thread, g);

    // barabasi albert forest
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_barabasi_albert_forest(thread, g, 5, 100, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 100);
    jgrapht_capi_handles_destroy(thread, g);

    // complete
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_complete(thread, g, 10);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_handles_destroy(thread, g);

    // bipartite complete
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_bipartite_complete(thread, g, 10, 10);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 20);
    jgrapht_capi_handles_destroy(thread, g);

    // empty
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_empty(thread, g, 10);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_handles_destroy(thread, g);

    // gnm random 
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_gnm_random(thread, g, 10, 30, 0, 0, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_handles_destroy(thread, g);

    // gnp random
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_gnp_random(thread, g, 10, 0.1, 0, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_handles_destroy(thread, g);

    // ring
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_ring(thread, g, 10);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_handles_destroy(thread, g);

    // scalefree
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_scalefree(thread, g, 10, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_handles_destroy(thread, g);

    // watts strogatz
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_watts_strogatz(thread, g, 10, 2, 0.1, 0, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_handles_destroy(thread, g);

    // kleinberg
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_kleinberg_smallworld(thread, g, 10, 2, 1, 2, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 100);
    jgrapht_capi_handles_destroy(thread, g);

    // TODO: check complement

    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    int v;
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    jgrapht_capi_graph_add_edge(thread, g, 0, 1, &v);
    void *gtarget;
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &gtarget);
    jgrapht_capi_generate_complement(thread, gtarget, g, 0);
    jgrapht_capi_graph_vertices_count(thread, gtarget, &vcount);
    assert(vcount == 3);
    jgrapht_capi_handles_destroy(thread, g);
    jgrapht_capi_handles_destroy(thread, gtarget);

    // generalized petersen
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_generalized_petersen(thread, g, 10, 4);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 20);
    jgrapht_capi_handles_destroy(thread, g);

    // grid
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_grid(thread, g, 5, 5);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 25);
    jgrapht_capi_handles_destroy(thread, g);
    
    // hypercube
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_hypercube(thread, g, 3);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 8);
    jgrapht_capi_handles_destroy(thread, g);

    // linear
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_linear(thread, g, 10);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_handles_destroy(thread, g);

    // random regular
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_random_regular(thread, g, 10, 3, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_handles_destroy(thread, g);

    // star
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_star(thread, g, 10);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_handles_destroy(thread, g);

    // wheel
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_wheel(thread, g, 10, 0);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 10);
    jgrapht_capi_handles_destroy(thread, g);

    // windmill
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_windmill(thread, g, 3, 3, 0);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 7);
    jgrapht_capi_handles_destroy(thread, g);

    // linearized_chord_diagram
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    jgrapht_capi_generate_linearized_chord_diagram(thread, g, 20, 5, 17);
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 20);
    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
