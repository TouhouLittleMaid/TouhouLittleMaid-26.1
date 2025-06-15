package com.github.tartaricacid.touhoulittlemaid.util.functional;

@FunctionalInterface
public interface QuadPredicate<T, U, V, W> {
    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @param w the fourth input argument
     * @return {@code true} if the input arguments match the predicate,
     * otherwise {@code false}
     */
    boolean test(T t, U u, V v, W w);

    /**
     * Returns a composed {@code QuadPredicate} that represents a short-circuiting logical AND of this
     * predicate and another. When evaluating the composed predicate, if this predicate is {@code false},
     * then the {@code other} predicate is not evaluated.
     *
     * @param other a predicate that will be logically-ANDed with this predicate
     * @return a composed {@code QuadPredicate} that represents the short-circuiting logical AND of this
     * predicate and the {@code other} predicate
     */
    default QuadPredicate<T, U, V, W> and(QuadPredicate<? super T, ? super U, ? super V, ? super W> other) {
        return (t, u, v, w) -> test(t, u, v, w) && other.test(t, u, v, w);
    }
}
