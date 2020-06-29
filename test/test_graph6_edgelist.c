#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

char *expected= "G?qa`_";

void write_to_file(char* filename, char *str) { 
    FILE* fp = fopen(filename, "w");
    fprintf(fp, "%s", str);
    fclose(fp);
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
    jgrapht_capi_import_edgelist_attrs_string_graph6sparse6(thread, expected, NULL, NULL, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 8);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    
    // no attrs
    jgrapht_capi_import_edgelist_noattrs_string_graph6sparse6(thread, expected, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 8);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // write to tmp file and read back
    write_to_file("test_edgelist.g6", expected);

    jgrapht_capi_import_edgelist_attrs_file_graph6sparse6(thread, "test_edgelist.g6", NULL, NULL, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 8);
    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_import_edgelist_noattrs_file_graph6sparse6(thread, "test_edgelist.g6", &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 8);
    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);


    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
