package com.atividadeProgramada.AtividadeProgramada2.service;

import com.atividadeProgramada.AtividadeProgramada2.entity.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;

import com.atividadeProgramada.AtividadeProgramada2.repository.AtividadeRepository;
import com.atividadeProgramada.AtividadeProgramada2.entity.Atividade;

@Service
@AllArgsConstructor
@Transactional
public class AtividadeService {

    private final AtividadeRepository atividadeRepository;

    public List<Atividade> ListarAtividade() {
        return atividadeRepository.findAll();
    }

    //trecho para procurar atividade relacionada a usuario
    // Busca só as atividades do usuário logado (USER)
    public List<Atividade> listarPorUsuario(Usuario usuario) {
        return atividadeRepository.findByUsuario(usuario);
    }
    //trecho para procurar atividade relacionada a usuario
    // Busca uma atividade pelo ID (usado na edição)
    public Atividade buscarPorId(String id) {
        return atividadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
    }

    public Atividade salvar(Atividade atividade) {
        Atividade validAtividade = Objects.requireNonNull(atividade, "atividade e obrigatoria");
        if (validAtividade.getHora() != null && !validAtividade.getHora().isEmpty()) {
            int hora = Integer.parseInt(validAtividade.getHora().split(":")[0]);
            if (hora >= 6 && hora < 12) validAtividade.setPeriodo("MANHA");
            else if (hora >= 12 && hora < 18) validAtividade.setPeriodo("TARDE");
            else validAtividade.setPeriodo("NOITE");
        }
        return atividadeRepository.save(validAtividade);
    }
    public void deletar(String nome) {
        atividadeRepository.deleteByNome(nome);
    }

    public Atividade atualizar(Atividade atividade) {
        Atividade validAtividade = Objects.requireNonNull(atividade, "atividade e obrigatoria");
        String id = Objects.requireNonNull(validAtividade.getId(), "id e obrigatorio para atualizar");

        return atividadeRepository.findById(id)
                .map(a -> {
                    a.setNome(validAtividade.getNome());
                    a.setData(validAtividade.getData());
                    a.setHora(validAtividade.getHora());
                    a.setDescricao(validAtividade.getDescricao());
                    a.setUsuario(validAtividade.getUsuario()); //para atribuir a atividade ao usuairo
                    a.setPeriodo(validAtividade.getPeriodo()); //atualizar periodo

                    if(a.getHora() != null && !a.getHora().isEmpty()) {
                        int hora = Integer.parseInt(a.getHora().split(":")[0]);
                        if (hora >= 6 && hora < 12) a.setPeriodo("MANHA");
                        else if (hora >= 12 && hora < 18) a.setPeriodo("TARDE");
                        else a.setPeriodo("NOITE");
                    }
                    return atividadeRepository.save(a);
                })
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));

    }
    public void concluir(String id) {
       Atividade atividade = buscarPorId(id);
       atividade.setConcluida(true);
       atividadeRepository.save(atividade);
    }
    public void deletarPorId(String id) {
        atividadeRepository.deleteById(id);
    }
    public void reabrir(String id) {
        Atividade atividade = buscarPorId(id);
        atividade.setConcluida(false);
        atividadeRepository.save(atividade);
    }
    public void deletarPorUsuario(Usuario usuario) {
        atividadeRepository.deleteByUsuario(usuario);
    }
}