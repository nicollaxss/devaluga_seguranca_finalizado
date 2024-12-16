package web.devaluga.repository.queries.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import web.devaluga.filter.UsuarioFilter;
import web.devaluga.model.Usuario;

public interface UsuarioQueries {

    Page<Usuario> pesquisar(UsuarioFilter filtro, Pageable pageable);
}
