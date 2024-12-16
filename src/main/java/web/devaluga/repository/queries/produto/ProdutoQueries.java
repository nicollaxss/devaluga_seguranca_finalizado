package web.devaluga.repository.queries.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import web.devaluga.filter.ProdutoFilter;
import web.devaluga.model.Produto;

public interface ProdutoQueries {

	Page<Produto> pesquisar(ProdutoFilter filtro, Pageable pageable);
	
}
