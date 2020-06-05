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
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class FornecedorDaoJDBC implements FornecedorDao {
/**
 * Classe FornecedorDaoJDBC - Métodos para comunicar com o banco de dados
 */
	Connection conn;
	/**
	 * Criar conexão com o banco de dados
	 * @param conn
	 */
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
			throw new SisComException(e.getMessage());
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
	public List<Fornecedor> filtrarCnpj(String cnpj) {
		List<Fornecedor> lista = new ArrayList<>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT * "
					+ "FROM fornecedor "
					+ "WHERE Cnpj like '" + cnpj + "%'");
			rs = st.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			do {
				Fornecedor fornecedor = instanciarFornecedor(rs);
				lista.add(fornecedor);
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

	/**
	 * Método para instanciar objeto do tipo Fornecedor
	 * @param rs
	 * @return objeto Fornecedor
	 * @throws SQLException
	 */
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
	public List<Fornecedor> estatisticaFornecedor(String dtInicio, String dtFinal) {
		List<Fornecedor> lista = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		PreparedStatement st2 = null;
		ResultSet rs2 = null;
		
		try {
			String where;
			if (dtInicio == null && dtFinal == null) {
				where = "";
			} else {
				where = "WHERE compra.DataCompra BETWEEN '" + dtInicio + "' AND '" + dtFinal + "' ";
			}
			st = conn.prepareStatement(
					"SELECT fornecedor.CodFornecedor, fornecedor.Nome, sum(itemcompra.valorCompra) as total " 
					+ "FROM fornecedor INNER JOIN compra " 
					+ "ON fornecedor.CodFornecedor = compra.CodFornecedor " 
					+ "INNER JOIN itemcompra "
					+ "ON itemcompra.CodCompra = compra.CodCompra "
					+ where
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
					Fornecedor fornecedor = new Fornecedor();
					fornecedor.setNome(rs.getString("Nome"));
					fornecedor.setQtdCompras(rs2.getInt("Qtd Comprada"));
					fornecedor.setVlrTotal(rs.getDouble("total"));
					lista.add(fornecedor);
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
