package model.dao;

import java.util.List;

import model.entities.*;
import model.exceptions.SisComException;

public interface CompraDao {

	void fazerCompra(Compra compra) throws SisComException;
	void deletarCompra(Integer cod) throws SisComException;
	List<Compra> encontrarCompras();
	List<ItemCompra> criarListaItemCompra(Integer codCompra);
	List<Compra> encontrarComprasNomeFornecedor(String nome);
}
