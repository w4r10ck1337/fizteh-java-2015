package ru.fizteh.fivt.students.w4r10ck1337.collectionquery.impl;

import ru.fizteh.fivt.students.w4r10ck1337.collectionquery.impl.exceptions.InvalidQueryException;

import java.util.stream.Stream;

/**
 * @author akormushin
 */
public interface Query<R> {

    Iterable<R> execute() throws InvalidQueryException;

    Stream<R> stream() throws InvalidQueryException;
}
