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
#define CRLF "\r\n"
#else 
#define CRLF "\n"
#endif

char *expected="\
Creator \"JGraphT GML Exporter\""CRLF"\
Version 1"CRLF"\
graph"CRLF"\
["CRLF"\
	label \"\""CRLF"\
	directed 0"CRLF"\
	node"CRLF"\
	["CRLF"\
		id 0"CRLF"\
		label \"label 0\""CRLF"\
	]"CRLF"\
	node"CRLF"\
	["CRLF"\
		id 1"CRLF"\
		label \"label 1\""CRLF"\
	]"CRLF"\
	node"CRLF"\
	["CRLF"\
		id 2"CRLF"\
		label \"label 2\""CRLF"\
	]"CRLF"\
	edge"CRLF"\
	["CRLF"\
		source 0"CRLF"\
		target 1"CRLF"\
	]"CRLF"\
	edge"CRLF"\
	["CRLF"\
		source 1"CRLF"\
		target 2"CRLF"\
	]"CRLF"\
]"CRLF"";

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

int preserveid(int x) { 
    return x;
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

    // first write a gml
    void *g;
    jgrapht_capi_ii_graph_create(thread, 0, 0, 0, 0, NULL, NULL, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_ii_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ii_graph_add_vertex(thread, g, NULL);
    jgrapht_capi_ii_graph_add_vertex(thread, g, NULL);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_ii_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_ii_graph_add_edge(thread, g, 1, 2, NULL);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test gml with extra attributes
    void *attr_store;
    jgrapht_capi_xx_attributes_store_create(thread, &attr_store);
    jgrapht_capi_ii_attributes_store_put_string_attribute(thread, attr_store, 0, "label", "label 0");
    jgrapht_capi_ii_attributes_store_put_string_attribute(thread, attr_store, 1, "label", "label 1");
    jgrapht_capi_ii_attributes_store_put_string_attribute(thread, attr_store, 2, "label", "label 2");

    jgrapht_capi_xx_export_file_gml(thread, g, "dummy.gml.out", 0, 0, 0, attr_store, NULL, NULL);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_handles_destroy(thread, g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // then read back
    jgrapht_capi_ii_graph_create(thread, 0, 0, 0, 0, NULL, NULL, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test gml with extra attributes
    jgrapht_capi_ii_import_file_gml(thread, g, "dummy.gml.out", preserveid, vertex_attribute, edge_attribute, NULL, NULL);

    int ecount;
    jgrapht_capi_ii_graph_edges_count(thread, g, &ecount);
    assert(ecount == 2);

    // test output to string
    void *out;
    jgrapht_capi_xx_export_string_gml(thread, g, 0, 0, 0, attr_store, NULL, NULL, &out);
    char *str;
    jgrapht_capi_handles_get_ccharpointer(thread, out, &str);
    printf("%s", str);
    assert(strcmp(str, expected) == 0);
    jgrapht_capi_handles_destroy(thread, out);

    jgrapht_capi_handles_destroy(thread, attr_store);
    jgrapht_capi_handles_destroy(thread, g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
