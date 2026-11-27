package br.com.cftecnologia.ordens;
import br.com.cftecnologia.ordens.persistencia.PersistenceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class MainApp extends Application {
 @Override public void start(Stage stage) throws java.io.IOException {
  if (getParameters().getRaw().contains("--demo")) {
   br.com.cftecnologia.ordens.repository.DadosDemo.inserir();
  }
  Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/view/login.fxml")), 1100, 700);
  var css = getClass().getResource("/styles/app.css");
  if(css != null) scene.getStylesheets().add(css.toExternalForm());
  stage.setTitle("Ordens de Serviço"); stage.setMinWidth(860); stage.setMinHeight(560); stage.setScene(scene); stage.show();
 }
 @Override public void stop() { PersistenceManager.fechar(); }
 public static void main(String[] args) { launch(args); }
}
