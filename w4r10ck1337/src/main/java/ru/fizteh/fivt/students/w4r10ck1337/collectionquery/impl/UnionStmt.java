package ru.fizteh.fivt.students.w4r10ck1337.collectionquery.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kormushin on 09.10.15.
 */
public class UnionStmt {
    private List<Object> prevResult = new ArrayList<>();

    <R> UnionStmt(Iterable<R> old) {
        old.forEach(t -> prevResult.add(t));
    }
    public <T> FromStmt<T> from(Iterable<T> list) {
        return new FromStmt<>(list.iterator(), prevResult);
    }
}
