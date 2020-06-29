#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

char *expected="\
Creator \"JGraphT GML Exporter\"\n\
Version 1\n\
graph\n\
[\n\
	label \"\"\n\
	directed 0\n\
	node\n\
	[\n\
		id 0\n\
		label \"label 0\"\n\
	]\n\
	node\n\
	[\n\
		id 1\n\
		label \"label 1\"\n\
	]\n\
	node\n\
	[\n\
		id 2\n\
		label \"label 2\"\n\
	]\n\
	edge\n\
	[\n\
		source 0\n\
		target 1\n\
	]\n\
	edge\n\
	[\n\
		source 1\n\
		target 2\n\
	]\n\
]\n";


void write_to_file(char* filename, char *str) { 
    FILE* fp = fopen(filename, "w");
    fprintf(fp, "%s", str);
    fclose(fp);
}

void vertex_attribute(char *v, char *key, char *value) { 
    if (strcmp(v, "0") == 0) { 
        if (strcmp(key, "label") == 0) { 
            assert(strcmp(value, "label 0") == 0);
        }
    }
    if (strcmp(v, "1") == 0) { 
        if (strcmp(key, "label") == 0) { 
            assert(strcmp(value, "label 1") == 0);
        }
    }
    if (strcmp(v, "2") == 0) { 
        if (strcmp(key, "label") == 0) { 
            assert(strcmp(value, "label 2") == 0);
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
    jgrapht_capi_import_edgelist_attrs_string_gml(thread, expected, NULL, NULL, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 2);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    
    // no attrs
    jgrapht_capi_import_edgelist_noattrs_string_gml(thread, expected, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 2);

    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // write to tmp file and read back
    
    write_to_file("test_gml_edgelist.gml", expected);

    jgrapht_capi_import_edgelist_attrs_file_gml(thread, "test_gml_edgelist.gml", vertex_attribute, NULL, &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 2);
    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_import_edgelist_noattrs_file_gml(thread, "test_gml_edgelist.gml", &edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    count = 0;
    jgrapht_capi_list_size(thread, edgelist, &count);
    assert (count == 2);
    jgrapht_capi_handles_destroy(thread, edgelist);
    assert(jgrapht_capi_error_get_errno(thread) == 0);


    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
