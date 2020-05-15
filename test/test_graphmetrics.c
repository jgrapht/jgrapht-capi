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

    void *g;
    assert(jgrapht_capi_graph_create(thread, 0, 1, 1, 1, &g) == 0);

    int v;
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    jgrapht_capi_graph_add_vertex(thread, g, &v);
    jgrapht_capi_graph_add_vertex(thread, g, &v);

    jgrapht_capi_graph_add_edge(thread, g, 0, 1, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 1, 2, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 2, 3, NULL);
    jgrapht_capi_graph_add_edge(thread, g, 3, 4, NULL);

    double diameter;
    jgrapht_capi_graph_metrics_diameter(thread, g, &diameter);

    double radius;
    jgrapht_capi_graph_metrics_radius(thread, g, &radius);

    int girth;
    jgrapht_capi_graph_metrics_girth(thread, g, &girth);

    long long triangles;
    jgrapht_capi_graph_metrics_triangles(thread, g, &triangles);

    // test measure

    void *center = NULL, *periphery = NULL, *pseudoPeriphery = NULL, *eccentricityMap = NULL;
    jgrapht_capi_graph_metrics_measure_graph(thread, g, &diameter, &radius, &center, &periphery, &pseudoPeriphery, &eccentricityMap);
    assert(center);
    assert(periphery);
    assert(pseudoPeriphery);
    assert(eccentricityMap);

    assert(jgrapht_capi_handles_destroy(thread, center) == 0);
    assert(jgrapht_capi_handles_destroy(thread, periphery) == 0);
    assert(jgrapht_capi_handles_destroy(thread, pseudoPeriphery) == 0);
    assert(jgrapht_capi_handles_destroy(thread, eccentricityMap) == 0);

    assert(jgrapht_capi_handles_destroy(thread, g) == 0);

    if (graal_detach_thread(thread) != 0) {
        fprintf(stderr, "graal_detach_thread error\n");
        exit(EXIT_FAILURE);
    }

    return EXIT_SUCCESS;
}