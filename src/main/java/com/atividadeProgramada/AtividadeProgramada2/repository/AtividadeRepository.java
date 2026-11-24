package com.atividadeProgramada.AtividadeProgramada2.repository;

import com.atividadeProgramada.AtividadeProgramada2.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import com.atividadeProgramada.AtividadeProgramada2.entity.Atividade;

import java.util.List;
import java.util.Optional;

public interface AtividadeRepository extends JpaRepository<Atividade, String> {
    Optional<Atividade> findByNome(String nome);
    // JpaRepository já fornece métodos prontos para operações básicas de CRUD
    void deleteByNome(String nome);

    List<Atividade> findByUsuario(Usuario usuario);
    void deleteByUsuario(Usuario usuario);
}

