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
import model.dao.ClienteDao;
import model.entities.Cliente;
import model.exceptions.SisComException;

public class ClienteDaoJDBC implements ClienteDao {

	Connection conn;
	
	public ClienteDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void inserirCliente(Cliente obj) throws SisComException {
		
		PreparedStatement st = null;
		
		try {
			//Teste para ver se cpf já está cadastrado
			if (encontrarPorCpf(obj.getCpf()) != null) {
				throw new SisComException("Erro! CPF já cadastrado!");
			}
			
			st = conn.prepareStatement(
					"INSERT INTO cliente "
					+ "(Nome, Telefone, Email, DataCadastro, Cpf, LimiteCredito) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getNome());
			st.setString(2, obj.getTelefone());
			st.setString(3, obj.getEmail());
			st.setDate(4, new java.sql.Date(obj.getDataCad().getTime()));
			st.setString(5, obj.getCpf());
			st.setDouble(6, obj.getLimiteCredito());
			
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
		} catch (DbIntegrityException e) {
			System.out.println(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deletarCliente(Cliente obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement("DELETE FROM cliente WHERE CodCliente = ?");
			
			st.setInt(1, obj.getCodigo());
			
			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}	
	}

	@Override
	public List<Cliente> encontrarTodos() {
		List<Cliente> lista = new ArrayList<>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT * "
					+ "FROM cliente "
					+ "ORDER BY Nome");
			rs = st.executeQuery();
			
			while (rs.next()) {
				Cliente cliente = instanciarCliente(rs);
				lista.add(cliente);
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
	public Cliente encontrarPorCpf(String dado) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT * " 
					+ "FROM cliente "
					+ "WHERE Cpf = ?");
			
			st.setString(1, dado);			
			rs = st.executeQuery();
			
			if (rs.next()) {
				Cliente cliente = instanciarCliente(rs);
				return cliente;
			}			
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Cliente instanciarCliente(ResultSet rs) throws SQLException {
		Cliente obj = new Cliente();
		obj.setCodigo(rs.getInt("CodCliente"));
		obj.setNome(rs.getString("Nome"));
		obj.setTelefone(rs.getString("Telefone"));
		obj.setEmail(rs.getString("Email"));
		obj.setDataCad(new java.util.Date(rs.getTimestamp("DataCadastro").getTime()));
		obj.setCpf(rs.getString("Cpf"));
		obj.setLimiteCredito(rs.getDouble("LimiteCredito"));		
		return obj;
	}

	@Override
	public List<String> estatisticaCliente() {
		List<String> lista = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		PreparedStatement st2 = null;
		ResultSet rs2 = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT cliente.CodCliente, cliente.Nome, sum(itemvenda.valorVenda) as total " 
					+ "FROM cliente INNER JOIN venda " 
					+ "ON cliente.CodCliente = venda.CodCliente " 
					+ "INNER JOIN itemvenda "
					+ "ON itemvenda.CodVenda = venda.CodVenda " 
					+ "GROUP BY cliente.CodCliente, cliente.Nome "
					+ "ORDER BY cliente.Nome");
			
			rs = st.executeQuery();
			if (!rs.next()) {
				return null;
			}
			
			 do {
				st2 = conn.prepareStatement(
						"SELECT count(*) as 'Qtd Compras' "
						+ "FROM venda "
						+ "WHERE CodCliente = " + rs.getInt("CodCliente"));
				rs2 = st2.executeQuery();
				
				if (rs2.next()) {
					lista.add("Nome: " + rs.getString("Nome") 
					+ " - Número de compras: " + rs2.getInt("Qtd Compras")
					+ " - Total gasto: R$" + String.format("%.2f", rs.getDouble("total")));
				}
			} while (rs.next());
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
