package model.exceptions;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class SisComException extends Exception {
/**
 * Classe SisComException - Tratamento de erros relacionados ao sistema
 */
	private static final long serialVersionUID = 1L;

	private String nomeProduto;
	private Integer estoque;
	private String mensagemErro;
	
	/**
	 * Método construtor da classe
	 * @param nomeProduto
	 * @param estoque
	 * @param mensagemErro
	 */
	public SisComException(String nomeProduto, Integer estoque, String mensagemErro) {
		super(mensagemErro);
		this.nomeProduto = nomeProduto;
		this.estoque = estoque;
	}
	
	/**
	 * Método construtor apenas com mensagem
	 * @param mensagemErro
	 */
	public SisComException(String mensagemErro) {
		this.mensagemErro = mensagemErro;
	}

	public String getNomeProduto() {
		return nomeProduto;
	}

	public Integer getEstoque() {
		return estoque;
	}

	public String getMensagemErro() {
		return mensagemErro;
	}
}
