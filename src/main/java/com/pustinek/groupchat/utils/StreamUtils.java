package com.pustinek.groupchat.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class StreamUtils {
    public static <T> Collector<T, ?, T> singletonCollector() {
        return getCollectorFunction(getListTSingleFunction());
    }

    private static <T> Collector<T, ?, T> getCollectorFunction(Function<List<T>, T> listTFunction) {
        return Collectors.collectingAndThen(Collectors.toList(), listTFunction);
    }


    private static <T> Function<List<T>, T> getListTSingleFunction() {
        return list -> {
            if (list.size() != 1) {
                throw new IllegalStateException();
            }
            return list.get(0);
        };
    }
}
