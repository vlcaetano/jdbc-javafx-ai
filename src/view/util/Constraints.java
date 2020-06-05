package view.util;

import javafx.scene.control.TextField;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class Constraints {
/**
 * Classe Constraints - Métodos para limitar o que o usuário pode digitar nos TextFields
 */
	
	/**
	 * Método para limitar o usuário a digitar apenas números
	 * @param txt
	 */
	public static void setTextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
	        if (newValue != null && !newValue.matches("\\d*")) {
	        	txt.setText(oldValue);
	        }
	    });
	}
	
	/**
	 * Método para limitar a quantidade de caracteres digitados
	 * @param txt
	 * @param max
	 */
	public static void setTextFieldMaxLength(TextField txt, int max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
	        if (newValue != null && newValue.length() > max) {
	        	txt.setText(oldValue);
	        }
	    });
	}

	/**
	 * Método para limitar o usuário a digitar apenas números de ponto flutuante
	 * @param txt
	 */
	public static void setTextFieldDouble(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
		    	if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
                    txt.setText(oldValue);
                }
		    });
	}
}