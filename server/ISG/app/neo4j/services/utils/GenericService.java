package neo4j.services.utils;

import neo4j.Neo4jSessionFactory;
import neo4j.models.Entity;
import org.neo4j.ogm.session.Session;

import java.util.Collection;

/**
 * Created by Lycantropus on 14-04-2016.
 */
public abstract class GenericService<T> implements Service<T> {

    private static final int DEPTH_LIST = 0;
    private static final int DEPTH_ENTITY = 1;
    private Session session = Neo4jSessionFactory.getInstance().getNeo4jSession();

    public Session getSession() {
        return session;
    }

    @Override
    public Collection<T> findAll() {
        return session.loadAll(getEntityType(), DEPTH_LIST);
    }

    @Override
    public T find(Long id) {
        return session.load(getEntityType(), id, DEPTH_ENTITY);
    }

    @Override
    public void delete(Long id) {
        session.delete(session.load(getEntityType(), id));
    }

    @Override
    public T createOrUpdate(T entity) {
        session.save(entity, DEPTH_ENTITY);
        return find(((Entity) entity).getId());
    }

    public abstract Class<T> getEntityType();
}
