#include <stdio.h>
#include <stdlib.h>

#ifdef _WIN32
#include <crtdbg.h>
#endif 
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>


int main() {
    
#ifdef _WIN32
    _CrtSetReportMode( _CRT_WARN, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_WARN, _CRTDBG_FILE_STDERR);
    _CrtSetReportMode( _CRT_ERROR, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_ERROR, _CRTDBG_FILE_STDERR);
    _CrtSetReportMode( _CRT_ASSERT, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_ASSERT, _CRTDBG_FILE_STDERR);
#endif
 
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_ll_graph_create(thread, 1, 1, 1, 1, NULL, NULL, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    long long v1;
    jgrapht_capi_lx_graph_add_vertex(thread, g, &v1);
    long long v2;
    jgrapht_capi_lx_graph_add_vertex(thread, g, &v2);
    long long v3;
    jgrapht_capi_lx_graph_add_vertex(thread, g, &v3);
    long long v4;
    jgrapht_capi_lx_graph_add_vertex(thread, g, &v4);
    long long v5;
    jgrapht_capi_lx_graph_add_vertex(thread, g, &v5);

    long long vcount;
    jgrapht_capi_lx_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 5);

    long long e12;
    jgrapht_capi_ll_graph_add_edge(thread, g, v1, v2, &e12);
    long long e23_1;
    jgrapht_capi_ll_graph_add_edge(thread, g, v2, v3, &e23_1);
    long long e23_2;
    jgrapht_capi_ll_graph_add_edge(thread, g, v2, v3, &e23_2);
    long long e24;
    jgrapht_capi_ll_graph_add_edge(thread, g, v2, v4, &e24);
    long long e44;
    jgrapht_capi_ll_graph_add_edge(thread, g, v4, v4, &e44);
    long long e55_1;
    jgrapht_capi_ll_graph_add_edge(thread, g, v5, v5, &e55_1);
    long long e52;
    jgrapht_capi_ll_graph_add_edge(thread, g, v5, v2, &e52);
    long long e55_2;
    jgrapht_capi_ll_graph_add_edge(thread, g, v5, v5, &e55_2);

    // inout
    long long d;
    assert(jgrapht_capi_lx_graph_degree_of(thread, g, v1, &d) == 0);
    assert(d == 1);
    assert(jgrapht_capi_lx_graph_degree_of(thread, g, v2, &d) == 0);
    assert(d == 5);
    assert(jgrapht_capi_lx_graph_degree_of(thread, g, v3, &d) == 0);
    assert(d == 2);
    assert(jgrapht_capi_lx_graph_degree_of(thread, g, v4, &d) == 0);
    assert(d == 3);
    assert(jgrapht_capi_lx_graph_degree_of(thread, g, v5, &d) == 0);
    assert(d == 5);

    void *eit;
    jgrapht_capi_lx_graph_vertex_create_eit(thread, g, v1, &eit);
    long long v;
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e12);
    int hasnext;
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_eit(thread, g, v2, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e12);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e52);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e23_1);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e23_2);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e24);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_eit(thread, g, v3, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e23_1);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e23_2);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_eit(thread, g, v4, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e24);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e44);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);    
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_eit(thread, g, v5, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e55_1);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e55_2);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e52);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    // incoming
    assert(jgrapht_capi_lx_graph_indegree_of(thread, g, v1, &d) == 0);
    assert(d == 0);
    assert(jgrapht_capi_lx_graph_indegree_of(thread, g, v2, &d) == 0);
    assert(d == 2);
    assert(jgrapht_capi_lx_graph_indegree_of(thread, g, v3, &d) == 0);
    assert(d == 2);
    assert(jgrapht_capi_lx_graph_indegree_of(thread, g, v4, &d) == 0);
    assert(d == 2);
    assert(jgrapht_capi_lx_graph_indegree_of(thread, g, v5, &d) == 0);
    assert(d == 2);

    jgrapht_capi_lx_graph_vertex_create_in_eit(thread, g, v1, &eit);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_in_eit(thread, g, v2, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e12);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e52);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_in_eit(thread, g, v3, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e23_1);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e23_2);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_in_eit(thread, g, v4, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e24);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e44);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_in_eit(thread, g, v5, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e55_1);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e55_2);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    // outgoing
    assert(jgrapht_capi_lx_graph_outdegree_of(thread, g, v1, &d) == 0);
    assert(d == 1);
    assert(jgrapht_capi_lx_graph_outdegree_of(thread, g, v2, &d) == 0);
    assert(d == 3);
    assert(jgrapht_capi_lx_graph_outdegree_of(thread, g, v3, &d) == 0);
    assert(d == 0);
    assert(jgrapht_capi_lx_graph_outdegree_of(thread, g, v4, &d) == 0);
    assert(d == 1);
    assert(jgrapht_capi_lx_graph_outdegree_of(thread, g, v5, &d) == 0);
    assert(d == 3);
    
    jgrapht_capi_lx_graph_vertex_create_out_eit(thread, g, v1, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e12);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_out_eit(thread, g, v2, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e23_1);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e23_2);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e24);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_out_eit(thread, g, v3, &eit);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);    
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_out_eit(thread, g, v4, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e44);
    assert(jgrapht_capi_it_hasnext(thread, eit, &hasnext) == 0);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, eit);

    jgrapht_capi_lx_graph_vertex_create_out_eit(thread, g, v5, &eit);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e55_1);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
    assert(v == e52);
    assert(jgrapht_capi_it_next_long(thread, eit, &v) == 0);
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
