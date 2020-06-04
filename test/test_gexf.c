#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

char *input="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\
<gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\" \
xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\" \
xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> \
<graph defaultedgetype=\"undirected\">\
    <nodes>\
            <node id=\"1\" label=\"1\"/>\
            <node id=\"2\" label=\"2\"/>\
            <node id=\"3\" label=\"3\"/>\
        </nodes>\
        <edges>\
            <edge id=\"1\" source=\"2\" target=\"3\" />\
            <edge id=\"0\" source=\"1\" target=\"2\" />\
            <edge id=\"2\" source=\"3\" target=\"1\" />\
        </edges>\
        </graph>\
</gexf>";

char *expected="\
<?xml version=\"1.0\" encoding=\"UTF-8\"?><gexf xmlns=\"http://www.gexf.net/1.2draft\" xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\" version=\"1.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n\
    <graph defaultedgetype=\"undirected\">\n\
        <attributes class=\"edge\">\n\
            <attribute id=\"0\" title=\"cost\" type=\"double\"/>\n\
        </attributes>\n\
        <nodes>\n\
            <node id=\"0\" label=\"0\"/>\n\
            <node id=\"1\" label=\"1\"/>\n\
            <node id=\"2\" label=\"2\"/>\n\
        </nodes>\n\
        <edges>\n\
            <edge id=\"0\" source=\"1\" target=\"2\">\n\
                <attvalues>\n\
                    <attvalue for=\"0\" value=\"5.4\"/>\n\
                </attvalues>\n\
            </edge>\n\
            <edge id=\"1\" source=\"0\" target=\"1\">\n\
                <attvalues>\n\
                    <attvalue for=\"0\" value=\"6.5\"/>\n\
                </attvalues>\n\
            </edge>\n\
            <edge id=\"2\" source=\"2\" target=\"0\">\n\
                <attvalues>\n\
                    <attvalue for=\"0\" value=\"9.2\"/>\n\
                </attvalues>\n\
            </edge>\n\
        </edges>\n\
    </graph>\n\
</gexf>\n";

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


    // test gml with extra attributes
    jgrapht_capi_import_string_gexf(thread, g, input, NULL, 1, NULL, NULL, NULL, NULL);
    assert(jgrapht_capi_error_get_errno(thread) == 0);


    int vcount;
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 3);

    int ecount;
    jgrapht_capi_graph_edges_count(thread, g, &ecount);
    assert(ecount == 3);

    // write it to file

    void *attrs_registry;
    jgrapht_capi_attributes_registry_create(thread, &attrs_registry);

    jgrapht_capi_attributes_registry_register_attribute(thread, attrs_registry, "cost", "edge", "double", NULL);

    void *attr_store;
    jgrapht_capi_attributes_store_create(thread, &attr_store);
    jgrapht_capi_attributes_store_put_double_attribute(thread, attr_store, 0, "cost", 5.4);
    jgrapht_capi_attributes_store_put_double_attribute(thread, attr_store, 1, "cost", 6.5);
    jgrapht_capi_attributes_store_put_double_attribute(thread, attr_store, 2, "cost", 9.2);

    jgrapht_capi_export_file_gexf(thread, g, "dummy.gexf.out", attrs_registry, NULL, attr_store, NULL, NULL, 0, 0, 0, 0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // now read back 
    jgrapht_capi_handles_destroy(thread, g);
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);

    jgrapht_capi_import_file_gexf(thread, g, "dummy.gexf.out", import_id, 1, NULL, edge_attribute, NULL, NULL);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test output to string
    void *out;
    jgrapht_capi_export_string_gexf(thread, g, attrs_registry, NULL, attr_store, NULL, NULL, 0, 0, 0, 0, &out);
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
