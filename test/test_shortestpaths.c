#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 1, 0, 0, 1, &g);
    assert(jgrapht_capi_get_errno(thread) == 0);

    long long v;
    long long e;
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
    
    void *gp;
    double weight; 
    long long start_vertex, end_vertex;

    // test between vertices
    jgrapht_capi_sp_exec_dijkstra_get_path_between_vertices(thread, g, 0, 4, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 42.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_destroy(thread, gp);

    // test null if no path
    jgrapht_capi_sp_exec_dijkstra_get_path_between_vertices(thread, g, 5, 0, &gp);
    assert(gp == NULL);

    // bidirectional dijkstra between vertices
    jgrapht_capi_sp_exec_bidirectional_dijkstra_get_path_between_vertices(thread, g, 0, 4, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 42.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_destroy(thread, gp);

    // dijkstra will multiple queries
    void *singlesource;
    jgrapht_capi_sp_exec_dijkstra_get_singlesource_from_vertex(thread, g, 0, &singlesource);
    jgrapht_capi_sp_singlesource_get_path_to_vertex(thread, singlesource, 4, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 42.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_destroy(thread, gp);
    jgrapht_capi_sp_singlesource_get_path_to_vertex(thread, singlesource, 3, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 40.0);
    assert(start_vertex == 0);
    assert(end_vertex == 3);
    jgrapht_capi_destroy(thread, gp);
    jgrapht_capi_destroy(thread, singlesource);

    // bellman ford with multiple queries
    jgrapht_capi_sp_exec_bellmanford_get_singlesource_from_vertex(thread, g, 0, &singlesource);
    jgrapht_capi_sp_singlesource_get_path_to_vertex(thread, singlesource, 4, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 42.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_destroy(thread, gp);
    jgrapht_capi_sp_singlesource_get_path_to_vertex(thread, singlesource, 3, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 40.0);
    assert(start_vertex == 0);
    assert(end_vertex == 3);
    jgrapht_capi_destroy(thread, gp);
    jgrapht_capi_destroy(thread, singlesource);

    // bfs with multiple queries
    jgrapht_capi_sp_exec_bfs_get_singlesource_from_vertex(thread, g, 0, &singlesource);
    jgrapht_capi_sp_singlesource_get_path_to_vertex(thread, singlesource, 4, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 42.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_destroy(thread, gp);
    jgrapht_capi_sp_singlesource_get_path_to_vertex(thread, singlesource, 3, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 40.0);
    assert(start_vertex == 0);
    assert(end_vertex == 3);
    jgrapht_capi_destroy(thread, gp);
    jgrapht_capi_destroy(thread, singlesource);

    // allpairs
    void *allpairs;
    jgrapht_capi_sp_exec_johnson_get_allpairs(thread, g, &allpairs);
    jgrapht_capi_sp_allpairs_get_path_between_vertices(thread, allpairs, 0, 4, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 42.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_destroy(thread, gp);
    jgrapht_capi_sp_allpairs_get_singlesource_from_vertex(thread, allpairs, 0, &singlesource);
    jgrapht_capi_sp_singlesource_get_path_to_vertex(thread, singlesource, 4, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 42.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_destroy(thread, gp);
    jgrapht_capi_destroy(thread, singlesource);
    jgrapht_capi_destroy(thread, allpairs);

    jgrapht_capi_sp_exec_floydwarshall_get_allpairs(thread, g, &allpairs);
    jgrapht_capi_sp_allpairs_get_path_between_vertices(thread, allpairs, 0, 4, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 42.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_destroy(thread, gp);
    jgrapht_capi_sp_allpairs_get_singlesource_from_vertex(thread, allpairs, 0, &singlesource);
    jgrapht_capi_sp_singlesource_get_path_to_vertex(thread, singlesource, 4, &gp);
    jgrapht_capi_graphpath_get_fields(thread, gp, &weight, &start_vertex, &end_vertex, NULL);
    assert(weight == 42.0);
    assert(start_vertex == 0);
    assert(end_vertex == 4);
    jgrapht_capi_destroy(thread, gp);
    jgrapht_capi_destroy(thread, singlesource);
    jgrapht_capi_destroy(thread, allpairs);


    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}