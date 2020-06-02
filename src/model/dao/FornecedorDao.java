package model.dao;

import java.util.List;

import model.entities.Fornecedor;
import model.exceptions.SisComException;

public interface FornecedorDao {

	void inserirFornecedor(Fornecedor obj) throws SisComException;
	void deletarFornecedor(Fornecedor obj);
	List<Fornecedor> encontrarTodos();
	Fornecedor encontrarPorCnpj(String cnpj);
	public List<String> estatisticaFornecedor();
}
