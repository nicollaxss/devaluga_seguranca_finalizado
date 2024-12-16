package web.devaluga.repository.queries.aluguel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import web.devaluga.filter.AluguelFilter;
import web.devaluga.model.Aluguel;

public interface AluguelQueries {

    Page<Aluguel> pesquisar(AluguelFilter filtro, Pageable pageable);
}
