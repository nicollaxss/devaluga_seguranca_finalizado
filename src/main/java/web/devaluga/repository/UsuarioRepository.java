package web.devaluga.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import web.devaluga.model.Usuario;
import web.devaluga.repository.queries.usuario.UsuarioQueries;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, UsuarioQueries{
	
	Usuario findByNomeUsuarioIgnoreCase(String nomeUsuario);
	
	
}
