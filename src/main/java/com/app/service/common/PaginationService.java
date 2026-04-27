package com.app.service.common;

import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PaginationService {

    public <T> Page<T> getPage(Function<Pageable, Page<T>> finder, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return finder.apply(pageable);
    }
}

