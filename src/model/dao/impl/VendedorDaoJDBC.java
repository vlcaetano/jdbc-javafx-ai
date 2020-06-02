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
import model.dao.VendedorDao;
import model.entities.Vendedor;
import model.exceptions.SisComException;

public class VendedorDaoJDBC implements VendedorDao {

	Connection conn;
	
	public VendedorDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void inserirVendedor(Vendedor obj) throws SisComException {
		
		PreparedStatement st = null;
		
		try {
			//Teste para ver se cpf já está cadastrado
			if (encontrarPorCpf(obj.getCpf()) != null) {
				throw new SisComException("Erro! CPF já cadastrado!");
			}
			
			st = conn.prepareStatement(
					"INSERT INTO vendedor "
					+ "(Nome, Telefone, Email, DataCadastro, Cpf, MetaMensal) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getNome());
			st.setString(2, obj.getTelefone());
			st.setString(3, obj.getEmail());
			st.setDate(4, new java.sql.Date(obj.getDataCad().getTime()));
			st.setString(5, obj.getCpf());
			st.setDouble(6, obj.getMetaMensal());
			
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
	public void deletarVendedor(Vendedor obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM vendedor WHERE CodVendedor = ?");
			
			st.setInt(1, obj.getCodigo());
			
			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}	
	}

	@Override
	public List<Vendedor> encontrarTodos() {
		List<Vendedor> lista = new ArrayList<>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT * "
					+ "FROM vendedor "
					+ "ORDER BY Nome");
			rs = st.executeQuery();
			
			while (rs.next()) {
				Vendedor vendedor = instanciarVendedor(rs);
				lista.add(vendedor);
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
	public Vendedor encontrarPorCpf(String dado) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT * " 
					+ "FROM vendedor "
					+ "WHERE Cpf = ?");
			
			st.setString(1, dado);			
			rs = st.executeQuery();
			
			if (rs.next()) {
				Vendedor vendedor = instanciarVendedor(rs);
				return vendedor;
			}			
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Vendedor instanciarVendedor(ResultSet rs) throws SQLException {
		Vendedor obj = new Vendedor();
		
		obj.setCodigo(rs.getInt("CodVendedor"));
		obj.setNome(rs.getString("Nome"));
		obj.setTelefone(rs.getString("Telefone"));
		obj.setEmail(rs.getString("Email"));
		obj.setDataCad(new java.util.Date(rs.getTimestamp("DataCadastro").getTime()));
		obj.setCpf(rs.getString("Cpf"));
		obj.setMetaMensal(rs.getDouble("MetaMensal"));		
		return obj;
	}

	@Override
	public List<String> estatisticaVendedor() {
		List<String> lista = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		PreparedStatement st2 = null;
		ResultSet rs2 = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT vendedor.CodVendedor, vendedor.Nome, sum(itemvenda.valorVenda) as total " 
					+ "FROM vendedor INNER JOIN venda " 
					+ "ON vendedor.CodVendedor = venda.CodVendedor " 
					+ "INNER JOIN itemvenda "
					+ "ON itemvenda.CodVenda = venda.CodVenda " 
					+ "GROUP BY vendedor.CodVendedor, vendedor.Nome "
					+ "ORDER BY vendedor.Nome");
			
			rs = st.executeQuery();
			if (!rs.next()) {
				return null;
			}
			
			do {
				st2 = conn.prepareStatement(
						"SELECT count(*) as 'Qtd Vendas' "
						+ "FROM venda "
						+ "WHERE CodVendedor = " + rs.getInt("CodVendedor"));
				rs2 = st2.executeQuery();
				
				if (rs2.next()) {
					lista.add("Nome: " + rs.getString("Nome") 
					+ " - Número de vendas realizadas: " + rs2.getInt("Qtd Vendas")
					+ " - Total das vendas: R$" + String.format("%.2f", rs.getDouble("total")));
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
