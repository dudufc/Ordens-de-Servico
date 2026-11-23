package br.com.cftecnologia.ordens.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

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
        exibir("Visão geral", "Bem-vindo ao sistema de ordens de serviço da CF Tecnologia.",
                "Etapa 1: estrutura inicial, entidades e persistência JPA.");
    }

    @FXML
    private void mostrarClientes() {
        exibir("Clientes", "Aqui serão feitos o cadastro, a busca, a edição e a exclusão de clientes.",
                "Próxima etapa: CRUD de clientes com TableView e formulário.");
    }

    @FXML
    private void mostrarEquipamentos() {
        exibir("Equipamentos", "Cada equipamento ficará vinculado ao cliente responsável.",
                "Etapa planejada: CRUD de equipamentos com seleção de cliente.");
    }

    @FXML
    private void mostrarOrdens() {
        exibir("Ordens de serviço", "As ordens registrarão problema, equipamento, status e valor estimado.",
                "Etapa planejada: fluxo de abertura e acompanhamento de ordens.");
    }

    private void exibir(String titulo, String descricao, String etapa) {
        tituloPagina.setText(titulo);
        descricaoPagina.setText(descricao);
        etapaPagina.setText(etapa);
    }
}

