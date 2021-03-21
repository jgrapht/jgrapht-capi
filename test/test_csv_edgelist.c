#include <stdio.h>
#include <stdlib.h>

#ifdef _WIN32
#include <crtdbg.h>
#endif 
#include <assert.h>

#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

char *expected="0,1,3\n1,0,2\n3,0,2\n2,1,3\n";

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
    jgrapht_capi_ii_import_edgelist_attrs_string_csv(thread, expected, NULL, NULL, CSV_FORMAT_ADJACENCY_LIST, 0, 0, 0, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 8);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    
    // no attrs
    jgrapht_capi_xx_import_edgelist_noattrs_string_csv(thread, expected, CSV_FORMAT_ADJACENCY_LIST, 0, 0, 0, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 8);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);


    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
