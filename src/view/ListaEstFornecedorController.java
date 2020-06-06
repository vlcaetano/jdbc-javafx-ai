package view;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import controller.Comercial;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Fornecedor;
import model.exceptions.SisComException;
import view.util.Alerts;
import view.util.Utils;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class ListaEstFornecedorController  implements Initializable {
/**
 * Classe ListaEstFornecedorController - Controller da view ListaEstFornecedor.fxml
 */
	private Comercial objBiz;
	
	@FXML
	private TableView<Fornecedor> tableViewEstFornecedor;
	
	@FXML
	private TableColumn<Fornecedor, String> tableColumnNomeFornecedor;
	@FXML
	private TableColumn<Fornecedor, Integer> tableColumnQtdCompras;
	@FXML
	private TableColumn<Fornecedor, Double> tableColumnVlrTotal;
	
	@FXML
	private DatePicker dpInicio;
	@FXML
	private DatePicker dpFinal;
	
	@FXML
	private Button btMostrarTodos;
	@FXML
	private Button btFiltrar;
	
	private ObservableList<Fornecedor> obsList;
	
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
				throw new SisComException("Data inicial não pode ser depois da data final");
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			String dataInicioStr = sdf.format(Date.from(instantInicial));
			String dataFinalStr = sdf.format(Date.from(instantFinal));
			
			List<Fornecedor> list = objBiz.estatisticasFornecedores(dataInicioStr, dataFinalStr);
			if (list != null) {
				obsList = FXCollections.observableArrayList(list);
				tableViewEstFornecedor.setItems(obsList);
			} else {
				tableViewEstFornecedor.setItems(null);
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
	
	/**
	 * Método para inicializar campos da view
	 */
	private void initializeNodes() {
		tableColumnQtdCompras.setStyle("-fx-alignment: CENTER;");
		tableColumnVlrTotal.setStyle("-fx-alignment: CENTER;");
		
		tableColumnNomeFornecedor.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnQtdCompras.setCellValueFactory(new PropertyValueFactory<>("qtdCompras"));
		tableColumnVlrTotal.setCellValueFactory(new PropertyValueFactory<>("vlrTotal"));
		Utils.formatTableColumnDouble(tableColumnVlrTotal, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewEstFornecedor.prefHeightProperty().bind(stage.heightProperty());
	}
	
	/**
	 * Método para atualizar dados da tabela
	 */
	public void updateTableView() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		List<Fornecedor> list = objBiz.estatisticasFornecedores(null, null);
		obsList = FXCollections.observableArrayList(list);
		tableViewEstFornecedor.setItems(obsList);
	}
}
