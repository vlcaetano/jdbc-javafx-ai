package model.entities;

import java.util.Date;

public class Fornecedor extends Pessoa {

	private static final long serialVersionUID = 1L;
	
	private String cnpj;
	private String nomeContato;
	
	private Integer qtdCompras;
	private Double vlrTotal;
	
	public Fornecedor() {
	}
	
	public Fornecedor(Integer codigo, String nome, String telefone, String email, Date dataCad, String cnpj,
			String nomeContato) {
		super(codigo, nome, telefone, email, dataCad);
		this.cnpj = cnpj;
		this.nomeContato = nomeContato;
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

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getNomeContato() {
		return nomeContato;
	}

	public void setNomeContato(String nomeContato) {
		this.nomeContato = nomeContato;
	}

	@Override
	public Fornecedor tipoPessoa() {
		return this;
	}

	@Override
	public String toString() {
		return super.toString() + " - CNPJ: " + cnpj + " - Nome Contato: " + nomeContato;
	}
}
