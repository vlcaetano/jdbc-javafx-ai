package view;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Produto;

public class ListaProdutoController implements Initializable {
	
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
	private Button btNovo;
	
	@FXML
	public void onBtNovoAction() {
		System.out.println("onBtNovoAction");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnPrecoUnitario.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
		tableColumnEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
		tableColumnEstoqueMinimo.setCellValueFactory(new PropertyValueFactory<>("estoqueMinimo"));
		tableColumnDataCadastro.setCellValueFactory(new PropertyValueFactory<>("dataCad"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewProduto.prefHeightProperty().bind(stage.heightProperty());
	}

}
