package ru.fizteh.fivt.students.w4r10ck1337.collectionquery.impl;

import ru.fizteh.fivt.students.w4r10ck1337.collectionquery.impl.exceptions.InvalidQueryException;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

public class FromStmt<T> {
    private List<T> objects = new ArrayList<>();
    private List<Object> prevResult;

    FromStmt(Iterator<T> iterator, List<Object> prevResult) {
        this.prevResult = prevResult;
        iterator.forEachRemaining(objects::add);
    }

    public static <T> FromStmt<T> from(Iterable<T> iterable) {
        return new FromStmt<>(iterable.iterator(), null);
    }

    public static <T> FromStmt<T> from(Stream<T> stream) {
        return new FromStmt<>(stream.iterator(), null);
    }

    @SuppressWarnings("unchecked")
    public static <T> FromStmt<T> from(Query query) throws InvalidQueryException {
        return new FromStmt<>(query.execute().iterator(), null);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> select(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(prevResult, objects, false, clazz, s);
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param s
     * @param <R>
     * @return statement resulting in collection of R
     */
    public final <R> SelectStmt<T, R> select(Function<T, R> s) throws InvalidQueryException {
        //Dirty?
        Class resultClass = null;
        if (!objects.isEmpty()) {
            resultClass = s.apply(objects.get(0)).getClass();
        } else {
            throw new InvalidQueryException("from statement is empty");
        }
        //
        return new SelectStmt(prevResult, objects, false, resultClass, s);
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param first
     * @param second
     * @param <F>
     * @param <S>
     * @return statement resulting in collection of R
     */
    public final <F, S> SelectStmt<T, Tuple<F, S>> select(Function<T, F> first, Function<T, S> second) {
        return new SelectStmt<>(prevResult, objects, false, null, first, second);
    }

    @SafeVarargs
    public final <R> SelectStmt<T, R> selectDistinct(Class<R> clazz, Function<T, ?>... s) {
        return new SelectStmt<>(prevResult, objects, true, clazz, s);
    }

    /**
     * Selects the only defined expression as is without wrapper.
     *
     * @param s
     * @param <R>
     * @return statement resulting in collection of R
     */
    public final <R> SelectStmt<T, R> selectDistinct(Function<T, R> s) throws InvalidQueryException {
        //Dirty?
        Class resultClass = null;
        if (!objects.isEmpty()) {
            resultClass = s.apply(objects.get(0)).getClass();
        } else {
            throw new InvalidQueryException("From statement is empty");
        }
        //
        return new SelectStmt(prevResult, objects, true, resultClass, s);
    }

    public <J> JoinClause<T, J> join(Iterable<J> iterable) {
        return new JoinClause<>(objects, iterable.iterator());
    }

    public <J> JoinClause<T, J> join(Stream<J> stream) {
        return new JoinClause<>(objects, stream.iterator());
    }

    public <J> JoinClause<T, J> join(Query<J> stream) throws InvalidQueryException {
        return new JoinClause<>(objects, stream.execute().iterator());
    }

    public class JoinClause<T, J> {
        private List<T> left;
        private Iterator<J> right;

        public JoinClause(List<T> left, Iterator<J> right) {
            this.left = left;
            this.right = right;
        }

        public FromStmt<Tuple<T, J>> on(BiPredicate<T, J> condition) {
            List<Tuple<T, J>> objects = new ArrayList<>();
            for (T first : left) {
                right.forEachRemaining(second -> {
                    if (condition.test(first, second)) {
                        objects.add(new Tuple<>(first, second));
                    }
                });
            }
            return new FromStmt<>(objects.iterator(), null);
        }
        public <K extends Comparable<?>> FromStmt<Tuple<T, J>> on(
                Function<T, K> leftKey,
                Function<J, K> rightKey) {
            List<Tuple<T, J>> objects = new ArrayList<>();
            HashMap<K, List<J>> map = new HashMap<>();
            right.forEachRemaining(second -> {
                K key = rightKey.apply(second);
                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<>());
                }
                map.get(key).add(second);
            });

            for (T first : left) {
                K key = leftKey.apply(first);
                if (map.containsKey(key)) {
                    map.get(key).forEach(t -> objects.add(new Tuple<>(first, t)));
                }
            }
            return new FromStmt<>(objects.iterator(), null);
        }
    }
}
