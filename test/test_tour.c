#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jgrapht_capi_types.h>
#include <jgrapht_capi.h>


int check_tour(graal_isolatethread_t *thread, void *tour, double expected_weight) {

    double weight;
    int start_vertex;
    int end_vertex;

    jgrapht_capi_handles_get_graphpath(thread, tour, &weight, &start_vertex, &end_vertex, NULL);
    //printf("%lf\n", weight);
    assert(weight == expected_weight);
    assert(start_vertex == end_vertex);

    // here we might actually check the cycle
    // for now we only check the API

    return 1;
}

int main() {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        exit(EXIT_FAILURE);
    }

    assert(jgrapht_capi_error_get_errno(thread) == 0);

    void *g;
    jgrapht_capi_graph_create(thread, 0, 0, 0, 1, &g);
    assert(jgrapht_capi_error_get_errno(thread) == 0);

    jgrapht_capi_generate_complete(thread, g, 8);

    // run 
    void *tour;
    assert(jgrapht_capi_tour_tsp_greedy_heuristic(thread, g, &tour) == 0);
    assert(check_tour(thread, tour, 8.0));
    jgrapht_capi_handles_destroy(thread,  tour);

    assert(jgrapht_capi_tour_tsp_nearest_insertion_heuristic(thread, g, &tour) == 0);
    assert(check_tour(thread, tour, 8.0));
    jgrapht_capi_handles_destroy(thread,  tour);

    assert(jgrapht_capi_tour_tsp_nearest_neighbor_heuristic(thread, g, 17, &tour) == 0);
    assert(check_tour(thread, tour, 8.0));
    jgrapht_capi_handles_destroy(thread,  tour);

    assert(jgrapht_capi_tour_tsp_random(thread, g, 17, &tour) == 0);
    assert(check_tour(thread, tour, 8.0));
    jgrapht_capi_handles_destroy(thread,  tour);

    assert(jgrapht_capi_tour_metric_tsp_christofides(thread, g, &tour) == 0);
    assert(check_tour(thread, tour, 8.0));
    jgrapht_capi_handles_destroy(thread,  tour);

    assert(jgrapht_capi_tour_metric_tsp_two_approx(thread, g, &tour) == 0);
    assert(check_tour(thread, tour, 8.0));
    jgrapht_capi_handles_destroy(thread,  tour);

    assert(jgrapht_capi_tour_tsp_held_karp(thread, g, &tour) == 0);
    assert(check_tour(thread, tour, 8.0));
    jgrapht_capi_handles_destroy(thread,  tour);

    // TODO: test the rest of the API
    // missing: hamiltonian_palmer
    // missing: two_opt_heuristic
    // missing: two_opt_heuristic_improve

    jgrapht_capi_handles_destroy(thread, g);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}
