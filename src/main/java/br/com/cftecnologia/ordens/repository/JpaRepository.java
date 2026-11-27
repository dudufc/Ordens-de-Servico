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
        return emTransacao(em -> {
            if (registro instanceof br.com.cftecnologia.ordens.model.Cliente c) {
                String email = c.getEmail() == null ? "" : c.getEmail().strip().toLowerCase(java.util.Locale.ROOT);
                var contas = c.getId() == null ? java.util.List.<br.com.cftecnologia.ordens.model.Usuario>of() : em.createQuery("select u from Usuario u where u.cliente.id = :id", br.com.cftecnologia.ordens.model.Usuario.class).setParameter("id", c.getId()).getResultList();
                if (!contas.isEmpty() && email.isBlank()) throw new IllegalArgumentException("Cliente com conta precisa de e-mail.");
                if (!email.isBlank()) {
                    var outros = em.createQuery("select c from Cliente c where lower(c.email) = :email", br.com.cftecnologia.ordens.model.Cliente.class).setParameter("email", email).getResultList();
                    if (outros.stream().anyMatch(x -> !java.util.Objects.equals(x.getId(), c.getId()))) throw new IllegalArgumentException("E-mail já cadastrado.");
                    var usuarios = em.createQuery("select u from Usuario u where lower(u.email) = :email", br.com.cftecnologia.ordens.model.Usuario.class).setParameter("email", email).getResultList();
                    if (usuarios.stream().anyMatch(u -> u.getCliente() == null || !java.util.Objects.equals(u.getCliente().getId(), c.getId()))) throw new IllegalArgumentException("E-mail já utilizado por outra conta.");
                }
                c.setEmail(email);
                contas.forEach(u -> u.setEmail(email));
            }
            return em.merge(registro);
        });
    }

    public boolean excluir(Long id) {
        return emTransacao(em -> {
            T registro = em.find(tipo, id);
            if (registro == null) {
                return false;
            }
            if (tipo == br.com.cftecnologia.ordens.model.Cliente.class) {
                if (temVinculo(em, "select count(e) from Equipamento e where e.cliente.id = :id", id)
                    || temVinculo(em, "select count(u) from Usuario u where u.cliente.id = :id", id))
                    throw new IllegalStateException("Cliente possui equipamentos ou conta vinculada.");
            }
            if (tipo == br.com.cftecnologia.ordens.model.Equipamento.class &&
                temVinculo(em, "select count(o) from OrdemServico o where o.equipamento.id = :id", id))
                throw new IllegalStateException("Equipamento possui chamados vinculados.");
            em.remove(registro);
            return true;
        });
    }

    private boolean temVinculo(EntityManager em, String jpql, Long id) {
        return em.createQuery(jpql, Long.class).setParameter("id", id).getSingleResult() > 0;
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

