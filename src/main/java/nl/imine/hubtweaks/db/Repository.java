package nl.imine.hubtweaks.db;

import java.util.Collection;
import java.util.Optional;

public interface Repository<I, T> {

    /**
     * Load the repository from the file system or database. This method should always be called before using a repository
     */
    void loadAll();

    /**
     * Get all items currently in the repository
     *
     * @return A collection containing all items in the repository
     */
    Collection<T> getAll();

    /**
     * Find a single value in the repository with a given id
     *
     * @param id The id;
     * @return An optional that might contain the object if one with the same id was found in the repository
     */
    Optional<T> findOne(I id);

    /**
     * Adds an item to the repository.
     * This method should automatically detect Id collissions and overwrite the old value with the new item
     *
     * @param item
     */
    void addOne(T item);

    void delete(T item);

    default void deleteAll(Collection<T> all) {
        for (T item : all) {
            delete(item);
        }
    }

    /**
     * Adds a collection of items to the repository. By default this uses the addOne implementation of the repository for each item in the collection.
     *
     * @param all
     */
    default void addAll(Collection<T> all) {
        for (T item : all) {
            addOne(item);
        }
    }
}
