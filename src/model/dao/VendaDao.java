package model.dao;

import java.util.List;

import model.entities.*;
import model.exceptions.SisComException;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public interface VendaDao {
/**
 * Interface VendaDao - Possui os métodos relacionados aos Data Access Object da venda
 */
	void fazerVenda(Venda compra) throws SisComException;
	void deletarVenda(Integer cod) throws SisComException;
	List<Venda> encontrarVendas(String dtInicio, String dtFinal);
	List<ItemVenda> criarListaItemVenda(Integer Venda);
	List<Venda> encontrarVendasNomeCliente(String nome);
	List<Venda> encontrarVendasNomeVendedor(String nome);
}