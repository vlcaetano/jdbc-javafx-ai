package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import controller.Comercial;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import view.util.Alerts;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemCliente;
	
	@FXML
	private MenuItem menuItemVendedor;
	
	@FXML
	private MenuItem menuItemFornecedor;
	
	@FXML
	private MenuItem menuItemProduto;
	
	@FXML
	private MenuItem menuItemVendas;
	
	@FXML
	private MenuItem menuItemCompras;
	
	@FXML
	public void onMenuItemClienteAction() {
		//loadView("/view/ListaCliente.fxml", x -> {});
		System.out.println("onMenuItemClienteAction");
	}
	
	@FXML
	public void onMenuItemVendedorAction() {
		System.out.println("onMenuItemVendedorAction");
	}
	
	@FXML
	public void onMenuItemFornecedorAction() {
		loadView("/view/ListaFornecedor.fxml", (ListaFornecedorController controller) -> {
			controller.setObjBiz(new Comercial());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemProdutoAction() {
		loadView("/view/ListaProduto.fxml", (ListaProdutoController controller) -> {
			controller.setObjBiz(new Comercial());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemVendasAction() {
		System.out.println("onMenuItemVendasAction");
	}
	
	@FXML
	public void onMenuItemComprasAction() {
		System.out.println("onMenuItemComprasAction");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox)((ScrollPane)mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			T controller = loader.getController();
			initializingAction.accept(controller);
		} catch (IOException e) {
			Alerts.showAlert("IOException", "Erro carregando a tela", e.getMessage(), AlertType.ERROR);
		}
	}
}
