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
import view.listeners.DataChangeListener;
import view.util.Alerts;
import view.util.Constraints;
import view.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Produto;
import model.exceptions.ValidationException;

public class FormProdutoController implements Initializable {
	
	private Produto entidade;
	
	private Comercial objBiz;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtCodigo;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtPrecoUnitario;
	
	@FXML
	private TextField txtEstoque;
	
	@FXML
	private TextField txtEstoqueMinimo;
	
	@FXML
	private TextField txtDataCadastro;
	
	@FXML
	private Label labelErrorNome;
	
	@FXML
	private Label labelErrorPrecoUnitario;
	
	@FXML
	private Label labelErrorEstoque;
	
	@FXML
	private Label labelErrorEstoqueMinimo;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	public void setProduto(Produto entidade) {
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
			objBiz.inserirProduto(entidade);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro salvando o objeto", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}		
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Produto getFormData() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Produto obj = new Produto();
		
		ValidationException exception = new ValidationException("Erro na validação");
		//obj.setCodigo(Utils.tryParseToInt(txtCodigo.getText()));
		obj.setCodigo(null);

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "Preenchimento obrigatório");
		}
		obj.setNome(txtNome.getText());
		
		if (txtPrecoUnitario.getText() == null || txtPrecoUnitario.getText().trim().equals("")) {
			exception.addError("precoUnitario", "Preenchimento obrigatório");
		}
		obj.setPrecoUnitario(Utils.tryParseToDouble(txtPrecoUnitario.getText()));
		
		if (txtEstoque.getText() == null || txtEstoque.getText().trim().equals("")) {
			exception.addError("estoque", "Preenchimento obrigatório");
		}
		obj.setEstoque(Utils.tryParseToInt(txtEstoque.getText()));
		
		if (txtEstoqueMinimo.getText() == null || txtEstoqueMinimo.getText().trim().equals("")) {
			exception.addError("estoqueMinimo", "Preenchimento obrigatório");
		}
		obj.setEstoqueMinimo(Utils.tryParseToInt(txtEstoqueMinimo.getText()));
		
		try {
			obj.setDataCad(sdf.parse(txtDataCadastro.getText()));
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
		Constraints.setTextFieldInteger(txtCodigo);
		Constraints.setTextFieldMaxLength(txtNome, 60);
		Constraints.setTextFieldDouble(txtPrecoUnitario);
		Constraints.setTextFieldInteger(txtEstoque);
		Constraints.setTextFieldInteger(txtEstoqueMinimo);
		Constraints.setTextFieldMaxLength(txtDataCadastro, 10); //Deixar assim mesmo ou passar para datePicker??
		//Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		//initializeComboBoxDepartment();
	}
	
	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está com valor null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		txtCodigo.setText(String.valueOf(entidade.getCodigo()));
		txtNome.setText(entidade.getNome());
		Locale.setDefault(Locale.US);
		txtPrecoUnitario.setText(String.format("%.2f", entidade.getPrecoUnitario()));
		txtEstoque.setText(String.valueOf(entidade.getEstoque()));
		txtEstoqueMinimo.setText(String.valueOf(entidade.getEstoqueMinimo()));
		if (entidade.getDataCad() != null) {
			txtDataCadastro.setText(sdf.format(entidade.getDataCad()));
		} else {
			txtDataCadastro.setText(sdf.format(new Date()));
		}
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet(); 
		
		labelErrorNome.setText(fields.contains("nome") ? errors.get("nome") : "");
		labelErrorPrecoUnitario.setText(fields.contains("precoUnitario") ? errors.get("precoUnitario") : "");
		labelErrorEstoque.setText(fields.contains("estoque") ? errors.get("estoque") : "");
		labelErrorEstoqueMinimo.setText(fields.contains("estoqueMinimo") ? errors.get("estoqueMinimo") : "");
	}
}
