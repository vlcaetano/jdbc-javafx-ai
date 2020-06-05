package model.dao;

import java.util.List;

import model.entities.Cliente;
import model.exceptions.SisComException;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public interface ClienteDao {
/**
 * Interface ClienteDao - Possui os métodos relacionados aos Data Access Object do cliente
 */
	void inserirCliente(Cliente obj) throws SisComException;
	void deletarCliente(Cliente obj);
	List<Cliente> encontrarTodos();
	Cliente encontrarPorCpf(String cpf);
	public List<Cliente> estatisticaCliente(String dtInicio, String dtFinal);
	List<Cliente> filtrarCpf(String cpf);
}
