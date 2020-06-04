#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

char *expected="\
<?xml version=\"1.0\" encoding=\"UTF-8\"?><graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n\
    <key id=\"key0\" for=\"edge\" attr.name=\"cost\" attr.type=\"double\"/>\n\
    <graph edgedefault=\"undirected\">\n\
        <node id=\"0\"/>\n\
        <node id=\"1\"/>\n\
        <node id=\"2\"/>\n\
        <edge source=\"0\" target=\"1\">\n\
            <data key=\"key0\">5.4</data>\n\
        </edge>\n\
        <edge source=\"1\" target=\"2\">\n\
            <data key=\"key0\">6.5</data>\n\
        </edge>\n\
        <edge source=\"2\" target=\"0\">\n\
            <data key=\"key0\">9.2</data>\n\
        </edge>\n\
    </graph>\n\
</graphml>\n";

void edge_attribute(int e, char *key, char *value) { 
    if (e == 0) { 
        if (strcmp(key, "cost") == 0) { 
            assert(strcmp(value, "5.4") == 0);
        }
    }
    if (e == 1) { 
        if (strcmp(key, "cost") == 0) { 
            assert(strcmp(value, "6.5") == 0);
        }
    }
    if (e == 2) { 
        if (strcmp(key, "cost") == 0) { 
            assert(strcmp(value, "9.2") == 0);
        }
    }
}

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

    // import a gexf from string
    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 0, NULL);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // write it to file

    void *attrs_registry;
    jgrapht_capi_attributes_registry_create(thread, &attrs_registry);
    jgrapht_capi_attributes_registry_register_attribute(thread, attrs_registry, "cost", "edge", "double", NULL);

    void *attr_store;
    jgrapht_capi_attributes_store_create(thread, &attr_store);
    jgrapht_capi_attributes_store_put_double_attribute(thread, attr_store, 0, "cost", 5.4);
    jgrapht_capi_attributes_store_put_double_attribute(thread, attr_store, 1, "cost", 6.5);
    jgrapht_capi_attributes_store_put_double_attribute(thread, attr_store, 2, "cost", 9.2);

    jgrapht_capi_export_file_graphml(thread, g, "dummy.graphml.simple.out", attrs_registry, NULL, attr_store, NULL, 0, 0, 0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // now read back 
    jgrapht_capi_handles_destroy(thread, g);
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);

    jgrapht_capi_import_file_graphml_simple(thread, g, "dummy.graphml.simple.out", import_id, 1, NULL, edge_attribute, NULL, NULL);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test output to string
    void *out;
    jgrapht_capi_export_string_graphml(thread, g, attrs_registry, NULL, attr_store, NULL, 0, 0, 0, &out);
    char *str;
    jgrapht_capi_handles_get_ccharpointer(thread, out, &str);
    //printf("%s", str);
    assert(strcmp(str, expected) == 0);
    jgrapht_capi_handles_destroy(thread, out);


    jgrapht_capi_handles_destroy(thread, attr_store);
    jgrapht_capi_handles_destroy(thread, attrs_registry);
    jgrapht_capi_handles_destroy(thread, g);


    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
