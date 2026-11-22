package com.senai.primeiraaplicacaoweb.repository;

import com.senai.primeiraaplicacaoweb.model.ContaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContaRepository extends JpaRepository<ContaModel, Long> {

    Optional<ContaModel> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
