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
    jgrapht_capi_graph_create(thread, 1, 1, 1, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int v1;
    jgrapht_capi_graph_add_vertex(thread, g, &v1);
    int v2;
    jgrapht_capi_graph_add_vertex(thread, g, &v2);
    int v3;
    jgrapht_capi_graph_add_vertex(thread, g, &v3);
    int v4;
    jgrapht_capi_graph_add_vertex(thread, g, &v4);
    int v5;
    jgrapht_capi_graph_add_vertex(thread, g, &v5);

    int vcount;
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 5);

    int e12;
    jgrapht_capi_graph_add_edge(thread, g, v1, v2, &e12);
    int e23_1;
    jgrapht_capi_graph_add_edge(thread, g, v2, v3, &e23_1);
    int e23_2;
    jgrapht_capi_graph_add_edge(thread, g, v2, v3, &e23_2);
    int e24;
    jgrapht_capi_graph_add_edge(thread, g, v2, v4, &e24);
    int e44;
    jgrapht_capi_graph_add_edge(thread, g, v4, v4, &e44);
    int e55_1;
    jgrapht_capi_graph_add_edge(thread, g, v5, v5, &e55_1);
    int e52;
    jgrapht_capi_graph_add_edge(thread, g, v5, v2, &e52);
    int e55_2;
    jgrapht_capi_graph_add_edge(thread, g, v5, v5, &e55_2);

    // inout
    int d;
    assert(jgrapht_capi_graph_degree_of(thread, g, v1, &d) == 0);
    assert(d == 1);
    assert(jgrapht_capi_graph_degree_of(thread, g, v2, &d) == 0);
    assert(d == 5);
    assert(jgrapht_capi_graph_degree_of(thread, g, v3, &d) == 0);
    assert(d == 2);
    assert(jgrapht_capi_graph_degree_of(thread, g, v4, &d) == 0);
    assert(d == 3);
    assert(jgrapht_capi_graph_degree_of(thread, g, v5, &d) == 0);
    assert(d == 5);

    void *eit;
    jgrapht_capi_graph_vertex_create_eit(thread, g, v1, &eit);
    int v;
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e12);
    int hasnext;
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_eit(thread, g, v2, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e12);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e52);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e23_1);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e23_2);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e24);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_eit(thread, g, v3, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e23_1);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e23_2);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_eit(thread, g, v4, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e24);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e44);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);    
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_eit(thread, g, v5, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e55_1);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e55_2);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e52);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    // incoming
    assert(jgrapht_capi_graph_indegree_of(thread, g, v1, &d) == 0);
    assert(d == 0);
    assert(jgrapht_capi_graph_indegree_of(thread, g, v2, &d) == 0);
    assert(d == 2);
    assert(jgrapht_capi_graph_indegree_of(thread, g, v3, &d) == 0);
    assert(d == 2);
    assert(jgrapht_capi_graph_indegree_of(thread, g, v4, &d) == 0);
    assert(d == 2);
    assert(jgrapht_capi_graph_indegree_of(thread, g, v5, &d) == 0);
    assert(d == 2);

    jgrapht_capi_graph_vertex_create_in_eit(thread, g, v1, &eit);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_in_eit(thread, g, v2, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e12);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e52);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_in_eit(thread, g, v3, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e23_1);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e23_2);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_in_eit(thread, g, v4, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e24);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e44);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_in_eit(thread, g, v5, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e55_1);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e55_2);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    // outgoing
    assert(jgrapht_capi_graph_outdegree_of(thread, g, v1, &d) == 0);
    assert(d == 1);
    assert(jgrapht_capi_graph_outdegree_of(thread, g, v2, &d) == 0);
    assert(d == 3);
    assert(jgrapht_capi_graph_outdegree_of(thread, g, v3, &d) == 0);
    assert(d == 0);
    assert(jgrapht_capi_graph_outdegree_of(thread, g, v4, &d) == 0);
    assert(d == 1);
    assert(jgrapht_capi_graph_outdegree_of(thread, g, v5, &d) == 0);
    assert(d == 3);
    
    jgrapht_capi_graph_vertex_create_out_eit(thread, g, v1, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e12);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_out_eit(thread, g, v2, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e23_1);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e23_2);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e24);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_out_eit(thread, g, v3, &eit);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);    
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_out_eit(thread, g, v4, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e44);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_graph_vertex_create_out_eit(thread, g, v5, &eit);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e55_1);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e52);
    assert(jgrapht_capi_it_next_int(thread, eit, &v) == 0);
    assert(v == e55_2);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_handles_destroy(thread, g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}