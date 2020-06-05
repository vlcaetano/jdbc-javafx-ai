package view.util;

import javafx.scene.control.TextField;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class Constraints {
/**
 * Classe Constraints - M�todos para limitar o que o usu�rio pode digitar nos TextFields
 */
	
	/**
	 * M�todo para limitar o usu�rio a digitar apenas n�meros
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
	 * M�todo para limitar a quantidade de caracteres digitados
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
	 * M�todo para limitar o usu�rio a digitar apenas n�meros de ponto flutuante
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