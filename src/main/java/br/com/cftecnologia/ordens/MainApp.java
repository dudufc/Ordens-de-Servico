package br.com.cftecnologia.ordens;

import br.com.cftecnologia.ordens.persistencia.PersistenceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1100, 700);
        URL css = getClass().getResource("/styles/app.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("CF Ordens de Serviço");
        stage.setMinWidth(860);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        PersistenceManager.fechar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

