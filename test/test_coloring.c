#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

void assert_coloring(graal_isolatethread_t *thread, void *g, void *map) { 
    void *eit;
    jgrapht_capi_graph_create_all_eit(thread, g, &eit);
    int has_next;
    while(1) { 
        jgrapht_capi_it_hasnext(thread, eit, &has_next);
        if (!has_next) { 
            break;
        }
        int e;
        jgrapht_capi_it_next_int(thread, eit, &e);
        int s; 
        jgrapht_capi_graph_edge_source(thread, g, e, &s);
        int t;
        jgrapht_capi_graph_edge_target(thread, g, e, &t);
        int source_color, target_color;
        jgrapht_capi_map_int_int_get(thread, map, s, &source_color);
        jgrapht_capi_map_int_int_get(thread, map, t, &target_color);
        assert(source_color != target_color);
    }
    jgrapht_capi_handles_destroy(thread, eit);
}

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int flag;
    assert(jgrapht_capi_graph_is_directed(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_undirected(thread, g, &flag) == 0);
    assert(flag == 1);
    assert(jgrapht_capi_graph_is_weighted(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_allowing_selfloops(thread, g, &flag) == 0);
    assert(flag == 0);
    assert(jgrapht_capi_graph_is_allowing_multipleedges(thread, g, &flag) == 0);
    assert(flag == 0);

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
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 6);
    assert(jgrapht_capi_graph_add_vertex(thread, g, &v) == 0);
    assert(v == 7);

    int e;
    assert(jgrapht_capi_graph_add_edge(thread, g, 0, 1, &e) == 0);
    assert(e == 0);
    assert(jgrapht_capi_graph_add_edge(thread, g, 1, 2, &e) == 0);
    assert(e == 1);
    assert(jgrapht_capi_graph_add_edge(thread, g, 2, 3, &e) == 0);
    assert(e == 2);
    assert(jgrapht_capi_graph_add_edge(thread, g, 3, 0, &e) == 0);
    assert(e == 3);
    assert(jgrapht_capi_graph_add_edge(thread, g, 4, 5, &e) == 0);
    assert(e == 4);
    assert(jgrapht_capi_graph_add_edge(thread, g, 5, 6, &e) == 0);
    assert(e == 5);
    assert(jgrapht_capi_graph_add_edge(thread, g, 6, 7, &e) == 0);
    assert(e == 6);
    assert(jgrapht_capi_graph_add_edge(thread, g, 7, 4, &e) == 0);
    assert(e == 7);
    assert(jgrapht_capi_graph_add_edge(thread, g, 3, 4, &e) == 0);
    assert(e == 8);
    assert(jgrapht_capi_graph_add_edge(thread, g, 5, 3, &e) == 0);
    assert(e == 9);

    void *c;
    int colors;
    jgrapht_capi_coloring_exec_greedy(thread, g, &colors, &c);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(colors == 3);
    assert_coloring(thread, g, c);
    jgrapht_capi_handles_destroy(thread, c);

    jgrapht_capi_coloring_exec_greedy_smallestdegreelast(thread, g, &colors, &c);
    assert(colors == 3);
    assert_coloring(thread, g, c);
    jgrapht_capi_handles_destroy(thread, c);

    jgrapht_capi_coloring_exec_backtracking_brown(thread, g, &colors, &c);
    assert(colors == 3);
    assert_coloring(thread, g, c);
    jgrapht_capi_handles_destroy(thread, c);

    jgrapht_capi_coloring_exec_greedy_largestdegreefirst(thread, g, &colors, &c);
    assert(colors == 3);
    assert_coloring(thread, g, c);
    jgrapht_capi_handles_destroy(thread, c);

    jgrapht_capi_coloring_exec_greedy_random_with_seed(thread, g, 13, &colors, &c);
    assert(colors == 3);
    assert_coloring(thread, g, c);
    jgrapht_capi_handles_destroy(thread, c);

    jgrapht_capi_coloring_exec_greedy_dsatur(thread, g, &colors, &c);
    assert(colors == 3);
    assert_coloring(thread, g, c);
    jgrapht_capi_handles_destroy(thread, c);

    // just test API, since this is not a coloring
    jgrapht_capi_coloring_exec_color_refinement(thread, g, &colors, &c);
    assert(colors == 8);
    jgrapht_capi_handles_destroy(thread, c);

    jgrapht_capi_handles_destroy(thread, g);

    // check chordal graph
    jgrapht_capi_graph_create(thread, 0, 0, 0, 1, &g);

    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 4, 5, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 5, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);


    jgrapht_capi_coloring_exec_chordal_minimum_coloring(thread, g, &colors, &c);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(colors == 3);
    jgrapht_capi_handles_destroy(thread, c);

    // cleanup
    jgrapht_capi_handles_destroy(thread, g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}