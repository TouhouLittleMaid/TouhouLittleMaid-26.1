package com.github.tartaricacid.touhoulittlemaid.util.functional;

@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {
    /**
     * Applies this function to the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @param w the fourth input argument
     * @return the function result
     */
    R apply(T t, U u, V v, W w);

    /**
     * Returns a composed {@code QuadFunction} that applies this function and then applies the {@code after} function.
     *
     * @param after the function to apply after this function
     * @return a composed {@code QuadFunction} that applies this function and then applies the {@code after} function
     */
    default <X> QuadFunction<T, U, V, W, X> andThen(QuadFunction<? super R, ? super U, ? super V, ? super W, ? extends X> after) {
        return (t, u, v, w) -> after.apply(apply(t, u, v, w), u, v, w);
    }
}
