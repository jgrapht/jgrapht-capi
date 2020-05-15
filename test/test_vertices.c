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

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 1, 1, 1, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    int vcount;
    assert(jgrapht_capi_graph_vertices_count(thread,  g, &vcount) == 0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    int ecount;
    assert(jgrapht_capi_graph_edges_count(thread,  g, &ecount) == 0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    
    int i;
    for(i = 0; i < NUM_VERTICES; i++) {
        int v;
        assert(jgrapht_capi_graph_add_vertex(thread,  g, &v) == 0);
        assert(v == i);
        assert(jgrapht_capi_error_get_errno(thread) == 0);
    }
    jgrapht_capi_graph_vertices_count(thread,  g, &vcount);
    assert(vcount == NUM_VERTICES);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_graph_edges_count(thread,  g, &ecount);
    assert(ecount == 0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int flag;
    for(i = 0; i < NUM_VERTICES; i++) {
        assert(jgrapht_capi_graph_contains_vertex(thread,  g, i, &flag) == 0);
        assert(flag);
        assert(jgrapht_capi_error_get_errno(thread) == 0);
    }

    int v;
    jgrapht_capi_graph_add_vertex(thread,  g, &v);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(v == NUM_VERTICES);
    jgrapht_capi_graph_vertices_count(thread,  g, &vcount);
    assert(vcount == NUM_VERTICES+1);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(jgrapht_capi_graph_contains_vertex(thread,  g, v, &flag) == 0);
    assert(flag);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_remove_vertex(thread,  g, v, &flag);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(jgrapht_capi_graph_contains_vertex(thread,  g, v, &flag) == 0);
    assert(!flag);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_graph_vertices_count(thread,  g, &vcount);
    assert(vcount == NUM_VERTICES);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test vertex iterator
    void *vit;
    jgrapht_capi_graph_create_all_vit(thread,  g, &vit);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    long x = 0;
    int value;
    while(1) { 
        jgrapht_capi_it_hasnext(thread,  vit, &flag);
        if (!flag) { 
            break;
        }
        jgrapht_capi_it_next_int(thread,  vit, &value);
        assert(value == x++);
    }
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread,  vit);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test vertex iterator (second use case)
    jgrapht_capi_graph_create_all_vit(thread,  g, &vit);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    x = 0;
    while(1) { 
        int ret;
        jgrapht_capi_it_next_int(thread,  vit, &ret);
        if (x < 1000) { 
            assert(ret == x);
            assert(jgrapht_capi_error_get_errno(thread) == 0);
        } else { 
            assert(jgrapht_capi_error_get_errno(thread) == STATUS_NO_SUCH_ELEMENT);
            break;
        }
        x++;
    }
    assert(jgrapht_capi_error_get_errno(thread) == STATUS_NO_SUCH_ELEMENT);
    jgrapht_capi_error_clear_errno(thread);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread,  vit);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
