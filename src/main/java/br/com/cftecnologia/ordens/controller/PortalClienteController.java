package br.com.cftecnologia.ordens.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
public class PortalClienteController {
 @FXML private VBox painel, area;
 @FXML private Label nome;
 @FXML private void initialize(){if(Sessao.usuario()==null||Sessao.admin())throw new IllegalStateException("Acesso negado");nome.setText("Bem-vindo, "+Sessao.usuario().getCliente().getNome()); mostrarEquipamentos();}
 private void abrir(String arquivo){try {VBox tela=FXMLLoader.load(getClass().getResource(arquivo)); area.getChildren().setAll(tela); VBox.setVgrow(tela,Priority.ALWAYS);}catch(Exception e){e.printStackTrace();new Alert(Alert.AlertType.ERROR,"Não foi possível abrir a tela.").showAndWait();}}
 @FXML private void mostrarEquipamentos(){abrir("/view/equipamentos.fxml");}
 @FXML private void mostrarOrdens(){abrir("/view/ordens.fxml");}
 @FXML private void trocarSenha(){SenhasDialog.alterar();}
 @FXML private void sair(){Sessao.sair();LoginController.abrir(painel,"/view/login.fxml");}
}
