package view;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Fornecedor;
import view.listeners.DataChangeListener;
import view.util.Alerts;
import view.util.Utils;

public class ListaFornecedorController  implements Initializable, DataChangeListener {
	private Comercial objBiz;
	
	@FXML
	private TableView<Fornecedor> tableViewFornecedor;
	
	@FXML
	private TableColumn<Fornecedor, Integer> tableColumnCodigo;
	@FXML
	private TableColumn<Fornecedor, String> tableColumnNome;
	@FXML
	private TableColumn<Fornecedor, String> tableColumnTelefone;
	@FXML
	private TableColumn<Fornecedor, String> tableColumnEmail;
	@FXML
	private TableColumn<Fornecedor, Date> tableColumnDataCadastro;
	@FXML
	private TableColumn<Fornecedor, String> tableColumnCnpj;
	@FXML
	private TableColumn<Fornecedor, String> tableColumnNomeContato;
	@FXML
	private TableColumn<Fornecedor, Fornecedor> tableColumnDeletar;
	
	@FXML
	private Button btNovo;
	
	private ObservableList<Fornecedor> obsList;
	
	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Fornecedor obj = new Fornecedor();
		createDialogForm(obj, "/view/FormFornecedor.fxml", parentStage);
	}
	
	public void setObjBiz(Comercial objBiz) {
		this.objBiz = objBiz;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnDataCadastro.setCellValueFactory(new PropertyValueFactory<>("dataCad"));
		Utils.formatTableColumnDate(tableColumnDataCadastro, "dd/MM/yyyy");
		tableColumnCnpj.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
		tableColumnNomeContato.setCellValueFactory(new PropertyValueFactory<>("nomeContato"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewFornecedor.prefHeightProperty().bind(stage.heightProperty());
	}
	
	public void updateTableView() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		List<Fornecedor> list = objBiz.listarFornecedores();
		obsList = FXCollections.observableArrayList(list);
		tableViewFornecedor.setItems(obsList);
		initRemoveButtons();
	}
	
	private void createDialogForm(Fornecedor obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FormFornecedorController controller = loader.getController();
			controller.setFornecedor(obj);
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
		tableColumnDeletar.setCellFactory(param -> new TableCell<Fornecedor, Fornecedor>() {
		private final Button button = new Button("deletar");
			@Override
			protected void updateItem(Fornecedor obj, boolean empty) {
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
	
	private void removeEntity(Fornecedor obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Deseja deletar o fornecedor?");
		
		if (result.get() == ButtonType.OK) {
			if (objBiz == null) {
				throw new IllegalStateException("ObjBiz está nulo!");
			}
			try {
				objBiz.deletarFonecedor(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao deletar", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
