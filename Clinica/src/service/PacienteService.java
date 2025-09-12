package service;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.Paciente;

@Stateless
public class PacienteService extends GenericService<Paciente> {

	public PacienteService() {
		super(Paciente.class);
	}
	
	public Paciente buscarCPF(String cpf) {
		if (cpf == null || cpf.trim().isEmpty()) {
			return null;
		}
		
		final CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();
	    final CriteriaQuery<Paciente> cQuery = cBuilder.createQuery(Paciente.class);
	    final Root<Paciente> pacienteRoot = cQuery.from(Paciente.class);
	    
	    cQuery.select(pacienteRoot);
	    cQuery.where(cBuilder.equal(pacienteRoot.get("cpf"), cpf));
	    
	    try {
	    	return getEntityManager().createQuery(cQuery).getSingleResult();
	    } catch (NoResultException e) {
	    	return null;
	    }
	}
}
