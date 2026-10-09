package com.atividadeProgramada.AtividadeProgramada2.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.atividadeProgramada.AtividadeProgramada2.entity.Role;
import com.atividadeProgramada.AtividadeProgramada2.entity.Usuario;
import com.atividadeProgramada.AtividadeProgramada2.repository.UsuarioRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(String id) {
        String validId = Objects.requireNonNull(id, "id e obrigatorio");
        return usuarioRepository.findById(validId);
        //exemplo de busca no postman
        //http://localhost:8080/api/usuarios/e0460116-50ae-463b-a965-5b0a22985a39
    }
    

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario salvar(Usuario usuario) {
        if (usuario.getRole() == null) {
            usuario.setRole(Role.USUARIO);
        }
        return usuarioRepository.save(usuario);
    }
    public Usuario salvarUsuario(String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha(senha);
        if(usuarioRepository.count() == 0){
            usuario.setRole(Role.ADMIN);
        }
        else {
            usuario.setRole(Role.USUARIO);
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Usuario usuario) {
        String id = Objects.requireNonNull(usuario.getId(), "id e obrigatorio para atualizar");
        return usuarioRepository.findById(id)
                .map(existente -> {
                    existente.setEmail(usuario.getEmail());
                    existente.setSenha(usuario.getSenha());
                    if (usuario.getRole() != null) {
                        existente.setRole(usuario.getRole());
                    }
                    return usuarioRepository.save(existente);
                })
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
    }

    public void excluir(String id) {
        String validId = Objects.requireNonNull(id, "id e obrigatorio");
        usuarioRepository.deleteById(validId);
    }

    // Verifica email e senha e retorna o usuario se estiver correto
    public Optional<Usuario> login(String email, String senha) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> u.getSenha().equals(senha));
    }
}
