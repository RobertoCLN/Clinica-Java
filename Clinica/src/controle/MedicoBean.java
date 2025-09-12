package controle;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import models.Endereco;
import models.Medico;
import service.EnderecoService;
import service.MedicoService;

@ManagedBean
@ViewScoped
public class MedicoBean {

	@EJB
	private MedicoService medicoService;
	
	@EJB
	private EnderecoService enderecoService;
	
	private Medico medico;
	private Endereco endereco;
	private List<Medico> medicos;
	private Boolean editar = false;
	private String nome;
	
	@PostConstruct
	public void iniciar() {
		limparFormulario();
		atualizarLista();
	}
	
	public void carregar(Medico medico) {
		this.medico = medico;
		this.editar = true;
	}
	
	public void salvar() {
		medicoService.merge(medico);
		atualizarLista();
		FacesContext.getCurrentInstance().addMessage("",
				new FacesMessage("Ação", "Medico gravado!"));
		limparFormulario();
		atualizarLista();
	}
	
	public void editar() {
		this.medicoService.merge(this.medico);
		atualizarLista();
		FacesContext.getCurrentInstance().addMessage("",
				new FacesMessage("Ação", "Medico editado!"));
		limparFormulario();
		atualizarLista();
	}
	
	public void excluir(Medico medico) {
		medicoService.remove(medico);
		atualizarLista();
		FacesContext.getCurrentInstance().addMessage("",
				new FacesMessage("Ação", "Medico apagado!"));
		
	}
	
	public void atualizarLista() {
		medicos = medicoService.listAll();
	}
	
	public void limparFormulario() {
		medico = new Medico();
		medico.setEndereco(new Endereco());
		this.editar = false;
	}
	
	public Medico getMedico() {
		return medico;
	}
	public void setMedico(Medico medico) {
		this.medico = medico;
	}
	public List<Medico> getMedicos() {
		return medicos;
	}
	public void setMedicos(List<Medico> medicos) {
		this.medicos = medicos;
	}

	public Boolean getEditar() {
		return editar;
	}

	public void setEditar(Boolean editar) {
		this.editar = editar;
	}
	
	
}
