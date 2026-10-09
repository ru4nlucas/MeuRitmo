package com.atividadeProgramada.AtividadeProgramada2.controller;

import com.atividadeProgramada.AtividadeProgramada2.entity.Atividade;
import com.atividadeProgramada.AtividadeProgramada2.entity.Role;
import com.atividadeProgramada.AtividadeProgramada2.entity.Usuario;
import com.atividadeProgramada.AtividadeProgramada2.service.AtividadeService;
import com.atividadeProgramada.AtividadeProgramada2.service.UsuarioService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/atividades")
@AllArgsConstructor
public class AtividadeController {

    private final AtividadeService atividadeService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null) {
            return "redirect:/login";
        }

        List<Atividade> atividades;

        if (logado.getRole() == Role.ADMIN) {
            atividades = atividadeService.ListarAtividade();
        } else {
            atividades = atividadeService.listarPorUsuario(logado);
        }

        model.addAttribute("atividades", atividades);
        model.addAttribute("usuarioLogado", logado);
        return "atividades/listar";
    }

    @GetMapping("/nova")
    public String formularioCriar(HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getRole() != Role.ADMIN) {
            return "redirect:/atividades";
        }

        List<Usuario> usuarios = usuarioService.listarUsuarios()
                .stream()
                .filter(u -> u.getRole() != Role.ADMIN)
                .toList();

        model.addAttribute("atividade", new Atividade());
        model.addAttribute("usuarios", usuarios);
        return "atividades/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Atividade atividade,
                         @RequestParam String usuarioId,
                         HttpSession session) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getRole() != Role.ADMIN) {
            return "redirect:/atividades";
        }

        Usuario aluno = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        atividade.setUsuario(aluno);
        atividadeService.salvar(atividade);
        return "redirect:/atividades";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable String id,
                                   HttpSession session,
                                   Model model) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getRole() != Role.ADMIN) {
            return "redirect:/atividades";
        }

        Atividade atividade = atividadeService.buscarPorId(id);

        List<Usuario> usuarios = usuarioService.listarUsuarios()
                .stream()
                .filter(u -> u.getRole() != Role.ADMIN)
                .toList();

        model.addAttribute("atividade", atividade);
        model.addAttribute("usuarios", usuarios);
        return "atividades/formulario";
    }

    @PostMapping("/atualizar")
    public String atualizar(@ModelAttribute Atividade atividade,
                            @RequestParam String usuarioId,
                            HttpSession session) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getRole() != Role.ADMIN) {
            return "redirect:/atividades";
        }

        Usuario aluno = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        atividade.setUsuario(aluno);
        atividadeService.atualizar(atividade);
        return "redirect:/atividades";
    }

    @GetMapping("/deletar/{nome}")
    public String deletar(@PathVariable String nome, HttpSession session) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getRole() != Role.ADMIN) {
            return "redirect:/atividades";
        }

        atividadeService.deletar(nome);
        return "redirect:/atividades";
    }
    @PostMapping("/concluir/{id}")
    public String concluir(@PathVariable String id, HttpSession session){
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");
        if(logado == null)return "redirect:/login";
        atividadeService.concluir(id);
        return "redirect:/atividades";
    }

    @GetMapping("/usuario/{id}")
    public String listarPorUsuario(@PathVariable String id,
                                   HttpSession session,
                                   Model model) {

        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getRole() != Role.ADMIN) {
            return "redirect:/home";
        }

        Usuario aluno = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Atividade> atividades = atividadeService.listarPorUsuario(aluno);

        model.addAttribute("usuarioLogado", logado);
        model.addAttribute("aluno", aluno);
        model.addAttribute("manha", atividades.stream().filter(a -> "MANHA".equals(a.getPeriodo())).toList());
        model.addAttribute("tarde", atividades.stream().filter(a -> "TARDE".equals(a.getPeriodo())).toList());
        model.addAttribute("noite", atividades.stream().filter(a -> "NOITE".equals(a.getPeriodo())).toList());

        return "atividades/listar";
    }
}
