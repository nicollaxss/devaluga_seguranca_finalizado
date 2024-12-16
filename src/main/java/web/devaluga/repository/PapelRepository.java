package web.devaluga.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import web.devaluga.model.Papel;

public interface PapelRepository extends JpaRepository<Papel, Long> {
}
