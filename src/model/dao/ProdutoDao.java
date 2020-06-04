package model.dao;

import java.util.List;

import model.entities.Produto;
import model.exceptions.SisComException;

public interface ProdutoDao {

	void inserirProduto(Produto obj) throws SisComException;
	void deletarProduto(Integer cod);
	List<Produto> encontrarTodos();
	List<Produto> encontrarAbaixoEstoqueMin();
	Produto encontrarPorCodigo(Integer cod);
}
