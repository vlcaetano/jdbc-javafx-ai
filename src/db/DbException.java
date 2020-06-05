package db;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class DbException extends RuntimeException {
/**
 * Classe DbException - Exceção para erros relacionados ao banco de dados
 */
	private static final long serialVersionUID = 1L;
	/**
	 * Método construtor
	 * @param msg
	 */
	public DbException(String msg) {
		super(msg);
	}
}
