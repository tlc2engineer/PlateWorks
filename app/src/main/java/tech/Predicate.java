package tech;

/**
 * Интерфейс предикат
 * @param <T>
 */
public interface Predicate<T>{
    boolean test(T item);
}
