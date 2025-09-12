package service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import models.Atendimento;
import models.Medico;
import util.Situacao;

@Stateless
public class AtendimentoService extends GenericService<Atendimento>{
	
	public AtendimentoService() {
		super(Atendimento.class);
	}
	
	public List<String> verificarConflitoDeHorario(Date data, List<Medico> medicos) {
	    if (data == null || medicos == null || medicos.isEmpty()) {
	        return new ArrayList<>(); 
	    }

	    String jpql = "SELECT DISTINCT CONCAT(m.primeiroNome, ' ', m.sobrenome) FROM Atendimento a JOIN a.medicos m WHERE a.dataEntrada = :data AND m IN :medicos";
	    
	    return getEntityManager().createQuery(jpql, String.class)
	                             .setParameter("data", data)
	                             .setParameter("medicos", medicos)
	                             .getResultList();
	}
	
	// No seu service/AtendimentoService.java

	public List<Atendimento> filtrar(Date dataInicio, Date dataFim, Medico medico, String cpf) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<Atendimento> cq = cb.createQuery(Atendimento.class);
	    Root<Atendimento> root = cq.from(Atendimento.class);
	    
	    // Fazemos um JOIN com Paciente para podermos filtrar pelo CPF
	    root.join("paciente");

	    List<Predicate> predicates = new ArrayList<>();

	    // Lógica para o filtro de data (igual à que você já tinha)
	    if (dataInicio != null) {
	        predicates.add(cb.greaterThanOrEqualTo(root.get("dataEntrada"), dataInicio));
	    }
	    if (dataFim != null) {
	        predicates.add(cb.lessThanOrEqualTo(root.get("dataEntrada"), dataFim));
	    }

	    // Lógica para o filtro de médico
	    if (medico != null) {
	        predicates.add(cb.isMember(medico, root.get("medicos")));
	    }
	    
	    // Lógica para o filtro de CPF do paciente
	    if (cpf != null && !cpf.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("paciente").get("cpf"), cpf));
	    }

	    cq.where(predicates.toArray(new Predicate[0]));
	    
	    // Adiciona uma ordenação para os resultados mais recentes aparecerem primeiro
	    cq.orderBy(cb.desc(root.get("dataEntrada")));

	    return getEntityManager().createQuery(cq).getResultList();
	}
	
	public void finalizarAtendimento(Long id, String parecer) {
		if (parecer == null || parecer.trim().isEmpty()) {
			throw new IllegalArgumentException("Parecer é obrigatório para finalizar o atendimento.");
		}

		Atendimento atendimento =  obtemPorId(id);
		if (atendimento != null) {
			atendimento.setSituacao(Situacao.FINALIZADO);
			atendimento.setParecer(parecer);
			merge(atendimento);
		}
	}
	
	public Atendimento buscarAtendimentoComMedicos(Long atendimentoId) {
	    return entityManager.createQuery(
	        "SELECT a FROM Atendimento a LEFT JOIN FETCH a.medicos WHERE a.id = :id", Atendimento.class)
	        .setParameter("id", atendimentoId)
	        .getSingleResult();
	}
}
