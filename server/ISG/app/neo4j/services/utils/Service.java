package neo4j.services.utils;

/**
 * Created by Lycantropus on 14-04-2016.
 */
public interface Service<T> {

    Iterable<T> findAll();

    T find(Long id);

    void delete(Long id);

    T createOrUpdate(T object);

    T createOrUpdate(T object, int DEPTH);

}
