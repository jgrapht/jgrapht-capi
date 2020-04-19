#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <jgrapht.h>


int main() { 
    jgrapht_thread_create();
    assert(jgrapht_is_thread_attached());

    assert(jgrapht_get_errno() == 0);

    void *g = jgrapht_graph_create(1, 0, 0, 0);
    assert(jgrapht_get_errno() == 0);

    long v0 = jgrapht_graph_add_vertex(g);
    assert(jgrapht_get_errno() == 0);
    long v1 = jgrapht_graph_add_vertex(g);
    assert(jgrapht_get_errno() == 0);
    long v2 = 2;

    jgrapht_graph_add_edge(g, v0, v2);
    assert(jgrapht_get_errno() != 0);
    assert(strcmp("no such vertex in graph: 2", jgrapht_get_errno_msg()) == 0);

    jgrapht_clear_errno();
    jgrapht_destroy(g);
    assert(jgrapht_get_errno() == 0);

    jgrapht_thread_destroy();
    assert(!jgrapht_is_thread_attached());

    return EXIT_SUCCESS;
}