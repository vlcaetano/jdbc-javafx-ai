package model.entities;

import java.io.Serializable;

public class ItemVenda implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Produto produto;
	private Integer quantVenda;
	private Double valorVenda;
	
	public ItemVenda() {
	}

	public ItemVenda(Produto produto, Integer quantVenda) {
		this.produto = produto;
		this.quantVenda = quantVenda;
		this.valorVenda = (double) quantVenda * produto.getPrecoUnitario();
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Integer getQuantVenda() {
		return quantVenda;
	}

	public void setQuantVenda(Integer quantVenda) {
		this.quantVenda = quantVenda;
	}

	public Double getValorVenda() {
		return valorVenda;
	}

	public void setValorVenda(Double valorVenda) {
		this.valorVenda = valorVenda;
	}

	@Override
	public String toString() {
		return getProduto().getNome() + " - " 
				+ getQuantVenda() + " - R$" 
				+ String.format("%.2f", getValorVenda());
	}
}
