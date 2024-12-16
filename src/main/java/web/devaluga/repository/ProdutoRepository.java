package web.devaluga.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import web.devaluga.model.Produto;
import web.devaluga.repository.queries.produto.ProdutoQueries;

public interface ProdutoRepository extends JpaRepository<Produto, Long>, ProdutoQueries {

}
