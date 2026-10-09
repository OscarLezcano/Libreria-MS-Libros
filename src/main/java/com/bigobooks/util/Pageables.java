package com.bigobooks.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class Pageables {

    private Pageables() {
    }

    public static Pageable of(Integer page, Integer size, int defaultPageSize, int maxPageSize) {
        int pageNo = page == null ? 0 : page;
        int pageSize = Math.min(size == null ? defaultPageSize : size, maxPageSize);
        return PageRequest.of(pageNo, pageSize);
    }
}
