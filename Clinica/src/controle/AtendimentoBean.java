package controle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import models.Atendimento;
import models.Endereco;
import models.Medico;
import models.Paciente;
import service.AtendimentoService;
import service.EnderecoService;
import service.MedicoService;
import service.PacienteService;
import util.Situacao;

@ManagedBean
@ViewScoped
public class AtendimentoBean {

	private String cpfPesquisa;
    private Paciente paciente;
    private Endereco endereco;
    private boolean pacienteEncontrado = false;
    private List<Medico> listaMedicos;
    private List<Medico> medicosSelecionados;
    private Date dataAtendimento;
	
	@EJB
	private PacienteService pacienteService;
	@EJB
	private MedicoService medicoService;
	@EJB 
	private AtendimentoService atendimentoService;
	@EJB
	private EnderecoService enderecoService;
	
	@PostConstruct
	public void inicializar() {
		limparFormulario();
		atualizarLista();
	}
	
	public void limparFormulario() {
		medicosSelecionados = new ArrayList<Medico>();
		paciente = new Paciente();
		endereco = new Endereco();
		paciente.setEndereco(endereco);
		cpfPesquisa = null;
		pacienteEncontrado = false;
		dataAtendimento = null;
	}
	
	public void atualizarLista() {
		listaMedicos = medicoService.listAll();
	}
	
	public void buscarCpfPaciente() {
		Paciente p = pacienteService.buscarCPF(cpfPesquisa);
		
		if (p != null) {
			this.paciente = p;
			this.endereco = p.getEndereco();
			this.pacienteEncontrado = true;
			FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Paciente encontrado!"));
		} else {
			limparFormulario();
			this.pacienteEncontrado = false;
			FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Paciente n�o encontrado. Prossiga com o cadastro."));
		}
	}

	public void gravarAtendimento() {
	    try {
	    	List<String> nomesMedicosEmConflito = atendimentoService.verificarConflitoDeHorario(dataAtendimento, medicosSelecionados);
	        
	        // Se a lista de nomes n�o estiver vazia, significa que h� um conflito.
	        if (!nomesMedicosEmConflito.isEmpty()) {
	            // Monta uma mensagem de erro amig�vel para o usu�rio.
	            String nomes = String.join(", ", nomesMedicosEmConflito);
	            String msg = "Conflito de hor�rio! O(s) m�dico(s): " + nomes + " j� possui(em) um atendimento agendado para este hor�rio.";
	            
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de Valida��o", msg));
	            
	            return; // PARA a execu��o do m�todo aqui para n�o salvar o atendimento.
	        }
	        
	        // Se for um paciente novo, copia o CPF da pesquisa para o objeto paciente.
	        if (!pacienteEncontrado) {
	            paciente.setCpf(cpfPesquisa);
	        }

	        // Criar e Preencher o Objeto Atendimento
	        Atendimento novoAtendimento = new Atendimento();
	        novoAtendimento.setPaciente(this.paciente); // Define o paciente (novo ou existente)
	        novoAtendimento.setMedicos(this.medicosSelecionados);
	        novoAtendimento.setDataEntrada(this.dataAtendimento);
	        novoAtendimento.setSituacao(Situacao.EM_ABERTO);
	        novoAtendimento.gerarNumeroAtendimento();

	        atendimentoService.merge(novoAtendimento);

	        String msg = "Atendimento " + novoAtendimento.getNumero() + " cadastrado com sucesso!";
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", msg));

	        limparFormulario();
	        atualizarLista();

	    } catch (Exception e) {
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Ocorreu um erro ao gravar o atendimento."));
	        e.printStackTrace();
	    }
	}

	public String getCpfPesquisa() {
		return cpfPesquisa;
	}

	public void setCpfPesquisa(String cpfPesquisa) {
		this.cpfPesquisa = cpfPesquisa;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public boolean isPacienteEncontrado() {
		return pacienteEncontrado;
	}

	public void setPacienteEncontrado(boolean pacienteEncontrado) {
		this.pacienteEncontrado = pacienteEncontrado;
	}

	public List<Medico> getListaMedicos() {
		return listaMedicos;
	}

	public void setListaMedicos(List<Medico> listaMedicos) {
		this.listaMedicos = listaMedicos;
	}

	public PacienteService getPacienteService() {
		return pacienteService;
	}

	public void setPacienteService(PacienteService pacienteService) {
		this.pacienteService = pacienteService;
	}

	public MedicoService getMedicoService() {
		return medicoService;
	}

	public void setMedicoService(MedicoService medicoService) {
		this.medicoService = medicoService;
	}

	public AtendimentoService getAtendimentoService() {
		return atendimentoService;
	}

	public void setAtendimentoService(AtendimentoService atendimentoService) {
		this.atendimentoService = atendimentoService;
	}

	public EnderecoService getEnderecoService() {
		return enderecoService;
	}

	public void setEnderecoService(EnderecoService enderecoService) {
		this.enderecoService = enderecoService;
	}

	public List<Medico> getMedicosSelecionados() {
		return medicosSelecionados;
	}

	public void setMedicosSelecionados(List<Medico> medicosSelecionados) {
		this.medicosSelecionados = medicosSelecionados;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}
	
	
	
}
