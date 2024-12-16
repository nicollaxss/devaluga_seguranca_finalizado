package web.devaluga.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web.devaluga.model.Aluguel;
import web.devaluga.repository.AluguelRepository;

@Service
@Transactional
public class AluguelService {

    private AluguelRepository aluguelRepository;

    public AluguelService(AluguelRepository aluguelRepository) {
        this.aluguelRepository = aluguelRepository;
    }

    public void salvar(Aluguel aluguel) {
        aluguelRepository.save(aluguel);
    }

    public void alterar(Aluguel aluguel) {
        aluguelRepository.save(aluguel);
    }

}
