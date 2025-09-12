package controle;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

import models.Atendimento;
import models.Medico;
import service.AtendimentoService;
import service.MedicoService;
import util.Situacao;

@ManagedBean
@ViewScoped
public class RelatorioBean {

	private Date filtroDataInicio;
	private Date filtroDataFim;
	private Long filtroIdMedico;
	private String filtroCpf;
	private String parecer;
	private Atendimento atendimentoSelecionado;
	private List<Atendimento> atendimentosFiltrados;
	private List<Medico> medicos;
	
	@EJB
	private AtendimentoService atendimentoService;
	@EJB
	private MedicoService medicoService;
	
	@PostConstruct
	public void inicializar() {
		medicos = medicoService.listAll();
	}
	
	public void filtrar() {
		Medico medico = null;
		
		if (filtroIdMedico != null) {
			medico = medicoService.obtemPorId(filtroIdMedico);
		}
		
		atendimentosFiltrados = atendimentoService.filtrar(filtroDataInicio, filtroDataFim, medico, filtroCpf);
	}
	
	public void selecionarAtendimento(Atendimento atendimento) {
		this.atendimentoSelecionado = atendimentoService.buscarAtendimentoComMedicos(atendimento.getId());
	}
	
	public void finalizarAtendimento() {
	    // Requisito 5.h: Parecer obrigatório
	    if (parecer == null || parecer.trim().isEmpty()) {
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de Validação", "O parecer é obrigatório."));
	        return; // Para a execução
	    }

	    if (atendimentoSelecionado != null) {
	        try {
	            // 1. Chama o método do serviço que faz a lógica no banco
	            atendimentoService.finalizarAtendimento(atendimentoSelecionado.getId(), parecer);
	            
	            // 2. Atualiza o objeto na lista que está na memória para a tela refletir a mudança
	            atendimentoSelecionado.setSituacao(Situacao.FINALIZADO);
	            atendimentoSelecionado.setParecer(parecer);
	            
	            if (atendimentosFiltrados != null) {
	                for (Atendimento a : atendimentosFiltrados) {
	                    if (a.getId() != null && a.getId().equals(atendimentoSelecionado.getId())) {
	                        a.setSituacao(Situacao.FINALIZADO);
	                        a.setParecer(parecer);
	                        break;
	                    }
	                }
	            }
	            
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Atendimento finalizado."));
	            
	            // 3. Limpa o campo de parecer para a próxima vez
	            this.parecer = null;
	            
	            // Esconde o diálogo
	            PrimeFaces.current().executeScript("PF('dialogParecer').hide()");
	        

	        } catch (Exception e) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Ocorreu um erro ao finalizar."));
	        }
	    }
	}
	
	public void excluirAtendimento(Atendimento atendimento) {

	    if (atendimento.getSituacao() == Situacao.FINALIZADO) {
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não é possível excluir um atendimento finalizado."));
	        return;
	    }
	    
	    try {
	        atendimentoService.remove(atendimento);
	        
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Atendimento excluído."));

	        filtrar();

	    } catch (Exception e) {
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Ocorreu um erro ao excluir."));
	    }
	}

	public Date getFiltroDataInicio() {
		return filtroDataInicio;
	}

	public void setFiltroDataInicio(Date filtroDataInicio) {
		this.filtroDataInicio = filtroDataInicio;
	}

	public Date getFiltroDataFim() {
		return filtroDataFim;
	}

	public void setFiltroDataFim(Date filtroDataFim) {
		this.filtroDataFim = filtroDataFim;
	}

	public Long getFiltroIdMedico() {
		return filtroIdMedico;
	}

	public void setFiltroIdMedico(Long filtroIdMedico) {
		this.filtroIdMedico = filtroIdMedico;
	}

	public String getFiltroCpf() {
		return filtroCpf;
	}

	public void setFiltroCpf(String filtroCpf) {
		this.filtroCpf = filtroCpf;
	}

	public List<Atendimento> getAtendimentosFiltrados() {
		return atendimentosFiltrados;
	}

	public void setAtendimentosFiltrados(List<Atendimento> atendimentosFiltrados) {
		this.atendimentosFiltrados = atendimentosFiltrados;
	}

	public List<Medico> getMedicos() {
		return medicos;
	}

	public void setMedicos(List<Medico> medicos) {
		this.medicos = medicos;
	}

	public AtendimentoService getAtendimentoService() {
		return atendimentoService;
	}

	public void setAtendimentoService(AtendimentoService atendimentoService) {
		this.atendimentoService = atendimentoService;
	}

	public MedicoService getMedicoService() {
		return medicoService;
	}

	public void setMedicoService(MedicoService medicoService) {
		this.medicoService = medicoService;
	}

	public Atendimento getAtendimentoSelecionado() {
		return atendimentoSelecionado;
	}

	public void setAtendimentoSelecionado(Atendimento atendimentoSelecionado) {
		this.atendimentoSelecionado = atendimentoSelecionado;
	}

	public String getParecer() {
		return parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer;
	}
}
