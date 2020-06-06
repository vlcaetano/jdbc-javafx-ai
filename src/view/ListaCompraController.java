package view;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import controller.Comercial;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Compra;
import model.exceptions.SisComException;
import view.listeners.DataChangeListener;
import view.util.Alerts;
import view.util.Utils;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class ListaCompraController  implements Initializable, DataChangeListener {
/**
 * Classe ListaCompraController - Controller da view ListaCompra.fxml
 */
	private Comercial objBiz;
	
	@FXML
	private TableView<Compra> tableViewCompra;
	
	@FXML
	private TableColumn<Compra, Integer> tableColumnCodCompra;
	@FXML
	private TableColumn<Compra, String> tableColumnNomeFornecedor;
	@FXML
	private TableColumn<Compra, Date> tableColumnDataCompra;
	@FXML
	private TableColumn<Compra, Double> tableColumnValorTotal;

	@FXML
	private TableColumn<Compra, Compra> tableColumnDeletar;
	@FXML
	private TableColumn<Compra, Compra> tableColumnDetalhes;
	
	
	@FXML
	private Button btNovo;
	@FXML
	private DatePicker dpInicio;
	@FXML
	private DatePicker dpFinal;
	
	@FXML
	private Button btMostrarTodos;
	@FXML
	private Button btFiltrar;
	
	private ObservableList<Compra> obsList;
	
	@FXML
	public void onBtNovoAction(ActionEvent event) {
		
		Stage parentStage = Utils.currentStage(event);
		Compra obj = new Compra();
		createDialogForm(obj, "/view/FormCompra.fxml", parentStage);
	}
	
	@FXML
	public void onBtMostrarTodosAction() {
		updateTableView();
		dpInicio.setValue(null);
		dpFinal.setValue(null);
	}
	
	@FXML
	public void onBtFiltrarAction() {
		try {
			Instant instantInicial = Instant.from(dpInicio.getValue().atStartOfDay(ZoneId.systemDefault()));
			Instant instantFinal = Instant.from(dpFinal.getValue().atStartOfDay(ZoneId.systemDefault()));
			
			if(instantInicial.isAfter(instantFinal)) {
				throw new SisComException("A data inicial não pode ser depois da data final");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			String dataInicioStr = sdf.format(Date.from(instantInicial));
			String dataFinalStr = sdf.format(Date.from(instantFinal));
			
			List<Compra> list = objBiz.listarCompras(dataInicioStr, dataFinalStr);
			if (list != null) {
				obsList = FXCollections.observableArrayList(list);
				tableViewCompra.setItems(obsList);
				initRemoveButtons();
				initDetailsButtons();
			} else {
				tableViewCompra.setItems(null);
				Alerts.showAlert("Não foram encontrados resultados", 
						null, "Sem compras registradas no período solicitado", AlertType.INFORMATION);
			}
		} catch (NullPointerException e) {
			Alerts.showAlert("Erro ao converter as datas", null, "Verifique se as datas estão preenchidas", AlertType.WARNING);
		} catch (SisComException e) {
			Alerts.showAlert("Erro ao converter as datas", null, e.getMensagemErro(), AlertType.WARNING);
		}
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
		tableColumnCodCompra.setStyle("-fx-alignment: CENTER;");
		tableColumnDataCompra.setStyle("-fx-alignment: CENTER;");
		tableColumnValorTotal.setStyle("-fx-alignment: CENTER;");
		tableColumnDeletar.setStyle("-fx-alignment: CENTER;");
		tableColumnDetalhes.setStyle("-fx-alignment: CENTER;");
		
		tableColumnCodCompra.setCellValueFactory(new PropertyValueFactory<>("numCompra"));
		tableColumnNomeFornecedor.setCellValueFactory(new PropertyValueFactory<>("nomeFornecedor"));
		tableColumnDataCompra.setCellValueFactory(new PropertyValueFactory<>("dataCompra"));
		Utils.formatTableColumnDate(tableColumnDataCompra, "dd/MM/yyyy");
		tableColumnValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
		Utils.formatTableColumnDouble(tableColumnValorTotal, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewCompra.prefHeightProperty().bind(stage.heightProperty());
	}
	
	/**
	 * Método para atualizar dados da tabela
	 */
	public void updateTableView() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		List<Compra> list = objBiz.listarCompras(null, null);
		obsList = FXCollections.observableArrayList(list);
		tableViewCompra.setItems(obsList);
		initRemoveButtons();
		initDetailsButtons();
	}
	
	/**
	 * Método para criar nova janela com formulário
	 * @param obj
	 * @param absoluteName
	 * @param parentStage
	 */
	private void createDialogForm(Compra obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FormCompraController controller = loader.getController();
			controller.setCompra(obj);
			controller.setObjBiz(new Comercial());
			controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.getIcons().add(new Image("/view/imagens/carrinhocomprasicon16.png"));
			dialogStage.setTitle("Dados da compra");
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
		tableColumnDeletar.setCellFactory(param -> new TableCell<Compra, Compra>() {
		private final Button button = new Button("deletar");
			@Override
			protected void updateItem(Compra obj, boolean empty) {
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
	 * Método para deletar compra
	 * @param obj
	 */
	private void removeEntity(Compra obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Deseja deletar a compra?");
		
		if (result.get() == ButtonType.OK) {
			if (objBiz == null) {
				throw new IllegalStateException("ObjBiz está nulo!");
			}
			try {
				objBiz.deletarCompra(obj.getNumCompra());
				updateTableView();
			} catch (SisComException e) {
				Alerts.showAlert("Erro ao deletar", null, e.getMensagemErro(), AlertType.ERROR);
			}
		}
	}
	
	/**
	 * Método para criar os botões de detalhes
	 */
	private void initDetailsButtons() {
		tableColumnDetalhes.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDetalhes.setCellFactory(param -> new TableCell<Compra, Compra>() {
		private final Button button = new Button("detalhes");
			@Override
			protected void updateItem(Compra obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDetails(obj, "/view/DetalhesCompra.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	/**
	 * Método para criar janela com os detalhes da compra
	 * @param obj
	 * @param absoluteName
	 * @param parentStage
	 */
	private void createDetails(Compra obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ListaDetalhesCompraController controller = loader.getController();
			controller.setCompra(obj);
			controller.setObjBiz(new Comercial());
			controller.updateTableView();

			Stage dialogStage = new Stage();
			dialogStage.getIcons().add(new Image("/view/imagens/detalhesicon.png"));
			dialogStage.setTitle("Detalhes da compra");
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
}
