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
    jgrapht_capi_graph_dag_create(thread, 1, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int flag;
    assert(jgrapht_capi_graph_is_directed(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_undirected(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_weighted(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_allowing_selfloops(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_allowing_multipleedges(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_allowing_cycles(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_modifiable(thread, g, &flag) == 0);
    assert(flag == 1);

    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    int count;
    jgrapht_capi_graph_vertices_count(thread, g, &count);
    assert(count == 6);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 4, 5, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 5, NULL);

    jgrapht_capi_error_print_stack_trace(thread);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_edges_count(thread, g, &count);
    assert(count == 6);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // add edge which would create cycle
    jgrapht_capi_graph_add_edge(thread, g, 5, 2, NULL);
    assert(jgrapht_capi_error_get_errno(thread) != 0);
    jgrapht_capi_error_clear_errno(thread);

    void *set;
    jgrapht_capi_graph_dag_vertex_ancestors(thread, g, 3, &set);
    jgrapht_capi_set_size(thread, set, &count);
    assert(count == 2);
    jgrapht_capi_handles_destroy(thread, set);

    jgrapht_capi_graph_dag_vertex_descendants(thread, g, 2, &set);
    jgrapht_capi_set_size(thread, set, &count);
    assert(count == 2);
    jgrapht_capi_handles_destroy(thread, set);

    // topological it
    void *it;
    int v;
    jgrapht_capi_graph_dag_topological_it(thread, g, &it);
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
    jgrapht_capi_handles_destroy(thread, it);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
