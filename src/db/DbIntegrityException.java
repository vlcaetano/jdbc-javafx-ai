package db;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class DbIntegrityException extends RuntimeException {
/**
 * Classe DbIntegrityException - Exceção para erros relacionados a integridade do banco de dados
 */
	private static final long serialVersionUID = 1L;
	/**
	 * Construtor da classe
	 * @param msg
	 */
	public DbIntegrityException(String msg) {
		super(msg);
	}
}
