package model.dao;

import java.util.List;

import model.entities.*;
import model.exceptions.SisComException;

public interface CompraDao {

	void fazerCompra(Compra compra);
	void deletarCompra(Integer cod) throws SisComException;
	List<Compra> encontrarCompras();
	List<Compra> encontrarComprasNomeFornecedor(String nome);
}
