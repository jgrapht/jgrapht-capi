#include <stdio.h>
#include <stdlib.h>
#include <math.h>
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
    jgrapht_capi_graph_create(thread, 1, 0, 0, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 0, 5.0);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 1, 2.0);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 2, 100.0);
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 3, 2.0);
    jgrapht_capi_graph_add_edge(thread, g, 0, 5, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 4, 20.0);
    jgrapht_capi_graph_add_edge(thread, g, 5, 3, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 5, 20.0);
    jgrapht_capi_graph_add_edge(thread, g, 6, 7, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 6, 50.0);
    jgrapht_capi_graph_add_edge(thread, g, 3, 6, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 7, 40.0);
    jgrapht_capi_graph_add_edge(thread, g, 0, 7, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 8, 70.0);
    jgrapht_capi_graph_add_edge(thread, g, 5, 7, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 9, 10.0);
    jgrapht_capi_graph_add_edge(thread, g, 5, 6, NULL);
    jgrapht_capi_graph_set_edge_weight(thread, g, 10, 100.0);

    
    // create CH
    void *ch;
    jgrapht_capi_sp_exec_contraction_hierarchy(thread, g, 1, 17, &ch);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test bidirectional dijkstra
    void *gp;
    jgrapht_capi_sp_exec_contraction_hierarchy_bidirectional_dijkstra_get_path_between_vertices(thread, ch, 0, 6, 500000, &gp); 
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    double weight; 
    int start_vertex;
    int end_vertex;
    jgrapht_capi_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 80.0);
    assert(start_vertex == 0);
    assert(end_vertex == 6);
    jgrapht_capi_handles_destroy(thread, gp);

    // test many to many
    void *sources, *targets, *mm;
    jgrapht_capi_set_linked_create(thread, &sources);
    jgrapht_capi_set_linked_create(thread, &targets);
    jgrapht_capi_set_int_add(thread, sources, 0, NULL);
    jgrapht_capi_set_int_add(thread, sources, 1, NULL);
    jgrapht_capi_set_int_add(thread, sources, 2, NULL);
    jgrapht_capi_set_int_add(thread, targets, 6, NULL);
    jgrapht_capi_set_int_add(thread, targets, 7, NULL);
    jgrapht_capi_set_int_add(thread, targets, 5, NULL);
    jgrapht_capi_sp_exec_contraction_hierarchy_get_manytomany(thread, ch, sources, targets, &mm);
    jgrapht_capi_handles_destroy(thread, sources);
    jgrapht_capi_handles_destroy(thread, targets);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // query with many to many
    jgrapht_capi_sp_manytomany_get_path_between_vertices(thread, mm, 0, 6, &gp);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 80.0);
    assert(start_vertex == 0);
    assert(end_vertex == 6);
    jgrapht_capi_handles_destroy(thread, gp);

    jgrapht_capi_sp_manytomany_get_path_between_vertices(thread, mm, 1, 7, &gp);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_get_graphpath(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    printf("%lf\n", weight);
    assert(weight == 192.0);
    assert(start_vertex == 1);
    assert(end_vertex == 7);
    jgrapht_capi_handles_destroy(thread, gp);

    // test many to many not computed pair (ERROR)
    jgrapht_capi_sp_manytomany_get_path_between_vertices(thread, mm, 5, 2, &gp);
    assert(jgrapht_capi_error_get_errno(thread) == STATUS_ILLEGAL_ARGUMENT);
    jgrapht_capi_error_clear_errno(thread);

    jgrapht_capi_handles_destroy(thread, mm);

    jgrapht_capi_handles_destroy(thread, ch);
    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
