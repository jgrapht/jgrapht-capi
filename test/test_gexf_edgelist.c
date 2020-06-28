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

char *input2="\
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


int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test read from string with extra attributes
    void *edgelist;
    jgrapht_capi_import_edgelist_attrs_string_gexf(thread, input, 1, NULL, NULL, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 3);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test read from string with no attrs
    jgrapht_capi_import_edgelist_noattrs_string_gexf(thread, input2, 1, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 3);

    jgrapht_capi_handles_destroy(thread, edgelist);


    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
