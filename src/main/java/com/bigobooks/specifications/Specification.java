package com.bigobooks.specifications;

public abstract class Specification<T> {

    public abstract void validateCreate(T dto);

    public abstract void validateUpdate(T dto);

    protected void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " es obligatorio y no puede estar vacio");
        }
    }

    protected void requireNotNull(Object value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " es obligatorio");
        }
    }

    protected void requireId(Long value, String field) {
        requireNotNull(value, field);
        inRange(1L, null, value);
    }

    protected <V extends Comparable<V>> void inRange(V min, V max, V value) {
        if (value == null) {
            throw new IllegalArgumentException("El valor no puede ser nulo.");
        }

        if (min != null && value.compareTo(min) < 0) {
            throw new IllegalArgumentException(
                    "El valor " + value + " es menor que el mínimo permitido (" + min + ").");
        }

        if (max != null && value.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                    "El valor " + value + " es mayor que el máximo permitido (" + max + ").");
        }
    }
}
