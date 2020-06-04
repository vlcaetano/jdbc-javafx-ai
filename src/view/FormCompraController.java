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
import model.entities.Compra;
import model.entities.Fornecedor;
import model.entities.ItemCompra;
import model.entities.Produto;
import model.exceptions.SisComException;
import view.listeners.DataChangeListener;
import view.util.Alerts;
import view.util.Constraints;
import view.util.Utils;

public class FormCompraController implements Initializable {

	private Compra entidade;
	
	private Comercial objBiz;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TableView<ItemCompra> tableViewItens;
	
	@FXML
	private TableColumn<ItemCompra, Integer> tableColumnCodProduto;
	@FXML
	private TableColumn<ItemCompra, String> tableColumnNomeProduto;
	@FXML
	private TableColumn<ItemCompra, Integer> tableColumnQuantidade;
	@FXML
	private TableColumn<ItemCompra, Double> tableColumnValorCompra;
	@FXML
	private TableColumn<ItemCompra, ItemCompra> tableColumnX;
	
	@FXML
	private ComboBox<Fornecedor> comboBoxFornecedor;
	@FXML
	private ComboBox<Produto> comboBoxProduto;
	
	@FXML
	private Button btAdicionarProduto;
	@FXML
	private Button btFinalizar;
	@FXML
	private Button btCancelar;
	
	@FXML
	private TextField txtQuantidade;
	
	private List<ItemCompra> listaItemCompra = new ArrayList<>();
	
	private ObservableList<Fornecedor> obsListFornecedor;
	private ObservableList<Produto> obsListProduto;
	private ObservableList<ItemCompra> obsListItemCompra;
	
	public void setCompra(Compra entidade) {
		this.entidade = entidade;
	}
	
	public void setObjBiz(Comercial objBiz) {
		this.objBiz = objBiz;
	}
	
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
			ItemCompra ic = new ItemCompra(comboBoxProduto.getValue(), qtd);
		
			//aqui que faria o teste se fosse venda
			/*if (ic.getProduto().getEstoque() < ic.getQuantCompra()) {			
			}*/
		
			for (ItemCompra item : listaItemCompra) {
				if (ic.getProduto().compareTo(item.getProduto()) == 0) {
					throw new SisComException("Produto já cadastrado no pedido");
				}
			}
			listaItemCompra.add(ic);
			updateTableView();
		} catch (SisComException e) {
			Alerts.showAlert("Falha ao adicionar produto", null, e.getMensagemErro(), AlertType.INFORMATION);
		}
		
	}
	
	public void updateTableView() {
		obsListItemCompra = FXCollections.observableArrayList(listaItemCompra);
		tableViewItens.setItems(obsListItemCompra);
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
			objBiz.fazerCompra(entidade);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (SisComException e) {
			Alerts.showAlert("Erro salvando o objeto", null, e.getMensagemErro(), AlertType.ERROR);
		}		
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Compra getFormData() throws SisComException {
		Compra obj = new Compra();
		
		if (listaItemCompra.isEmpty()) {
			throw new SisComException("Ainda não foram adicionados produtos!");
		}
		
		obj.setNumCompra(null);
		
		obj.setFornecedor(comboBoxFornecedor.getValue());
		
		obj.setCompraItens(listaItemCompra);
		
		obj.setDataCompra(new Date());
		
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
		Constraints.setTextFieldInteger(txtQuantidade);
		tableColumnCodProduto.setCellValueFactory(new PropertyValueFactory<>("codProduto"));
		tableColumnNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
		tableColumnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantCompra"));
		tableColumnValorCompra.setCellValueFactory(new PropertyValueFactory<>("valorCompra"));
		Utils.formatTableColumnDouble(tableColumnValorCompra, 2);
		
		initializeComboBoxFornecedor();
		initializeComboBoxProduto();
	}
	
	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade está com valor null");
		}
		comboBoxFornecedor.getSelectionModel().selectFirst();
		comboBoxProduto.getSelectionModel().selectFirst();
	}
	
	public void loadAssociatedObjects() {
		if (objBiz == null) {
			throw new IllegalStateException("ObjBiz está com valor null");
		}
		
		List<Fornecedor> listaFornecedor = objBiz.listarFornecedores();
		obsListFornecedor = FXCollections.observableArrayList(listaFornecedor);
		comboBoxFornecedor.setItems(obsListFornecedor);
		
		List<Produto> listaProduto = objBiz.listarProdutos();
		obsListProduto = FXCollections.observableArrayList(listaProduto);
		comboBoxProduto.setItems(obsListProduto);
	}
	
	private void initializeComboBoxFornecedor() {
		Callback<ListView<Fornecedor>, ListCell<Fornecedor>> factory = lv -> new ListCell<Fornecedor>() {
			@Override
			protected void updateItem(Fornecedor item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxFornecedor.setCellFactory(factory);
		comboBoxFornecedor.setButtonCell(factory.call(null));
	}
	
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
	
	private void initRemoveButtons() {
		tableColumnX.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnX.setCellFactory(param -> new TableCell<ItemCompra, ItemCompra>() {
		private final Button button = new Button("X");
			@Override
			protected void updateItem(ItemCompra obj, boolean empty) {
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
	
	private void removeEntity(ItemCompra obj) {
		listaItemCompra.remove(obj);
		updateTableView();
	}
}
