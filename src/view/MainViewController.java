package view;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import controller.Comercial;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import view.util.Alerts;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class MainViewController implements Initializable {
/**
 * Classe MainViewController - Controller da view MainView.fxml
 */
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
	private MenuItem menuItemEstClientes;
	
	@FXML
	private MenuItem menuItemEstVendedores;
	
	@FXML
	private MenuItem menuItemEstFornecedores;
	
	@FXML
	private Label labelRelogio;
	
	@FXML
	public void onMenuItemClienteAction() {
		loadView("/view/ListaCliente.fxml", (ListaClienteController controller) -> {
			controller.setObjBiz(new Comercial());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemVendedorAction() {
		loadView("/view/ListaVendedor.fxml", (ListaVendedorController controller) -> {
			controller.setObjBiz(new Comercial());
			controller.updateTableView();
		});
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
		loadView("/view/ListaVenda.fxml", (ListaVendaController controller) -> {
			controller.setObjBiz(new Comercial());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemComprasAction() {
		loadView("/view/ListaCompra.fxml", (ListaCompraController controller) -> {
			controller.setObjBiz(new Comercial());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemEstClientesAction() {
		loadView("/view/ListaEstCliente.fxml", (ListaEstClienteController controller) -> {
			controller.setObjBiz(new Comercial());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemEstVendedoresAction() {
		loadView("/view/ListaEstVendedor.fxml", (ListaEstVendedorController controller) -> {
			controller.setObjBiz(new Comercial());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemEstFornecedoresAction() {
		loadView("/view/ListaEstFornecedor.fxml", (ListaEstFornecedorController controller) -> {
			controller.setObjBiz(new Comercial());
			controller.updateTableView();
		});
	}
	
	/**
	 * M�todo para inicializar a MainView com um rel�gio mostrando a data
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
		labelRelogio.setText(sdf.format(new Date()));
		KeyFrame frame = new KeyFrame(Duration.millis(1000), e -> {
			Date agora = new Date();
			labelRelogio.setText(sdf.format(agora));
		});
		Timeline timeline = new Timeline(frame);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
	
	/**
	 * M�todo para carregar uma view
	 * @param absoluteName
	 * @param initializingAction
	 */
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
