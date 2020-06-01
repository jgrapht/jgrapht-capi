#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void * map;
    jgrapht_capi_map_linked_create(thread, &map);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    int exists;
    assert(jgrapht_capi_map_int_contains_key(thread, map, 4, &exists) == 0);
    assert(exists == 0);
    assert(jgrapht_capi_map_int_contains_key(thread, map, 5, &exists) == 0);
    assert(exists == 0);
    int size;
    assert(jgrapht_capi_map_size(thread, map, &size) == 0);
    assert(size == 0);

    double dvalue;
    assert(jgrapht_capi_map_int_double_get(thread, map, 5, &dvalue) == STATUS_ILLEGAL_ARGUMENT);
    assert(jgrapht_capi_error_get_errno(thread) == STATUS_ILLEGAL_ARGUMENT);
    jgrapht_capi_error_clear_errno(thread);
    assert(jgrapht_capi_error_get_errno(thread) == STATUS_SUCCESS);

    int i;
    for(i = 0; i < 1000; i++) { 
        jgrapht_capi_map_int_double_put(thread, map, i, 1000+i);
        assert(jgrapht_capi_error_get_errno(thread) == 0);
    }

    for(i = 0; i < 1000; i++) { 
        assert(jgrapht_capi_map_int_double_get(thread, map, i, &dvalue) == 0);
        assert(dvalue == 1000+i);
        assert(jgrapht_capi_error_get_errno(thread) == 0);
        int exists;
        assert(jgrapht_capi_map_int_contains_key(thread, map, i, &exists) == 0);
        assert(exists == 1);
        assert(jgrapht_capi_error_get_errno(thread) == 0);
    }

    long n = 0;
    void * kit;
    jgrapht_capi_map_keys_it_create(thread, map, &kit);
    int hasnext;
    while(1) { 
        jgrapht_capi_it_hasnext(thread, kit, &hasnext);
        if (!hasnext) { 
            break;
        }
        int k;
        jgrapht_capi_it_next_int(thread, kit, &k);
        assert(k == n);
        n++;
    }
    jgrapht_capi_handles_destroy(thread, kit);

    n = 0;
    jgrapht_capi_map_values_it_create(thread, map, &kit);
    while(1) { 
        jgrapht_capi_it_hasnext(thread, kit, &hasnext);
        if (!hasnext) { 
            break;
        }
        double v;
        jgrapht_capi_it_next_double(thread, kit, &v);
        assert(v == 1000+n);
        n++;
    }
    jgrapht_capi_handles_destroy(thread, kit);

    int map_size;
    assert(jgrapht_capi_map_size(thread, map, &map_size) == 0);
    assert(map_size == 1000);

    jgrapht_capi_map_clear(thread, map);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(jgrapht_capi_map_size(thread, map, &map_size) == 0);
    assert(map_size == 0);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_map_int_double_put(thread, map, 100, 150.0);
    double v;
    jgrapht_capi_map_int_double_get(thread, map, 100, &v);
    assert(v == 150.0);
    jgrapht_capi_map_int_double_remove(thread, map, 100, &v);
    assert(v == 150.0);
    assert(jgrapht_capi_map_int_double_remove(thread, map, 100, &v) == STATUS_ILLEGAL_ARGUMENT);
    jgrapht_capi_error_clear_errno(thread);

    jgrapht_capi_handles_destroy(thread, map);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    // test with string as value
    jgrapht_capi_map_linked_create(thread, &map);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    char *text = "hello world";
    jgrapht_capi_map_int_string_put(thread, map, 1, text);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    assert(jgrapht_capi_map_size(thread, map, &map_size) == 0);
    assert(map_size == 1);

    void *val;
    char *val_char;
    jgrapht_capi_map_int_string_get(thread, map, 1, &val);
    jgrapht_capi_handles_get_ccharpointer(thread, val, &val_char);
    jgrapht_capi_handles_destroy(thread, val);
    assert (strcmp(val_char, "hello world")==0);

    jgrapht_capi_handles_destroy(thread, map);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
