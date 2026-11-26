package br.com.cftecnologia.ordens.controller;

import javafx.fxml.FXML;
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
    private Label etapaPagina;

    @FXML
    private void initialize() {
        mostrarInicio();
    }

    @FXML
    private void mostrarInicio() {
        exibir("Visão geral", "Bem-vindo ao sistema de ordens de serviço.",
                "Use o menu para gerenciar clientes e equipamentos.");
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
        exibir("Ordens de serviço", "As ordens registrarão problema, equipamento, status e valor estimado.",
                "Cadastro de ordens de serviço em breve.");
    }

    private void exibir(String titulo, String descricao, String etapa) {
        painel.getChildren().removeIf(n -> n.getId() != null && (n.getId().equals("cadastroClientes") || n.getId().equals("cadastroEquipamentos")));
        resumo.setVisible(true);
        resumo.setManaged(true);
        tituloPagina.setText(titulo);
        descricaoPagina.setText(descricao);
        etapaPagina.setText(etapa);
    }
}

