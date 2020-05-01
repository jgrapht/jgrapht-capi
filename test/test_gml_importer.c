#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

void vertex_attribute(long long v, char *key, char *value) { 
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

void edge_attribute(long long e, char *key, char *value) { 
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

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 0, &g);
    assert(jgrapht_capi_get_errno(thread) == 0);

    // test gml with extra attributes
    jgrapht_capi_import_file_gml(thread, g, "dummy.gml.out", vertex_attribute, edge_attribute);

    long long ecount;
    jgrapht_capi_graph_edges_count(thread, g, &ecount);
    assert(ecount == 2);

    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
