package web.devaluga.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "aluguel")
// @DynamicUpdate
public class Aluguel implements Serializable {

	private static final long serialVersionUID = -3935828642122652510L;
	
	@Id
	@SequenceGenerator(name="gerador4", sequenceName="aluguel_codigo_seq", allocationSize=1)
	@GeneratedValue(generator="gerador4", strategy = GenerationType.SEQUENCE)
	private Long codigo;

	// Varios alugueis deve referir a um usuario
	// @ManyToOne(fetch = FetchType.LAZY)
	@ManyToOne
	@JoinColumn(name = "usuario_codigo")
	private Usuario usuario;

	// um produto pode estar em varios alugueis
	// @ManyToOne(fetch = FetchType.LAZY)
	@ManyToOne
	@JoinColumn(name = "produto_codigo")
	private Produto produto;

	@NotNull(message = "A data de inicio é obrigatória")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "data_inicio")
	private LocalDate dataInicio;

	@NotNull(message = "A data do final é obrigatória")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "data_fim")
	private LocalDate dataFim;

	@Min(value = 1, message = "O número de doses do lote deve ser maior que 0")
	@Column(name = "valor_total")
	private Float valorTotal;

	@Enumerated(EnumType.STRING)
	private Status status = Status.ATIVO;

	// GETTERS AND SETTERS

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public LocalDate getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(LocalDate dataInicio) {
		this.dataInicio = dataInicio;
	}

	public LocalDate getDataFim() {
		return dataFim;
	}

	public void setDataFim(LocalDate dataFim) {
		this.dataFim = dataFim;
	}

	public Float getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Float valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
		result = prime * result + ((produto == null) ? 0 : produto.hashCode());
		result = prime * result + ((dataInicio == null) ? 0 : dataInicio.hashCode());
		result = prime * result + ((dataFim == null) ? 0 : dataFim.hashCode());
		result = prime * result + ((valorTotal == null) ? 0 : valorTotal.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		Aluguel other = (Aluguel) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		if (produto == null) {
			if (other.produto != null)
				return false;
		} else if (!produto.equals(other.produto))
			return false;
		if (dataInicio == null) {
			if (other.dataInicio != null)
				return false;
		} else if (!dataInicio.equals(other.dataInicio))
			return false;
		if (dataFim == null) {
			if (other.dataFim != null)
				return false;
		} else if (!dataFim.equals(other.dataFim))
			return false;
		if (valorTotal == null) {
			if (other.valorTotal != null)
				return false;
		} else if (!valorTotal.equals(other.valorTotal))
			return false;
		if (status != other.status)
			return false;
		return true;
	}
	
	

}
