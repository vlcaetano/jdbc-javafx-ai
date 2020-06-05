package model.exceptions;

import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class ValidationException extends RuntimeException {
/**
 * Classe ValidationException - Métodos usados para dar set nos textos das Labels de erro
 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> errors = new HashMap<>();

	/**
	 * Método construtor da classe
	 * @param msg
	 */
	public ValidationException(String msg) {
		super(msg);
	}
	
	public Map<String, String> getErrors(){
		return errors;
	}
	
	/**
	 * Método para adicionar erro ao Map
	 * @param fieldName
	 * @param errorMessage
	 */
	public void addError(String fieldName, String errorMessage) {
		errors.put(fieldName, errorMessage);
	}
}
