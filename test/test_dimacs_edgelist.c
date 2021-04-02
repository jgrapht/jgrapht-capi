#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef _WIN32
#include <crtdbg.h>
#endif 
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

char *input="c\nc SOURCE: Generated using the JGraphT library\nc\np edge 4 4\ne 1 2\ne 2 3\ne 3 4\ne 4 1\n";

void write_to_file(char* filename, char *str) { 
    FILE* fp = fopen(filename, "w");
    fprintf(fp, "%s", str);
    fclose(fp);
}

void any_attribute(char *v, char *key, char *value) { 
    // nothing    
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



    // test read from string with extra attributes
    void *edgelist;
    jgrapht_capi_ii_import_edgelist_attrs_string_dimacs(thread, input, NULL, NULL, 
        &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int count = 0;
    jgrapht_capi_x_list_size(thread, edgelist, &count);
    assert (count == 4);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);


    // no attrs
    jgrapht_capi_xx_import_edgelist_noattrs_string_dimacs(thread, input, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    count = 0;
    jgrapht_capi_x_list_size(thread, edgelist, &count);
    assert (count == 4);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // write to tmp file and read back
    write_to_file("test_dimacs_edgelist.dimacs", input);

    jgrapht_capi_ii_import_edgelist_attrs_file_dimacs(thread, "test_dimacs_edgelist.dimacs", any_attribute, NULL, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    count = 0;
    jgrapht_capi_x_list_size(thread, edgelist, &count);
    assert (count == 4);
    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);


    jgrapht_capi_xx_import_edgelist_noattrs_file_dimacs(thread, "test_dimacs_edgelist.dimacs", &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    count = 0;
    jgrapht_capi_x_list_size(thread, edgelist, &count);
    assert (count == 4);
    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);



    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
