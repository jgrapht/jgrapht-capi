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
    jgrapht_capi_graph_create(thread, 0, 0, 0, 1, &g);

    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 3, NULL);


    void *map;
    int value;
    jgrapht_capi_scoring_exec_alpha_centrality(thread, g, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    jgrapht_capi_scoring_exec_custom_alpha_centrality(thread, g, 0.5, 0.3, 3, 0.0000001, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    jgrapht_capi_scoring_exec_betweenness_centrality(thread, g, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    jgrapht_capi_scoring_exec_custom_betweenness_centrality(thread, g, 1, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    jgrapht_capi_scoring_exec_closeness_centrality(thread, g, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    jgrapht_capi_scoring_exec_custom_closeness_centrality(thread, g, 0, 1, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    jgrapht_capi_scoring_exec_harmonic_centrality(thread, g, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    jgrapht_capi_scoring_exec_custom_harmonic_centrality(thread, g, 0, 1, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    jgrapht_capi_scoring_exec_pagerank(thread, g, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    jgrapht_capi_scoring_exec_custom_pagerank(thread, g, 0.85, 20, 0.00001, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    int degeneracy;
    jgrapht_capi_scoring_exec_coreness(thread, g, &degeneracy, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    double global, avg;
    jgrapht_capi_scoring_exec_clustering_coefficient(thread, g, &global, &avg, &map);
    jgrapht_capi_map_int_contains_key(thread, map, 0, &value);
    assert(value == 1);
    jgrapht_capi_handles_destroy(thread, map);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
