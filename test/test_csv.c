#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

char *expected="0,1,3\n1,0,2\n3,0,2\n2,1,3\n";

int import_id(const char *id) { 
    return atol(id);
}

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 0, NULL);

    // write file
    jgrapht_capi_export_file_csv(thread, g, "dummy.csv.out", CSV_FORMAT_ADJACENCY_LIST, 0, 0, 0, NULL);
    jgrapht_capi_handles_destroy(thread, g);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // read file
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_import_file_csv(thread, g, "dummy.csv.out", import_id, NULL, NULL, CSV_FORMAT_ADJACENCY_LIST, 0, 0, 0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    
    int count;
    jgrapht_capi_graph_vertices_count(thread, g, &count);
    assert(count == 4);
    jgrapht_capi_graph_edges_count(thread, g, &count);
    assert(count == 4);

    // test output to string
    void *out;
    jgrapht_capi_export_string_csv(thread, g, CSV_FORMAT_ADJACENCY_LIST, 0, 0, 0, NULL, &out);
    char *str;
    jgrapht_capi_handles_get_ccharpointer(thread, out, &str);
    assert(strcmp(str, expected) == 0);
    //printf("%s", str);
    jgrapht_capi_handles_destroy(thread, out);

    jgrapht_capi_handles_destroy(thread, g);
    
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
