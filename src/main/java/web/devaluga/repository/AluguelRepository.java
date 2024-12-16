package web.devaluga.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import web.devaluga.model.Aluguel;
import web.devaluga.repository.queries.aluguel.AluguelQueries;

public interface AluguelRepository extends JpaRepository<Aluguel, Long>, AluguelQueries {

}
