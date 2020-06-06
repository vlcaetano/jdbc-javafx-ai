package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class Main extends Application {
	
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
        FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/view/Splash.fxml"));
        StackPane splashPane = splashLoader.load();
        
        Stage splashStage = new Stage(StageStyle.TRANSPARENT);
        final Scene scene = new Scene(splashPane);
        //scene.setFill(Color.TRANSPARENT);
        splashStage.setScene(scene);

        Service<Boolean> splashService = new Service<Boolean>() {
            @Override
            public void start() {
                super.start();
                splashStage.show();
            }
            
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        Thread.sleep(1000); // Delay de 4s
                        return true;
                    }
                };
            }
            
            @Override
            protected void succeeded() {
                splashStage.close();
                chamarTelaPrincipal(primaryStage);
            }
        };
        splashService.start();
	}
	
	public void chamarTelaPrincipal(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			mainScene = new Scene(scrollPane);
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Controle de Vendas");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Scene getMainScene() {
		return mainScene;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
