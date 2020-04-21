#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi.h>

#define ITERATOR_NO_SUCH_ELEMENT 100

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_get_errno(thread) == 0);

    void *g = jgrapht_capi_graph_create(thread, 0, 0, 0, 1);
    assert(jgrapht_capi_get_errno(thread) == 0);

    assert(!jgrapht_capi_graph_is_directed(thread, g));
    assert(jgrapht_capi_graph_is_undirected(thread, g));
    assert(jgrapht_capi_graph_is_weighted(thread, g));
    assert(!jgrapht_capi_graph_is_allowing_selfloops(thread, g));
    assert(!jgrapht_capi_graph_is_allowing_multipleedges(thread, g));

    assert(jgrapht_capi_graph_add_vertex(thread, g) == 0);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 1);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 2);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 3);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 4);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 5);
    assert(jgrapht_capi_graph_add_vertex(thread, g) == 6);

    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 1) == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 2) == 1);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 3) == 2);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 4) == 3);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 5) == 4);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 6) == 5);

    // test the greedy for the API and the results

    void *vc = jgrapht_capi_vertexcover_exec_greedy(thread, g);
    double vc_w = jgrapht_capi_vertexcover_get_weight(thread, vc);
    assert(vc_w == 1.0);
    void *vit = jgrapht_capi_vertexcover_create_vit(thread, vc);
    assert(jgrapht_capi_it_next_long(thread, vit) == 0);
    assert(!jgrapht_capi_it_hasnext(thread, vit));
    jgrapht_capi_destroy(thread, vit);
    jgrapht_capi_destroy(thread, vc);

    void * map;
    jgrapht_capi_map_linked_create(thread, &map);
    jgrapht_capi_map_long_double_put(thread, map, 0, 1000.0);
    jgrapht_capi_map_long_double_put(thread, map, 1, 2.0);
    jgrapht_capi_map_long_double_put(thread, map, 2, 2.0);
    jgrapht_capi_map_long_double_put(thread, map, 3, 2.0);
    jgrapht_capi_map_long_double_put(thread, map, 4, 2.0);
    jgrapht_capi_map_long_double_put(thread, map, 5, 2.0);
    jgrapht_capi_map_long_double_put(thread, map, 6, 2.0);

    vc = jgrapht_capi_vertexcover_exec_greedy_weighted(thread, g, map);
    vc_w = jgrapht_capi_vertexcover_get_weight(thread, vc);
    assert(vc_w == 12.0);
    vit = jgrapht_capi_vertexcover_create_vit(thread, vc);
    assert(jgrapht_capi_it_next_long(thread, vit) == 1);
    assert(jgrapht_capi_it_next_long(thread, vit) == 2);
    assert(jgrapht_capi_it_next_long(thread, vit) == 3);
    assert(jgrapht_capi_it_next_long(thread, vit) == 4);
    assert(jgrapht_capi_it_next_long(thread, vit) == 5);
    assert(jgrapht_capi_it_next_long(thread, vit) == 6);
    assert(!jgrapht_capi_it_hasnext(thread, vit));
    jgrapht_capi_destroy(thread, vit);
    jgrapht_capi_destroy(thread, vc);

    // test the remaining algs only for the API

    vc = jgrapht_capi_vertexcover_exec_clarkson(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);
    jgrapht_capi_destroy(thread, vc);

    vc = jgrapht_capi_vertexcover_exec_clarkson_weighted(thread, g, map);
    assert(jgrapht_capi_get_errno(thread) == 0);
    jgrapht_capi_destroy(thread, vc);

    vc = jgrapht_capi_vertexcover_exec_edgebased(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);
    jgrapht_capi_destroy(thread, vc);

    vc = jgrapht_capi_vertexcover_exec_baryehudaeven(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);
    jgrapht_capi_destroy(thread, vc);

    vc = jgrapht_capi_vertexcover_exec_baryehudaeven_weighted(thread, g, map);
    assert(jgrapht_capi_get_errno(thread) == 0);
    jgrapht_capi_destroy(thread, vc);

    vc = jgrapht_capi_vertexcover_exec_exact(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);
    jgrapht_capi_destroy(thread, vc);

    vc = jgrapht_capi_vertexcover_exec_exact_weighted(thread, g, map);
    assert(jgrapht_capi_get_errno(thread) == 0);
    jgrapht_capi_destroy(thread, vc);

    // cleanup
    jgrapht_capi_destroy(thread, map);
    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}