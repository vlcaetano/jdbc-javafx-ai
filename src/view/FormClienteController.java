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
import db.DbException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Cliente;
import model.exceptions.SisComException;
import model.exceptions.ValidationException;
import view.listeners.DataChangeListener;
import view.util.Alerts;
import view.util.Constraints;
import view.util.Utils;

public class FormClienteController implements Initializable {

	private Cliente entidade;
	
	private Comercial objBiz;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtTelefone;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private TextField txtCpf;
	
	@FXML
	private TextField txtLimiteCredito;
	
	@FXML
	private Label labelDataCadastro;
	
	@FXML
	private Label labelErrorNome;
	
	@FXML
	private Label labelErrorTelefone;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorCpf;
	
	@FXML
	private Label labelErrorLimiteCredito;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	public void setCliente(Cliente entidade) {
		this.entidade = entidade;
	}
	
	public void setObjBiz(Comercial objBiz) {
		this.objBiz = objBiz;
	}
	
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
		} catch (DbException e) {
			Alerts.showAlert("Erro salvando o objeto", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (SisComException e) {
			Alerts.showAlert("Erro salvando o objeto", null, e.getMessage(), AlertType.ERROR);
		}		
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Cliente getFormData() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Cliente obj = new Cliente();
		
		ValidationException exception = new ValidationException("Erro na validação");
		//obj.setCodigo(Utils.tryParseToInt(txtCodigo.getText()));
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
		
		if (txtCpf.getText() == null || txtCpf.getText().trim().equals("")) {
			exception.addError("cpf", "Preenchimento obrigatório");
		}
		obj.setCpf(txtCpf.getText());
		
		if (txtLimiteCredito.getText() == null || txtLimiteCredito.getText().trim().equals("")) {
			exception.addError("limiteCredito", "Preenchimento obrigatório");
		}
		obj.setLimiteCredito(Utils.tryParseToDouble(txtLimiteCredito.getText()));
		
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
	
	private void initializeNodes() {
		Constraints.setTextFieldMaxLength(txtNome, 60);
		Constraints.setTextFieldMaxLength(txtTelefone, 20);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Constraints.setTextFieldInteger(txtCpf);
		Constraints.setTextFieldDouble(txtLimiteCredito);
		
		//initializeComboBoxDepartment();
	}
	
	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está com valor null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		txtNome.setText(entidade.getNome());
		txtTelefone.setText(entidade.getTelefone());
		txtEmail.setText(entidade.getEmail());
		txtCpf.setText(entidade.getCpf());
		Locale.setDefault(Locale.US);
		txtLimiteCredito.setText(String.format("%.2f", entidade.getLimiteCredito()));
		if (entidade.getDataCad() != null) {
			labelDataCadastro.setText(sdf.format(entidade.getDataCad()));
		} else {
			labelDataCadastro.setText(sdf.format(new Date()));
		}
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet(); 
		
		labelErrorNome.setText(fields.contains("nome") ? errors.get("nome") : "");
		labelErrorTelefone.setText(fields.contains("telefone") ? errors.get("telefone") : "");
		labelErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");
		labelErrorCpf.setText(fields.contains("cpf") ? errors.get("cpf") : "");
		labelErrorLimiteCredito.setText(fields.contains("limiteCredito") ? errors.get("limiteCredito") : "");
	}
}
