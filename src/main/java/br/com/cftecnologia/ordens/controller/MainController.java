package br.com.cftecnologia.ordens.controller;

import javafx.fxml.FXML;
import br.com.cftecnologia.ordens.model.*;
import br.com.cftecnologia.ordens.persistencia.PersistenceManager;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Locale;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import java.io.IOException;
import javafx.scene.control.Label;

public class MainController {
    @FXML private VBox painel;
    @FXML private VBox resumo;

    @FXML
    private Label tituloPagina;

    @FXML
    private Label descricaoPagina;

    @FXML
    private Label abertas, andamento, concluidas, canceladas, clientes, equipamentos, aviso;
    @FXML private ListView<String> recentes;

    @FXML
    private void initialize() {
        if (!Sessao.admin()) throw new IllegalStateException("Acesso negado");
        mostrarInicio();
    }

    @FXML
    private void mostrarInicio() {
        exibir("Visão geral", "Acompanhe os atendimentos de TI.", "");
        atualizarResumo();
    }

    @FXML
    private void mostrarClientes() {
        try {
            var tela = FXMLLoader.<VBox>load(getClass().getResource("/view/clientes.fxml"));
            exibir("Clientes", "Gerencie os dados dos clientes.", "");
            resumo.setVisible(false);
            resumo.setManaged(false);
            painel.getChildren().add(tela);
            VBox.setVgrow(tela, javafx.scene.layout.Priority.ALWAYS);
        } catch (IOException | RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Não foi possível abrir o cadastro de clientes.").showAndWait();
        }
    }

    @FXML
    private void mostrarEquipamentos() {
        try {
            var tela = FXMLLoader.<VBox>load(getClass().getResource("/view/equipamentos.fxml"));
            exibir("Equipamentos", "Gerencie os equipamentos dos clientes.", "");
            resumo.setVisible(false);
            resumo.setManaged(false);
            painel.getChildren().add(tela);
            VBox.setVgrow(tela, javafx.scene.layout.Priority.ALWAYS);
        } catch (IOException | RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Não foi possível abrir o cadastro de equipamentos.").showAndWait();
        }
    }

    @FXML
    private void mostrarOrdens() {
        try {
            var tela = FXMLLoader.<VBox>load(getClass().getResource("/view/ordens.fxml"));
            exibir("Ordens de serviço", "Gerencie os atendimentos.", "");
            resumo.setVisible(false); resumo.setManaged(false);
            painel.getChildren().add(tela); VBox.setVgrow(tela, javafx.scene.layout.Priority.ALWAYS);
        } catch (IOException | RuntimeException e) { e.printStackTrace(); new Alert(Alert.AlertType.ERROR, "Não foi possível abrir as ordens.").showAndWait(); }
    }

    @FXML private void trocarSenha() { SenhasDialog.alterar(); }
    @FXML private void sair() { Sessao.sair(); LoginController.abrir(painel, "/view/login.fxml"); }

    private void exibir(String titulo, String descricao, String etapa) {
        painel.getChildren().removeIf(n -> n != resumo && n != tituloPagina && n != descricaoPagina && !"rotuloPainel".equals(n.getId()));
        resumo.setVisible(true);
        resumo.setManaged(true);
        tituloPagina.setText(titulo);
        descricaoPagina.setText(descricao);

    }

    @FXML
    private void atualizarResumo() {
        var em = PersistenceManager.getFactory().createEntityManager();
        try {
            var totais = new EnumMap<StatusOrdem, Long>(StatusOrdem.class);
            for (Object[] linha : em.createQuery("select o.status, count(o) from OrdemServico o group by o.status", Object[].class).getResultList()) {
                totais.put((StatusOrdem) linha[0], (Long) linha[1]);
            }
            abertas.setText(totais.getOrDefault(StatusOrdem.ABERTA, 0L).toString());
            andamento.setText(totais.getOrDefault(StatusOrdem.EM_ANDAMENTO, 0L).toString());
            concluidas.setText(totais.getOrDefault(StatusOrdem.CONCLUIDA, 0L).toString());
            canceladas.setText(totais.getOrDefault(StatusOrdem.CANCELADA, 0L).toString());
            clientes.setText(em.createQuery("select count(c) from Cliente c", Long.class).getSingleResult().toString());
            equipamentos.setText(em.createQuery("select count(e) from Equipamento e", Long.class).getSingleResult().toString());
            var formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
            var lista = em.createQuery("select o from OrdemServico o join fetch o.equipamento e join fetch e.cliente order by o.dataAbertura desc, o.id desc", OrdemServico.class)
                    .setMaxResults(5).getResultList().stream()
                    .map(o -> "#" + o.getId() + "  |  " + o.getEquipamento().getCliente().getNome() + "  |  " + o.getStatus() + "  |  " + o.getDataAbertura().format(formato))
                    .toList();
            recentes.setItems(FXCollections.observableArrayList(lista));
            aviso.setText("Dados atualizados.");
        } catch (RuntimeException e) {
            for (Label l : new Label[]{abertas, andamento, concluidas, canceladas, clientes, equipamentos}) l.setText("—");
            recentes.getItems().clear();
            aviso.setText("Não foi possível consultar os dados. Tente atualizar novamente.");
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}

