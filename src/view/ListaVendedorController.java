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
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Vendedor;
import view.listeners.DataChangeListener;
import view.util.Alerts;
import view.util.Utils;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class ListaVendedorController  implements Initializable, DataChangeListener {
/**
 * Classe ListaVendedorController - Controller da view ListaVendedor.fxml
 */
	private Comercial objBiz;
	
	@FXML
	private TableView<Vendedor> tableViewVendedor;
	
	@FXML
	private TableColumn<Vendedor, Integer> tableColumnCodigo;
	@FXML
	private TableColumn<Vendedor, String> tableColumnNome;
	@FXML
	private TableColumn<Vendedor, String> tableColumnTelefone;
	@FXML
	private TableColumn<Vendedor, String> tableColumnEmail;
	@FXML
	private TableColumn<Vendedor, Date> tableColumnDataCadastro;
	@FXML
	private TableColumn<Vendedor, String> tableColumnCpf;
	@FXML
	private TableColumn<Vendedor, Double> tableColumnMetaMensal;
	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnDeletar;
	
	@FXML
	private TextField txtFiltrar;
	@FXML
	public void onTxtFiltrarKeyTyped() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz est� nulo!");
		}
		List<Vendedor> list = objBiz.filtrarVendedores(txtFiltrar.getText());
		if (list != null) {
			obsList = FXCollections.observableArrayList(list);
			tableViewVendedor.setItems(obsList);
			initRemoveButtons();
		} else {
			tableViewVendedor.setItems(null);
		}
		
	}
	
	@FXML
	private Button btNovo;
	
	private ObservableList<Vendedor> obsList;
	
	@FXML
	public void onBtNovoAction(ActionEvent event) {
		txtFiltrar.setText("");
		updateTableView();
		
		Stage parentStage = Utils.currentStage(event);
		Vendedor obj = new Vendedor();
		createDialogForm(obj, "/view/FormVendedor.fxml", parentStage);
	}
	
	public void setObjBiz(Comercial objBiz) {
		this.objBiz = objBiz;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	/**
	 * M�todo para inicializar campos da view
	 */
	private void initializeNodes() {
		tableColumnCodigo.setStyle("-fx-alignment: CENTER;");
		tableColumnTelefone.setStyle("-fx-alignment: CENTER;");
		tableColumnDataCadastro.setStyle("-fx-alignment: CENTER;");
		tableColumnCpf.setStyle("-fx-alignment: CENTER;");
		tableColumnMetaMensal.setStyle("-fx-alignment: CENTER;");
		tableColumnDeletar.setStyle("-fx-alignment: CENTER;");
		
		tableColumnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnDataCadastro.setCellValueFactory(new PropertyValueFactory<>("dataCad"));
		Utils.formatTableColumnDate(tableColumnDataCadastro, "dd/MM/yyyy");
		tableColumnCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
		tableColumnMetaMensal.setCellValueFactory(new PropertyValueFactory<>("metaMensal"));
		Utils.formatTableColumnDouble(tableColumnMetaMensal, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewVendedor.prefHeightProperty().bind(stage.heightProperty());
	}
	
	/**
	 * M�todo para atualizar dados da tabela
	 */
	public void updateTableView() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz est� nulo!");
		}
		List<Vendedor> list = objBiz.listarVendedores();
		obsList = FXCollections.observableArrayList(list);
		tableViewVendedor.setItems(obsList);
		initRemoveButtons();
	}
	
	/**
	 * M�todo para criar nova janela com formul�rio
	 * @param obj
	 * @param absoluteName
	 * @param parentStage
	 */
	private void createDialogForm(Vendedor obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FormVendedorController controller = loader.getController();
			controller.setVendedor(obj);
			controller.setObjBiz(new Comercial());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			
			Stage dialogStage = new Stage();
			dialogStage.getIcons().add(new Image("/view/imagens/pessoaicon.png"));
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
	 * M�todo para criar os bot�es de deletar
	 */
	private void initRemoveButtons() {
		tableColumnDeletar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDeletar.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
		private final Button button = new Button("deletar");
			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
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
	 * M�todo para deletar vendedor
	 * @param obj
	 */
	private void removeEntity(Vendedor obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirma��o", "Deseja deletar o vendedor?");
		
		if (result.get() == ButtonType.OK) {
			if (objBiz == null) {
				throw new IllegalStateException("ObjBiz est� nulo!");
			}
			try {
				objBiz.deletarVendedor(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao deletar", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
