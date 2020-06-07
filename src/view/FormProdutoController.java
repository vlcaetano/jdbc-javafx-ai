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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Produto;
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
public class FormProdutoController implements Initializable {
/**
 * Classe FormProdutoController - Controller da view FormProduto.fxml
 */	
	private Produto entidade;
	
	private Comercial objBiz;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtPrecoUnitario;
	
	@FXML
	private TextField txtEstoque;
	
	@FXML
	private TextField txtEstoqueMinimo;
	
	@FXML
	private Label labelDataCadastro;
	
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
			objBiz.inserirProduto(entidade);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (SisComException e) {
			Alerts.showAlert("Erro salvando o produto", null, e.getMensagemErro(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
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
	 * Método para recuperar os dados do cliente cadastrados no formulário
	 * @return objeto Produto
	 * @throws ValidationException
	 */
	private Produto getFormData() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Produto obj = new Produto();
		
		ValidationException exception = new ValidationException("Erro na validação");
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
		Constraints.setTextFieldDouble(txtPrecoUnitario);
		Constraints.setTextFieldInteger(txtEstoque);
		Constraints.setTextFieldInteger(txtEstoqueMinimo);
	}
	
	/**
	 * Método para atualizar os dados do formulário
	 * @throws IllegalStateException
	 */
	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está com valor null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		txtNome.setText(entidade.getNome());
		Locale.setDefault(Locale.US);
		txtPrecoUnitario.setText(String.format("%.2f", entidade.getPrecoUnitario()));
		txtEstoque.setText(String.valueOf(entidade.getEstoque()));
		txtEstoqueMinimo.setText(String.valueOf(entidade.getEstoqueMinimo()));
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
		labelErrorPrecoUnitario.setText(fields.contains("precoUnitario") ? errors.get("precoUnitario") : "");
		labelErrorEstoque.setText(fields.contains("estoque") ? errors.get("estoque") : "");
		labelErrorEstoqueMinimo.setText(fields.contains("estoqueMinimo") ? errors.get("estoqueMinimo") : "");
	}
}
