package br.com.cftecnologia.ordens.controller;

import br.com.cftecnologia.ordens.model.Cliente;
import br.com.cftecnologia.ordens.repository.JpaRepository;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Comparator;
import java.util.Locale;

public class ClienteController {
    @FXML private TextField nome, email, telefone, pesquisa;
    @FXML private TableView<Cliente> tabela;
    @FXML private TableColumn<Cliente, String> colunaNome, colunaEmail, colunaTelefone;
    @FXML private Button excluir;
    @FXML private Label quantidade;
    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();
    private JpaRepository<Cliente> repository;
    private Long idSelecionado;

    @FXML private void initialize() {
        colunaNome.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNome()));
        colunaEmail.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getEmail()));
        colunaTelefone.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTelefone()));
        FilteredList<Cliente> filtrados = new FilteredList<>(clientes);
        tabela.setItems(filtrados);
        pesquisa.textProperty().addListener((obs, anterior, atual) -> {
            String termo = atual.strip().toLowerCase(Locale.ROOT);
            filtrados.setPredicate(c -> c.getNome().toLowerCase(Locale.ROOT).contains(termo));
        });
        quantidade.textProperty().bind(Bindings.size(filtrados).asString("%d cliente(s)"));
        excluir.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, anterior, atual) -> {
            idSelecionado = atual == null ? null : atual.getId();
            nome.setText(atual == null ? "" : atual.getNome());
            email.setText(atual == null ? "" : atual.getEmail());
            telefone.setText(atual == null ? "" : atual.getTelefone());
        });
        try { repository = new JpaRepository<>(Cliente.class); atualizar(); }
        catch (RuntimeException e) { erro("Não foi possível carregar os clientes."); }
    }

    private void atualizar() {
        clientes.setAll(repository.listarTodos().stream()
                .sorted(Comparator.comparing(Cliente::getNome, String.CASE_INSENSITIVE_ORDER)).toList());
    }

    @FXML private void limpar() {
        tabela.getSelectionModel().clearSelection();
        idSelecionado = null;
        nome.clear(); email.clear(); telefone.clear(); nome.requestFocus();
    }

    @FXML private void salvar() {
        String n = nome.getText().strip(), e = email.getText().strip(), t = telefone.getText().strip();
        if (n.isEmpty() || n.length() > 120 || e.length() > 120 || t.length() > 30) {
            erro("Informe um nome de até 120 caracteres. E-mail: até 120; telefone: até 30."); return;
        }
        if (!e.isEmpty() && !e.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            erro("Informe um e-mail válido."); return;
        }
        try {
            Cliente cliente = idSelecionado == null ? new Cliente(n, e, t) : repository.buscarPorId(idSelecionado).orElseThrow();
            cliente.setNome(n); cliente.setEmail(e); cliente.setTelefone(t);
            repository.salvar(cliente);
        } catch (RuntimeException ex) { erro("Não foi possível salvar o cliente."); return; }
        recarregar("Cliente salvo com sucesso.");
    }

    @FXML private void excluir() {
        Cliente cliente = tabela.getSelectionModel().getSelectedItem();
        if (cliente == null) return;
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Excluir " + cliente.getNome() + "?", ButtonType.YES, ButtonType.NO);
        if (confirmacao.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        try {
            if (!repository.excluir(cliente.getId())) { erro("Cliente não encontrado."); return; }
        } catch (RuntimeException ex) { erro("Não foi possível excluir. Verifique se existem equipamentos vinculados."); return; }
        recarregar("Cliente excluído com sucesso.");
    }

    private void recarregar(String mensagem) {
        limpar();
        try { atualizar(); new Alert(Alert.AlertType.INFORMATION, mensagem).showAndWait(); }
        catch (RuntimeException ex) { erro(mensagem + " Não foi possível atualizar a lista; reabra a tela."); }
    }

    private void erro(String mensagem) { new Alert(Alert.AlertType.ERROR, mensagem).showAndWait(); }
}
