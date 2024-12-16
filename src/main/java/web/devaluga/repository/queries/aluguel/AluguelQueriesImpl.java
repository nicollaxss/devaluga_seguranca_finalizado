package web.devaluga.repository.queries.aluguel;

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
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import web.devaluga.filter.AluguelFilter;
import web.devaluga.model.Aluguel;
import web.devaluga.model.Produto;
import web.devaluga.model.Status;
import web.devaluga.model.Usuario;
import web.devaluga.repository.pagination.PaginacaoUtil;

public class AluguelQueriesImpl implements AluguelQueries {

    private static final Logger logger = LoggerFactory.getLogger(AluguelQueriesImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Aluguel> pesquisar(AluguelFilter filtro, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Aluguel> criteriaQuery = builder.createQuery(Aluguel.class);
        Root<Aluguel> a = criteriaQuery.from(Aluguel.class);
        a.fetch("produto", JoinType.INNER);
        a.fetch("usuario", JoinType.INNER);

        // a.fetch("produto", JoinType.LEFT);
        // a.fetch("usuario", JoinType.LEFT);

        TypedQuery<Aluguel> typedQuery;

		List<Predicate> predicateList = new ArrayList<>();
		List<Predicate> predicateListTotal = new ArrayList<>();
		Predicate[] predArray;
		Predicate[] predArrayTotal;

		if (filtro.getCodigo() != null) {
			predicateList.add(builder.equal(a.<Long>get("codigo"), filtro.getCodigo()));
		}
		
		if (StringUtils.hasText(filtro.getNomeProduto())) {
			predicateList.add(builder.like(builder.lower(a.<Produto>get("produto").<String>get("nome")),
					"%" + filtro.getNomeProduto().toLowerCase() + "%"));
		}

        if (StringUtils.hasText(filtro.getNomeUsuario())) {
			predicateList.add(builder.like(builder.lower(a.<Usuario>get("usuario").<String>get("nome")),
					"%" + filtro.getNomeProduto().toLowerCase() + "%"));
		}
		
		if (filtro.getValorMinimo() != null) {
			predicateList.add(builder.greaterThanOrEqualTo(a.<Float>get("valorTotal"),
					filtro.getValorMinimo()));
		}
		if (filtro.getValorMaximo() != null) {
			predicateList.add(builder.lessThanOrEqualTo(a.<Float>get("valorTotal"),
					filtro.getValorMaximo()));
		}
		
		predicateList.add(builder.equal(a.<Status>get("status"), Status.ATIVO));

		predArray = new Predicate[predicateList.size()];
		predicateList.toArray(predArray);
		criteriaQuery.select(a).where(predArray);
		PaginacaoUtil.prepararOrdem(a, criteriaQuery, builder, pageable);
		typedQuery = em.createQuery(criteriaQuery);
		PaginacaoUtil.prepararIntervalo(typedQuery, pageable);
		typedQuery.setHint("hibernate.query.passDistinctThrough", false);
		List<Aluguel> alugueis = typedQuery.getResultList();
		logger.info("Calculando o total de registros que o filtro retornará.");
		CriteriaQuery<Long> criteriaQueryTotal = builder.createQuery(Long.class);
		Root<Aluguel> pTotal = criteriaQueryTotal.from(Aluguel.class);
		criteriaQueryTotal.select(builder.count(pTotal));

		if (filtro.getCodigo() != null) {
			predicateListTotal.add(builder.equal(pTotal.<Long>get("codigo"), filtro.getCodigo()));
		}
		
		if (StringUtils.hasText(filtro.getNomeProduto())) {
			predicateListTotal.add(builder.like(builder.lower(pTotal.<Produto>get("produto").<String>get("nome")),
					"%" + filtro.getNomeProduto().toLowerCase() + "%"));
		}
		
        if (StringUtils.hasText(filtro.getNomeUsuario())) {
			predicateListTotal.add(builder.like(builder.lower(pTotal.<Usuario>get("usuario").<String>get("nome")),
					"%" + filtro.getNomeUsuario().toLowerCase() + "%"));
		}

		if (filtro.getValorMinimo() != null) {
			predicateListTotal.add(builder.greaterThanOrEqualTo(pTotal.<Float>get("valorTotal"),
					filtro.getValorMinimo()));
		}
		if (filtro.getValorMaximo() != null) {
			predicateListTotal.add(builder.lessThanOrEqualTo(pTotal.<Float>get("valorTotal"),
					filtro.getValorMaximo()));
		}
		
		predicateListTotal.add(builder.equal(pTotal.<Status>get("status"), Status.ATIVO));

		predArrayTotal = new Predicate[predicateListTotal.size()];
		predicateListTotal.toArray(predArrayTotal);
		criteriaQueryTotal.where(predArrayTotal);
		TypedQuery<Long> typedQueryTotal = em.createQuery(criteriaQueryTotal);
		long totalAlugueis = typedQueryTotal.getSingleResult();
		logger.info("O filtro retornará {} registros.", totalAlugueis);
		Page<Aluguel> page = new PageImpl<>(alugueis, pageable, totalAlugueis);
		return page;
	}
}
