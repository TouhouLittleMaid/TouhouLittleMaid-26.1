package com.github.tartaricacid.touhoulittlemaid.util.functional;

@FunctionalInterface
public interface TriPredicate<T, U, V> {
    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return {@code true} if the input arguments match the predicate,
     * otherwise {@code false}
     */
    boolean test(T t, U u, V v);

    /**
     * Returns a composed {@code TriPredicate} that represents a short-circuiting logical AND of this
     * predicate and another. When evaluating the composed predicate, if this predicate is {@code false},
     * then the {@code other} predicate is not evaluated.
     *
     * @param other a predicate that will be logically-ANDed with this predicate
     * @return a composed {@code TriPredicate} that represents the short-circuiting logical AND of this
     * predicate and the {@code other} predicate
     */
    default TriPredicate<T, U, V> and(TriPredicate<? super T, ? super U, ? super V> other) {
        return (t, u, v) -> test(t, u, v) && other.test(t, u, v);
    }
}
