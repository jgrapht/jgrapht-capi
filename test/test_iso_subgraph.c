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

    void *g1;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 1, &g1);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_add_vertex(thread, g1, NULL);
    jgrapht_capi_graph_add_vertex(thread, g1, NULL);
    jgrapht_capi_graph_add_vertex(thread, g1, NULL);
    jgrapht_capi_graph_add_vertex(thread, g1, NULL);

    jgrapht_capi_graph_add_edge(thread, g1, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g1, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g1, 2, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g1, 3, 0, NULL);

    void *g2;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 1, &g2);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_add_vertex(thread, g2, NULL);
    jgrapht_capi_graph_add_vertex(thread, g2, NULL);
    jgrapht_capi_graph_add_vertex(thread, g2, NULL);

    jgrapht_capi_graph_add_edge(thread, g2, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g2, 1, 2, NULL);

    int are_iso = 0;
    void *map_it;
    jgrapht_capi_isomorphism_exec_vf2_subgraph(thread, g1, g2, &are_iso, &map_it);
    jgrapht_capi_error_print_stack_trace(thread);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    assert(are_iso);

    void *map;
    jgrapht_capi_it_next_object(thread, map_it, &map);

    int has_other_vertex;
    int other_vertex;
    jgrapht_capi_isomorphism_graph_mapping_vertex_correspondence(thread, map, 0, 1, &has_other_vertex, &other_vertex);
    assert(has_other_vertex && other_vertex == 0);
    jgrapht_capi_isomorphism_graph_mapping_vertex_correspondence(thread, map, 1, 1, &has_other_vertex, &other_vertex);
    assert(has_other_vertex && other_vertex == 1);
    jgrapht_capi_isomorphism_graph_mapping_vertex_correspondence(thread, map, 2, 1, &has_other_vertex, &other_vertex);
    assert(has_other_vertex && other_vertex == 2);
    jgrapht_capi_isomorphism_graph_mapping_vertex_correspondence(thread, map, 3, 1, &has_other_vertex, &other_vertex);
    assert(!has_other_vertex);
    jgrapht_capi_handles_destroy(thread, map);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // next mapping
    jgrapht_capi_it_next_object(thread, map_it, &map);
    jgrapht_capi_isomorphism_graph_mapping_vertex_correspondence(thread, map, 0, 1, &has_other_vertex, &other_vertex);
    assert(has_other_vertex && other_vertex == 0);
    jgrapht_capi_isomorphism_graph_mapping_vertex_correspondence(thread, map, 1, 1, &has_other_vertex, &other_vertex);
    assert(!has_other_vertex);
    jgrapht_capi_isomorphism_graph_mapping_vertex_correspondence(thread, map, 2, 1, &has_other_vertex, &other_vertex);
    assert(has_other_vertex && other_vertex == 2);
    jgrapht_capi_isomorphism_graph_mapping_vertex_correspondence(thread, map, 3, 1, &has_other_vertex, &other_vertex);
    assert(has_other_vertex && other_vertex == 1);
    jgrapht_capi_handles_destroy(thread, map);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int has_next = 0;
    jgrapht_capi_it_hasnext(thread, map_it, &has_next);
    assert(has_next == 1);

    jgrapht_capi_handles_destroy(thread, map_it);
    jgrapht_capi_handles_destroy(thread, g1);
    jgrapht_capi_handles_destroy(thread, g2);

    assert(jgrapht_capi_error_get_errno(thread) == 0);
    
    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
