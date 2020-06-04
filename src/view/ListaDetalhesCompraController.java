package view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import controller.Comercial;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Compra;
import model.entities.ItemCompra;
import view.util.Utils;

public class ListaDetalhesCompraController  implements Initializable {

	private Comercial objBiz;
	
	private Compra entidade;
	
	@FXML
	private TableView<ItemCompra> tableViewItemCompra;
	
	@FXML
	private TableColumn<ItemCompra, String> tableColumnNomeProduto;
	@FXML
	private TableColumn<ItemCompra, Integer> tableColumnQuantidade;
	@FXML
	private TableColumn<ItemCompra, Double> tableColumnValorCompra;
	
	
	private ObservableList<ItemCompra> obsList;
	
	public void setCompra(Compra entidade) {
		this.entidade = entidade;
	}
	
	public void setObjBiz(Comercial objBiz) {
		this.objBiz = objBiz;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
		tableColumnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantCompra"));
		tableColumnValorCompra.setCellValueFactory(new PropertyValueFactory<>("valorCompra"));
		Utils.formatTableColumnDouble(tableColumnValorCompra, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewItemCompra.prefHeightProperty().bind(stage.heightProperty());
	}
	
	public void updateTableView() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		List<ItemCompra> list = objBiz.listarItemCompras(entidade.getNumCompra());
		obsList = FXCollections.observableArrayList(list);
		tableViewItemCompra.setItems(obsList);
	}
}
