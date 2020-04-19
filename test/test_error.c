#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include <jgrapht_capi.h>


int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    } 

    assert(jgrapht_capi_get_errno(thread) == 0);

    void *g = jgrapht_capi_graph_create(thread, 1, 0, 0, 0);
    assert(jgrapht_capi_get_errno(thread) == 0);

    long v0 = jgrapht_capi_graph_add_vertex(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);
    long v1 = jgrapht_capi_graph_add_vertex(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);
    long v2 = 2;

    jgrapht_capi_graph_add_edge(thread, g, v0, v2);
    assert(jgrapht_capi_get_errno(thread) != 0);
    assert(strcmp("no such vertex in graph: 2", jgrapht_capi_get_errno_msg(thread)) == 0);

    jgrapht_capi_clear_errno(thread);
    jgrapht_capi_destroy(thread, g);
    assert(jgrapht_capi_get_errno(thread) == 0);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}