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
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Venda;
import model.exceptions.SisComException;
import view.listeners.DataChangeListener;
import view.util.Alerts;
import view.util.Utils;

public class ListaVendaController  implements Initializable, DataChangeListener {

	private Comercial objBiz;
	
	@FXML
	private TableView<Venda> tableViewVenda;
	
	@FXML
	private TableColumn<Venda, Integer> tableColumnCodVenda;
	@FXML
	private TableColumn<Venda, String> tableColumnNomeCliente;
	@FXML
	private TableColumn<Venda, String> tableColumnNomeVendedor;
	@FXML
	private TableColumn<Venda, Integer> tableColumnFormaPagamento;
	@FXML
	private TableColumn<Venda, Date> tableColumnDataVenda;
	@FXML
	private TableColumn<Venda, Double> tableColumnValorTotal;

	@FXML
	private TableColumn<Venda, Venda> tableColumnDeletar;
	@FXML
	private TableColumn<Venda, Venda> tableColumnDetalhes;
	
	@FXML
	private DatePicker dpInicio;
	@FXML
	private DatePicker dpFinal;
	
	@FXML
	private Button btMostrarTodos;
	@FXML
	private Button btFiltrar;
	@FXML
	private Button btNovo;
	
	private ObservableList<Venda> obsList;
	
	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Venda obj = new Venda();
		createDialogForm(obj, "/view/FormVenda.fxml", parentStage);
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
			
			List<Venda> list = objBiz.listarVendas(dataInicioStr, dataFinalStr);
			if (list != null) {
				obsList = FXCollections.observableArrayList(list);
				tableViewVenda.setItems(obsList);
				initRemoveButtons();
				initDetailsButtons();
			} else {
				tableViewVenda.setItems(null);
				Alerts.showAlert("Não foram encontrados resultados", 
						null, "Sem vendas registradas no período solicitado", AlertType.INFORMATION);
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

	private void initializeNodes() {
		tableColumnCodVenda.setCellValueFactory(new PropertyValueFactory<>("numVenda"));
		tableColumnNomeCliente.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
		tableColumnNomeVendedor.setCellValueFactory(new PropertyValueFactory<>("nomeVendedor"));
		tableColumnFormaPagamento.setCellValueFactory(new PropertyValueFactory<>("formaPagto"));
		tableColumnDataVenda.setCellValueFactory(new PropertyValueFactory<>("dataVenda"));
		Utils.formatTableColumnDate(tableColumnDataVenda, "dd/MM/yyyy");
		tableColumnValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
		Utils.formatTableColumnDouble(tableColumnValorTotal, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewVenda.prefHeightProperty().bind(stage.heightProperty());
	}
	
	public void updateTableView() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		List<Venda> list = objBiz.listarVendas(null, null);
		obsList = FXCollections.observableArrayList(list);
		tableViewVenda.setItems(obsList);
		initRemoveButtons();
		initDetailsButtons();
	}
	
	private void createDialogForm(Venda obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FormVendaController controller = loader.getController();
			controller.setVenda(obj);
			controller.setObjBiz(new Comercial());
			controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Dados da venda");
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
	
	private void initRemoveButtons() {
		tableColumnDeletar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDeletar.setCellFactory(param -> new TableCell<Venda, Venda>() {
		private final Button button = new Button("deletar");
			@Override
			protected void updateItem(Venda obj, boolean empty) {
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
	
	private void removeEntity(Venda obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Deseja deletar a venda?");
		
		if (result.get() == ButtonType.OK) {
			if (objBiz == null) {
				throw new IllegalStateException("ObjBiz está nulo!");
			}
			try {
				objBiz.deletarVenda(obj.getNumVenda());
				updateTableView();
			} catch (SisComException e) {
				Alerts.showAlert("Erro ao deletar", null, e.getMensagemErro(), AlertType.ERROR);
			}
		}
	}
	
	private void initDetailsButtons() {
		tableColumnDetalhes.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDetalhes.setCellFactory(param -> new TableCell<Venda, Venda>() {
		private final Button button = new Button("detalhes");
			@Override
			protected void updateItem(Venda obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDetails(obj, "/view/DetalhesVenda.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	private void createDetails(Venda obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ListaDetalhesVendaController controller = loader.getController();
			controller.setVenda(obj);
			controller.setObjBiz(new Comercial());
			controller.updateTableView();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Detalhes da venda");
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
