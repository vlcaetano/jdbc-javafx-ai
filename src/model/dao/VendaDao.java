package model.dao;

import java.util.List;

import model.entities.*;
import model.exceptions.SisComException;

public interface VendaDao {

	void fazerVenda(Venda compra) throws SisComException;
	void deletarVenda(Integer cod) throws SisComException;
	List<Venda> encontrarVendas(String dtInicio, String dtFinal);
	List<ItemVenda> criarListaItemVenda(Integer Venda);
	List<Venda> encontrarVendasNomeCliente(String nome);
	List<Venda> encontrarVendasNomeVendedor(String nome);
}