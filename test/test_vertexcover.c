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
    jgrapht_capi_graph_create(thread, 0, 0, 0, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int flag;
    assert(jgrapht_capi_graph_is_directed(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_undirected(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_weighted(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_allowing_selfloops(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_allowing_multipleedges(thread, g, &flag) == 0);
    assert(flag == 0);

    int i;
    for(i = 0; i < 7; i++) { 
        jgrapht_capi_graph_add_vertex(thread, g, NULL);
    }

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 5, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 6, NULL);

    // test the greedy for the API and the results

    void *vc;
    double vc_w;
    jgrapht_capi_vertexcover_exec_greedy(thread, g, &vc_w, &vc);
    assert(vc_w == 1.0);

    int size;
    jgrapht_capi_set_size(thread, vc, &size);
    assert(size == 1);
    int contains;
    jgrapht_capi_set_int_contains(thread, vc, 0, &contains);
    assert(contains == 1);

    jgrapht_capi_handles_destroy(thread, vc);

    void * map;
    jgrapht_capi_map_linked_create(thread, &map);
    jgrapht_capi_map_int_double_put(thread, map, 0, 1000.0);
    jgrapht_capi_map_int_double_put(thread, map, 1, 2.0);
    jgrapht_capi_map_int_double_put(thread, map, 2, 2.0);
    jgrapht_capi_map_int_double_put(thread, map, 3, 2.0);
    jgrapht_capi_map_int_double_put(thread, map, 4, 2.0);
    jgrapht_capi_map_int_double_put(thread, map, 5, 2.0);
    jgrapht_capi_map_int_double_put(thread, map, 6, 2.0);

    jgrapht_capi_vertexcover_exec_greedy_weighted(thread, g, map, &vc_w, &vc);
    assert(vc_w == 12.0);
    void *vit;
    jgrapht_capi_set_it_create(thread, vc, &vit);

    int v;
    assert(jgrapht_capi_it_next_int(thread, vit, &v) == 0);
    assert(v == 1);
    assert(jgrapht_capi_it_next_int(thread, vit, &v) == 0);
    assert(v == 2);
    assert(jgrapht_capi_it_next_int(thread, vit, &v) == 0);
    assert(v == 3);
    assert(jgrapht_capi_it_next_int(thread, vit, &v) == 0);
    assert(v == 4);
    assert(jgrapht_capi_it_next_int(thread, vit, &v) == 0);
    assert(v == 5);
    assert(jgrapht_capi_it_next_int(thread, vit, &v) == 0);
    assert(v == 6);
    int hasnext;
    assert(jgrapht_capi_it_hasnext(thread, vit, &hasnext) == 0);
    assert(hasnext == 0);

    jgrapht_capi_handles_destroy(thread, vit);
    jgrapht_capi_handles_destroy(thread, vc);

    // test the remaining algs only for the API

    jgrapht_capi_vertexcover_exec_clarkson(thread, g, &vc_w, &vc);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, vc);

    jgrapht_capi_vertexcover_exec_clarkson_weighted(thread, g, map, &vc_w, &vc);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, vc);

    jgrapht_capi_vertexcover_exec_edgebased(thread, g, &vc_w, &vc);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, vc);

    jgrapht_capi_vertexcover_exec_baryehudaeven(thread, g, &vc_w, &vc);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, vc);

    jgrapht_capi_vertexcover_exec_baryehudaeven_weighted(thread, g, map, &vc_w, &vc);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, vc);

    jgrapht_capi_vertexcover_exec_exact(thread, g, &vc_w, &vc);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, vc);

    jgrapht_capi_vertexcover_exec_exact_weighted(thread, g, map, &vc_w, &vc);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, vc);

    // cleanup
    jgrapht_capi_handles_destroy(thread, map);
    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
