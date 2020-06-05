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
import model.entities.Venda;
import model.entities.ItemVenda;
import view.util.Utils;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class ListaDetalhesVendaController  implements Initializable {
/**
 * Classe ListaDetalhesVendaController - Controller da view DetalhesVenda.fxml
 */
	private Comercial objBiz;
	
	private Venda entidade;
	
	@FXML
	private TableView<ItemVenda> tableViewItemVenda;
	
	@FXML
	private TableColumn<ItemVenda, String> tableColumnNomeProduto;
	@FXML
	private TableColumn<ItemVenda, Integer> tableColumnQuantidade;
	@FXML
	private TableColumn<ItemVenda, Double> tableColumnValorVenda;
	
	
	private ObservableList<ItemVenda> obsList;
	
	public void setVenda(Venda entidade) {
		this.entidade = entidade;
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
		tableColumnNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
		tableColumnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantVenda"));
		tableColumnValorVenda.setCellValueFactory(new PropertyValueFactory<>("valorVenda"));
		Utils.formatTableColumnDouble(tableColumnValorVenda, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewItemVenda.prefHeightProperty().bind(stage.heightProperty());
	}
	
	/**
	 * Método para atualizar dados da tabela
	 */
	public void updateTableView() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		List<ItemVenda> list = objBiz.listarItemVendas(entidade.getNumVenda());
		obsList = FXCollections.observableArrayList(list);
		tableViewItemVenda.setItems(obsList);
	}
}
