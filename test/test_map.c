#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi.h>

#define ILLEGAL_ARGUMENT 2

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (thread, graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_get_errno(thread) == 0);

    void * map;
    jgrapht_capi_map_linked_create(thread, &map);
    assert(jgrapht_capi_get_errno(thread) == 0);

    int exists;
    assert(jgrapht_capi_map_long_contains_key(thread, map, 4, &exists) == 0);
    assert(exists == 0);
    assert(jgrapht_capi_map_long_contains_key(thread, map, 5, &exists) == 0);
    assert(exists == 0);
    long long size;
    assert(jgrapht_capi_map_size(thread, map, &size) == 0);
    assert(size == 0);

    double dvalue;
    assert(jgrapht_capi_map_long_double_get(thread, map, 5, &dvalue) == ILLEGAL_ARGUMENT);
    assert(jgrapht_capi_get_errno(thread) == ILLEGAL_ARGUMENT);
    jgrapht_capi_clear_errno(thread);
    assert(jgrapht_capi_get_errno(thread) == 0);

    for(int i = 0; i < 1000; i++) { 
        jgrapht_capi_map_long_double_put(thread, map, i, 1000+i);
        assert(jgrapht_capi_get_errno(thread) == 0);
    }

    for(int i = 0; i < 1000; i++) { 
        assert(jgrapht_capi_map_long_double_get(thread, map, i, &dvalue) == 0);
        assert(dvalue == 1000+i);
        assert(jgrapht_capi_get_errno(thread) == 0);
        int exists;
        assert(jgrapht_capi_map_long_contains_key(thread, map, i, &exists) == 0);
        assert(exists == 1);
        assert(jgrapht_capi_get_errno(thread) == 0);
    }

    long n = 0;
    void * kit;
    jgrapht_capi_map_keys_it_create(thread, map, &kit);
    while(jgrapht_capi_it_hasnext(thread, kit)) { 
        long k = jgrapht_capi_it_next_long(thread, kit);
        assert(k == n);
        n++;
    }
    jgrapht_capi_destroy(thread, kit);

    n = 0;
    jgrapht_capi_map_values_it_create(thread, map, &kit);
    while(jgrapht_capi_it_hasnext(thread, kit)) { 
        double v = jgrapht_capi_it_next_double(thread, kit);
        assert(v == 1000+n);
        n++;
    }
    jgrapht_capi_destroy(thread, kit);

    long long map_size;
    assert(jgrapht_capi_map_size(thread, map, &map_size) == 0);
    assert(map_size == 1000);

    jgrapht_capi_map_clear(thread, map);
    assert(jgrapht_capi_get_errno(thread) == 0);
    assert(jgrapht_capi_map_size(thread, map, &map_size) == 0);
    assert(map_size == 0);
    assert(jgrapht_capi_get_errno(thread) == 0);

    jgrapht_capi_destroy(thread, map);
    assert(jgrapht_capi_get_errno(thread) == 0);
   
    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}