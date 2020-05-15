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

    int isbipartite;
    void * part1, * part2;
    jgrapht_capi_partition_exec_bipartite(thread, g, &isbipartite, &part1, &part2);
    assert(isbipartite == 1);

    int contains;
    jgrapht_capi_set_int_contains(thread, part1, 0, &contains);
    assert(contains == 1);
    jgrapht_capi_set_int_contains(thread, part1, 1, &contains);
    assert(contains == 0);
    jgrapht_capi_set_int_contains(thread, part1, 2, &contains);
    assert(contains == 1);
    jgrapht_capi_set_int_contains(thread, part1, 3, &contains);
    assert(contains == 0);

    jgrapht_capi_set_int_contains(thread, part2, 0, &contains);
    assert(contains == 0);
    jgrapht_capi_set_int_contains(thread, part2, 1, &contains);
    assert(contains == 1);
    jgrapht_capi_set_int_contains(thread, part2, 2, &contains);
    assert(contains == 0);
    jgrapht_capi_set_int_contains(thread, part2, 3, &contains);
    assert(contains == 1);


    jgrapht_capi_handles_destroy(thread, part1);
    jgrapht_capi_handles_destroy(thread, part2);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}