#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

char label[100];

char* vertex_label(long long v) { 
    sprintf(label, "label %lld", v);
    return label;
}

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_get_errno(thread) == 0);

    long long v;
    long long e;
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 0, NULL);

    // just test the API with a dummy file
    jgrapht_capi_export_file_dimacs(thread, g, "dummy.dimacs.out", DIMACS_FORMAT_COLORING);

    // test gml with extra attributes
    void *attr_store;
    jgrapht_capi_attributes_store_create(thread, &attr_store);
    jgrapht_capi_attributes_store_put_string_attribute(thread, attr_store, 0, "label", "label 0");
    jgrapht_capi_attributes_store_put_string_attribute(thread, attr_store, 1, "label", "label 1");
    jgrapht_capi_attributes_store_put_string_attribute(thread, attr_store, 2, "label", "label 2");

    jgrapht_capi_export_file_gml(thread, g, "dummy.gml.out", 0, attr_store, NULL);
    jgrapht_capi_destroy(thread, attr_store);

    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
