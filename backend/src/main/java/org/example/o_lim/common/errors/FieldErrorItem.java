package org.example.o_lim.common.errors;

public record FieldErrorItem(
        String field,
        String rejected,
        String message
) {}
