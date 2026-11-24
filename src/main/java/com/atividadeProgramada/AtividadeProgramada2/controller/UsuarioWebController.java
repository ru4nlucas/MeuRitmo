package com.atividadeProgramada.AtividadeProgramada2.controller;

import com.atividadeProgramada.AtividadeProgramada2.entity.Role;
import com.atividadeProgramada.AtividadeProgramada2.entity.Usuario;
import com.atividadeProgramada.AtividadeProgramada2.service.AtividadeService;
import com.atividadeProgramada.AtividadeProgramada2.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
@AllArgsConstructor
public class UsuarioWebController {

    private final UsuarioService usuarioService;
    private final AtividadeService atividadeService;

    // abre o formulário vazio para criar usuário
    @GetMapping("/novo")
    public String formularioCriar(HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getRole() != Role.ADMIN) {
            return "redirect:/home";
        }

        model.addAttribute("usuario", new Usuario());
        return "usuarios/formulario";
    }

    // salva o novo usuário (sempre como USUARIO)
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario, HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getRole() != Role.ADMIN) {
            return "redirect:/home";
        }

        usuarioService.salvarUsuario(usuario.getEmail(), usuario.getSenha());
        return "redirect:/home";
    }

    // abre o formulário preenchido para editar
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable String id, HttpSession session, Model model) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getRole() != Role.ADMIN) {
            return "redirect:/home";
        }

        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        model.addAttribute("usuario", usuario);
        return "usuarios/formulario";
    }

    @PostMapping("/atualizar")
    public String atualizar(@ModelAttribute Usuario usuario, HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getRole() != Role.ADMIN) {
            return "redirect:/home";
        }

        usuarioService.atualizar(usuario);
        return "redirect:/home";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable String id, HttpSession session) {
        Usuario logado = (Usuario) session.getAttribute("usuarioLogado");

        if (logado == null || logado.getRole() != Role.ADMIN) {
            return "redirect:/home";
        }
        usuarioService.buscarPorId(id).ifPresent(u -> {
            atividadeService.deletarPorUsuario(u); // apaga as atividades primeiro
            usuarioService.excluir(id);            // depois o usuário
        });
        
        return "redirect:/home";
    }


}