#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht.h>

#define NUM_VERTICES 1000
#define ITERATOR_NO_SUCH_ELEMENT 100

int main() { 
    jgrapht_thread_create();
    assert(jgrapht_is_thread_attached());

    assert(jgrapht_get_errno() == 0);

    void *g = jgrapht_graph_create(1, 1, 1, 1);
    assert(jgrapht_get_errno() == 0);
    assert(jgrapht_graph_vertices_count(g) == 0);
    assert(jgrapht_get_errno() == 0);
    assert(jgrapht_graph_edges_count(g) == 0);
    assert(jgrapht_get_errno() == 0);
    
    for(int i = 0; i < NUM_VERTICES; i++) {
        assert(jgrapht_graph_add_vertex(g) == i);
        assert(jgrapht_get_errno() == 0);
    }
    assert(jgrapht_graph_vertices_count(g) == NUM_VERTICES);
    assert(jgrapht_get_errno() == 0);
    assert(jgrapht_graph_edges_count(g) == 0);
    assert(jgrapht_get_errno() == 0);

    for(int i = 0; i < NUM_VERTICES; i++) {
        assert(jgrapht_graph_contains_vertex(g, i));
        assert(jgrapht_get_errno() == 0);
    }

    long v = jgrapht_graph_add_vertex(g);
    assert(jgrapht_get_errno() == 0);
    assert(v == NUM_VERTICES);
    assert(jgrapht_graph_vertices_count(g) == NUM_VERTICES+1);
    assert(jgrapht_get_errno() == 0);
    assert(jgrapht_graph_contains_vertex(g, v));
    assert(jgrapht_get_errno() == 0);

    jgrapht_graph_remove_vertex(g, v);
    assert(jgrapht_get_errno() == 0);
    assert(!jgrapht_graph_contains_vertex(g, v));
    assert(jgrapht_get_errno() == 0);
    assert(jgrapht_graph_vertices_count(g) == NUM_VERTICES);
    assert(jgrapht_get_errno() == 0);

    // test vertex iterator
    void *vit = jgrapht_graph_create_all_vit(g);
    assert(jgrapht_get_errno() == 0);
    long x = 0;
    while(jgrapht_it_hasnext(vit)) { 
        assert(jgrapht_it_next(vit) == x++);
    }
    assert(jgrapht_get_errno() == 0);
    jgrapht_destroy(vit);
    assert(jgrapht_get_errno() == 0);

    // test vertex iterator (second use case)
    vit = jgrapht_graph_create_all_vit(g);
    assert(jgrapht_get_errno() == 0);
    x = 0;
    while(1) { 
        long ret = jgrapht_it_next(vit);
        if (x < 1000) { 
            assert(ret == x);
            assert(jgrapht_get_errno() == 0);
        } else { 
            assert(jgrapht_get_errno() == ITERATOR_NO_SUCH_ELEMENT);
            break;
        }
        x++;
    }
    assert(jgrapht_get_errno() == ITERATOR_NO_SUCH_ELEMENT);
    jgrapht_clear_errno();
    assert(jgrapht_get_errno() == 0);
    jgrapht_destroy(vit);
    assert(jgrapht_get_errno() == 0);

    jgrapht_destroy(g);
    assert(jgrapht_get_errno() == 0);

    jgrapht_thread_destroy();
    assert(!jgrapht_is_thread_attached());

}