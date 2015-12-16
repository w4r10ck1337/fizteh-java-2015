package ru.fizteh.fivt.students.w4r10ck1337.collectionquery.impl;

import ru.fizteh.fivt.students.w4r10ck1337.collectionquery.Aggregates;
import ru.fizteh.fivt.students.w4r10ck1337.collectionquery.impl.exceptions.CreateResultObjectException;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by kormushin on 06.10.15.
 */
public class SelectStmt<T, R> implements Query<R> {
    private Class resultClass;
    private Function[] resultFunctions;
    private List<T> objects;
    private List<List<T>> groups = new ArrayList<>();
    private List<R> prevResult, result = new ArrayList<>();
    private Predicate<T> wherePredicate;
    private Function<T, ?>[] groupByFunctions;
    private Predicate<R>  havingPredicate;
    private Comparator<R>[] comparators;
    private boolean isDistinct;
    private int limit = -1;

    @SafeVarargs
    public SelectStmt(List<R> prevResult, List<T> objects,
                      boolean isDistinct, Class<R> resultClass, Function<T, ?>... s) {
        this.prevResult = prevResult;
        this.objects = objects;
        this.resultClass = resultClass;
        this.resultFunctions = s;
        this.isDistinct = isDistinct;
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

    public UnionStmt union() throws CreateResultObjectException {
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
        if (groupByFunctions == null || groupByFunctions.length == 0) {
            for (T object : objects) {
                ArrayList<T> newList = new ArrayList<>();
                newList.add(object);
                groups.add(newList);
            }
        } else {
            Map<Integer, ArrayList<T>> mapGroups = new HashMap<>();
            for (T object : objects) {
                Object[] result = new Object[groupByFunctions.length];
                for (int i = 0; i < groupByFunctions.length; i++) {
                    result[i] = groupByFunctions[i].apply(object);
                }
                int hash = Objects.hash(result);
                if (!mapGroups.containsKey(hash)) {
                    mapGroups.put(hash, new ArrayList<>());
                }
                mapGroups.get(hash).add(object);
            }
            mapGroups.keySet().forEach(t -> groups.add(mapGroups.get(t)));
        }
    }

    private void createNewObjects() throws CreateResultObjectException {
        for (List<T> group : groups) {
            Object[] args = new Object[resultFunctions.length];
            Class[] classes = new Class[resultFunctions.length];
            for (int i = 0; i < resultFunctions.length; i++) {
                if (resultFunctions[i] instanceof Aggregates.Aggregator) {
                    args[i] = resultFunctions[i].apply(group);
                } else {
                    args[i] = resultFunctions[i].apply(group.get(0));
                }
                classes[i] = args[i].getClass();
            }
            try {
                result.add((R) resultClass.getConstructor(classes).newInstance(args));
            } catch (Exception e) {
                throw new CreateResultObjectException();
            }
        }
    }

    private void applyHaving() {
        if (havingPredicate == null) {
            return;
        }
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

    private void applyOrderBy() {
        if (comparators == null) {
            return;
        }
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
        result.sort(comparator);
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
    public Iterable<R> execute() throws CreateResultObjectException {
        applyWhere();
        applyGroupBy();
        createNewObjects();
        applyHaving();
        deleteDuplicates();
        applyOrderBy();
        applyLimit();
        applyUnion();
        return result;
    }

    @Override
    public Stream<R> stream() {
        throw new UnsupportedOperationException();
    }
}
