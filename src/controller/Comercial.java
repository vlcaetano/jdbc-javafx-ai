package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.dao.ClienteDao;
import model.dao.CompraDao;
import model.dao.DaoFactory;
import model.dao.FornecedorDao;
import model.dao.ProdutoDao;
import model.dao.VendaDao;
import model.dao.VendedorDao;
import model.entities.*;
import model.exceptions.SisComException;

public class Comercial {

	/*private List<Pessoa> pessoas = new ArrayList<>();
	private List<Produto> produtos = new ArrayList<>();
	private List<Compra> compras;  = new ArrayList<>();
	private List<Venda> vendas  = new ArrayList<>();*/ //Controle feito por banco de dados
	
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
	
	public Fornecedor pesquisarFornecedor(String cnpj) {
		return fornecedorDao.encontrarPorCnpj(cnpj);
	}
	
	public Cliente pesquisarCliente(String cpf) {
		return clienteDao.encontrarPorCpf(cpf);
	}
	
	public Vendedor pesquisarVendedor(String cpf) {
		return vendedorDao.encontrarPorCpf(cpf);
	}
	
	public List<Fornecedor> listarFornecedores(){
		return fornecedorDao.encontrarTodos();
	}
	
	public List<Cliente> listarClientes(){
		return clienteDao.encontrarTodos();
	}
	
	public List<Vendedor> listarVendedores(){
		return vendedorDao.encontrarTodos();
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
	
	public List<String> estatisticasFornecedores() {
		return fornecedorDao.estatisticaFornecedor();
	}
	
	public List<String> estatisticasClientes() {
		return clienteDao.estatisticaCliente();
	}
	
	public List<String> estatisticasVendedores(){
		return vendedorDao.estatisticaVendedor();
	}
	
	public void inserirProduto(Produto produto) {
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
	
	public void fazerCompra(Compra compra) {
		compraDao.fazerCompra(compra);
	}
	
	public void deletarCompra(Integer cod) throws SisComException {
		compraDao.deletarCompra(cod);
	}
	
	public List<Compra> listarComprasPorFornecedor(Date dataInicio, Date dataFinal, String nome){
		List<Compra> lista = compraDao.encontrarComprasNomeFornecedor(nome);
		if (lista == null) {
			return null;
		}
		
		List<Compra> listaPorPeriodo = new ArrayList<>();
		for (Compra c : lista) {
			if (c.getDataCompra().compareTo(dataInicio) >= 0 && 
					c.getDataCompra().compareTo(dataFinal) <= 0) {
				listaPorPeriodo.add(c);
			}
		}
		listaPorPeriodo.sort((c1, c2) -> c2.getDataCompra().compareTo(c1.getDataCompra()));
		return listaPorPeriodo;
	}
	
	public void fazerVenda(Venda venda) throws SisComException {
		vendaDao.fazerVenda(venda);
	}
	
	public void deletarVenda(Integer cod) throws SisComException {
		vendaDao.deletarVenda(cod);
	}
	
	public List<Venda> listarVendasPorCliente(Date dataInicio, Date dataFinal, String nome){
		List<Venda> lista = vendaDao.encontrarVendasNomeCliente(nome);
		if (lista == null) {
			return null;
		}
		
		List<Venda> listaPorPeriodo = new ArrayList<>();
		for (Venda v : lista) {
			if (v.getDataVenda().compareTo(dataInicio) >= 0 && 
					v.getDataVenda().compareTo(dataFinal) <= 0) {
				listaPorPeriodo.add(v);
			}
		}
		listaPorPeriodo.sort((c1, c2) -> c2.getDataVenda().compareTo(c1.getDataVenda()));
		return listaPorPeriodo;
	}
	
	public List<Venda> listarVendasPorVendedor(Date dataInicio, Date dataFinal, String nome){
		List<Venda> lista = vendaDao.encontrarVendasNomeVendedor(nome);
		if (lista == null) {
			return null;
		}
		
		List<Venda> listaPorPeriodo = new ArrayList<>();
		for (Venda v : lista) {
			if (v.getDataVenda().compareTo(dataInicio) >= 0 && 
					v.getDataVenda().compareTo(dataFinal) <= 0) {
				listaPorPeriodo.add(v);
			}
		}
		listaPorPeriodo.sort((c1, c2) -> c2.getDataVenda().compareTo(c1.getDataVenda()));
		return listaPorPeriodo;
	}
}
