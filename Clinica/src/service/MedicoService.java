package service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import models.Medico;
	
@Stateless
public class MedicoService extends GenericService<Medico> {
	
	public MedicoService() {
		super(Medico.class);
	}

	public List<Medico> ordernarMedicoPorNome() {

		final CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();
		final CriteriaQuery<Medico> cQuery = cBuilder.createQuery(Medico.class);
		final Root<Medico> rootMedico = cQuery.from(Medico.class);

		cQuery.orderBy(cBuilder.asc(rootMedico.get("primeiroNome")),
				cBuilder.asc(rootMedico.get("sobrenome")));

		List<Medico> resultado = getEntityManager().createQuery(cQuery).getResultList();

		return resultado;
	}
}
