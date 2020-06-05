package db;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class DbException extends RuntimeException {
/**
 * Classe DbException - Exce��o para erros relacionados ao banco de dados
 */
	private static final long serialVersionUID = 1L;
	/**
	 * M�todo construtor
	 * @param msg
	 */
	public DbException(String msg) {
		super(msg);
	}
}
