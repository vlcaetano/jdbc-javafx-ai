package view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import controller.Comercial;
import db.DbIntegrityException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Produto;
import view.listeners.DataChangeListener;
import view.util.Alerts;
import view.util.Utils;

public class ListaProdutoController implements Initializable, DataChangeListener {
	private Comercial objBiz;
	
	@FXML
	private TableView<Produto> tableViewProduto;
	
	@FXML
	private TableColumn<Produto, Integer> tableColumnCodigo;
	
	@FXML
	private TableColumn<Produto, String> tableColumnNome;
	
	@FXML
	private TableColumn<Produto, Double> tableColumnPrecoUnitario;
	
	@FXML
	private TableColumn<Produto, Integer> tableColumnEstoque;
	
	@FXML
	private TableColumn<Produto, Integer> tableColumnEstoqueMinimo;
	
	@FXML
	private TableColumn<Produto, Date> tableColumnDataCadastro;
	
	@FXML
	private TableColumn<Produto, Produto> tableColumnDeletar;
	
	@FXML
	private Button btNovo;
	@FXML
	private Button btAbaixoEstoqueMin;
	@FXML
	private Button btTodos;
	
	private ObservableList<Produto> obsList;
	
	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Produto obj = new Produto();
		createDialogForm(obj, "/view/FormProduto.fxml", parentStage);
	}

	public void setObjBiz(Comercial objBiz) {
		this.objBiz = objBiz;
	}
	
	@FXML
	public void onBtAbaixoEstoqueMinAction() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		List<Produto> list = objBiz.listarProdutos();
		List<Produto> listaAbaixoEstoque = new ArrayList<Produto>();
		for (Produto p : list) {
			if (p.getEstoque() < p.getEstoqueMinimo()) {
				listaAbaixoEstoque.add(p);
			}
		}
		if (!list.isEmpty()) {
			obsList = FXCollections.observableArrayList(listaAbaixoEstoque);
			tableViewProduto.setItems(obsList);
			initRemoveButtons();
		} else {
			tableViewProduto.setItems(null);
		}
	}
	
	@FXML
	public void onBtTodosAction() {
		updateTableView();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnPrecoUnitario.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
		Utils.formatTableColumnDouble(tableColumnPrecoUnitario, 2);
		tableColumnEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
		tableColumnEstoqueMinimo.setCellValueFactory(new PropertyValueFactory<>("estoqueMinimo"));
		tableColumnDataCadastro.setCellValueFactory(new PropertyValueFactory<>("dataCad"));
		Utils.formatTableColumnDate(tableColumnDataCadastro, "dd/MM/yyyy");
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewProduto.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		List<Produto> list = objBiz.listarProdutos();
		obsList = FXCollections.observableArrayList(list);
		tableViewProduto.setItems(obsList);
		initRemoveButtons();
	}
	
	private void createDialogForm(Produto obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FormProdutoController controller = loader.getController();
			controller.setProduto(obj);
			controller.setObjBiz(new Comercial());
			//controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			// para carregar uma janela na frente de outra, é necessário um novo stage
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Dados para cadastro");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false); // não poder alterar o tamanho da janela
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL); // não pode usar a janela anterior antes de fechar essa
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}
	
	private void initRemoveButtons() {
		tableColumnDeletar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDeletar.setCellFactory(param -> new TableCell<Produto, Produto>() {
		private final Button button = new Button("deletar");
			@Override
			protected void updateItem(Produto obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Produto obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Deseja deletar o produto?");
		
		if (result.get() == ButtonType.OK) {
			if (objBiz == null) {
				throw new IllegalStateException("ObjBiz está nulo!");
			}
			try {
				objBiz.deletarProduto(obj.getCodigo());
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao deletar", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
