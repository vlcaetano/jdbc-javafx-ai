package model.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.exceptions.SisComException;

public class Produto implements Comparable<Produto>, Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer codigo;
	private String nome;
	private Double precoUnitario;
	private Integer estoque;
	private Integer estoqueMinimo;
	private Date dataCad;
	
	public Produto() {
	}

	public Produto(Integer codigo, String nome, Double precoUnitario, Integer estoque, Integer estoqueMinimo,
			Date dataCad) {
		this.codigo = codigo;
		this.nome = nome;
		this.precoUnitario = precoUnitario;
		this.estoque = estoque;
		this.estoqueMinimo = estoqueMinimo;
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

	public Double getPrecoUnitario() {
		return precoUnitario;
	}

	public void setPrecoUnitario(Double precoUnitario) {
		this.precoUnitario = precoUnitario;
	}

	public Integer getEstoque() {
		return estoque;
	}

	public void setEstoque(Integer estoque) {
		this.estoque = estoque;
	}

	public Integer getEstoqueMinimo() {
		return estoqueMinimo;
	}

	public void setEstoqueMinimo(Integer estoqueMinimo) {
		this.estoqueMinimo = estoqueMinimo;
	}

	public Date getDataCad() {
		return dataCad;
	}

	public void setDataCad(Date dataCad) {
		this.dataCad = dataCad;
	}
	
	public Integer adicionarQuantidade(Integer quant) {
		return estoque += quant;
	}
	
	public Integer decrementarQuantidade(Integer quant) throws SisComException {
		if (estoque - quant < 0) {
			throw new SisComException(nome, estoque, "Estoque Insuficiente. " + nome + " possui apenas " + estoque + "unidades no estoque");
		} else {
			return estoque -= quant;
		}
	}

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
		Produto other = (Produto) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return "Código: " + codigo + " - Nome: " + nome 
				+ " - Preço unitário: R$" + String.format("%.2f", precoUnitario)
				+ " - Estoque: " + estoque + " - Estoque Mínimo: " + estoqueMinimo 
				+ " - Data cadastro: " + sdf.format(dataCad);
	}

	@Override
	public int compareTo(Produto obj) {
		return nome.toUpperCase().compareTo(obj.getNome().toUpperCase());
	}
}
