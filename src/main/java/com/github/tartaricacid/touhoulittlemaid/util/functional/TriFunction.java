package com.github.tartaricacid.touhoulittlemaid.util.functional;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    /**
     * Applies this function to the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     * @return the function result
     */
    R apply(T t, U u, V v);

    /**
     * Returns a composed {@code TriFunction} that applies this function and then applies the {@code after} function.
     *
     * @param after the function to apply after this function
     * @return a composed {@code TriFunction} that applies this function and then applies the {@code after} function
     */
    default <W> TriFunction<T, U, V, W> andThen(TriFunction<? super R, ? super U, ? super V, ? extends W> after) {
        return (t, u, v) -> after.apply(apply(t, u, v), u, v);
    }
}
