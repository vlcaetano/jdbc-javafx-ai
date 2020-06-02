package model.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Venda implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer numVenda;
	private Cliente cliente;
	private Vendedor vendedor;
	private List<ItemVenda> vendaItens;
	private Integer formaPagto;
	private Date dataVenda;
	
	public Venda() {
	}

	public Venda(Integer numVenda, Cliente cliente, Vendedor vendedor, List<ItemVenda> vendaItens,
			Integer formaPagto, Date dataVenda) {
		this.numVenda = numVenda;
		this.cliente = cliente;
		this.vendedor = vendedor;
		this.vendaItens = vendaItens;
		this.formaPagto = formaPagto;
		this.dataVenda = dataVenda;
	}

	public Integer getNumVenda() {
		return numVenda;
	}

	public void setNumVenda(Integer numVenda) {
		this.numVenda = numVenda;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	public List<ItemVenda> getVendaItens() {
		return vendaItens;
	}

	public void setVendaItens(List<ItemVenda> vendaItens) {
		this.vendaItens = vendaItens;
	}

	public Integer getFormaPagto() {
		return formaPagto;
	}

	public void setFormaPagto(Integer formaPagto) {
		this.formaPagto = formaPagto;
	}

	public Date getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(Date dataVenda) {
		this.dataVenda = dataVenda;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numVenda == null) ? 0 : numVenda.hashCode());
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
		Venda other = (Venda) obj;
		if (numVenda == null) {
			if (other.numVenda != null)
				return false;
		} else if (!numVenda.equals(other.numVenda))
			return false;
		return true;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		return "Data: " + sdf.format(getDataVenda())		
			+ " - Cliente: " + getCliente().getNome()
			+ " - Vendedor: " + getVendedor().getNome()
			+ " - Código da venda: " + getNumVenda();
	}
}
