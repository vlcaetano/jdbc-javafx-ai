package model.entities;

import java.util.Date;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public class Cliente extends Pessoa {

	private static final long serialVersionUID = 1L;
	
	private String cpf;
	private Double limiteCredito;
	
	private Integer qtdCompras;
	private Double vlrTotal;
	
	/**
	 * M�todo construtor padr�o
	 */
	public Cliente() {
	}
	
	/**
	 * M�todo construtor da classe
	 * @param codigo
	 * @param nome
	 * @param telefone
	 * @param email
	 * @param dataCad
	 * @param cpf
	 * @param limiteCredito
	 */
	public Cliente(Integer codigo, String nome, String telefone, String email, Date dataCad, String cpf,
			Double limiteCredito) {
		super(codigo, nome, telefone, email, dataCad);
		this.cpf = cpf;
		this.limiteCredito = limiteCredito;
	}
	
	public Integer getQtdCompras() {
		return qtdCompras;
	}
	
	public void setQtdCompras(Integer qtdCompras) {
		this.qtdCompras = qtdCompras;
	}
	
	public Double getVlrTotal() {
		return vlrTotal;
	}
	
	public void setVlrTotal(Double vlrTotal) {
		this.vlrTotal = vlrTotal;
	}
	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Double getLimiteCredito() {
		return limiteCredito;
	}

	public void setLimiteCredito(Double limiteCredito) {
		this.limiteCredito = limiteCredito;
	}

	@Override
	public Cliente tipoPessoa() {
		return this;
	}

	@Override
	public String toString() {
		return super.toString() + " - CPF: " + cpf + " - Limite de Cr�dito: R$" + String.format("%.2f", limiteCredito);
	}
}
