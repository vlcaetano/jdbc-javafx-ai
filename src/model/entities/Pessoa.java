package model.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 
 * @author Vitor Lima Caetano
 *
 */
public abstract class Pessoa implements Comparable<Pessoa>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer codigo;
	private String nome;
	private String telefone;
	private String email;
	private Date dataCad;
	
	/**
	 * Método construtor padrão
	 */
	public Pessoa() {
	}

	/**
	 * Método construtor da classe
	 * @param codigo
	 * @param nome
	 * @param telefone
	 * @param email
	 * @param dataCad
	 */
	public Pessoa(Integer codigo, String nome, String telefone, String email, Date dataCad) {
		this.codigo = codigo;
		this.nome = nome;
		this.telefone = telefone;
		this.email = email;
		this.dataCad = dataCad;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDataCad() {
		return dataCad;
	}

	public void setDataCad(Date dataCad) {
		this.dataCad = dataCad;
	}
	
	public abstract Pessoa tipoPessoa();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pessoa other = (Pessoa) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(Pessoa obj) {
		return nome.toUpperCase().compareTo(obj.getNome().toUpperCase());
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return "Código: " + codigo + " - Nome: " + nome + " - Tel: " + telefone
				+ " - Email: " + email + " - Data cadastro: " + sdf.format(dataCad);
	}
}
