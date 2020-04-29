#ifndef __JGRAPHT_CAPI_TYPES_H
#define __JGRAPHT_CAPI_TYPES_H

typedef enum { 
    SUCCESS = 0,
    ERROR,
    ILLEGAL_ARGUMENT,
    UNSUPPORTED_OPERATION,
    INDEX_OUT_OF_BOUNDS,
    NO_SUCH_ELEMENT,
    NULL_POINTER,
    CLASS_CAST,
    IO_ERROR,
    EXPORT_ERROR,
} status_t;


typedef enum {
    SHORTEST_PATH = 0,
    MAX_CLIQUE,
    COLORING,
} dimacs_format_t;

#endif

