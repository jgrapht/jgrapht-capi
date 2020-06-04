#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

void vertex_attribute(int v, char *key, char *value) { 
    if (v == 0) { 
        if (strcmp(key, "ID") == 0) { 
            assert(strcmp(value, "0") == 0);
        }
        if (strcmp(key, "label") == 0) { 
            assert(strcmp(value, "label 0") == 0);
        }
    }
    if (v == 1) { 
        if (strcmp(key, "ID") == 0) { 
            assert(strcmp(value, "1") == 0);
        }
        if (strcmp(key, "label") == 0) { 
            assert(strcmp(value, "label 1") == 0);
        }
    }
    if (v == 2) { 
        if (strcmp(key, "ID") == 0) { 
            assert(strcmp(value, "2") == 0);
        }
        if (strcmp(key, "label") == 0) { 
            assert(strcmp(value, "κόμβος 2") == 0);
        }
    }
}

void edge_attribute(int e, char *key, char *value) { 
    if (e == 0) { 
        if (strcmp(key, "label") == 0) { 
            assert(strcmp(value, "edge 0-1") == 0);
        }
    }
    if (e == 1) { 
        if (strcmp(key, "label") == 0) { 
            assert(strcmp(value, "edge 1-2") == 0);
        }
    }
}

long import_id_from_file(const char *id_from_file) { 
    return atol(id_from_file);
}

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // first write a gml
    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int i;
    for(i = 0; i < 10; i++) {
        jgrapht_capi_graph_add_vertex(thread, g, NULL);
    }
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 5, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 6, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 7, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 8, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 0, 9, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 4, 5, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 5, 6, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 6, 7, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 7, 8, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 8, 9, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 9, 1, NULL);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test json with extra attributes
    void *attr_store;
    jgrapht_capi_attributes_store_create(thread, &attr_store);
    jgrapht_capi_attributes_store_put_string_attribute(thread, attr_store, 0, "label", "label 0");
    jgrapht_capi_attributes_store_put_string_attribute(thread, attr_store, 1, "label", "label 1");
    jgrapht_capi_attributes_store_put_string_attribute(thread, attr_store, 2, "label", "κόμβος 2");
    jgrapht_capi_attributes_store_put_double_attribute(thread, attr_store, 0, "cost", 100.5);

    jgrapht_capi_export_file_json(thread, g, "dummy2.json.out", attr_store, NULL, NULL);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, attr_store);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_handles_destroy(thread, g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // then read back
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test json with extra attributes
    jgrapht_capi_import_file_json(thread, g, "dummy2.json.out", import_id_from_file, vertex_attribute, edge_attribute, NULL, NULL);

    int ecount;
    jgrapht_capi_graph_edges_count(thread, g, &ecount);
    assert(ecount == 18);

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
