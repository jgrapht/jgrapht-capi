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

    void *list;
    jgrapht_capi_list_create(thread, &list);

    int exists;
    jgrapht_capi_list_int_contains(thread, list, 4, &exists);
    assert(exists == 0);

    int did_not_exist;
    jgrapht_capi_list_int_add(thread, list, 4, &did_not_exist);
    assert(did_not_exist);
    jgrapht_capi_list_int_contains(thread, list, 4, &exists);
    assert(exists == 1);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_list_int_add(thread, list, 100, &did_not_exist);
    assert(did_not_exist);
    jgrapht_capi_list_int_contains(thread, list, 100, &exists);
    assert(exists == 1);

    jgrapht_capi_list_int_add(thread, list, 500, &did_not_exist);
    assert(did_not_exist);
    jgrapht_capi_list_int_contains(thread, list, 500, &exists);
    assert(exists == 1);

    int size;
    jgrapht_capi_list_size(thread, list, &size);
    assert(size == 3);

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_list_int_remove(thread, list, 500);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
    jgrapht_capi_list_int_contains(thread, list, 500, &exists);
    assert(exists == 0);

    jgrapht_capi_list_size(thread, list, &size);
    assert(size == 2);

    void * it;
    int elem;
    jgrapht_capi_list_it_create(thread, list, &it);
    jgrapht_capi_it_next_int(thread, it, &elem);
    assert(elem == 4);
    jgrapht_capi_it_next_int(thread, it, &elem);
    assert(elem == 100);
    int hasnext;
    jgrapht_capi_it_hasnext(thread, it, &hasnext);
    assert(hasnext == 0);
    jgrapht_capi_handles_destroy(thread, it);

    jgrapht_capi_list_clear(thread, list);
    jgrapht_capi_list_size(thread, list, &size);
    assert(size == 0);

    jgrapht_capi_handles_destroy(thread, list);
    assert(jgrapht_capi_error_get_errno(thread) == 0);
   
    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}