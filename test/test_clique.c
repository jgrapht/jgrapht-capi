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

    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 1, &g);

    int v;
    int e;
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

    void *cliqueit;
    jgrapht_capi_clique_exec_bron_kerbosch(thread, g, 0, &cliqueit);

    int hasnext = 0;
    jgrapht_capi_it_hasnext(thread, cliqueit, &hasnext);
    assert(hasnext == 1);

    // clique is a Set<Long>
    void *clique;
    jgrapht_capi_it_next_object(thread, cliqueit, &clique);
    void *vit;
    jgrapht_capi_set_it_create(thread, clique, &vit);
    while(1) { 
        jgrapht_capi_it_hasnext(thread, vit, &hasnext);
        if (!hasnext) 
            break;
        jgrapht_capi_it_next_int(thread, vit, &v);
    }
    jgrapht_capi_handles_destroy(thread, vit);
    jgrapht_capi_handles_destroy(thread, clique);
    printf("\n");

    jgrapht_capi_it_hasnext(thread, cliqueit, &hasnext);
    assert(hasnext == 1);

    // clique is a Set<Long>
    jgrapht_capi_it_next_object(thread, cliqueit, &clique);
    jgrapht_capi_set_it_create(thread, clique, &vit);
    while(1) { 
        jgrapht_capi_it_hasnext(thread, vit, &hasnext);
        if (!hasnext) 
            break;
        jgrapht_capi_it_next_int(thread, vit, &v);
    }
    jgrapht_capi_handles_destroy(thread, vit);
    jgrapht_capi_handles_destroy(thread, clique);

    jgrapht_capi_it_hasnext(thread, cliqueit, &hasnext);
    assert(hasnext == 1);

    // more cliques present

    jgrapht_capi_handles_destroy(thread, cliqueit);
    jgrapht_capi_handles_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
