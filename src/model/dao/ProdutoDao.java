package model.dao;

import java.util.List;

import model.entities.Produto;
import model.exceptions.SisComException;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public interface ProdutoDao {
/**
 * Interface ProdutoDao - Possui os métodos relacionados aos Data Access Object do produto
 */
	void inserirProduto(Produto obj) throws SisComException;
	void deletarProduto(Integer cod);
	List<Produto> encontrarTodos();
	List<Produto> encontrarAbaixoEstoqueMin();
	Produto encontrarPorCodigo(Integer cod);
}
