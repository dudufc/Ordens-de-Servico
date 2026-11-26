package br.com.cftecnologia.ordens.controller;

import br.com.cftecnologia.ordens.model.*;
import br.com.cftecnologia.ordens.repository.JpaRepository;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.*;

public class EquipamentoController {
    @FXML private TextField tipo, marca, modelo, serie, pesquisa;
    @FXML private ComboBox<Cliente> cliente;
    @FXML private TableView<Equipamento> tabela;
    @FXML private TableColumn<Equipamento,String> colunaTipo, colunaMarca, colunaModelo, colunaSerie, colunaCliente;
    @FXML private Button excluir;
    @FXML private Label quantidade;
    private final ObservableList<Equipamento> equipamentos = FXCollections.observableArrayList();
    private JpaRepository<Equipamento> repo;
    private JpaRepository<Cliente> clientes;
    private Long selecionado;

    @FXML private void initialize() {
        colunaTipo.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTipo()));
        colunaMarca.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getMarca()));
        colunaModelo.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getModelo()));
        colunaSerie.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNumeroSerie()));
        colunaCliente.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCliente().getNome()));
        FilteredList<Equipamento> filtrados = new FilteredList<>(equipamentos);
        tabela.setItems(filtrados);
        pesquisa.textProperty().addListener((o,a,b) -> filtrados.setPredicate(e ->
            String.join(" ", texto(e.getTipo()), texto(e.getMarca()), texto(e.getModelo()), texto(e.getNumeroSerie()), e.getCliente().getNome()).toLowerCase(Locale.ROOT).contains(b.strip().toLowerCase(Locale.ROOT))));
        quantidade.textProperty().bind(Bindings.size(filtrados).asString("%d equipamento(s)"));
        excluir.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());
        tabela.getSelectionModel().selectedItemProperty().addListener((o,a,e) -> {
            selecionado = e == null ? null : e.getId();
            tipo.setText(e == null ? "" : texto(e.getTipo()));
            marca.setText(e == null ? "" : texto(e.getMarca()));
            modelo.setText(e == null ? "" : texto(e.getModelo()));
            serie.setText(e == null ? "" : texto(e.getNumeroSerie()));
            cliente.setValue(e == null ? null : cliente.getItems().stream().filter(c -> Objects.equals(c.getId(), e.getCliente().getId())).findFirst().orElse(null));
        });
        try { repo = new JpaRepository<>(Equipamento.class); clientes = new JpaRepository<>(Cliente.class); atualizar(); }
        catch (RuntimeException e) { erro("Não foi possível carregar os equipamentos."); }
    }
    private static String texto(String s) { return s == null ? "" : s; }
    private void atualizar() {
        cliente.getItems().setAll(clientes.listarTodos().stream().sorted(Comparator.comparing(Cliente::getNome, String.CASE_INSENSITIVE_ORDER)).toList());
        equipamentos.setAll(repo.listarTodos().stream().sorted(Comparator.comparing(Equipamento::getTipo, String.CASE_INSENSITIVE_ORDER)).toList());
    }
    @FXML private void limpar() {
        tabela.getSelectionModel().clearSelection(); selecionado = null;
        tipo.clear(); marca.clear(); modelo.clear(); serie.clear(); cliente.setValue(null); tipo.requestFocus();
    }
    @FXML private void salvar() {
        String t = tipo.getText().strip(), m = marca.getText().strip(), mo = modelo.getText().strip(), s = serie.getText().strip();
        Cliente c = cliente.getValue();
        if (c == null || t.isEmpty() || t.length()>80 || m.length()>80 || mo.length()>80 || s.length()>100) {
            erro("Selecione um cliente e informe o tipo. Tipo, marca e modelo: até 80 caracteres; série: até 100."); return;
        }
        try {
            Equipamento e = selecionado == null ? new Equipamento(t,m,mo,s,c) : repo.buscarPorId(selecionado).orElseThrow();
            e.setTipo(t); e.setMarca(m); e.setModelo(mo); e.setNumeroSerie(s); e.setCliente(c); repo.salvar(e);
        } catch (RuntimeException ex) { erro("Não foi possível salvar o equipamento."); return; }
        recarregar("Equipamento salvo com sucesso.");
    }
    @FXML private void excluir() {
        Equipamento e = tabela.getSelectionModel().getSelectedItem(); if (e == null) return;
        if (new Alert(Alert.AlertType.CONFIRMATION, "Excluir este equipamento?", ButtonType.YES, ButtonType.NO).showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        try { if (!repo.excluir(e.getId())) { erro("Equipamento não encontrado."); return; } }
        catch (RuntimeException ex) { erro("Não foi possível excluir. Verifique se existem ordens vinculadas."); return; }
        recarregar("Equipamento excluído com sucesso.");
    }
    private void recarregar(String mensagem) {
        limpar(); try { atualizar(); new Alert(Alert.AlertType.INFORMATION, mensagem).showAndWait(); }
        catch (RuntimeException ex) { erro(mensagem + " Reabra a tela para atualizar a lista."); }
    }
    private void erro(String mensagem) { new Alert(Alert.AlertType.ERROR, mensagem).showAndWait(); }
}
