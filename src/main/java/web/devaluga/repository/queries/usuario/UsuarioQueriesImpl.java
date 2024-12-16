package web.devaluga.repository.queries.usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import web.devaluga.filter.UsuarioFilter;
import web.devaluga.model.Status;
import web.devaluga.model.Usuario;
import web.devaluga.repository.pagination.PaginacaoUtil;

public class UsuarioQueriesImpl implements UsuarioQueries {

	private static final Logger logger = LoggerFactory.getLogger(UsuarioQueriesImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Override
	public Page<Usuario> pesquisar(UsuarioFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Usuario> criteriaQuery = builder.createQuery(Usuario.class);
		Root<Usuario> p = criteriaQuery.from(Usuario.class);
		TypedQuery<Usuario> typedQuery;
		List<Predicate> predicateList = new ArrayList<>();
		List<Predicate> predicateListTotal = new ArrayList<>();
		Predicate[] predArray;
		Predicate[] predArrayTotal;
		if (filtro.getCodigo() != null) {
			predicateList.add(builder.equal(p.<Long>get("codigo"), filtro.getCodigo()));
		}
		if (StringUtils.hasText(filtro.getNome())) {
			predicateList.add(builder.like(builder.lower(p.<String>get("nome")),
					"%" + filtro.getNome().toLowerCase() + "%"));
		}

		// predicateList.add(builder.equal(p.<Status>get("status"), Status.ATIVO));

		predArray = new Predicate[predicateList.size()];
		predicateList.toArray(predArray);
		criteriaQuery.select(p).where(predArray);
		PaginacaoUtil.prepararOrdem(p, criteriaQuery, builder, pageable);
		typedQuery = em.createQuery(criteriaQuery);
		PaginacaoUtil.prepararIntervalo(typedQuery, pageable);
		typedQuery.setHint("hibernate.query.passDistinctThrough", false);
		List<Usuario> usuario = typedQuery.getResultList();
		logger.info("Calculando o total de registros que o filtro retornará.");
		CriteriaQuery<Long> criteriaQueryTotal = builder.createQuery(Long.class);
		Root<Usuario> pTotal = criteriaQueryTotal.from(Usuario.class);
		criteriaQueryTotal.select(builder.count(pTotal));
		if (filtro.getCodigo() != null) {
			predicateListTotal.add(builder.equal(pTotal.<Long>get("codigo"), filtro.getCodigo()));
		}
		if (StringUtils.hasText(filtro.getNome())) {
			predicateListTotal.add(builder.like(builder.lower(pTotal.<String>get("nome")),
					"%" + filtro.getNome().toLowerCase() + "%"));
		}
	
		// predicateList.add(builder.equal(pTotal.<Status>get("status"), Status.ATIVO));

		predArrayTotal = new Predicate[predicateListTotal.size()];
		predicateListTotal.toArray(predArrayTotal);
		criteriaQueryTotal.where(predArrayTotal);
		TypedQuery<Long> typedQueryTotal = em.createQuery(criteriaQueryTotal);
		long totalUsuarios = typedQueryTotal.getSingleResult();
		logger.info("O filtro retornará {} registros.", totalUsuarios);
		Page<Usuario> page = new PageImpl<>(usuario, pageable, totalUsuarios);
		return page;
	}

}
