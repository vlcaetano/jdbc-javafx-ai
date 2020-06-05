package view.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class Alerts {
/**
 * Classe Alerts - Métodos para criar janelas de alerta e optional
 */
	
	/**
	 * Método para mostrar alerta
	 * @param title
	 * @param header
	 * @param content
	 * @param type
	 */
	public static void showAlert(String title, String header, String content, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.show();
	}
	
	/**
	 * Método para criar optional
	 * @param title
	 * @param content
	 * @return
	 */
	public static Optional<ButtonType> showConfirmation(String title, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		return alert.showAndWait();
	}
}