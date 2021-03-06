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
import model.dao.VendaDao;
import model.dao.DaoFactory;
import model.dao.ProdutoDao;
import model.entities.Venda;
import model.entities.Vendedor;
import model.entities.Cliente;
import model.entities.ItemVenda;
import model.entities.Produto;
import model.exceptions.SisComException;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class VendaDaoJDBC implements VendaDao {
/**
 * Classe VendaDaoJDBC - M�todos para comunicar com o banco de dados
 */
	Connection conn;
	/**
	 * Criar conex�o com o banco de dados
	 * @param conn
	 */
	public VendaDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void fazerVenda(Venda obj) throws SisComException {
		PreparedStatement st = null;
		
		try {
			//Criar venda
			st = conn.prepareStatement(
					"INSERT INTO venda "
					+ "(CodCliente, CodVendedor, FormaPagamento, DataVenda) "
					+ "VALUES "
					+ "(?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setInt(1, obj.getCliente().getCodigo());
			st.setInt(2, obj.getVendedor().getCodigo());
			st.setInt(3, obj.getFormaPagto());
			st.setDate(4, new java.sql.Date(obj.getDataVenda().getTime()));
			
			int linhasAfetadas = st.executeUpdate();
			
			if (linhasAfetadas > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int cod = rs.getInt(1);
					obj.setNumVenda(cod);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Erro! Nenhuma linha foi alterada!");
			}
			
			for (ItemVenda iv : obj.getVendaItens()) {
				criarItemVenda(iv.getProduto().getCodigo(), obj.getNumVenda(), iv.getQuantVenda(), iv.getValorVenda());
				atualizarEstoque(iv.getProduto().decrementarQuantidade(iv.getQuantVenda()), iv.getProduto().getCodigo());
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} catch (SisComException e) {
			throw new SisComException("Erro ao decrementar estoque!");
		} finally {
			DB.closeStatement(st);
		}
		
	}
	
	/**
	 * M�todo para inserir itens da venda no banco de dados
	 * @param codProduto
	 * @param codVenda
	 * @param qtd
	 * @param valor
	 * @throws SQLException
	 */
	private void criarItemVenda(Integer codProduto, Integer codVenda, Integer qtd, Double valor) throws SQLException {
		PreparedStatement st = null;
		
		st = conn.prepareStatement(
				"INSERT INTO itemvenda "
				+ "(CodProduto, CodVenda, QuantVenda, valorVenda) "
				+ "VALUES "
				+ "(?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS);
		st.setInt(1, codProduto);
		st.setInt(2, codVenda);
		st.setInt(3, qtd);
		st.setDouble(4, valor);
		
		int linhasAfetadas = st.executeUpdate();
		
		if (linhasAfetadas == 0) {
			throw new DbException("Erro! Nenhuma linha foi alterada!");
		}
		DB.closeStatement(st);
	}

	/**
	 * M�todo para atualizar estoques dos produtos no banco de dados ap�s uma venda
	 * @param qtd
	 * @param cod
	 * @throws SQLException
	 */
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
	public void deletarVenda(Integer codVenda) throws SisComException {
		ProdutoDao produtoDao = DaoFactory.criarProdutoDao();
		
		PreparedStatement st = null;
		PreparedStatement st2 = null;
		PreparedStatement st3 = null;
		
		ResultSet rs = null;
		
		try {
			//atualizar o estoque
			st = conn.prepareStatement("SELECT CodProduto, QuantVenda FROM itemvenda WHERE CodVenda = ?");
			st.setInt(1, codVenda);
			rs = st.executeQuery();
			
			if (!rs.next()) {
				throw new SisComException("N�o foi encontrada a venda para o c�digo");
			}
			
			do {
				Produto produto = produtoDao.encontrarPorCodigo(rs.getInt("CodProduto"));
				produto.adicionarQuantidade(rs.getInt("QuantVenda"));
				atualizarEstoque(produto.getEstoque(), produto.getCodigo());
			} while (rs.next());
			//apagar na tabela itemvenda
			st2 = conn.prepareStatement("DELETE FROM itemvenda WHERE CodVenda = ?");			
			st2.setInt(1, codVenda);			
			st2.executeUpdate();
			//apagar na tabela venda
			st3 = conn.prepareStatement("DELETE FROM venda WHERE CodVenda = ?");			
			st3.setInt(1, codVenda);			
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
	public List<Venda> encontrarVendas(String dtInicio, String dtFinal) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			String where = "";
			if (dtInicio != null && dtFinal != null) {
				where = "WHERE venda.DataVenda BETWEEN '" + dtInicio + "' AND '" + dtFinal + "'";
			}
			
			st = conn.prepareStatement(
					"SELECT venda.*, vendedor.Nome as VendNome, vendedor.Cpf as VendCpf, "
					+ "cliente.Nome as CliNome, cliente.Cpf as CliCpf "
					+ "FROM venda INNER JOIN vendedor "
					+ "ON venda.CodVendedor = vendedor.CodVendedor "
					+ "INNER JOIN cliente "
					+ "ON venda.CodCliente = cliente.CodCliente "
					+ where
					+ "ORDER BY venda.DataVenda DESC");

			rs = st.executeQuery();
			
			List<Venda> list = new ArrayList<>();
			Map<Integer, Vendedor> map = new HashMap<>();
			Map<Integer, Cliente> map2 = new HashMap<>();
			
			if (!rs.next()) {
				return null;
			}
			
			do {
				Venda obj = criarVenda(map, map2, rs);
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
	
	@Override
	public List<Venda> encontrarVendasNomeCliente(String nome) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT venda.*, vendedor.Nome as VendNome, vendedor.Cpf as VendCpf, "
					+ "cliente.Nome as CliNome, cliente.Cpf as CliCpf "
					+ "FROM venda INNER JOIN vendedor "
					+ "ON venda.CodVendedor = vendedor.CodVendedor "
					+ "INNER JOIN cliente "
					+ "ON venda.CodCliente = cliente.CodCliente "
					+ "WHERE cliente.nome LIKE '" + nome + "%' "
					+ "ORDER BY cliente.Nome");
			
			rs = st.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			List<Venda> list = new ArrayList<>();
			Map<Integer, Vendedor> map = new HashMap<>();
			Map<Integer, Cliente> map2 = new HashMap<>();
			
			do {
				Venda obj = criarVenda(map, map2, rs);
				list.add(obj);
			} while(rs.next());
			return list;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	@Override
	public List<Venda> encontrarVendasNomeVendedor(String nome) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT venda.*, vendedor.Nome as VendNome, vendedor.Cpf as VendCpf, "
					+ "cliente.Nome as CliNome, cliente.Cpf as CliCpf "
					+ "FROM venda INNER JOIN vendedor "
					+ "ON venda.CodVendedor = vendedor.CodVendedor "
					+ "INNER JOIN cliente "
					+ "ON venda.CodCliente = cliente.CodCliente "
					+ "WHERE vendedor.nome LIKE '" + nome + "%' "
					+ "ORDER BY vendedor.Nome");

			rs = st.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			List<Venda> list = new ArrayList<>();
			Map<Integer, Vendedor> map = new HashMap<>();
			Map<Integer, Cliente> map2 = new HashMap<>();
			
			do {
				Venda obj = criarVenda(map, map2, rs);						
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

	/**
	 * M�todo para criar uma venda
	 * @param map
	 * @param map2
	 * @param rs
	 * @return objeto Venda
	 * @throws SQLException
	 */
	private Venda criarVenda(Map<Integer, Vendedor> map, Map<Integer, Cliente> map2, ResultSet rs) throws SQLException {
		Vendedor vendedor = map.get(rs.getInt("CodVendedor"));
		if (vendedor == null) {
			vendedor = instanciarVendedor(rs);
			map.put(rs.getInt("CodVendedor"), vendedor);
		}
		
		Cliente cliente = map2.get(rs.getInt("CodCliente"));
		if (cliente == null) {
			cliente = instanciarCliente(rs);
			map2.put(rs.getInt("CodCliente"), cliente);
		}
					
		Venda obj = instanciarVenda(rs, vendedor, cliente);
		return obj;
	}

	/**
	 * M�todo para instanciar um objeto do tipo Venda
	 * @param rs
	 * @param vendedor
	 * @param cliente
	 * @return objeto Venda
	 * @throws SQLException
	 */
	private Venda instanciarVenda(ResultSet rs, Vendedor vendedor, Cliente cliente) throws SQLException {
		Venda venda = new Venda();
		venda.setNumVenda(rs.getInt("CodVenda"));
		venda.setCliente(cliente);
		venda.setVendedor(vendedor);
		venda.setFormaPagto(rs.getInt("FormaPagamento"));
		venda.setDataVenda(rs.getDate("DataVenda"));
		venda.setVendaItens(criarListaItemVenda(venda.getNumVenda()));
		return venda;
	}
	
	@Override
	public List<ItemVenda> criarListaItemVenda(Integer codVenda){
		PreparedStatement st = null;
		ResultSet rs = null;
		List<ItemVenda> lista = new ArrayList<>();
		ProdutoDao produtoDao = DaoFactory.criarProdutoDao();
		try {
			st = conn.prepareStatement("SELECT * FROM itemvenda WHERE CodVenda = ?");			
			st.setInt(1, codVenda);
			
			rs = st.executeQuery();
			while (rs.next()) {
				Produto produto = produtoDao.encontrarPorCodigo(rs.getInt("CodProduto"));
				lista.add(new ItemVenda(produto, rs.getInt("QuantVenda")));
			}
			return lista;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	/**
	 * M�todo para instanciar um objeto do tipo Vendedor
	 * @param rs
	 * @return objeto Vendedor
	 * @throws SQLException
	 */
	private Vendedor instanciarVendedor(ResultSet rs) throws SQLException {
		Vendedor vendedor = new Vendedor();
		vendedor.setCodigo(rs.getInt("CodVendedor"));
		vendedor.setNome(rs.getString("VendNome"));
		vendedor.setCpf(rs.getString("VendCpf"));
		return vendedor;
	}
	
	/**
	 * M�todo para instanciar um objeto do tipo Cliente
	 * @param rs
	 * @return objeto Cliente
	 * @throws SQLException
	 */
	private Cliente instanciarCliente(ResultSet rs) throws SQLException {
		Cliente cliente = new Cliente();
		cliente.setCodigo(rs.getInt("CodCliente"));
		cliente.setNome(rs.getString("CliNome"));
		cliente.setCpf("CliCpf");
		return cliente;
	}
}
