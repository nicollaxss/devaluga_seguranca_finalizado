package web.devaluga.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxLocation;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxLocation;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSwap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import web.devaluga.filter.ProdutoFilter;
import web.devaluga.model.Status;
import web.devaluga.model.Produto;
import web.devaluga.notificacao.NotificacaoSweetAlert2;
import web.devaluga.notificacao.TipoNotificaoSweetAlert2;
import web.devaluga.pagination.PageWrapper;
import web.devaluga.repository.ProdutoRepository;
import web.devaluga.service.ProdutoService;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    private ProdutoRepository produtoRepository;
    private ProdutoService produtoService;

    public ProdutoController(ProdutoRepository produtoRepository, ProdutoService produtoService) {
        this.produtoRepository = produtoRepository;
        this.produtoService = produtoService;
    }

    // @GetMapping("/todas")
    // public String mostrarTodosProdutos(Model model) {
    // List<Produto> produtos = produtoRepository.findAll();
    // logger.info("Produtos buscadas: {}", produtos);
    // model.addAttribute("produtos", produtos);
    // return "produtos/todas";
    // }

    @GetMapping("/nova")
    public String abrirCadastroProduto(Produto produto) {
        return "produtos/nova";
    }

    @HxRequest
    @GetMapping("/nova")
    public String abrirCadastroProdutoHTMX(Produto produto) {
        return "produtos/nova :: formulario";
    }

    @PostMapping("/nova")
    public String cadastrarProduto(Produto produto) {
        produtoService.salvar(produto);
        return "redirect:/produtos/sucesso";
    }

    @GetMapping("/sucesso")
    public String abrirMensagemSucesso(Model model) {
        model.addAttribute("mensagem", "Produto cadastrada com sucesso");
        return "mensagem";
    }

    @HxRequest
    @PostMapping("/nova")
    public String cadastrarProdutoHTMX(@Valid Produto produto, BindingResult result, HtmxResponse.Builder htmxResponse) {
        if (result.hasErrors()) {
            logger.info("A produto recebida para cadastrar não é válida.");
            logger.info("Erros encontrados:");
            for (FieldError erro : result.getFieldErrors()) {
                logger.info("{}", erro);
            }
            return "produtos/nova :: formulario";
        } else {
            produtoService.salvar(produto);
            HtmxLocation hl = new HtmxLocation("/produtos/sucesso");
            hl.setTarget("#main");
            hl.setSwap("outerHTML");
            htmxResponse.location(hl);
            return "mensagem";
        }
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/sucesso")
    public String abrirMensagemSucessoHTMX(Produto produto, Model model) {
        model.addAttribute("notificacao", new NotificacaoSweetAlert2("Produto cadastrada com sucesso!",
                TipoNotificaoSweetAlert2.SUCCESS, 4000));
        return "produtos/nova :: formulario";
    }

    @GetMapping("/abrirpesquisar")
    public String abrirPaginaPesquisa() {
        return "produtos/pesquisar";
    }

    @HxRequest
    @GetMapping("/abrirpesquisar")
    public String abrirPaginaPesquisaHTMX() {
        return "produtos/pesquisar :: formulario";
    }

    @GetMapping("/pesquisar")
    public String pesquisar(ProdutoFilter filtro, Model model,
            @PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest request) {
        Page<Produto> pagina = produtoRepository.pesquisar(filtro, pageable);
        logger.info("Produtos pesquisadas: {}", pagina);
        PageWrapper<Produto> paginaWrapper = new PageWrapper<>(pagina, request);
        model.addAttribute("pagina", paginaWrapper);
        return "produtos/produtos";
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/pesquisar")
    public String pesquisarHTMX(ProdutoFilter filtro, Model model,
            @PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest request) {
        Page<Produto> pagina = produtoRepository.pesquisar(filtro, pageable);
        logger.info("Produtos pesquisadas: {}", pagina);
        PageWrapper<Produto> paginaWrapper = new PageWrapper<>(pagina, request);
        model.addAttribute("pagina", paginaWrapper);
        return "produtos/produtos :: tabela";
    }

    @PostMapping("/abriralterar")
    public String abrirAlterar(Produto produto) {
        return "produtos/alterar";
    }

    @HxRequest
    @PostMapping("/abriralterar")
    public String abrirAlterarHTMX(Produto produto, HtmxResponse.Builder htmxResponse,
    Authentication authentication) {
        if (authentication == null || !isAdmin(authentication)) {
			HtmxLocation hl = new HtmxLocation("/produtos/erro1");
            hl.setTarget("#main");
            hl.setSwap("outerHTML");
            htmxResponse.location(hl);
            return "mensagem";
        } 
        return "produtos/alterar :: formulario";
    }

    @PostMapping("/alterar")
    public String alterar(Produto produto, HtmxResponse.Builder htmxResponse,
    Authentication authentication) {
        if (authentication == null || !isAdmin(authentication)) {
			HtmxLocation hl = new HtmxLocation("/produtos/erro1");
            hl.setTarget("#main");
            hl.setSwap("outerHTML");
            htmxResponse.location(hl);
            return "mensagem";
        } 
        produtoService.alterar(produto);
        return "redirect:/produtos/sucesso2";
    }

    @GetMapping("/sucesso2")
    public String abrirMensagemSucesso2(Model model) {
        model.addAttribute("mensagem", "Produto alterada com sucesso");
        return "mensagem";
    }

    @HxRequest
    @PostMapping("/alterar")
    public String alterarHTMX(@Valid Produto produto, 
    BindingResult result, HtmxResponse.Builder htmxResponse,
    Authentication authentication) {
        if (result.hasErrors()) {
            logger.info("A produto recebida para alterar não é válida.");
            logger.info("Erros encontrados:");
            for (FieldError erro : result.getFieldErrors()) {
                logger.info("{}", erro);
            }
            return "produtos/alterar :: formulario";
        } 
        
        if (authentication == null || !isAdmin(authentication)) {
			HtmxLocation hl = new HtmxLocation("/produtos/erro1");
            hl.setTarget("#main");
            hl.setSwap("outerHTML");
            htmxResponse.location(hl);
            return "mensagem";
        } 
        
        else {
            produtoService.alterar(produto);
            HtmxLocation hl = new HtmxLocation("/produtos/sucesso2");
            hl.setTarget("#main");
            hl.setSwap("outerHTML");
            htmxResponse.location(hl);
            return "mensagem";
        }
    }

    // Método para verificar se o usuário é admin
	private boolean isAdmin(Authentication authentication) {
		return authentication.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
	}

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/sucesso2")
    public String abrirMensagemSucesso2HTMX(Model model) {
        model.addAttribute("notificacao", new NotificacaoSweetAlert2("Produto alterada com sucesso!",
                TipoNotificaoSweetAlert2.SUCCESS, 4000));
        return "produtos/pesquisar :: formulario";
    }

    @PostMapping("/remover")
    public String remover(Produto produto, Authentication authentication, HtmxResponse.Builder htmxResponse) {
        if (authentication == null || !isAdmin(authentication)) {
			HtmxLocation hl = new HtmxLocation("/produtos/erro1");
            hl.setTarget("#main");
            hl.setSwap("outerHTML");
            htmxResponse.location(hl);
            return "mensagem";
        } 
        produto.setStatus(Status.INATIVO);
        produtoService.alterar(produto);
        return "redirect:/produtos/sucesso3";
    }

    @GetMapping("/sucesso3")
    public String abrirMensagemSucesso3(Model model) {
        model.addAttribute("mensagem", "Produto removida com sucesso");
        return "mensagem";
    }

    @HxRequest
    @HxLocation(path = "/produtos/sucesso3", target = "#main", swap = "outerHTML")
    @PostMapping("/remover")
    public String removerHTMX(Produto produto, Authentication authentication, HtmxResponse.Builder htmxResponse) {
        if (authentication == null || !isAdmin(authentication)) {
			HtmxLocation hl = new HtmxLocation("/produtos/erro1");
            hl.setTarget("#main");
            hl.setSwap("outerHTML");
            htmxResponse.location(hl);
            return "mensagem";
        } 
        produto.setStatus(Status.INATIVO);
        produtoService.alterar(produto);
        return "mensagem";
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/sucesso3")
    public String abrirMensagemSucesso3HTMX(Model model) {
        model.addAttribute("notificacao", new NotificacaoSweetAlert2("Produto removida com sucesso!",
                TipoNotificaoSweetAlert2.SUCCESS, 4000));
        return "produtos/pesquisar :: formulario";
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/erro1")
    public String abrirMensagemErro1HTMX(Model model) {
        model.addAttribute("notificacao", new NotificacaoSweetAlert2("Você não tem permissão!",
                TipoNotificaoSweetAlert2.ERROR, 4000));
        return "produtos/pesquisar :: formulario";
    }

}
