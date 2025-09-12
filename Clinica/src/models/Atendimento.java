package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import util.Situacao;

@Entity
public class Atendimento {

	@Id 
	@GeneratedValue
	private Long id;
	
	private Long numero;
	private Date dataEntrada;
	private Situacao situacao;
	private String parecer;
	
	@ManyToMany
	@JoinTable(name = "atendimento_medico", joinColumns = @JoinColumn(name = "atendimento_id"), inverseJoinColumns = @JoinColumn(name = "medico_id"))
	private List<Medico> medicos;
	
	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	private Paciente paciente;
	
	public Atendimento() {
		
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public Situacao getSituacao() {
		return situacao;
	}

	public void setSituacao(Situacao situacao) {
		this.situacao = situacao;
	}

	public String getParecer() {
		return parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer;
	}
	
	public void gerarNumeroAtendimento() {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String timestampStr = LocalDateTime.now().format(formatter);
		this.numero = Long.parseLong(timestampStr);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Medico> getMedicos() {
		return medicos;
	}

	public void setMedicos(List<Medico> medicos) {
		this.medicos = medicos;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}
	
	
}
