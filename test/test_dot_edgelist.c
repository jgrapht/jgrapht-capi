#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

char *expected = "strict graph G {\n  0;\n  1;\n  2;\n  0 -- 1 [ cost=\"5.4\" ];\n  1 -- 2 [ cost=\"6.5\" ];\n  2 -- 0 [ cost=\"9.2\" ];\n}\n";


void write_to_file(char* filename, char *str) { 
    FILE* fp = fopen(filename, "w");
    fprintf(fp, "%s", str);
    fclose(fp);
}

void edge_attribute(int v, char *key, char *value) {
    //printf("%d-%s-%s\n", v, key, value);
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
    jgrapht_capi_import_edgelist_attrs_string_dot(thread, expected, NULL, NULL, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 3);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    
    // no attrs
    jgrapht_capi_import_edgelist_noattrs_string_dot(thread, expected, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 3);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // write to tmp file and read back
    write_to_file("test_dot_edgelist.dot", expected);

    jgrapht_capi_import_edgelist_attrs_file_dot(thread, "test_dot_edgelist.dot", NULL, edge_attribute, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 3);
    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_import_edgelist_noattrs_file_dot(thread, "test_dot_edgelist.dot", &edgelist);
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
