package web.devaluga.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web.devaluga.model.Usuario;
import web.devaluga.repository.UsuarioRepository;

@Service
public class CadastroUsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Transactional
	public void salvar(Usuario usuario) {
		usuarioRepository.save(usuario);
	}

	@Transactional
	public Usuario pesquisarPorNome(String nomeUsuario) {
		return usuarioRepository.findByNomeUsuarioIgnoreCase(nomeUsuario);
	}
}
