package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.ProdutoDao;
import model.entities.Produto;

public class ProdutoDaoJDBC implements ProdutoDao {

	Connection conn;
	
	public ProdutoDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void inserirProduto(Produto obj) {
		
		PreparedStatement st = null;
		
		try {
			
			st = conn.prepareStatement(
					"INSERT INTO produto "
					+ "(Nome, PrecoUnitario, Estoque, EstoqueMinimo, DataCadastro) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getNome());
			st.setDouble(2, obj.getPrecoUnitario());
			st.setInt(3, obj.getEstoque());
			st.setInt(4, obj.getEstoqueMinimo());			
			st.setDate(5, new java.sql.Date(obj.getDataCad().getTime()));
			
			int linhasAfetadas = st.executeUpdate();
			
			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int cod = rs.getInt(1);
					obj.setCodigo(cod);;
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Erro! Nenhuma linha foi alterada!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deletarProduto(Integer cod) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM produto WHERE CodProduto = ?");
			
			st.setInt(1, cod);
			
			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}	
	}

	@Override
	public List<Produto> encontrarTodos() {
		List<Produto> lista = new ArrayList<>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT * "
					+ "FROM produto "
					+ "ORDER BY Nome");
			rs = st.executeQuery();
			
			while (rs.next()) {
				Produto produto = instanciarProduto(rs);
				lista.add(produto);
			}
			return lista;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}
	
	@Override
	public List<Produto> encontrarAbaixoEstoqueMin(){
		List<Produto> lista = new ArrayList<>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT * "
					+ "FROM produto "
					+ "WHERE Estoque < EstoqueMinimo "
					+ "ORDER BY Nome");
			rs = st.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			 do {
				Produto produto = instanciarProduto(rs);
				lista.add(produto);
			} while (rs.next());
			return lista;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public Produto encontrarPorCodigo(Integer cod) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT * " 
					+ "FROM produto "
					+ "WHERE CodProduto = ?");
			
			st.setInt(1, cod);			
			rs = st.executeQuery();
			
			if (rs.next()) {
				Produto produto = instanciarProduto(rs);
				return produto;
			}			
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Produto instanciarProduto(ResultSet rs) throws SQLException {
		Produto obj = new Produto();
		obj.setCodigo(rs.getInt("CodProduto"));
		obj.setNome(rs.getString("Nome"));
		obj.setPrecoUnitario(rs.getDouble("PrecoUnitario"));
		obj.setEstoque(rs.getInt("Estoque"));
		obj.setEstoqueMinimo(rs.getInt("EstoqueMinimo"));
		obj.setDataCad(new java.util.Date(rs.getTimestamp("DataCadastro").getTime()));
				
		return obj;
	}


}
