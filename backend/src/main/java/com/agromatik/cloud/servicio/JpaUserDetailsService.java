package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Usuario;
import com.agromatik.cloud.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public JpaUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Convertir el Enum 'TipoUsuario' a una 'GrantedAuthority' de Spring Security
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + usuario.getTipo().name())
        );

        // MODIFICACIÓN IMPORTANTE
        // Usamos el constructor completo de UserDetails para pasar el estado 'activo'.
        return new User(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                usuario.getActivo(), // enabled (true si está activo, false si está "soft-deleted")
                true, // accountNonExpired (asumimos true)
                true, // credentialsNonExpired (asumimos true)
                true, // accountNonLocked (asumimos true)
                authorities
        );
    }
}