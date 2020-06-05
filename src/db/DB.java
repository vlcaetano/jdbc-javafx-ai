package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class DB {
	/**
	 * Classe DB - métodos responsáveis por criar e fechar a conexão com o banco de dados,
	 * bem como para fechar ResultSets e Statements
	 */
	private static Connection conn = null;
	
	/**
	 * Método para criar a conexão com o banco de dados
	 * @return Connection
	 * @throws DbException
	 */
	public static Connection getConnection() {
		if (conn == null) {
			try {
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				conn = DriverManager.getConnection(url, props);
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		
		return conn;
	}
	
	/**
	 * Método para fechar a conexão com o banco de dados
	 */
	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	/**
	 * Método para carregar as propriedades do arquivo db.properties
	 * @return
	 */
	private static Properties loadProperties() {
		try (FileInputStream fs = new FileInputStream("db.properties")) {
			Properties props = new Properties();
			props.load(fs);
			return props;
		} catch (IOException e) {
			throw new DbException(e.getMessage());
		}
	}
	
	/**
	 * Método para fechar um Statement
	 * @param st
	 */
	public static void closeStatement(Statement st) {
		if(st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	/**
	 * Método para fechar um ResultSet
	 * @param rs
	 */
	public static void closeResultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
}
