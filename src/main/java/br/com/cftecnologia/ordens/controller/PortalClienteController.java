package br.com.cftecnologia.ordens.controller;
import javafx.fxml.FXML;import javafx.scene.control.Label;import javafx.scene.layout.VBox;
public class PortalClienteController {
 @FXML private VBox painel; @FXML private Label nome;
 @FXML private void initialize(){if(Sessao.usuario()==null||Sessao.admin())throw new IllegalStateException("Acesso negado");nome.setText("Bem-vindo, "+Sessao.usuario().getCliente().getNome());}
 @FXML private void sair(){Sessao.sair();LoginController.abrir(painel,"/view/login.fxml");}
}
