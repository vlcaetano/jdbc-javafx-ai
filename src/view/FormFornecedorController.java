package view;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import controller.Comercial;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Fornecedor;
import model.exceptions.SisComException;
import model.exceptions.ValidationException;
import view.listeners.DataChangeListener;
import view.util.Alerts;
import view.util.Constraints;
import view.util.Utils;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class FormFornecedorController implements Initializable {
/**
 * Classe FormFornecedorController - Controller da view FormFornecedor.fxml
 */
	private Fornecedor entidade;
	
	private Comercial objBiz;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtTelefone;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private TextField txtCnpj;
	
	@FXML
	private TextField txtNomeContato;
	
	@FXML
	private Label labelDataCadastro;
	
	@FXML
	private Label labelErrorNome;
	
	@FXML
	private Label labelErrorTelefone;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorCnpj;
	
	@FXML
	private Label labelErrorNomeContato;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	public void setFornecedor(Fornecedor entidade) {
		this.entidade = entidade;
	}
	
	public void setObjBiz(Comercial objBiz) {
		this.objBiz = objBiz;
	}
	
	/**
	 * Método para inscrever como DataChangeListener a classe que chamou essa
	 * @param listener
	 */
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		if (entidade == null) {
			throw new IllegalStateException("Entidade está nula!");
		}
		try {
			entidade = getFormData();
			objBiz.inserirPessoa(entidade);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (SisComException e) {
			Alerts.showAlert("Erro salvando o fornecedor", null, e.getMensagemErro(), AlertType.ERROR);
		}		
	}

	/**
	 * Método para notificar a classe cadastrada que houve mudança de dados
	 */
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	/**
	 * Método para recuperar os dados do fornecedor cadastrados no formulário
	 * @return objeto Fornecedor
	 */
	private Fornecedor getFormData() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Fornecedor obj = new Fornecedor();
		
		ValidationException exception = new ValidationException("Erro na validação");
		obj.setCodigo(null);

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "Preenchimento obrigatório");
		}
		obj.setNome(txtNome.getText());
		
		if (txtTelefone.getText() == null || txtTelefone.getText().trim().equals("")) {
			exception.addError("telefone", "Preenchimento obrigatório");
		}
		obj.setTelefone(txtTelefone.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Preenchimento obrigatório");
		}
		obj.setEmail(txtEmail.getText());
		
		if (txtCnpj.getText() == null || txtCnpj.getText().trim().equals("")) {
			exception.addError("cnpj", "Preenchimento obrigatório");
		}
		obj.setCnpj(txtCnpj.getText());
		
		if (txtNomeContato.getText() == null || txtNomeContato.getText().trim().equals("")) {
			exception.addError("nomeContato", "Preenchimento obrigatório");
		}
		obj.setNomeContato(txtNomeContato.getText());
		
		try {
			obj.setDataCad(sdf.parse(labelDataCadastro.getText()));
		} catch (ParseException e) {
			Alerts.showAlert("Erro recuperando a data", null, e.getMessage(), AlertType.ERROR);
		}
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	/**
	 * Método para limitar o que pode ser digitado em cada campo
	 */
	private void initializeNodes() {
		Constraints.setTextFieldMaxLength(txtNome, 60);
		Constraints.setTextFieldMaxLength(txtTelefone, 20);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Constraints.setTextFieldMaxLength(txtCnpj, 30);
		Constraints.setTextFieldMaxLength(txtNomeContato, 60);
	}
	
	/**
	 * Método para atualizar dados do formulário
	 */
	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está com valor null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		txtNome.setText(entidade.getNome());
		Locale.setDefault(Locale.US);
		txtTelefone.setText(entidade.getTelefone());
		txtEmail.setText(entidade.getEmail());
		txtCnpj.setText(entidade.getCnpj());
		txtNomeContato.setText(entidade.getNomeContato());
		if (entidade.getDataCad() != null) {
			labelDataCadastro.setText(sdf.format(entidade.getDataCad()));
		} else {
			labelDataCadastro.setText(sdf.format(new Date()));
		}
	}
	
	/**
	 * Método para dar set nas Labels de erro do formulário
	 * @param errors
	 */
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet(); 
		
		labelErrorNome.setText(fields.contains("nome") ? errors.get("nome") : "");
		labelErrorTelefone.setText(fields.contains("telefone") ? errors.get("telefone") : "");
		labelErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");
		labelErrorCnpj.setText(fields.contains("cnpj") ? errors.get("cnpj") : "");
		labelErrorNomeContato.setText(fields.contains("nomeContato") ? errors.get("nomeContato") : "");
	}
}
