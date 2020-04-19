#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi.h>

#define NUM_VERTICES 1000
#define ITERATOR_NO_SUCH_ELEMENT 100

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_get_errno(thread) == 0);

    void *g = jgrapht_capi_graph_create(thread, 1, 1, 1, 1);
    assert(jgrapht_capi_get_errno(thread) == 0);
    assert(jgrapht_capi_graph_vertices_count(thread,  g) == 0);
    assert(jgrapht_capi_get_errno(thread) == 0);
    assert(jgrapht_capi_graph_edges_count(thread,  g) == 0);
    assert(jgrapht_capi_get_errno(thread) == 0);
    
    for(int i = 0; i < NUM_VERTICES; i++) {
        assert(jgrapht_capi_graph_add_vertex(thread,  g) == i);
        assert(jgrapht_capi_get_errno(thread) == 0);
    }
    assert(jgrapht_capi_graph_vertices_count(thread,  g) == NUM_VERTICES);
    assert(jgrapht_capi_get_errno(thread) == 0);
    assert(jgrapht_capi_graph_edges_count(thread,  g) == 0);
    assert(jgrapht_capi_get_errno(thread) == 0);

    for(int i = 0; i < NUM_VERTICES; i++) {
        assert(jgrapht_capi_graph_contains_vertex(thread,  g, i));
        assert(jgrapht_capi_get_errno(thread) == 0);
    }

    long v = jgrapht_capi_graph_add_vertex(thread,  g);
    assert(jgrapht_capi_get_errno(thread) == 0);
    assert(v == NUM_VERTICES);
    assert(jgrapht_capi_graph_vertices_count(thread,  g) == NUM_VERTICES+1);
    assert(jgrapht_capi_get_errno(thread) == 0);
    assert(jgrapht_capi_graph_contains_vertex(thread,  g, v));
    assert(jgrapht_capi_get_errno(thread) == 0);

    jgrapht_capi_graph_remove_vertex(thread,  g, v);
    assert(jgrapht_capi_get_errno(thread) == 0);
    assert(!jgrapht_capi_graph_contains_vertex(thread,  g, v));
    assert(jgrapht_capi_get_errno(thread) == 0);
    assert(jgrapht_capi_graph_vertices_count(thread,  g) == NUM_VERTICES);
    assert(jgrapht_capi_get_errno(thread) == 0);

    // test vertex iterator
    void *vit = jgrapht_capi_graph_create_all_vit(thread,  g);
    assert(jgrapht_capi_get_errno(thread) == 0);
    long x = 0;
    while(jgrapht_capi_it_hasnext(thread,  vit)) { 
        assert(jgrapht_capi_it_next(thread,  vit) == x++);
    }
    assert(jgrapht_capi_get_errno(thread) == 0);
    jgrapht_capi_destroy(thread,  vit);
    assert(jgrapht_capi_get_errno(thread) == 0);

    // test vertex iterator (second use case)
    vit = jgrapht_capi_graph_create_all_vit(thread,  g);
    assert(jgrapht_capi_get_errno(thread) == 0);
    x = 0;
    while(1) { 
        long ret = jgrapht_capi_it_next(thread,  vit);
        if (x < 1000) { 
            assert(ret == x);
            assert(jgrapht_capi_get_errno(thread) == 0);
        } else { 
            assert(jgrapht_capi_get_errno(thread) == ITERATOR_NO_SUCH_ELEMENT);
            break;
        }
        x++;
    }
    assert(jgrapht_capi_get_errno(thread) == ITERATOR_NO_SUCH_ELEMENT);
    jgrapht_capi_clear_errno(thread);
    assert(jgrapht_capi_get_errno(thread) == 0);
    jgrapht_capi_destroy(thread,  vit);
    assert(jgrapht_capi_get_errno(thread) == 0);

    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}