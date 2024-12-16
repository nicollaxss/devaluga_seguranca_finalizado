package web.devaluga.controller;

import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication; //pegar o nome do usuario logado
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxLocation;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSwap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import web.devaluga.filter.AluguelFilter;
import web.devaluga.filter.ProdutoFilter;
import web.devaluga.model.Aluguel;
import web.devaluga.model.Produto;
import web.devaluga.model.Status;
import web.devaluga.model.Usuario;
import web.devaluga.notificacao.NotificacaoSweetAlert2;
import web.devaluga.notificacao.TipoNotificaoSweetAlert2;
import web.devaluga.pagination.PageWrapper;
import web.devaluga.repository.AluguelRepository;
import web.devaluga.repository.ProdutoRepository;
import web.devaluga.repository.UsuarioRepository;
import web.devaluga.service.AluguelService;
import web.devaluga.service.CadastroUsuarioService;

@Controller
@RequestMapping("/alugueis")
public class AluguelController {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    private ProdutoRepository produtoRepository;
    private AluguelService aluguelService;
    private AluguelRepository aluguelRepository;

    private UsuarioRepository usuarioRepository;
    // private UsuarioService usuarioService;

    private CadastroUsuarioService cadastroUsuarioService;

    public AluguelController(ProdutoRepository produtoRepository, AluguelService aluguelService,
            AluguelRepository aluguelRepository, UsuarioRepository usuarioRepository,
            CadastroUsuarioService cadastroUsuarioService) {
        this.produtoRepository = produtoRepository;
        this.aluguelService = aluguelService;
        this.aluguelRepository = aluguelRepository;

        this.usuarioRepository = usuarioRepository;
        this.cadastroUsuarioService = cadastroUsuarioService;
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/escolherproduto")
    public String abrirEscolhaProdutoHTMX() {
        return "alugueis/escolherproduto :: formulario";
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/pesquisarproduto")
    public String pesquisarprodutoHTMX(ProdutoFilter filtro, Model model,
            @PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest request) {
        Page<Produto> pagina = produtoRepository.pesquisar(filtro, pageable);
        logger.info("Produtos pesquisados: {}", pagina);
        PageWrapper<Produto> paginaWrapper = new PageWrapper<>(pagina, request);
        model.addAttribute("pagina", paginaWrapper);
        return "alugueis/produtos :: tabela";
    }

    // pegar o usuario logado e salvar na sessao, mandar ele pra salvar o nome e
    // fazer o aluguel salvando o nome do usuario

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @PostMapping("/produtoescolhido")
    public String abrirCadastroAluguelHTMX(Produto produto, Aluguel aluguel, HttpSession session,
            Authentication authentication) {
        session.setAttribute("produto", produto);

        return "alugueis/novo :: formulario";
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @PostMapping("/calcular")
    public String cadastrarAluguelHTMX(@Valid Aluguel aluguel, BindingResult result, HtmxResponse.Builder htmxResponse,
            HttpSession sessao, Authentication authentication) {

        if (result.hasErrors()) {
            logger.info("O aluguel recebido para calcular não é válido.");
            logger.info("Erros encontrados:");
            for (FieldError erro : result.getFieldErrors()) {
                logger.info("{}", erro);
            }
            return "alugueis/novo :: formulario";
        }

        Produto produto = (Produto) sessao.getAttribute("produto");

        long dias = ChronoUnit.DAYS.between(aluguel.getDataInicio(), aluguel.getDataFim());
        Float valorTotal = produto.getPrecoDiario() * dias;

        aluguel.setValorTotal(valorTotal);

        logger.info("aluguel ta sendo: {}", aluguel.getDataFim());

        return "alugueis/confirmarvalor :: formulario";
    }

    @PostMapping("/alugar")
    public String cadastrarProdutoHTMX(@Valid Aluguel aluguel, BindingResult result, HtmxResponse.Builder htmxResponse,
            HttpSession sessao, Authentication authentication) {
        if (result.hasErrors()) {
            logger.info("O aluguel recebido para cadastrar não é válido.");
            logger.info("Erros encontrados:");
            for (FieldError erro : result.getFieldErrors()) {
                logger.info("{}", erro);
            }
            return "alugueis/novo :: formulario";
        } else {
            // // logica para pegar o usuario logado
            String nomeUsuario = authentication.getName();

            logger.info("O nome de usuario eh esse: {}", nomeUsuario);

            Usuario usuario = cadastroUsuarioService.pesquisarPorNome(nomeUsuario);

            logger.info("O usuario eh esse: {}", usuario);

            aluguel.setUsuario(usuario);
            aluguel.setDataInicio(aluguel.getDataInicio());
            aluguel.setDataFim((aluguel.getDataFim()));
            aluguel.setProduto((Produto) sessao.getAttribute("produto"));
            aluguel.setValorTotal(aluguel.getValorTotal());
            aluguelService.salvar(aluguel);

            logger.info("O aluguel é: {}", aluguel);

            sessao.removeAttribute("produto");
            HtmxLocation hl = new HtmxLocation("/alugueis/sucesso");
            hl.setTarget("#main");
            hl.setSwap("outerHTML");
            htmxResponse.location(hl);
            return "mensagem";
        }
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/sucesso")
    public String abrirMensagemSucessoHTMX(Model model) {
        model.addAttribute("notificacao", new NotificacaoSweetAlert2("Aluguel cadastrado com sucesso",
                TipoNotificaoSweetAlert2.SUCCESS, 4000));
        return "alugueis/escolherproduto :: formulario";
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/abrirpesquisar")
    public String abrirPaginaPesquisaHTMX() {
        return "alugueis/pesquisar :: formulario";
    }

    @GetMapping("/pesquisar")
    public String pesquisar(AluguelFilter filtro, Model model,
            @PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest request) {
        Page<Aluguel> pagina = aluguelRepository.pesquisar(filtro, pageable);
        logger.info("Alugueis pesquisados: {}", pagina);
        PageWrapper<Aluguel> paginaWrapper = new PageWrapper<>(pagina, request);
        model.addAttribute("pagina", paginaWrapper);
        return "alugueis/alugueis";
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/pesquisar")
    public String pesquisarHTMX(AluguelFilter filtro, Model model,
            @PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest request) {
        Page<Aluguel> pagina = aluguelRepository.pesquisar(filtro, pageable);
        logger.info("Alugueis pesquisadas: {}", pagina);
        PageWrapper<Aluguel> paginaWrapper = new PageWrapper<>(pagina, request);
        model.addAttribute("pagina", paginaWrapper);
        return "alugueis/alugueis :: tabela";
    }

    // @HxRequest
    // @HxTriggerAfterSwap("htmlAtualizado")
    // @PostMapping("/abriralterar")
    // public String abrirAlterarHTMX(Aluguel aluguel) {
    // return "alugueis/alterar :: formulario";
    // }

    // @HxRequest
    // @PostMapping("/alterar")
    // public String alterarHTMX(@Valid Aluguel aluguel, BindingResult result,
    // HtmxResponse.Builder htmxResponse) {
    // if (result.hasErrors()) {
    // logger.info("O aluguel recebido para alterar não é válido.");
    // logger.info("Erros encontrados:");
    // for (FieldError erro : result.getFieldErrors()) {
    // logger.info("{}", erro);
    // }
    // return "alugueis/alterar :: formulario";
    // } else {
    // aluguelService.alterar(aluguel);
    // HtmxLocation hl = new HtmxLocation("/alugueis/sucesso2");
    // hl.setTarget("#main");
    // hl.setSwap("outerHTML");
    // htmxResponse.location(hl);
    // return "mensagem";
    // }
    // }

    // @HxRequest
    // @HxTriggerAfterSwap("htmlAtualizado")
    // @GetMapping("/sucesso2")
    // public String abrirMensagemSucesso2HTMX(Model model) {
    // model.addAttribute("notificacao", new NotificacaoSweetAlert2("Aluguel
    // alterado com sucesso",
    // TipoNotificaoSweetAlert2.SUCCESS, 4000));
    // return "alugueis/pesquisar :: formulario";
    // }

    @HxRequest
    @PostMapping("/remover")
    public String removerHTMX(@RequestParam("codigo") Long codigo,
            @RequestParam("nomeUsuario") String nomeUsuario,
            Authentication authentication, Aluguel aluguel, HtmxResponse.Builder htmxResponse) {
        
        if (authentication == null){
            HtmxLocation hl = new HtmxLocation("/alugueis/erro2");
            hl.setTarget("#main");
            hl.setSwap("outerHTML");
            htmxResponse.location(hl);
            return "mensagem";
        }

        // Obtenha o nome do usuário autenticado
        String nomeUsuarioLogado = authentication.getName();

        logger.info("O usuario logado é esse: {}", nomeUsuarioLogado);

        // Verifique se o usuário logado tem permissão
        if (!nomeUsuarioLogado.equals(nomeUsuario) && !isAdmin(authentication)) {
            // System.out.println("O usuario nao tem permissao de remover");
            HtmxLocation hl = new HtmxLocation("/alugueis/erro1");
            hl.setTarget("#main");
            hl.setSwap("outerHTML");
            htmxResponse.location(hl);
            return "mensagem";

            // return "alugueis/pesquisar :: formulario";
        }

        

        aluguel.setStatus(Status.INATIVO);
        aluguelService.alterar(aluguel);
        HtmxLocation hl = new HtmxLocation("/alugueis/sucesso3");
            hl.setTarget("#main");
            hl.setSwap("outerHTML");
            htmxResponse.location(hl);
        return "mensagem";
    }

    // Método para verificar se o usuário é admin
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/sucesso3")
    public String abrirMensagemSucesso3HTMX(Model model) {
        model.addAttribute("notificacao", new NotificacaoSweetAlert2("Aluguel removido com sucesso",
                TipoNotificaoSweetAlert2.SUCCESS, 4000));
        return "alugueis/pesquisar :: formulario";
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/erro1")
    public String abrirMensagemErro1HTMX(Model model) {
        model.addAttribute("notificacao", new NotificacaoSweetAlert2("O usuario não tem permissão para remover",
                TipoNotificaoSweetAlert2.ERROR, 4000));
        return "alugueis/pesquisar :: formulario";
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/erro2")
    public String abrirMensagemErro2HTMX(Model model) {
        model.addAttribute("notificacao", new NotificacaoSweetAlert2("Precisa estar logado para remover!",
                TipoNotificaoSweetAlert2.ERROR, 4000));
        return "alugueis/pesquisar :: formulario";
    }

    @HxRequest
    @HxTriggerAfterSwap("htmlAtualizado")
    @GetMapping("/erro3")
    public String abrirMensagemErro3HTMX(Model model) {
        model.addAttribute("notificacao", new NotificacaoSweetAlert2("Você ja está logado",
                TipoNotificaoSweetAlert2.ERROR, 4000));
        return "usuarios/cadastrar :: formulario";
    }


}
