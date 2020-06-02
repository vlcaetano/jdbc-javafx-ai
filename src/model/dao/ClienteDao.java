package model.dao;

import java.util.List;

import model.entities.Cliente;
import model.exceptions.SisComException;

public interface ClienteDao {

	void inserirCliente(Cliente obj) throws SisComException;
	void deletarCliente(Cliente obj);
	List<Cliente> encontrarTodos();
	Cliente encontrarPorCpf(String cpf);
	public List<String> estatisticaCliente();
}
