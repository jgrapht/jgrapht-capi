#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

char *expected="{\"creator\":\"JGraphT JSON Exporter\",\"version\":\"1\",\"nodes\":[{\"id\":\"0\",\"label\":\"label 0\",\"cost\":100.5},{\"id\":\"1\",\"label\":\"label 1\"},{\"id\":\"2\",\"label\":\"label 2\"}],\"edges\":[{\"source\":\"0\",\"target\":\"1\"},{\"source\":\"1\",\"target\":\"2\"}]}";

void vertex_attribute(char *v, char *key, char *value) { 
    if (strcmp(v, "0") == 0) { 
        if (strcmp(key, "ID") == 0) { 
            assert(strcmp(value, "0") == 0);
        }
        if (strcmp(key, "label") == 0) { 
            assert(strcmp(value, "label 0") == 0);
        }
    }
    if (strcmp(v, "1") == 0) { 
        if (strcmp(key, "ID") == 0) { 
            assert(strcmp(value, "1") == 0);
        }
        if (strcmp(key, "label") == 0) { 
            assert(strcmp(value, "label 1") == 0);
        }
    }
    if (strcmp(v, "2") == 0) { 
        if (strcmp(key, "ID") == 0) { 
            assert(strcmp(value, "2") == 0);
        }
        if (strcmp(key, "label") == 0) { 
            assert(strcmp(value, "label 2") == 0);
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

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // first write a json
    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test with extra attributes
    void *attr_store;
    jgrapht_capi_attributes_store_create(thread, &attr_store);
    jgrapht_capi_attributes_store_put_string_attribute(thread, attr_store, 0, "label", "label 0");
    jgrapht_capi_attributes_store_put_string_attribute(thread, attr_store, 1, "label", "label 1");
    jgrapht_capi_attributes_store_put_string_attribute(thread, attr_store, 2, "label", "label 2");
    jgrapht_capi_attributes_store_put_double_attribute(thread, attr_store, 0, "cost", 100.5);

    jgrapht_capi_export_file_json(thread, g, "dummy.edgelist.json.out", attr_store, NULL, NULL);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_handles_destroy(thread, g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // then read back
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test json with extra attributes
    void *edgelist;
    jgrapht_capi_import_edgelist_attrs_file_json(thread, "dummy.edgelist.json.out", vertex_attribute, edge_attribute, &edgelist);

    int size;
    jgrapht_capi_list_size(thread, edgelist, &size);
    assert(size == 2);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, edgelist);
    jgrapht_capi_handles_destroy(thread, attr_store);

    // test json with no attributes
    jgrapht_capi_import_edgelist_noattrs_file_json(thread, "dummy.edgelist.json.out", &edgelist);
    jgrapht_capi_list_size(thread, edgelist, &size);
    assert(size == 2);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, edgelist);

    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
