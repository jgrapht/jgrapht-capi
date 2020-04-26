#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi.h>

#define ITERATOR_NO_SUCH_ELEMENT 100

int check_tour(graal_isolatethread_t *thread, void *tour, double expected_weight) {

    double weight;
    long long start_vertex;
    long long end_vertex;

    jgrapht_capi_graphpath_get_fields(thread, tour, &weight, &start_vertex, &end_vertex, NULL);
    printf("%lf\n", weight);
    assert(weight == expected_weight);
    assert(start_vertex == end_vertex);

    // here we might actually check the cycle
    // for now we only check the API

    return 1;
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
    jgrapht_capi_graph_create(thread, 0, 0, 0, 1, &g);
    assert(jgrapht_capi_get_errno(thread) == 0);

    jgrapht_capi_generate_complete(thread, g, 8);

    // run 
    void *tour;
    assert(jgrapht_capi_tour_tsp_greedy_heuristic(thread, g, &tour) == 0);
    assert(check_tour(thread, tour, 8.0));
    jgrapht_capi_destroy(thread,  tour);


    // TODO: test the rest of the API


    jgrapht_capi_destroy(thread, g);

    if (thread, graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
