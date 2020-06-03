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
import model.entities.Cliente;

public class ListaClienteController  implements Initializable {

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
		tableColumnCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
		tableColumnLimiteCredito.setCellValueFactory(new PropertyValueFactory<>("limiteCredito"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewCliente.prefHeightProperty().bind(stage.heightProperty());
	}
}
