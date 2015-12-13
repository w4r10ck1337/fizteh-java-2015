package ru.fizteh.fivt.students.w4r10ck1337.collectionquery.impl;

import java.util.stream.Stream;

/**
 * @author akormushin
 */
public interface Query<R> {

    Iterable<R> execute();

    Stream<R> stream();
}
