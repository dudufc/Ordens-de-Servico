package br.com.cftecnologia.ordens.controller;
import br.com.cftecnologia.ordens.repository.Autenticacao;
import javafx.fxml.*;import javafx.scene.*;import javafx.scene.control.*;import javafx.scene.layout.*;
public class LoginController {
 @FXML private VBox painel; @FXML private TextField email; @FXML private PasswordField senha; @FXML private Button cadastro; @FXML private Label aviso;
 private final Autenticacao auth=new Autenticacao();
 @FXML private void initialize(){boolean inicial=auth.primeiroAcesso();cadastro.setText(inicial?"Configurar administrador":"Criar conta de cliente");aviso.setText(inicial?"Primeiro acesso: configure o administrador antes de entrar.":"Entre com sua conta.");}
 @FXML private void entrar(){try{var u=auth.entrar(email.getText(),senha.getText()).orElseThrow(()->new IllegalArgumentException("E-mail ou senha inválidos."));Sessao.entrar(u);abrir(painel,Sessao.admin()?"/view/main.fxml":"/view/portal-cliente.fxml");}catch(RuntimeException e){Sessao.sair();erro(e.getMessage());}}
 @FXML private void cadastrar(){boolean admin=auth.primeiroAcesso();Dialog<ButtonType> d=new Dialog<>();d.setTitle(admin?"Configurar administrador":"Criar conta");TextField n=new TextField(),e=new TextField();PasswordField p=new PasswordField();n.setPromptText("Nome");e.setPromptText("E-mail");p.setPromptText("Senha (mínimo 8 caracteres)");VBox v=new VBox(10);if(!admin)v.getChildren().add(n);v.getChildren().addAll(e,p);d.getDialogPane().setContent(v);d.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);if(d.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK){try{auth.cadastrar(n.getText(),e.getText(),p.getText(),admin);email.setText(e.getText());senha.clear();initialize();new Alert(Alert.AlertType.INFORMATION,"Conta criada. Faça login.").showAndWait();}catch(RuntimeException ex){erro(ex.getMessage());}}}
 static void abrir(Node node,String recurso){try{Parent tela=FXMLLoader.load(LoginController.class.getResource(recurso));node.getScene().setRoot(tela);}catch(java.io.IOException e){e.printStackTrace();throw new IllegalStateException("Não foi possível abrir a tela.",e);}}
 private void erro(String m){new Alert(Alert.AlertType.ERROR,m==null?"Não foi possível concluir a operação.":m).showAndWait();}
}
