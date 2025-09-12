package models;

import javax.persistence.Entity;

@Entity
public class Medico extends Pessoa{
	
	private String crm;
	private String especialidade;
	
	public Medico() {
		
	}

	public String getCrm() {
		return crm;
	}

	public void setCrm(String crm) {
		this.crm = crm;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	
}
