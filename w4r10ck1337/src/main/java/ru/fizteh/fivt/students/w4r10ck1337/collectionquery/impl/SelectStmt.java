package ru.fizteh.fivt.students.w4r10ck1337.collectionquery.impl;

import java.io.PrintStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by kormushin on 06.10.15.
 */
public class SelectStmt<T, R> implements Query<R> {
    private List<T> objects;
    private List<R> prevResult, result;
    private Predicate<T> wherePredicate;
    private Function<T, ?>[] groupByFunctions;
    private Predicate<R>  havingPredicate;
    private Comparator<R>[] comparators;
    private boolean isDistinct;
    int limit;

    public SelectStmt(ArrayList<T> objects) {
        this.objects = objects;
    }

    @SafeVarargs
    public SelectStmt(Function<T, R>... s) {
        throw new UnsupportedOperationException();
    }

    public SelectStmt<T, R> where(Predicate<T> predicate) {
        this.wherePredicate = predicate;
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> groupBy(Function<T, ?>... expressions) {
        this.groupByFunctions = expressions;
        return this;
    }

    @SafeVarargs
    public final SelectStmt<T, R> orderBy(Comparator<R>... comparators) {
        this.comparators = comparators;
        return this;
    }

    public SelectStmt<T, R> having(Predicate<R> condition) {
        this.havingPredicate = condition;
        return this;
    }

    public SelectStmt<T, R> limit(int amount) {
        limit = amount;
        return this;
    }

    public UnionStmt union() {
        return new UnionStmt(this.execute());
    }

    private void applyWhere() {
        if (wherePredicate == null) {
            return;
        }
        List<T> filtered = new ArrayList<>();
        objects.stream().filter(wherePredicate::test).forEach(filtered::add);
        objects = filtered;
    }

    private void applyGroupBy() {

    }

    private void createNewObjects() {

    }

    private void applyHaving() {
        List<R> filtered = new ArrayList<>();
        result.stream().filter(havingPredicate::test).forEach(filtered::add);
        result = filtered;
    }

    private void deleteDuplicates() {
        if (!isDistinct) {
            return;
        }
        Set<Integer> hashes = new HashSet<>();
        List<R> filtered = new ArrayList<>();
        for (R object : result) {
            if (!hashes.contains(object.hashCode())) {
                filtered.add(object);
                hashes.add(object.hashCode());
            }
        }
        result = filtered;
    }

    private void orderBy() {
        Comparator<R> comparator = new Comparator<R>() {
            @Override
            public int compare(R o1, R o2) {
                for (Comparator<R> cmp : comparators) {
                    if (cmp.compare(o1, o2) != 0) {
                        return cmp.compare(o1, o2);
                    }
                }
                return 0;
            }
        };
        if (comparators != null) {
            result.sort(comparator);
        }
    }

    private void applyLimit() {
        if (limit == -1) {
            return;
        }
        while (result.size() > limit) {
            result.remove(result.size() - 1);
        }
    }

    private void applyUnion() {
        if (prevResult != null) {
            prevResult.addAll(result);
            result = prevResult;
        }
    }

    @Override
    public Iterable<R> execute() {
        List<R> result = new ArrayList<>();
        applyWhere();
        applyGroupBy();
        createNewObjects();
        deleteDuplicates();
        applyUnion();
        return result;
    }

    @Override
    public Stream<R> stream() {
        throw new UnsupportedOperationException();
    }
}
