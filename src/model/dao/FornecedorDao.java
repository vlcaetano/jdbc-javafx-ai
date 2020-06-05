package model.dao;

import java.util.List;

import model.entities.Fornecedor;
import model.exceptions.SisComException;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public interface FornecedorDao {
/**
 * Interface FornecedorDao - Possui os métodos relacionados aos Data Access Object do fornecedor
 */
	void inserirFornecedor(Fornecedor obj) throws SisComException;
	void deletarFornecedor(Fornecedor obj);
	List<Fornecedor> encontrarTodos();
	Fornecedor encontrarPorCnpj(String cnpj);
	
	public List<Fornecedor> estatisticaFornecedor(String dtInicio, String dtFinal);
	List<Fornecedor> filtrarCnpj(String cnpj);
}
