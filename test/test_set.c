#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>


int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    void * set;
    jgrapht_capi_set_linked_create(thread, &set);

    int exists;
    jgrapht_capi_set_int_contains(thread, set, 4, &exists);
    assert(exists == 0);

    int did_not_exist;
    jgrapht_capi_set_int_add(thread, set, 4, &did_not_exist);
    assert(did_not_exist);
    jgrapht_capi_set_int_contains(thread, set, 4, &exists);
    assert(exists == 1);

    jgrapht_capi_set_int_add(thread, set, 100, &did_not_exist);
    assert(did_not_exist);
    jgrapht_capi_set_int_contains(thread, set, 100, &exists);
    assert(exists == 1);

    jgrapht_capi_set_int_add(thread, set, 500, &did_not_exist);
    assert(did_not_exist);
    jgrapht_capi_set_int_contains(thread, set, 500, &exists);
    assert(exists == 1);

    int size;
    jgrapht_capi_set_size(thread, set, &size);
    assert(size == 3);

    jgrapht_capi_set_int_remove(thread, set, 500);
    jgrapht_capi_set_int_contains(thread, set, 500, &exists);
    assert(exists == 0);

    jgrapht_capi_set_size(thread, set, &size);
    assert(size == 2);

    void * it;
    int elem;
    jgrapht_capi_set_it_create(thread, set, &it);
    jgrapht_capi_it_next_int(thread, it, &elem);
    assert(elem == 4);
    jgrapht_capi_it_next_int(thread, it, &elem);
    assert(elem == 100);
    int hasnext;
    jgrapht_capi_it_hasnext(thread, it, &hasnext);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, it);

    jgrapht_capi_set_clear(thread, set);
    jgrapht_capi_set_size(thread, set, &size);
    assert(size == 0);

    jgrapht_capi_handles_destroy(thread, set);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
   
    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}