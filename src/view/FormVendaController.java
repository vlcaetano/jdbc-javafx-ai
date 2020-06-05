package view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import controller.Comercial;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.entities.Venda;
import model.entities.Vendedor;
import model.entities.Cliente;
import model.entities.ItemVenda;
import model.entities.Produto;
import model.exceptions.SisComException;
import view.listeners.DataChangeListener;
import view.util.Alerts;
import view.util.Constraints;
import view.util.Utils;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class FormVendaController implements Initializable {
/**
 * Classe FormVendaController - Controller da view FormVenda.fxml
 */	
	
	private Venda entidade;
	
	private Comercial objBiz;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TableView<ItemVenda> tableViewItens;
	
	@FXML
	private TableColumn<ItemVenda, Integer> tableColumnCodProduto;
	@FXML
	private TableColumn<ItemVenda, String> tableColumnNomeProduto;
	@FXML
	private TableColumn<ItemVenda, Integer> tableColumnQuantidade;
	@FXML
	private TableColumn<ItemVenda, Double> tableColumnValorVenda;
	@FXML
	private TableColumn<ItemVenda, ItemVenda> tableColumnX;
	
	@FXML
	private ComboBox<Cliente> comboBoxCliente;
	@FXML
	private ComboBox<Vendedor> comboBoxVendedor;
	@FXML
	private ComboBox<Produto> comboBoxProduto;
	@FXML
	private ComboBox<String> comboBoxFormaPag;
	
	@FXML
	private Button btAdicionarProduto;
	@FXML
	private Button btFinalizar;
	@FXML
	private Button btCancelar;
	
	@FXML
	private TextField txtQuantidade;
	
	private List<ItemVenda> listaItemVenda = new ArrayList<>();
	
	private ObservableList<Cliente> obsListCliente;
	private ObservableList<Vendedor> obsListVendedor;
	private ObservableList<Produto> obsListProduto;
	private ObservableList<String> obsListFormaPag;
	
	private ObservableList<ItemVenda> obsListItemVenda;
	
	public void setVenda(Venda entidade) {
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
	public void onBtAdicionarProdutoAction(){
		try {
			Integer qtd = Utils.tryParseToInt(txtQuantidade.getText());
			if (qtd == null || qtd == 0) {
				throw new SisComException("Informe a quantidade");
			}
			ItemVenda iv = new ItemVenda(comboBoxProduto.getValue(), qtd);
		
			if (iv.getProduto().getEstoque() < iv.getQuantVenda()) {
				throw new SisComException("Quantidade maior que possuída no estoque. "
						+ "Atualmente o produto possui " + iv.getProduto().getEstoque() + " unidade(s).");
			}
		
			for (ItemVenda item : listaItemVenda) {
				if (iv.getProduto().compareTo(item.getProduto()) == 0) {
					throw new SisComException("Produto já cadastrado no pedido");
				}
			}
			listaItemVenda.add(iv);
			updateTableView();
		} catch (SisComException e) {
			Alerts.showAlert("Falha ao adicionar produto", null, e.getMensagemErro(), AlertType.INFORMATION);
		}
		
	}
	
	/**
	 * Método para atualizar tabela
	 */
	public void updateTableView() {
		obsListItemVenda = FXCollections.observableArrayList(listaItemVenda);
		tableViewItens.setItems(obsListItemVenda);
		initRemoveButtons();
	}
	
	@FXML
	public void onBtFinalizarAction(ActionEvent event) {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está nulo!");
		}
		if (entidade == null) {
			throw new IllegalStateException("Entidade está nula!");
		}
		try {
			entidade = getFormData();
			objBiz.fazerVenda(entidade);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (SisComException e) {
			Alerts.showAlert("Erro salvando o objeto", null, e.getMensagemErro(), AlertType.ERROR);
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
	 * Método para recuperar os dados da venda cadastrados no formulário
	 * @return objeto Venda
	 * @throws SisComException
	 */
	private Venda getFormData() throws SisComException {
		Venda obj = new Venda();
		
		if (listaItemVenda.isEmpty()) {
			throw new SisComException("Ainda não foram adicionados produtos!");
		}
		
		obj.setNumVenda(null);
		
		obj.setCliente(comboBoxCliente.getValue());
		
		obj.setVendedor(comboBoxVendedor.getValue());
		
		obj.setVendaItens(listaItemVenda);
		
		obj.setDataVenda(new Date());
		
		if (comboBoxFormaPag.getValue().equals("À vista")) {
			obj.setFormaPagto(1);
		} else {
			Double valorTotal = 0.0;
			for (ItemVenda item : listaItemVenda) {
				valorTotal += item.getValorVenda();
			}
			if (valorTotal > obj.getCliente().getLimiteCredito()) {
				throw new SisComException("Cliente não possui limite o suficiente para a compra");
			}
			obj.setFormaPagto(2);
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
	 * Método para inicializar os campos da view
	 */
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtQuantidade);
		tableColumnCodProduto.setCellValueFactory(new PropertyValueFactory<>("codProduto"));
		tableColumnNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
		tableColumnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantVenda"));
		tableColumnValorVenda.setCellValueFactory(new PropertyValueFactory<>("valorVenda"));
		Utils.formatTableColumnDouble(tableColumnValorVenda, 2);
		
		initializeComboBoxCliente();
		initializeComboBoxVendedor();
		initializeComboBoxProduto();
	}
	
	/**
	 * Método para atualizar campos do formulário
	 * @throws IllegalStateException
	 */
	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está com valor null");
		}
		comboBoxCliente.getSelectionModel().selectFirst();
		comboBoxVendedor.getSelectionModel().selectFirst();
		comboBoxProduto.getSelectionModel().selectFirst();
		comboBoxFormaPag.getSelectionModel().selectFirst();
	}
	
	/**
	 * Método para preencher os combo box com os dados dos clientes, vendedores,
	 * produtos e forma de pagamento
	 * @throws IllegalStateException
	 */
	public void loadAssociatedObjects() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está com valor null");
		}
		
		List<Cliente> listaCliente = objBiz.listarClientes();
		obsListCliente = FXCollections.observableArrayList(listaCliente);
		comboBoxCliente.setItems(obsListCliente);
		
		List<Vendedor> listaVendedor = objBiz.listarVendedores();
		obsListVendedor = FXCollections.observableArrayList(listaVendedor);
		comboBoxVendedor.setItems(obsListVendedor);
		
		List<Produto> listaProduto = objBiz.listarProdutos();
		obsListProduto = FXCollections.observableArrayList(listaProduto);
		comboBoxProduto.setItems(obsListProduto);
		
		List<String> listaFormaPag = new ArrayList<>();
		listaFormaPag.add("À vista");
		listaFormaPag.add("A prazo");
		obsListFormaPag = FXCollections.observableArrayList(listaFormaPag);
		comboBoxFormaPag.setItems(obsListFormaPag);
		
	}
	
	/**
	 * Método para inicializar a combo box do cliente
	 */
	private void initializeComboBoxCliente() {
		Callback<ListView<Cliente>, ListCell<Cliente>> factory = lv -> new ListCell<Cliente>() {
			@Override
			protected void updateItem(Cliente item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxCliente.setCellFactory(factory);
		comboBoxCliente.setButtonCell(factory.call(null));
	}
	
	/**
	 * Método para inicializar a combo box do vendedor
	 */
	private void initializeComboBoxVendedor() {
		Callback<ListView<Vendedor>, ListCell<Vendedor>> factory = lv -> new ListCell<Vendedor>() {
			@Override
			protected void updateItem(Vendedor item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxVendedor.setCellFactory(factory);
		comboBoxVendedor.setButtonCell(factory.call(null));
	}
	
	/**
	 * Método para inicializar a combo box do produto
	 */
	private void initializeComboBoxProduto() {
		Callback<ListView<Produto>, ListCell<Produto>> factory = lv -> new ListCell<Produto>() {
			@Override
			protected void updateItem(Produto item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxProduto.setCellFactory(factory);
		comboBoxProduto.setButtonCell(factory.call(null));
	}
	
	/**
	 * Método para criar os botões para retirar produtos da lista
	 */
	private void initRemoveButtons() {
		tableColumnX.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnX.setCellFactory(param -> new TableCell<ItemVenda, ItemVenda>() {
		private final Button button = new Button("X");
			@Override
			protected void updateItem(ItemVenda obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}
	
	/**
	 * Método para retirar produto da lista
	 * @param obj
	 */
	private void removeEntity(ItemVenda obj) {
		listaItemVenda.remove(obj);
		updateTableView();
	}
}
