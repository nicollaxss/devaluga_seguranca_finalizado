package web.devaluga.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web.devaluga.model.Produto;
import web.devaluga.repository.ProdutoRepository;

@Service
@Transactional
public class ProdutoService {

    private ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public void salvar(Produto produto) {
        produtoRepository.save(produto);
    }

    public void alterar(Produto produto) {
        produtoRepository.save(produto);
    }

    public void remover(Produto produto) {
        produtoRepository.delete(produto);
    }
}
