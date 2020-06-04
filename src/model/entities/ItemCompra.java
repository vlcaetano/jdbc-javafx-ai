package model.entities;

import java.io.Serializable;

public class ItemCompra implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Produto produto;
	private Integer quantCompra;
	private Double valorCompra;
	
	private Integer codProduto;
	private String nomeProduto;
	
	public ItemCompra() {
	}

	public ItemCompra(Produto produto, Integer quantCompra) {
		this.produto = produto;
		this.quantCompra = quantCompra;
		this.valorCompra = (double) quantCompra * produto.getPrecoUnitario();
		codProduto = produto.getCodigo();
		nomeProduto = produto.getNome();
	}
	
	public Integer getCodProduto() {		
		return codProduto;
	}
	
	public String getNomeProduto() {
		return nomeProduto;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Integer getQuantCompra() {
		return quantCompra;
	}

	public void setQuantCompra(Integer quantCompra) {
		this.quantCompra = quantCompra;
	}

	public Double getValorCompra() {
		return valorCompra;
	}

	public void setValorCompra(Double valorCompra) {
		this.valorCompra = valorCompra;
	}

	@Override
	public String toString() {
		return getProduto().getNome() + " - " 
				+ getQuantCompra() + " - R$" 
				+ String.format("%.2f", getValorCompra());
	}
}
