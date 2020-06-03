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
import model.entities.Fornecedor;

public class ListaFornecedorController  implements Initializable {

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
		tableColumnTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnDataCadastro.setCellValueFactory(new PropertyValueFactory<>("dataCad"));
		tableColumnCnpj.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
		tableColumnNomeContato.setCellValueFactory(new PropertyValueFactory<>("nomeContato"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewFornecedor.prefHeightProperty().bind(stage.heightProperty());
	}
}
