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


void write_to_file(char* filename, char *str) { 
    FILE* fp = fopen(filename, "w");
    fprintf(fp, "%s", str);
    fclose(fp);
}

void edge_attribute(int v, char *key, char *value) {
    assert (v>=0 & v<=2);
    if (v == 0) { 
        if (strcmp(key, "cost") == 0) { 
            assert(strcmp(value, "5.4") == 0);
        }
    }
    if (v == 1) { 
        if (strcmp(key, "cost") == 0) { 
            assert(strcmp(value, "6.5") == 0);
        }
    }
    if (v == 2) { 
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
    jgrapht_capi_import_edgelist_attrs_string_graphml_simple(thread, expected, 1, NULL, NULL, 
        &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 3);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    
    // no attrs
    jgrapht_capi_import_edgelist_noattrs_string_graphml_simple(thread, expected, 1, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 3);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // write to tmp file and read back
    
    write_to_file("test_graphml_simple_edgelist.graphml", expected);

    jgrapht_capi_import_edgelist_attrs_file_graphml_simple(thread, "test_graphml_simple_edgelist.graphml", 1, NULL, edge_attribute, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 3);
    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_import_edgelist_noattrs_file_graphml_simple(thread, "test_graphml_simple_edgelist.graphml", 1, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 3);
    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);


    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
