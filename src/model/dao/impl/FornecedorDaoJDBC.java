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
import model.dao.FornecedorDao;
import model.entities.Fornecedor;
import model.exceptions.SisComException;

public class FornecedorDaoJDBC implements FornecedorDao {

	Connection conn;
	
	public FornecedorDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void inserirFornecedor(Fornecedor obj) throws SisComException {
		
		PreparedStatement st = null;
		
		try {
			//Teste para ver se cnpj já está cadastrado
			if (encontrarPorCnpj(obj.getCnpj()) != null) {
				throw new SisComException("Erro! CNPJ já cadastrado!");
			}
			
			st = conn.prepareStatement(
					"INSERT INTO fornecedor "
					+ "(Nome, Telefone, Email, DataCadastro, Cnpj, NomeContato) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getNome());
			st.setString(2, obj.getTelefone());
			st.setString(3, obj.getEmail());
			st.setDate(4, new java.sql.Date(obj.getDataCad().getTime()));
			st.setString(5, obj.getCnpj());
			st.setString(6, obj.getNomeContato());
			
			int linhasAfetadas = st.executeUpdate();
			
			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int cod = rs.getInt(1);
					obj.setCodigo(cod);;
				}
				DB.closeResultSet(rs);
			} else {
				throw new SisComException("Erro! Nenhuma linha foi alterada!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deletarFornecedor(Fornecedor obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM fornecedor WHERE CodFornecedor = ?");
			
			st.setInt(1, obj.getCodigo());
			
			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}	
	}

	@Override
	public List<Fornecedor> encontrarTodos() {
		List<Fornecedor> lista = new ArrayList<>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT * "
					+ "FROM fornecedor "
					+ "ORDER BY Nome");
			rs = st.executeQuery();
			
			while (rs.next()) {
				Fornecedor fornecedor = instanciarFornecedor(rs);
				lista.add(fornecedor);
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
	public Fornecedor encontrarPorCnpj(String dado) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT * " 
					+ "FROM fornecedor "
					+ "WHERE Cnpj = ?");
			
			st.setString(1, dado);			
			rs = st.executeQuery();
			
			if (rs.next()) {
				Fornecedor fornecedor = instanciarFornecedor(rs);
				return fornecedor;
			}			
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Fornecedor instanciarFornecedor(ResultSet rs) throws SQLException {
		Fornecedor obj = new Fornecedor();
		obj.setCodigo(rs.getInt("CodFornecedor"));
		obj.setNome(rs.getString("Nome"));
		obj.setTelefone(rs.getString("Telefone"));
		obj.setEmail(rs.getString("Email"));
		obj.setDataCad(new java.util.Date(rs.getTimestamp("DataCadastro").getTime()));
		obj.setCnpj(rs.getString("Cnpj"));
		obj.setNomeContato(rs.getString("NomeContato"));		
		return obj;
	}

	@Override
	public List<String> estatisticaFornecedor() {
		List<String> lista = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		PreparedStatement st2 = null;
		ResultSet rs2 = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT fornecedor.CodFornecedor, fornecedor.Nome, sum(itemcompra.valorCompra) as total " 
					+ "FROM fornecedor INNER JOIN compra " 
					+ "ON fornecedor.CodFornecedor = compra.CodFornecedor " 
					+ "INNER JOIN itemcompra "
					+ "ON itemcompra.CodCompra = compra.CodCompra " 
					+ "GROUP BY fornecedor.CodFornecedor, fornecedor.Nome "
					+ "ORDER BY fornecedor.Nome");
			
			rs = st.executeQuery();
			if (!rs.next()) {
				return null;
			}
			
			do {
				st2 = conn.prepareStatement(
						"SELECT count(*) AS 'Qtd Comprada' "
						+ "FROM compra "
						+ "WHERE CodFornecedor = " + rs.getInt("CodFornecedor"));
				rs2 = st2.executeQuery();
				
				if (rs2.next()) {
					lista.add("Fornecedor: " + rs.getString("Nome") 
					+ " - Número de compras: " + rs2.getInt("Qtd Comprada")
					+ " - Total gasto: R$" + String.format("%.2f", rs.getDouble("total")));
				}
			}while (rs.next());
			return lista;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeStatement(st2);
			DB.closeResultSet(rs);
			DB.closeResultSet(rs2);
		}
	}
	
}
