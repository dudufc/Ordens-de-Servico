package br.com.cftecnologia.ordens.controller;
import br.com.cftecnologia.ordens.model.*;
import br.com.cftecnologia.ordens.repository.JpaRepository;
import br.com.cftecnologia.ordens.persistencia.PersistenceManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Locale;
public class OrdemController {
 @FXML private ComboBox<Equipamento> equipamento;
 @FXML private ComboBox<StatusOrdem> status;
 @FXML private TextArea problema, observacoes;
 @FXML private TextField pesquisa;
 @FXML private Button excluir, salvar, iniciar, finalizar, cancelar, editar;
 @FXML private javafx.scene.layout.VBox formulario, dadosTecnicos;
 private Dialog<Void> dialogo;
 @FXML private TableView<OrdemServico> tabela;
 @FXML private TableColumn<OrdemServico,String> numero, cliente, item, situacao, abertura;
 private final JpaRepository<OrdemServico> repo = new JpaRepository<>(OrdemServico.class);
 private final javafx.collections.ObservableList<OrdemServico> lista = FXCollections.observableArrayList();
 private boolean admin; private Long clienteId;
 private boolean permitido(Equipamento e){return e!=null && (admin || Objects.equals(clienteId,e.getCliente().getId()));}
 @FXML private void initialize(){
  if(Sessao.usuario()==null) throw new IllegalStateException("Acesso negado");
  admin=Sessao.admin(); if(!admin) clienteId=Objects.requireNonNull(Sessao.usuario().getCliente()).getId();
  equipamento.setItems(FXCollections.observableArrayList(new JpaRepository<>(Equipamento.class).listarTodos().stream().filter(this::permitido).toList()));
  equipamento.setConverter(new javafx.util.StringConverter<Equipamento>(){public String toString(Equipamento e){return e==null?"":e.getCliente().getNome()+" — "+e;} public Equipamento fromString(String s){return null;}});
  status.getItems().setAll(StatusOrdem.values()); status.setValue(StatusOrdem.ABERTA);
  status.setDisable(!admin); observacoes.setEditable(admin); excluir.setVisible(admin); excluir.setManaged(admin);
  numero.setCellValueFactory(x->new ReadOnlyStringWrapper(String.valueOf(x.getValue().getId())));
  cliente.setCellValueFactory(x->new ReadOnlyStringWrapper(x.getValue().getEquipamento().getCliente().getNome()));
  item.setCellValueFactory(x->new ReadOnlyStringWrapper(x.getValue().getEquipamento().toString()));
  situacao.setCellValueFactory(x->new ReadOnlyStringWrapper(x.getValue().getStatus().toString()));
  abertura.setCellValueFactory(x->new ReadOnlyStringWrapper(x.getValue().getDataAbertura().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
  var filtro=new FilteredList<>(lista); tabela.setItems(filtro);
  pesquisa.textProperty().addListener((o,a,b)->filtro.setPredicate(x->(x.getId()+" "+x.getEquipamento()+" "+x.getEquipamento().getCliente().getNome()+" "+x.getStatus()+" "+x.getDescricaoProblema()).toLowerCase(Locale.ROOT).contains(b.strip().toLowerCase(Locale.ROOT))));
  tabela.getSelectionModel().selectedItemProperty().addListener((o,a,b)->{if(b!=null){mostrarFormulario(false);equipamento.getItems().stream().filter(e->Objects.equals(e.getId(),b.getEquipamento().getId())).findFirst().ifPresent(equipamento::setValue); problema.setText(b.getDescricaoProblema()); observacoes.setText(b.getObservacoes()); status.setValue(b.getStatus());} salvar.setDisable(!admin&&b!=null); equipamento.setDisable(!admin&&b!=null); problema.setEditable(admin||b==null);});
  for(var b:java.util.List.of(iniciar,finalizar,cancelar)){b.setVisible(admin);b.setManaged(admin);b.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());}
  editar.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());
  excluir.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull()); limpar(); atualizar();
 }
 private void atualizar(){var em=PersistenceManager.getFactory().createEntityManager(); try {String q="select o from OrdemServico o join fetch o.equipamento e join fetch e.cliente c"; var consulta=em.createQuery(q+(admin?"":" where c.id = :id")+" order by o.id desc",OrdemServico.class); if(!admin)consulta.setParameter("id",clienteId); lista.setAll(consulta.getResultList());}finally{em.close();}}
 private void mostrarFormulario(boolean visivel){
  if(!visivel){if(dialogo!=null){dialogo.close();}return;}
  boolean edicao=admin && tabela.getSelectionModel().getSelectedItem()!=null;
  dadosTecnicos.setVisible(edicao); dadosTecnicos.setManaged(edicao);
  salvar.setText(edicao?"Salvar alterações":"Abrir chamado");
  if(formulario.getParent() instanceof javafx.scene.layout.Pane p)p.getChildren().remove(formulario);
  formulario.setVisible(true); formulario.setManaged(true);
  dialogo=new Dialog<>(); dialogo.setTitle(edicao?"Editar chamado":"Abrir chamado");
  dialogo.initOwner(tabela.getScene().getWindow());
  var pane=dialogo.getDialogPane(); pane.setContent(formulario); pane.setPrefWidth(580);
  pane.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
  pane.getButtonTypes().add(ButtonType.CANCEL);
  dialogo.setOnHidden(event->{pane.setContent(null); dialogo=null;});
  dialogo.show();
 }
 @FXML private void abrirChamado(){limpar();mostrarFormulario(true);}
 @FXML private void editarChamado(){
  var o=tabela.getSelectionModel().getSelectedItem(); if(o==null)return;
  var a=new Alert(Alert.AlertType.INFORMATION); a.setTitle("Detalhes do chamado"); a.setHeaderText("Chamado #"+o.getId()+" - "+o.getStatus());
  var t=new TextArea("Equipamento: "+o.getEquipamento()+"\n\nProblema: "+o.getDescricaoProblema()+"\n\nObservações: "+Objects.toString(o.getObservacoes(), ""));
  t.setEditable(false); t.setWrapText(true); t.setPrefSize(480,260); a.getDialogPane().setContent(t); a.showAndWait();
 }
 @FXML private void iniciarChamado(){alterarStatus("EM_ANDAMENTO");}
 @FXML private void finalizarChamado(){encerrar(false);}
 @FXML private void cancelarChamado(){encerrar(true);}
 private void encerrar(boolean cancelamento){
  if(!admin)return; var o=tabela.getSelectionModel().getSelectedItem(); if(o==null)return;
  if(o.getStatus()==StatusOrdem.CONCLUIDA || o.getStatus()==StatusOrdem.CANCELADA){erro("Chamado já encerrado.");return;}
  var d=new Dialog<ButtonType>(); d.setTitle(cancelamento?"Cancelar chamado":"Finalizar chamado"); d.initOwner(tabela.getScene().getWindow());
  var texto=new TextArea(); texto.setWrapText(true); texto.setPrefRowCount(5);
  var campos=new javafx.scene.layout.VBox(10,new Label(cancelamento?"Motivo do cancelamento *":"Solução realizada *"),texto);
  var confirmar=new ButtonType(cancelamento?"Confirmar cancelamento":"Finalizar chamado",ButtonBar.ButtonData.OK_DONE);
  var pane=d.getDialogPane(); pane.setContent(campos); pane.setPrefWidth(520); pane.getButtonTypes().addAll(confirmar,ButtonType.CANCEL);
  pane.lookupButton(confirmar).addEventFilter(javafx.event.ActionEvent.ACTION,event->{
   try{if(texto.getText().isBlank()||texto.getText().length()>2000)throw new IllegalArgumentException("Informe a descrição (até 2000 caracteres).");
    var atual=repo.buscarPorId(o.getId()).orElseThrow();
    atual.setObservacoes(texto.getText().strip());  atual.setStatus(cancelamento?StatusOrdem.CANCELADA:StatusOrdem.CONCLUIDA); repo.salvar(atual);
   }catch(RuntimeException ex){event.consume();erro("Não foi possível encerrar: "+ex.getMessage());}
  });
  if(d.showAndWait().orElse(ButtonType.CANCEL)==confirmar){limpar();atualizar();aviso("Chamado encerrado.");}
 }
 private void alterarStatus(String destino){if(!admin)return;var o=tabela.getSelectionModel().getSelectedItem();if(o==null)return;try{var novo=StatusOrdem.valueOf(destino);if(o.getStatus()==novo)return;if(o.getStatus()!=StatusOrdem.ABERTA){erro("Somente chamados abertos podem ser iniciados.");return;}if(new Alert(Alert.AlertType.CONFIRMATION,"Alterar status para "+novo+"?",ButtonType.YES,ButtonType.NO).showAndWait().orElse(ButtonType.NO)!=ButtonType.YES)return;o.setStatus(novo);repo.salvar(o);limpar();atualizar();aviso("Status atualizado.");}catch(RuntimeException ex){atualizar();erro("Não foi possível alterar o status: "+ex.getMessage());}}
 @FXML private void limpar(){mostrarFormulario(false);tabela.getSelectionModel().clearSelection(); equipamento.setValue(null); problema.clear(); observacoes.clear(); status.setValue(StatusOrdem.ABERTA);}
 @FXML private void salvar(){try{var selecionada=tabela.getSelectionModel().getSelectedItem(); if(!admin&&selecionada!=null)return; var e=equipamento.getValue(); if(!permitido(e)||problema.getText().isBlank()||problema.getText().length()>2000||observacoes.getText().length()>2000)throw new IllegalArgumentException("Selecione um equipamento e informe um problema válido (até 2000 caracteres)."); var ordem=selecionada==null?new OrdemServico(e,problema.getText().strip(),BigDecimal.ZERO):selecionada; ordem.setEquipamento(e); ordem.setDescricaoProblema(problema.getText().strip()); if(admin&&selecionada!=null){ordem.setStatus(Objects.requireNonNull(status.getValue())); ordem.setObservacoes(observacoes.getText());}else{ordem.setStatus(StatusOrdem.ABERTA);} repo.salvar(ordem); limpar(); atualizar(); aviso("Ordem salva.");}catch(RuntimeException ex){erro("Não foi possível salvar. Verifique os campos.");}}
 @FXML private void excluir(){if(!admin)return; var o=tabela.getSelectionModel().getSelectedItem(); if(o==null)return; if(new Alert(Alert.AlertType.CONFIRMATION,"Excluir a ordem?",ButtonType.YES,ButtonType.NO).showAndWait().orElse(ButtonType.NO)!=ButtonType.YES)return; try{repo.excluir(o.getId()); limpar(); atualizar(); aviso("Ordem excluída.");}catch(RuntimeException ex){erro("Não foi possível excluir.");}}
 private void aviso(String s){new Alert(Alert.AlertType.INFORMATION,s).showAndWait();} private void erro(String s){new Alert(Alert.AlertType.ERROR,s).showAndWait();}
}
