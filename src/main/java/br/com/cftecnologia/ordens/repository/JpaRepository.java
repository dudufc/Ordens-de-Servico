package br.com.cftecnologia.ordens.repository;

import br.com.cftecnologia.ordens.persistencia.PersistenceManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class JpaRepository<T> {

    private final EntityManagerFactory factory;
    private final Class<T> tipo;

    public JpaRepository(Class<T> tipo) {
        this(PersistenceManager.getFactory(), tipo);
    }

    public JpaRepository(EntityManagerFactory factory, Class<T> tipo) {
        this.factory = factory;
        this.tipo = tipo;
    }

    public T salvar(T registro) {
        return emTransacao(em -> em.merge(registro));
    }

    public boolean excluir(Long id) {
        return emTransacao(em -> {
            T registro = em.find(tipo, id);
            if (registro == null) {
                return false;
            }
            em.remove(registro);
            return true;
        });
    }

    public Optional<T> buscarPorId(Long id) {
        return consultar(em -> Optional.ofNullable(em.find(tipo, id)));
    }

    public List<T> listarTodos() {
        return consultar(em -> {
            CriteriaQuery<T> consulta = em.getCriteriaBuilder().createQuery(tipo);
            consulta.select(consulta.from(tipo));
            return em.createQuery(consulta).getResultList();
        });
    }

    private <R> R consultar(Function<EntityManager, R> operacao) {
        EntityManager em = factory.createEntityManager();
        try {
            return operacao.apply(em);
        } catch (RuntimeException exception) {
            throw new RepositoryException("Não foi possível consultar os registros.", exception);
        } finally {
            em.close();
        }
    }

    private <R> R emTransacao(Function<EntityManager, R> operacao) {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transacao = em.getTransaction();
        try {
            transacao.begin();
            R resultado = operacao.apply(em);
            transacao.commit();
            return resultado;
        } catch (RuntimeException exception) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw new RepositoryException("Não foi possível salvar as alterações. Nenhum dado da operação foi confirmado.", exception);
        } finally {
            em.close();
        }
    }
}

