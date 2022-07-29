package services.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * A common methods class for business logic operations.<br>
 * When inheriting this class you should also implement {@link Common#getRepository()}.
 *
 * @param <E> entity class.
 * @param <ID> identification or id class of the entity <b>E</b>.
 */
public abstract class Common<E, ID> {

    protected abstract JpaRepository<E, ID> getRepository();

    public Optional<E> get(ID id) {
        return getRepository().findById(id);
    }

    public E update(E entity) {
        return getRepository().save(entity);
    }

    public void delete(ID id) {
        getRepository().deleteById(id);
    }

    public Page<E> list(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    public int count() {
        return (int) getRepository().count();
    }

    public Collection<E> createMany(Collection<E> collection) {
        return getRepository().saveAll(collection);
    }

    public Collection<E> getAll() {
        return getRepository().findAll();
    }

    public void deleteAll() {
        getRepository().deleteAll();
    }
}
