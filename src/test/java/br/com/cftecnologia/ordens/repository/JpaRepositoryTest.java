package br.com.cftecnologia.ordens.repository;

import br.com.cftecnologia.ordens.model.Cliente;
import br.com.cftecnologia.ordens.model.Equipamento;
import br.com.cftecnologia.ordens.model.OrdemServico;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpaRepositoryTest {

    @TempDir
    Path pastaTemporaria;

    @Test
    void deveSalvarEntidadesRelacionadasNoSqlite() {
        try (EntityManagerFactory factory = criarBancoTeste()) {
            JpaRepository<Cliente> clientes = new JpaRepository<>(factory, Cliente.class);
            JpaRepository<Equipamento> equipamentos = new JpaRepository<>(factory, Equipamento.class);
            JpaRepository<OrdemServico> ordens = new JpaRepository<>(factory, OrdemServico.class);

            Cliente cliente = clientes.salvar(new Cliente("Maria", "maria@exemplo.com", "99999-9999"));
            Equipamento equipamento = equipamentos.salvar(
                    new Equipamento("Notebook", "Dell", "Latitude", "SN123", cliente));
            OrdemServico ordem = ordens.salvar(
                    new OrdemServico(equipamento, "Não liga", new BigDecimal("150.00")));

            assertNotNull(cliente.getId());
            assertNotNull(equipamento.getId());
            assertNotNull(ordem.getId());
            assertNotNull(ordem.getDataAbertura());
            assertEquals(1, clientes.listarTodos().size());
            assertEquals(1, equipamentos.listarTodos().size());
            assertEquals(1, ordens.listarTodos().size());
            assertTrue(ordens.buscarPorId(ordem.getId()).isPresent());
        }
    }

    @Test
    void deveReverterTransacaoAoSalvarEquipamentoSemCliente() {
        try (EntityManagerFactory factory = criarBancoTeste()) {
            JpaRepository<Equipamento> equipamentos = new JpaRepository<>(factory, Equipamento.class);

            assertThrows(RepositoryException.class,
                    () -> equipamentos.salvar(new Equipamento("PC", "", "", "", null)));
            assertTrue(equipamentos.listarTodos().isEmpty());
        }
    }

    private EntityManagerFactory criarBancoTeste() {
        String url = "jdbc:sqlite:" + pastaTemporaria.resolve("teste.db").toAbsolutePath();
        return Persistence.createEntityManagerFactory("cfServicePU", Map.of(
                "jakarta.persistence.jdbc.url", url,
                "hibernate.hbm2ddl.auto", "create-drop"
        ));
    }
}

