package neo4j.services.utils;

import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;

/**
 * Created by Lycantropus on 14-04-2016.
 */
public interface Service<T> {

    Collection<T> findAll();

    Collection<T> findAllDetailedWithFilters(Filters filters);

    T find(Long id);

    void delete(Long id);

    T createOrUpdate(T object);

    T createOrUpdate(T object, int DEPTH);

}
