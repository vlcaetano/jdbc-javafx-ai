package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.CompraDao;
import model.dao.DaoFactory;
import model.dao.ProdutoDao;
import model.entities.Compra;
import model.entities.Fornecedor;
import model.entities.ItemCompra;
import model.entities.Produto;
import model.exceptions.SisComException;

public class CompraDaoJDBC implements CompraDao {

	Connection conn;
	
	public CompraDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void fazerCompra(Compra obj) {
		PreparedStatement st = null;
		
		try {
			//Criar compra
			st = conn.prepareStatement(
					"INSERT INTO compra "
					+ "(CodFornecedor, DataCompra) "
					+ "VALUES "
					+ "(?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setInt(1, obj.getFornecedor().getCodigo());		
			st.setDate(2, new java.sql.Date(obj.getDataCompra().getTime()));
			
			int linhasAfetadas = st.executeUpdate();
			
			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int cod = rs.getInt(1);
					obj.setNumCompra(cod);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Erro! Nenhuma linha foi alterada!");
			}
			
			for (ItemCompra ic : obj.getCompraItens()) {
				criarItemCompra(ic.getProduto().getCodigo(), obj.getNumCompra(), ic.getQuantCompra(), ic.getValorCompra());
				atualizarEstoque(ic.getProduto().adicionarQuantidade(ic.getQuantCompra()), ic.getProduto().getCodigo());
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}
	
	private void criarItemCompra(Integer codProduto, Integer codCompra, Integer qtd, Double valor) throws SQLException {
		PreparedStatement st = null;
		
		st = conn.prepareStatement(
				"INSERT INTO itemcompra "
				+ "(CodProduto, CodCompra, QuantCompra, valorCompra) "
				+ "VALUES "
				+ "(?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS);
		st.setInt(1, codProduto);
		st.setInt(2, codCompra);
		st.setInt(3, qtd);
		st.setDouble(4, valor);
		
		int linhasAfetadas = st.executeUpdate();
		
		if (linhasAfetadas == 0) {
			throw new DbException("Erro! Nenhuma linha foi alterada!");
		}
		DB.closeStatement(st);
	}

	private void atualizarEstoque(Integer qtd, Integer cod) throws SQLException {
		PreparedStatement st = null;
		st = conn.prepareStatement("UPDATE produto SET Estoque = ? WHERE CodProduto = ?");
		
		st.setInt(1, qtd);
		st.setInt(2, cod);
		
		int linhasAfetadas = st.executeUpdate();
		if (linhasAfetadas == 0) {
			throw new DbException("Erro! Nenhuma linha foi alterada!");
		}
		DB.closeStatement(st);
	}
	
	@Override
	public void deletarCompra(Integer codCompra) throws SisComException {
		ProdutoDao produtoDao = DaoFactory.criarProdutoDao();
		
		PreparedStatement st = null;
		PreparedStatement st2 = null;
		PreparedStatement st3 = null;
		
		ResultSet rs = null;
		
		try {
			//encontrar produtos na compra
			st = conn.prepareStatement("SELECT CodProduto, QuantCompra FROM itemcompra WHERE CodCompra = ?");
			st.setInt(1, codCompra);
			rs = st.executeQuery();
			if (!rs.next()) {
				throw new SisComException("Não foi encontrada a compra para o código");
			}
			//atualizar o estoque
			 do {
				Produto produto = produtoDao.encontrarPorCodigo(rs.getInt("CodProduto"));
				produto.decrementarQuantidade(rs.getInt("QuantCompra"));
				atualizarEstoque(produto.getEstoque(), produto.getCodigo());
			} while (rs.next());
			//apagar na tabela itemcompra
			st2 = conn.prepareStatement("DELETE FROM itemcompra WHERE CodCompra = ?");			
			st2.setInt(1, codCompra);			
			st2.executeUpdate();
			//apagar na tabela compra
			st3 = conn.prepareStatement("DELETE FROM compra WHERE CodCompra = ?");			
			st3.setInt(1, codCompra);			
			st3.executeUpdate();
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeStatement(st2);
			DB.closeStatement(st3);
			DB.closeResultSet(rs);
		}		
	}

	@Override
	public List<Compra> encontrarCompras() { //deveria fazer retornando itemcompra para usar na tabela no FX
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT compra.*, fornecedor.Nome "
					+ "FROM compra INNER JOIN fornecedor "
					+ "ON compra.CodFornecedor = fornecedor.CodFornecedor "
					+ "ORDER BY fornecedor.Nome");
					
			rs = st.executeQuery();
			
			List<Compra> list = new ArrayList<>();
			Map<Integer, Fornecedor> map = new HashMap<>();
			
			while (rs.next()) {
				
				Fornecedor fornecedor = map.get(rs.getInt("CodFornecedor"));	
				
				if (fornecedor == null) {
					fornecedor = instanciarFornecedor(rs);
					map.put(rs.getInt("CodFornecedor"), fornecedor);
				}
							
				Compra obj = instanciarCompra(rs, fornecedor);				
				list.add(obj);
			}
			return list;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	@Override
	public List<Compra> encontrarComprasNomeFornecedor(String nome){
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT compra.*, fornecedor.Nome "
					+ "FROM compra INNER JOIN fornecedor "
					+ "ON compra.CodFornecedor = fornecedor.CodFornecedor "
					+ "WHERE fornecedor.nome LIKE '" + nome + "%' "
					+ "ORDER BY fornecedor.Nome");
					
			rs = st.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			List<Compra> list = new ArrayList<>();
			Map<Integer, Fornecedor> map = new HashMap<>();
			
			 do {				
				Fornecedor fornecedor = map.get(rs.getInt("CodFornecedor"));
				if (fornecedor == null) {
					fornecedor = instanciarFornecedor(rs);
					map.put(rs.getInt("CodFornecedor"), fornecedor);
				}
							
				Compra obj = instanciarCompra(rs, fornecedor);				
				list.add(obj);
			} while (rs.next());
			return list;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Compra instanciarCompra(ResultSet rs, Fornecedor fornecedor) throws SQLException {
		Compra compra = new Compra();
		compra.setNumCompra(rs.getInt("CodCompra"));
		compra.setFornecedor(fornecedor);
		compra.setCompraItens(criarListaItemCompra(compra.getNumCompra()));
		compra.setDataCompra(rs.getDate("DataCompra"));
		return compra;
	}
	
	private List<ItemCompra> criarListaItemCompra(Integer codCompra){
		PreparedStatement st = null;
		ResultSet rs = null;
		List<ItemCompra> lista = new ArrayList<>();
		ProdutoDao produtoDao = DaoFactory.criarProdutoDao();
		try {
			st = conn.prepareStatement("SELECT * FROM itemcompra WHERE CodCompra = ?");			
			st.setInt(1, codCompra);
			
			rs = st.executeQuery();
			while (rs.next()) {
				Produto produto = produtoDao.encontrarPorCodigo(rs.getInt("CodProduto"));
				lista.add(new ItemCompra(produto, rs.getInt("QuantCompra")));
			}
			return lista;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Fornecedor instanciarFornecedor(ResultSet rs) throws SQLException {
		Fornecedor fornecedor = new Fornecedor();
		fornecedor.setCodigo(rs.getInt("CodFornecedor"));
		fornecedor.setNome(rs.getString("Nome"));
		return fornecedor;
	}
}
