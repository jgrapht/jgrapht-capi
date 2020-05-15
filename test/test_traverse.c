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

    void *g;
    jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int v;
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 0);
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 1);
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 2);
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 3);
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 4);
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 5);

    int e;
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 0, &e) == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 1, &e) == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 2, &e) == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2, &e) == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2, &e) == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2, &e) == 0);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, &e);
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, &e);
    jgrapht_capi_graph_add_edge(thread, g, 4, 5, &e);
    jgrapht_capi_graph_add_edge(thread, g, 2, 5, &e);
    jgrapht_capi_graph_set_edge_weight(thread, g, e, 100.0);

    // bfs from all
    void *it;
    jgrapht_capi_traverse_create_bfs_from_all_vertices_vit(thread, g, &it);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 0);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 1);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 2);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 3);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 5);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 4);
    jgrapht_capi_it_next_int(thread, it, &v);
    int hasnext;
    jgrapht_capi_it_hasnext(thread, it, &hasnext);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, it);

    // bfs from single
    jgrapht_capi_traverse_create_bfs_from_vertex_vit(thread, g, 2, &it);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 2);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 0);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 1);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 3);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 5);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 4);
    jgrapht_capi_it_next_int(thread, it, &v);
    jgrapht_capi_it_hasnext(thread, it, &hasnext);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, it);

    // lex bfs
    jgrapht_capi_traverse_create_lex_bfs_vit(thread, g, &it);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 0);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 1);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 2);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 3);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 5);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 4);
    jgrapht_capi_it_next_int(thread, it, &v);
    jgrapht_capi_it_hasnext(thread, it, &hasnext);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, it);


    // dfs all
    jgrapht_capi_traverse_create_dfs_from_all_vertices_vit(thread, g, &it);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 0);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 2);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 5);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 4);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 3);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 1);
    jgrapht_capi_it_next_int(thread, it, &v);
    jgrapht_capi_it_hasnext(thread, it, &hasnext);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, it);

    // dfs single vertex
    jgrapht_capi_traverse_create_dfs_from_vertex_vit(thread, g, 2, &it);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 2);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 5);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 4);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 3);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 1);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 0);
    jgrapht_capi_it_next_int(thread, it, &v);
    jgrapht_capi_it_hasnext(thread, it, &hasnext);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, it);

    // random walk from vertex
    jgrapht_capi_traverse_create_custom_random_walk_from_vertex_vit(thread, g, 2, 0, 4, 17, &it);
    jgrapht_capi_it_next_int(thread, it, &v);
    jgrapht_capi_it_next_int(thread, it, &v);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(jgrapht_capi_it_next_int(thread, it, &v) == 0);    
    jgrapht_capi_it_hasnext(thread, it, &hasnext);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, it);

    // max card
    jgrapht_capi_traverse_create_max_cardinality_vit(thread, g, &it);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 0);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 1);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 2);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 3);
    assert(jgrapht_capi_it_next_int(thread, it, &v) == 0);
    assert(v == 4);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 5);
    jgrapht_capi_it_next_int(thread, it, &v);
    jgrapht_capi_it_hasnext(thread, it, &hasnext);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, it);

    // degeneracy
    jgrapht_capi_traverse_create_degeneracy_ordering_vit(thread, g, &it);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 0);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 3);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 4);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 5);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 1);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 2);
    jgrapht_capi_it_next_int(thread, it, &v);
    jgrapht_capi_it_hasnext(thread, it, &hasnext);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, it);

    // closest first
    jgrapht_capi_traverse_create_closest_first_from_vertex_vit(thread, g, 0, &it);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 0);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 1);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 2);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 3);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 4);
    jgrapht_capi_it_next_int(thread, it, &v);
    assert(v == 5);
    jgrapht_capi_it_next_int(thread, it, &v);
    jgrapht_capi_it_hasnext(thread, it, &hasnext);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, it);


    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}