package model.dao;

import java.util.List;

import model.entities.Vendedor;
import model.exceptions.SisComException;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public interface VendedorDao {
/**
 * Interface VendedorDao - Possui os métodos relacionados aos Data Access Object do vendedor
 */
	void inserirVendedor(Vendedor obj) throws SisComException;
	void deletarVendedor(Vendedor obj);
	List<Vendedor> encontrarTodos();
	Vendedor encontrarPorCpf(String cpf);
	public List<Vendedor> estatisticaVendedor(String dtInicio, String dtFinal);
	List<Vendedor> filtrarCpf(String cpf);
}
