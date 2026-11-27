package br.com.cftecnologia.ordens.controller;
import br.com.cftecnologia.ordens.repository.Autenticacao;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
final class SenhasDialog {
 static void alterar(){abrir(null);}
 static void redefinir(Long id){if(Sessao.admin()&&id!=null)abrir(id);}
 private static void abrir(Long id){
  Dialog<ButtonType> d=new Dialog<>();d.setTitle(id==null?"Alterar senha":"Redefinir senha do cliente");
  PasswordField atual=new PasswordField(),nova=new PasswordField(),confirmar=new PasswordField();
  VBox v=new VBox(8);if(id==null)v.getChildren().addAll(new Label("Senha atual"),atual);
  v.getChildren().addAll(new Label("Nova senha (8 a 128 caracteres)"),nova,new Label("Confirmar nova senha"),confirmar);
  d.getDialogPane().setContent(v);d.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
  d.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(javafx.event.ActionEvent.ACTION,e->{try{
   if(!nova.getText().equals(confirmar.getText()))throw new IllegalArgumentException("As senhas não coincidem.");
   var auth=new Autenticacao();if(id==null)Sessao.entrar(auth.alterarSenha(Sessao.usuario(),atual.getText(),nova.getText()));else auth.redefinirSenha(Sessao.usuario(),id,nova.getText());
  }catch(RuntimeException ex){e.consume();new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait();}});
  if(d.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK)new Alert(Alert.AlertType.INFORMATION,"Senha atualizada.").showAndWait();
 }
}
