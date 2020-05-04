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


int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_get_errno(thread) == 0);

    // first write a gml
    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_get_errno(thread) == 0);

    // test gml with extra attributes
    jgrapht_capi_import_string_gexf(thread, g, input, NULL, 1, NULL, NULL);
    assert(jgrapht_capi_get_errno(thread) == 0);
    
    long long vcount;
    jgrapht_capi_graph_vertices_count(thread, g, &vcount);
    assert(vcount == 3);

    long long ecount;
    jgrapht_capi_graph_edges_count(thread, g, &ecount);
    assert(ecount == 3);

    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
