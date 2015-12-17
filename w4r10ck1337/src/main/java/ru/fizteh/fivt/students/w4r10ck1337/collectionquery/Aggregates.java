package ru.fizteh.fivt.students.w4r10ck1337.collectionquery;

import java.util.Collection;
import java.util.function.Function;

/**
 * Aggregate functions.
 *
 * @author akormushin
 */
public class Aggregates {

    public abstract static class Aggregator<C, T> implements Function<C, T> {
        public abstract T apply(Collection<C> collection);

        @Override
        public T apply(C c) {
            return null;
        }
    }

    /**
     * Maximum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable<T>> Function<C, T> max(Function<C, T> expression) {
        return new Aggregator<C, T>() {
            @Override
            public T apply(Collection<C> collection) {
                T max = null;
                for (C element : collection) {
                    T curr = expression.apply(element);
                    if (max == null) {
                        max = curr;
                    }
                    if (max.compareTo(curr) < 0) {
                        max = curr;
                    }
                }
                return max;
            }
        };
    }

    /**
     * Minimum value for expression for elements of given collecdtion.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Comparable<T>> Function<C, T> min(Function<C, T> expression) {
        return new Aggregator<C, T>() {
            @Override
            public T apply(Collection<C> collection) {
                T min = null;
                for (C element : collection) {
                    T curr = expression.apply(element);
                    if (min == null) {
                        min = curr;
                    }
                    if (min.compareTo(curr) > 0) {
                        min = curr;
                    }
                }
                return min;
            }
        };
    }

    /**
     * Number of items in source collection that turns this expression into not null.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T> Function<C, Long> count(Function<C, T> expression) {
        return new Aggregator<C, Long>() {
            @Override
            public Long apply(Collection<C> collection) {
                long count = 0;
                for (C element : collection) {
                    if (expression.apply(element) != null) {
                        count++;
                    }
                }
                return count;
            }
        };
    }

    /**
     * Average value for expression for elements of given collection.
     *
     * @param expression
     * @param <C>
     * @param <T>
     * @return
     */
    public static <C, T extends Number> Function<C, Double> avg(Function<C, T> expression) {
        return new Aggregator<C, Double>() {
            @Override
            public Double apply(Collection<C> collection) {
                Double sum = 0.0;
                for (C element : collection) {
                    sum += expression.apply(element).doubleValue();
                }
                return sum / collection.size();
            }
        };
    }

}
