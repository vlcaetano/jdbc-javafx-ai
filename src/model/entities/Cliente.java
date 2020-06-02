package model.entities;

import java.util.Date;

public class Cliente extends Pessoa {

	private static final long serialVersionUID = 1L;
	
	private String cpf;
	private Double limiteCredito;
	
	public Cliente() {
	}
	
	public Cliente(Integer codigo, String nome, String telefone, String email, Date dataCad, String cpf,
			Double limiteCredito) {
		super(codigo, nome, telefone, email, dataCad);
		this.cpf = cpf;
		this.limiteCredito = limiteCredito;
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
		return super.toString() + " - CPF: " + cpf + " - Limite de Crédito: R$" + String.format("%.2f", limiteCredito);
	}
}
