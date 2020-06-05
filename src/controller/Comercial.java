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
/**
 * 
 * @author Vitor Lima Caetano
 * 
 */
public class Comercial {
	/**
	 * Classe Comercial - métodos que tratam das regras do négocio e da interação com as classes de entidade com o Banco de Dados.
	 */
	private FornecedorDao fornecedorDao = DaoFactory.criarFornecedorDao();
	private ClienteDao clienteDao = DaoFactory.criarClienteDao();
	private VendedorDao vendedorDao = DaoFactory.criarVendedorDao();
	private ProdutoDao produtoDao = DaoFactory.criarProdutoDao();
	private CompraDao compraDao = DaoFactory.criarCompraDao();
	private VendaDao vendaDao = DaoFactory.criarVendaDao();
	
	/**
	 * Método construtor padrão
	 */
	public Comercial() {
	}
	
	/**
	 * Método para inserir pessoa no banco de dados
	 * @param pessoa
	 * @throws SisComException
	 */
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
	
	/**
	 * Método para listar todos os fornecedores no banco de dados
	 * @return lista com todos os fornecedores
	 */
	public List<Fornecedor> listarFornecedores(){
		return fornecedorDao.encontrarTodos();
	}
	
	/**
	 * Método para filtrar fornecedores que possuem parte ou todo o CNPJ igual ao informado
	 * @param cnpj
	 * @return lista de fornecedores
	 */
	public List<Fornecedor> filtrarFornecedores(String cnpj){
		return fornecedorDao.filtrarCnpj(cnpj);
	}
	
	/**
	 * Método para listar todos os clientes no banco de dados
	 * @return lista com todos os clientes
	 */
	public List<Cliente> listarClientes(){
		return clienteDao.encontrarTodos();
	}
	
	/**
	 * Método para filtrar clientes que possuem parte ou todo o CPF igual ao informado
	 * @param cpf
	 * @return lista de clientes
	 */
	public List<Cliente> filtrarClientes(String cpf){
		return clienteDao.filtrarCpf(cpf);
	}
	
	/**
	 * Método para listar todos os vendedores no banco de dados
	 * @return lista com todos os vendedores
	 */
	public List<Vendedor> listarVendedores(){
		return vendedorDao.encontrarTodos();
	}
	
	/**
	 * Método para filtrar vendedores que possuem parte ou todo o CPF igual ao informado
	 * @param cpf
	 * @return lista de vendedores
	 */
	public List<Vendedor> filtrarVendedores(String cpf){
		return vendedorDao.filtrarCpf(cpf);
	}
	
	/**
	 * Método para deletar um fornecedor do banco de dados
	 * @param obj
	 */
	public void deletarFonecedor(Fornecedor obj) {
		fornecedorDao.deletarFornecedor(obj);
	}
	
	/**
	 * Método para deletar um cliente do banco de dados
	 * @param obj
	 */
	public void deletarCliente(Cliente obj) {
		clienteDao.deletarCliente(obj);
	}
	
	/**
	 * Método para deletar um vendedor do banco de dados
	 * @param obj
	 */
	public void deletarVendedor(Vendedor obj) {
		vendedorDao.deletarVendedor(obj);
	}
	
	/**
	 * Método para obter estatística de compras agrupado por fornecedor por período de compras
	 * @param dtInicio
	 * @param dtFinal
	 * @return lista de fornecedores
	 */
	public List<Fornecedor> estatisticasFornecedores(String dtInicio, String dtFinal) {
		return fornecedorDao.estatisticaFornecedor(dtInicio, dtFinal);
	}
	
	/**
	 * Método para obter estatística de vendas agrupado por cliente por período de vendas
	 * @param dtInicio
	 * @param dtFinal
	 * @return lista de clientes
	 */
	public List<Cliente> estatisticasClientes(String dtInicio, String dtFinal) {
		return clienteDao.estatisticaCliente(dtInicio, dtFinal);
	}
	
	/**
	 * Método para obter estatística de vendas agrupado por vendedor por período de vendas
	 * @param dtInicio
	 * @param dtFinal
	 * @return lista de vendedores
	 */
	public List<Vendedor> estatisticasVendedores(String dtInicio, String dtFinal){
		return vendedorDao.estatisticaVendedor(dtInicio, dtFinal);
	}
	
	/**
	 * Método para inserir um produto no banco de dados
	 * @param produto
	 * @throws SisComException
	 */
	public void inserirProduto(Produto produto) throws SisComException {
		produtoDao.inserirProduto(produto);
	}
	
	/**
	 * Método para listar todos os produtos no banco de dados
	 * @return lista de produtos
	 */
	public List<Produto> listarProdutos(){
		return produtoDao.encontrarTodos();
	}
	
	/**
	 * Método para listar todos os produtos que possuem estoque menor que o estoque mínimo
	 * @return lista de produtos
	 */
	public List<Produto> listarAbaixoEstoqueMin(){
		return produtoDao.encontrarAbaixoEstoqueMin();
	}
	
	/**
	 * Método para apagar um produto do banco de dados através do código do produto
	 * @param cod
	 */
	public void deletarProduto(Integer cod) {
		produtoDao.deletarProduto(cod);
	}
	
	/**
	 * Método para fazer uma compra
	 * @param compra
	 * @throws SisComException
	 */
	public void fazerCompra(Compra compra) throws SisComException {
		compraDao.fazerCompra(compra);
	}
	
	/**
	 * Método para apagar uma compra do banco de dados
	 * @param cod
	 * @throws SisComException
	 */
	public void deletarCompra(Integer cod) throws SisComException {
		compraDao.deletarCompra(cod);
	}
	
	/**
	 * Método para listar todas as compras do banco de dados
	 * @param dtInicio
	 * @param dtFinal
	 * @return lista de compras
	 */
	public List<Compra> listarCompras(String dtInicio, String dtFinal) {
		return compraDao.encontrarCompras(dtInicio, dtFinal);
	}
	
	/**
	 * Método para listar itens comprados em uma determinada compra, encontrada por seu código
	 * @param codCompra
	 * @return lista de ItemCompra
	 */
	public List<ItemCompra> listarItemCompras(Integer codCompra) {
		return compraDao.criarListaItemCompra(codCompra);
	}
	
	/**
	 * Método para fazer uma venda
	 * @param venda
	 * @throws SisComException
	 */
	public void fazerVenda(Venda venda) throws SisComException {
		vendaDao.fazerVenda(venda);
	}
	
	/**
	 * Método para deletar uma venda
	 * @param cod
	 * @throws SisComException
	 */
	public void deletarVenda(Integer cod) throws SisComException {
		vendaDao.deletarVenda(cod);
	}
	
	/**
	 * Método para listar as vendas realizadas num determinado período
	 * @param dtInicio
	 * @param dtFinal
	 * @return lista de vendas
	 */
	public List<Venda> listarVendas(String dtInicio, String dtFinal) {
		return vendaDao.encontrarVendas(dtInicio, dtFinal);
	}
	
	/**
	 * Método para listar itens vendidos em uma determinada venda, encontrada por seu código
	 * @param codVenda
	 * @return lista de ItemVenda
	 */
	public List<ItemVenda> listarItemVendas(Integer codVenda) {
		return vendaDao.criarListaItemVenda(codVenda);
	}
}
