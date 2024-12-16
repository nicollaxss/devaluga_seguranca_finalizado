package web.devaluga.repository.queries.produto;

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
import web.devaluga.filter.ProdutoFilter;
import web.devaluga.model.Status;
import web.devaluga.model.Produto;
import web.devaluga.repository.pagination.PaginacaoUtil;

public class ProdutoQueriesImpl implements ProdutoQueries {

	private static final Logger logger = LoggerFactory.getLogger(ProdutoQueriesImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Override
	public Page<Produto> pesquisar(ProdutoFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Produto> criteriaQuery = builder.createQuery(Produto.class);
		Root<Produto> v = criteriaQuery.from(Produto.class);
		TypedQuery<Produto> typedQuery;
		List<Predicate> predicateList = new ArrayList<>();
		List<Predicate> predicateListTotal = new ArrayList<>();
		Predicate[] predArray;
		Predicate[] predArrayTotal;

		// Filtro para código
		if (filtro.getCodigo() != null) {
			predicateList.add(builder.equal(v.get("codigo"), filtro.getCodigo()));
		}

		// Filtro para nome
		if (StringUtils.hasText(filtro.getNome())) {
			predicateList.add(builder.like(builder.lower(v.get("nome")),
					"%" + filtro.getNome().toLowerCase() + "%"));
		}

		// Filtro para descrição
		if (StringUtils.hasText(filtro.getDescricao())) {
			predicateList.add(builder.like(builder.lower(v.get("descricao")),
					"%" + filtro.getDescricao().toLowerCase() + "%"));
		}

		// Filtro para preço (exato)
		if (filtro.getPrecoDiario() != null && filtro.getPrecoDiario() != 0) {
			predicateList.add(builder.equal(v.get("precodiario"), filtro.getPrecoDiario()));
		}

		// Filtro para status (ATIVO)
		predicateList.add(builder.equal(v.get("status"), Status.ATIVO));

		// Criando o array de Predicates para a consulta
		predArray = new Predicate[predicateList.size()];
		predicateList.toArray(predArray);
		criteriaQuery.select(v).where(predArray);

		// Aplicando paginação e ordenação
		PaginacaoUtil.prepararOrdem(v, criteriaQuery, builder, pageable);
		typedQuery = em.createQuery(criteriaQuery);
		PaginacaoUtil.prepararIntervalo(typedQuery, pageable);
		typedQuery.setHint("hibernate.query.passDistinctThrough", false);
		List<Produto> produtos = typedQuery.getResultList();

		logger.info("Calculando o total de registros que o filtro retornará.");
		// Filtro para contar o total de registros
		CriteriaQuery<Long> criteriaQueryTotal = builder.createQuery(Long.class);
		Root<Produto> vTotal = criteriaQueryTotal.from(Produto.class);
		criteriaQueryTotal.select(builder.count(vTotal));

		// Reaplicando os filtros de pesquisa para contagem
		if (filtro.getCodigo() != null) {
			predicateListTotal.add(builder.equal(vTotal.get("codigo"), filtro.getCodigo()));
		}
		if (StringUtils.hasText(filtro.getNome())) {
			predicateListTotal.add(builder.like(builder.lower(vTotal.get("nome")),
					"%" + filtro.getNome().toLowerCase() + "%"));
		}
		if (StringUtils.hasText(filtro.getDescricao())) {
			predicateListTotal.add(builder.like(builder.lower(vTotal.get("descricao")),
					"%" + filtro.getDescricao().toLowerCase() + "%"));
		}
		if (filtro.getPrecoDiario() != null && filtro.getPrecoDiario() != 0) {
			predicateListTotal.add(builder.equal(vTotal.get("precodiario"), filtro.getPrecoDiario()));
		}

		// Filtro para status
		predicateListTotal.add(builder.equal(vTotal.get("status"), Status.ATIVO));

		// Criando o array de Predicates para o total
		predArrayTotal = new Predicate[predicateListTotal.size()];
		predicateListTotal.toArray(predArrayTotal);
		criteriaQueryTotal.where(predArrayTotal);
		TypedQuery<Long> typedQueryTotal = em.createQuery(criteriaQueryTotal);
		long totalProdutos = typedQueryTotal.getSingleResult();

		logger.info("O filtro retornará {} registros.", totalProdutos);

		// Criando o Page com o resultado da consulta
		Page<Produto> page = new PageImpl<>(produtos, pageable, totalProdutos);
		return page;
	}
}
