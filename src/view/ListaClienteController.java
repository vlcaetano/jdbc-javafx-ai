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
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Cliente;
import view.listeners.DataChangeListener;
import view.util.Alerts;
import view.util.Utils;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class ListaClienteController  implements Initializable, DataChangeListener {
/**
 * Classe ListaClienteController - Controller da view ListaCliente.fxml
 */
	private Comercial objBiz;
	
	@FXML
	private TableView<Cliente> tableViewCliente;
	
	@FXML
	private TableColumn<Cliente, Integer> tableColumnCodigo;
	@FXML
	private TableColumn<Cliente, String> tableColumnNome;
	@FXML
	private TableColumn<Cliente, String> tableColumnTelefone;
	@FXML
	private TableColumn<Cliente, String> tableColumnEmail;
	@FXML
	private TableColumn<Cliente, Date> tableColumnDataCadastro;
	@FXML
	private TableColumn<Cliente, String> tableColumnCpf;
	@FXML
	private TableColumn<Cliente, Double> tableColumnLimiteCredito;
	@FXML
	private TableColumn<Cliente, Cliente> tableColumnDeletar;
	
	@FXML
	private TextField txtFiltrar;
	@FXML
	public void onTxtFiltrarKeyTyped() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		List<Cliente> list = objBiz.filtrarClientes(txtFiltrar.getText());
		if (list != null) {
			obsList = FXCollections.observableArrayList(list);
			tableViewCliente.setItems(obsList);
			initRemoveButtons();
		} else {
			tableViewCliente.setItems(null);
		}
		
	}
	
	@FXML
	private Button btNovo;
	
	private ObservableList<Cliente> obsList;
	
	@FXML
	public void onBtNovoAction(ActionEvent event) {
		txtFiltrar.setText("");
		updateTableView();
		
		Stage parentStage = Utils.currentStage(event);
		Cliente obj = new Cliente();
		createDialogForm(obj, "/view/FormCliente.fxml", parentStage);
	}
	
	public void setObjBiz(Comercial objBiz) {
		this.objBiz = objBiz;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	/**
	 * Método para inicializar campos da view
	 */
	private void initializeNodes() {
		tableColumnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnDataCadastro.setCellValueFactory(new PropertyValueFactory<>("dataCad"));
		Utils.formatTableColumnDate(tableColumnDataCadastro, "dd/MM/yyyy");
		tableColumnCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
		tableColumnLimiteCredito.setCellValueFactory(new PropertyValueFactory<>("limiteCredito"));
		Utils.formatTableColumnDouble(tableColumnLimiteCredito, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewCliente.prefHeightProperty().bind(stage.heightProperty());
	}
	
	/**
	 * Método para atualizar dados da tabela
	 */
	public void updateTableView() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		List<Cliente> list = objBiz.listarClientes();
		obsList = FXCollections.observableArrayList(list);
		tableViewCliente.setItems(obsList);
		initRemoveButtons();
	}
	
	/**
	 * Método para criar nova janela com formulário
	 * @param obj
	 * @param absoluteName
	 * @param parentStage
	 */
	private void createDialogForm(Cliente obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FormClienteController controller = loader.getController();
			controller.setCliente(obj);
			controller.setObjBiz(new Comercial());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Dados para cadastro");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
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
	
	/**
	 * Método para criar os botões de deletar
	 */
	private void initRemoveButtons() {
		tableColumnDeletar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDeletar.setCellFactory(param -> new TableCell<Cliente, Cliente>() {
		private final Button button = new Button("deletar");
			@Override
			protected void updateItem(Cliente obj, boolean empty) {
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
	
	/**
	 * Método para deletar cliente
	 * @param obj
	 */
	private void removeEntity(Cliente obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Deseja deletar o cliente?");
		
		if (result.get() == ButtonType.OK) {
			if (objBiz == null) {
				throw new IllegalStateException("ObjBiz está nulo!");
			}
			try {
				objBiz.deletarCliente(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao deletar", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
