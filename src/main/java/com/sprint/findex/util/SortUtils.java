package com.sprint.findex.util;

import org.springframework.data.domain.Sort;

public class SortUtils {

    public static Sort.Direction directionOf(String direction) {
        return "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

}
