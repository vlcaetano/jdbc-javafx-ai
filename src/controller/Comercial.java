package controller;

import java.util.List;

import model.dao.ClienteDao;
import model.dao.CompraDao;
import model.dao.DaoFactory;
import model.dao.FornecedorDao;
import model.dao.ProdutoDao;
import model.dao.VendaDao;
import model.dao.VendedorDao;
import model.entities.Cliente;
import model.entities.Compra;
import model.entities.Fornecedor;
import model.entities.ItemCompra;
import model.entities.ItemVenda;
import model.entities.Pessoa;
import model.entities.Produto;
import model.entities.Venda;
import model.entities.Vendedor;
import model.exceptions.SisComException;

public class Comercial {
	
	private FornecedorDao fornecedorDao = DaoFactory.criarFornecedorDao();
	private ClienteDao clienteDao = DaoFactory.criarClienteDao();
	private VendedorDao vendedorDao = DaoFactory.criarVendedorDao();
	private ProdutoDao produtoDao = DaoFactory.criarProdutoDao();
	private CompraDao compraDao = DaoFactory.criarCompraDao();
	private VendaDao vendaDao = DaoFactory.criarVendaDao();
	
	public Comercial() {
	}
	
	public void inserirPessoa(Pessoa pessoa) throws SisComException {
		if(pessoa instanceof Cliente) {
			clienteDao.inserirCliente((Cliente)pessoa);
		}
		if(pessoa instanceof Vendedor) {
			vendedorDao.inserirVendedor((Vendedor)pessoa);
		}
		if(pessoa instanceof Fornecedor) {
			fornecedorDao.inserirFornecedor((Fornecedor)pessoa);
		}
	}
	
	public List<Fornecedor> listarFornecedores(){
		return fornecedorDao.encontrarTodos();
	}
	
	public List<Fornecedor> filtrarFornecedores(String cnpj){
		return fornecedorDao.filtrarCnpj(cnpj);
	}
	
	public List<Cliente> listarClientes(){
		return clienteDao.encontrarTodos();
	}
	
	public List<Cliente> filtrarClientes(String cpf){
		return clienteDao.filtrarCpf(cpf);
	}
	
	public List<Vendedor> listarVendedores(){
		return vendedorDao.encontrarTodos();
	}
	
	public List<Vendedor> filtrarVendedores(String cpf){
		return vendedorDao.filtrarCpf(cpf);
	}
	
	public void deletarFonecedor(Fornecedor obj) {
		fornecedorDao.deletarFornecedor(obj);
	}
	
	public void deletarCliente(Cliente obj) {
		clienteDao.deletarCliente(obj);
	}
	
	public void deletarVendedor(Vendedor obj) {
		vendedorDao.deletarVendedor(obj);
	}
	
	public List<Fornecedor> estatisticasFornecedores(String dtInicio, String dtFinal) {
		return fornecedorDao.estatisticaFornecedor(dtInicio, dtFinal);
	}
	
	public List<Cliente> estatisticasClientes(String dtInicio, String dtFinal) {
		return clienteDao.estatisticaCliente(dtInicio, dtFinal);
	}
	
	public List<Vendedor> estatisticasVendedores(String dtInicio, String dtFinal){
		return vendedorDao.estatisticaVendedor(dtInicio, dtFinal);
	}
	
	public void inserirProduto(Produto produto) throws SisComException {
		produtoDao.inserirProduto(produto);
	}
	
	public Produto pesquisarProduto(Integer cod) {
		return produtoDao.encontrarPorCodigo(cod);
	}
	
	public List<Produto> listarProdutos(){
		return produtoDao.encontrarTodos();
	}
	
	public List<Produto> listarAbaixoEstoqueMin(){
		return produtoDao.encontrarAbaixoEstoqueMin();
	}
	
	public void deletarProduto(Integer cod) {
		produtoDao.deletarProduto(cod);
	}
	
	public void fazerCompra(Compra compra) throws SisComException {
		compraDao.fazerCompra(compra);
	}
	
	public void deletarCompra(Integer cod) throws SisComException {
		compraDao.deletarCompra(cod);
	}
	
	public List<Compra> listarCompras(String dtInicio, String dtFinal) {
		return compraDao.encontrarCompras(dtInicio, dtFinal);
	}
	
	public List<ItemCompra> listarItemCompras(Integer codCompra) {
		return compraDao.criarListaItemCompra(codCompra);
	}
	
	public void fazerVenda(Venda venda) throws SisComException {
		vendaDao.fazerVenda(venda);
	}
	
	public void deletarVenda(Integer cod) throws SisComException {
		vendaDao.deletarVenda(cod);
	}
	
	public List<Venda> listarVendas(String dtInicio, String dtFinal) {
		return vendaDao.encontrarVendas(dtInicio, dtFinal);
	}
	
	public List<ItemVenda> listarItemVendas(Integer codVenda) {
		return vendaDao.criarListaItemVenda(codVenda);
	}
}
