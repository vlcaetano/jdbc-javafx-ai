package model.dao;

import java.util.List;

import model.entities.Produto;

public interface ProdutoDao {

	void inserirProduto(Produto obj);
	void deletarProduto(Integer cod);
	List<Produto> encontrarTodos();
	List<Produto> encontrarAbaixoEstoqueMin();
	Produto encontrarPorCodigo(Integer cod);
}
