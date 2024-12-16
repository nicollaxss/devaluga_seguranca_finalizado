package web.devaluga.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxLocation;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSwap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import web.devaluga.filter.UsuarioFilter;
import web.devaluga.model.Papel;
import web.devaluga.model.Usuario;
import web.devaluga.notificacao.NotificacaoSweetAlert2;
import web.devaluga.notificacao.TipoNotificaoSweetAlert2;
import web.devaluga.pagination.PageWrapper;
import web.devaluga.repository.PapelRepository;
import web.devaluga.repository.UsuarioRepository;
import web.devaluga.service.CadastroUsuarioService;
import web.devaluga.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

	private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

	private PapelRepository papelRepository;
	private CadastroUsuarioService cadastroUsuarioService;
	private PasswordEncoder passwordEncoder;

	private UsuarioService usuarioService;

	private UsuarioRepository usuarioRepository;

	public UsuarioController(PapelRepository papelRepository, CadastroUsuarioService cadastroUsuarioService,
			PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
		this.papelRepository = papelRepository;
		this.cadastroUsuarioService = cadastroUsuarioService;
		this.passwordEncoder = passwordEncoder;

		this.usuarioRepository = usuarioRepository;
		this.usuarioService = usuarioService;
	}

	@GetMapping("/cadastrar")
	@HxRequest
	@HxTriggerAfterSwap("htmlAtualizado")
	public String abrirCadastroUsuario(Usuario usuario, Model model) {
		List<Papel> papeis = papelRepository.findAll();
		model.addAttribute("todosPapeis", papeis);
		return "usuarios/cadastrar :: formulario";
	}

	@PostMapping("/cadastrar")
	@HxRequest
	@HxTriggerAfterSwap("htmlAtualizado")
	public String cadastrarNovoUsuario(@Valid Usuario usuario,
			BindingResult resultado, Model model,
			RedirectAttributes redirectAttributes,
			Authentication authentication,
			HtmxResponse.Builder htmxResponse) {

		if (resultado.hasErrors()) {
			logger.info("O usuario recebido para cadastrar não é válido.");
			logger.info("Erros encontrados:");
			for (FieldError erro : resultado.getFieldErrors()) {
				logger.info("{}", erro);
			}
			List<Papel> papeis = papelRepository.findAll();
			model.addAttribute("todosPapeis", papeis);
			return "usuarios/cadastrar :: formulario";
		} 
		// else {
		// 	usuario.setAtivo(true);
		// 	usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		// 	cadastroUsuarioService.salvar(usuario);
		// 	redirectAttributes.addAttribute("mensagem", "Cadastro de usuário efetuado com sucesso.");
		// 	return "redirect:/usuarios/cadastrosucesso";
		// }

		if (!isAdmin(authentication)) {
            // // System.out.println("O usuario nao tem permissao de remover");
            // HtmxLocation hl = new HtmxLocation("/alugueis/erro2");
            // hl.setTarget("#main");
            // hl.setSwap("outerHTML");
            // htmxResponse.location(hl);
            // return "mensagem";
			redirectAttributes.addAttribute("mensagem", "Você já está logado.");
			return "redirect:/usuarios/cadastroerro";
        } 
		else {
			usuario.setAtivo(true);
			usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
			cadastroUsuarioService.salvar(usuario);
			redirectAttributes.addAttribute("mensagem", "Cadastro de usuário efetuado com sucesso.");
			return "redirect:/usuarios/cadastrosucesso";
		}
	}

	// Método para verificar se o usuário é admin
	private boolean isAdmin(Authentication authentication) {
		return authentication.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
	}

	@GetMapping("/cadastrosucesso")
	@HxRequest
	@HxTriggerAfterSwap("htmlAtualizado")
	public String mostrarCadastroSucesso(String mensagem, Usuario usuario, Model model) {
		List<Papel> papeis = papelRepository.findAll();
		model.addAttribute("todosPapeis", papeis);
		if (mensagem != null && !mensagem.isEmpty()) {
			model.addAttribute("notificacao", new NotificacaoSweetAlert2(mensagem,
					TipoNotificaoSweetAlert2.SUCCESS, 4000));
		}
		return "usuarios/cadastrar :: formulario";
	}

	@GetMapping("/cadastroerro")
	@HxRequest
	@HxTriggerAfterSwap("htmlAtualizado")
	public String mostrarCadastroErro(String mensagem, Usuario usuario, Model model) {
		// List<Papel> papeis = papelRepository.findAll();
		// model.addAttribute("todosPapeis", papeis);
		if (mensagem != null && !mensagem.isEmpty()) {
			model.addAttribute("notificacao", new NotificacaoSweetAlert2(mensagem,
					TipoNotificaoSweetAlert2.ERROR, 4000));
		}
		return "usuarios/cadastrar :: formulario";
	}

	// PESQUISA DE USUARIOS

	@GetMapping("/abrirpesquisar")
	public String abrirPaginaPesquisa() {
		return "usuarios/pesquisar";
	}

	@HxRequest
	@HxTriggerAfterSwap("htmlAtualizado")
	@GetMapping("/abrirpesquisar")
	public String abrirPaginaPesquisaHTMX() {
		return "usuarios/pesquisar :: formulario";
	}

	@GetMapping("/pesquisar")
	public String pesquisar(UsuarioFilter filtro, Model model,
			@PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
			HttpServletRequest request) {
		Page<Usuario> pagina = usuarioRepository.pesquisar(filtro, pageable);
		logger.info("Usuarios pesquisados: {}", pagina);
		PageWrapper<Usuario> paginaWrapper = new PageWrapper<>(pagina, request);
		model.addAttribute("pagina", paginaWrapper);
		return "usuarios/usuarios";
	}

	@HxRequest
	@HxTriggerAfterSwap("htmlAtualizado")
	@GetMapping("/pesquisar")
	public String pesquisarHTMX(UsuarioFilter filtro, Model model,
			@PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
			HttpServletRequest request) {
		Page<Usuario> pagina = usuarioRepository.pesquisar(filtro, pageable);
		logger.info("Usuarios pesquisadas: {}", pagina);
		PageWrapper<Usuario> paginaWrapper = new PageWrapper<>(pagina, request);
		model.addAttribute("pagina", paginaWrapper);
		return "usuarios/usuarios :: tabela";
	}

}