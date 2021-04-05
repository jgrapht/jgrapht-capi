#include <stdio.h>
#include <stdlib.h>

#ifdef _WIN32
#include <crtdbg.h>
#endif 
#include <assert.h>

#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

#ifdef _WIN32
char *expected = "strict graph G {\r\n  0;\r\n  1;\r\n  2;\r\n  0 -- 1 [ cost=\"5.4\" ];\r\n  1 -- 2 [ cost=\"6.5\" ];\r\n  2 -- 0 [ cost=\"9.2\" ];\r\n}\r\n";
#else
char *expected = "strict graph G {\n  0;\n  1;\n  2;\n  0 -- 1 [ cost=\"5.4\" ];\n  1 -- 2 [ cost=\"6.5\" ];\n  2 -- 0 [ cost=\"9.2\" ];\n}\n";
#endif

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
    
#ifdef _WIN32
    _CrtSetReportMode( _CRT_WARN, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_WARN, _CRTDBG_FILE_STDERR);
    _CrtSetReportMode( _CRT_ERROR, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_ERROR, _CRTDBG_FILE_STDERR);
    _CrtSetReportMode( _CRT_ASSERT, _CRTDBG_MODE_FILE);
    _CrtSetReportFile( _CRT_ASSERT, _CRTDBG_FILE_STDERR);
#endif

    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // import a gexf from string
    void *g;
    jgrapht_capi_ii_graph_create(thread, 0, 0, 0, 0, NULL, NULL, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ix_graph_add_vertex(thread, g, NULL);

    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 2, 0, NULL);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // write it to file

    void *attr_store;
    jgrapht_capi_xx_attributes_store_create(thread, &attr_store);
    jgrapht_capi_id_attributes_store_put(thread, attr_store, 0, "cost", 5.4);
    jgrapht_capi_id_attributes_store_put(thread, attr_store, 1, "cost", 6.5);
    jgrapht_capi_id_attributes_store_put(thread, attr_store, 2, "cost", 9.2);

    jgrapht_capi_xx_export_file_dot(thread, g, "dummy.dot.out", NULL, attr_store, NULL);
    assert(jgrapht_capi_error_get_errno(thread) == 0);


    // now read back 
    jgrapht_capi_handles_destroy(thread, g);
    jgrapht_capi_ii_graph_create(thread, 0, 0, 0, 0, NULL, NULL, &g);

    jgrapht_capi_ii_import_file_dot(thread, g, "dummy.dot.out", import_id, NULL, edge_attribute, NULL, NULL);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test output to string
    void *out;
    jgrapht_capi_xx_export_string_dot(thread, g, NULL, attr_store, NULL, &out);
    char *str;
    jgrapht_capi_handles_get_ccharpointer(thread, out, &str);
    assert(strcmp(str, expected) == 0);
    jgrapht_capi_handles_destroy(thread, out);


    jgrapht_capi_handles_destroy(thread, attr_store);
    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
