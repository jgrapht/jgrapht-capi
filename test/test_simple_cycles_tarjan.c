#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

#define _CRT_SECURE_NO_WARNINGS

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 1, 0, 0, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 0, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 4, 5, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 5, 2, NULL);

    void *cycleit;
    jgrapht_capi_cycles_simple_enumeration_exec_tarjan(thread, g, &cycleit);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // 1st cycle
    void *cycle;
    int hasnext;
    jgrapht_capi_it_hasnext(thread, cycleit, &hasnext);
    assert(hasnext==1);
    jgrapht_capi_it_next_object(thread, cycleit, &cycle);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // cycle is a List<Long> where each long is a vertex 

    void *vit;
    jgrapht_capi_list_it_create(thread, cycle, &vit);

    char result[100];
    result[0] = '\0';
    while(1) { 
        jgrapht_capi_it_hasnext(thread, vit, &hasnext);
        if (!hasnext) { 
            break;
        }
        int v;
        jgrapht_capi_it_next_int(thread, vit, &v);
        char vastext[100];
        sprintf(vastext, "%d", v);
        strcat(result, vastext);
    }
    //printf("%s\n", result);
    assert(strcmp(result, "0123") == 0);
    jgrapht_capi_handles_destroy(thread, vit);
    jgrapht_capi_handles_destroy(thread, cycle);


    // 2nd cycle
    jgrapht_capi_it_hasnext(thread, cycleit, &hasnext);
    assert(hasnext==1);
    jgrapht_capi_it_next_object(thread, cycleit, &cycle);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_list_it_create(thread, cycle, &vit);

    result[0] = '\0';
    while(1) { 
        jgrapht_capi_it_hasnext(thread, vit, &hasnext);
        if (!hasnext) { 
            break;
        }
        int v;
        jgrapht_capi_it_next_int(thread, vit, &v);
        char vastext[100];
        sprintf(vastext, "%d", v);
        strcat(result, vastext);
    }
    //printf("%s\n", result);
    assert(strcmp(result, "014523") == 0);
    jgrapht_capi_handles_destroy(thread, vit);
    jgrapht_capi_handles_destroy(thread, cycle);

    jgrapht_capi_it_hasnext(thread, cycleit, &hasnext);
    assert(hasnext==0);

    jgrapht_capi_handles_destroy(thread, cycleit);


    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
