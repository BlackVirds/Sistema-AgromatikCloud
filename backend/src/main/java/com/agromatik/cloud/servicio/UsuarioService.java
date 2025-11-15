package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Usuario;
import com.agromatik.cloud.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> getAll(){
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getByUuid(String uuid) {
        return usuarioRepository.findByUuid(uuid);
    }

    public Optional<Usuario> update(String uuid, Usuario datosActualizados) {
        return usuarioRepository.findByUuid(uuid).map(usuarioExistente -> {

            if (datosActualizados.getNombre() != null) {
                usuarioExistente.setNombre(datosActualizados.getNombre());
            }
            if (datosActualizados.getApellido() != null) {
                usuarioExistente.setApellido(datosActualizados.getApellido());
            }
            if (datosActualizados.getTelefono() != null) {
                usuarioExistente.setTelefono(datosActualizados.getTelefono());
            }
            if (datosActualizados.getTipo() != null) {
                usuarioExistente.setTipo(datosActualizados.getTipo());
            }
            if (datosActualizados.getSuscriptionPlan() != null) {
                usuarioExistente.setSuscriptionPlan(datosActualizados.getSuscriptionPlan());
            }
            if (datosActualizados.getActivo() != null) {
                usuarioExistente.setActivo(datosActualizados.getActivo());
            }
            if (datosActualizados.getConfiguraciones() != null) {
                usuarioExistente.setConfiguraciones(datosActualizados.getConfiguraciones());
            }

            return usuarioRepository.save(usuarioExistente);
        });
    }

    public Usuario save(Usuario usuarioNuevosDatos){
        // 1. Buscar si el email ya existe en la BD
        Optional<Usuario> usuarioExistenteOpt = usuarioRepository.findByEmail(usuarioNuevosDatos.getEmail());

        if (usuarioExistenteOpt.isPresent()) {
            // --- EL EMAIL YA EXISTE ---
            Usuario usuarioExistente = usuarioExistenteOpt.get();

            if (usuarioExistente.getActivo()) {
                // Caso 1: El email existe y el usuario está ACTIVO.
                // No se puede registrar. Lanzamos el error.
                throw new IllegalStateException("El email " + usuarioNuevosDatos.getEmail() + " ya está registrado y activo.");

            } else {
                // Caso 2: El email existe pero está INACTIVO (Soft Deleted).
                // ¡REACTIVAMOS LA CUENTA!
                // Actualizamos el registro viejo con los datos del nuevo registro.

                usuarioExistente.setNombre(usuarioNuevosDatos.getNombre());
                usuarioExistente.setApellido(usuarioNuevosDatos.getApellido());
                usuarioExistente.setTelefono(usuarioNuevosDatos.getTelefono());
                usuarioExistente.setTipo(Usuario.TipoUsuario.AGRICULTOR);
                usuarioExistente.setSuscriptionPlan(Usuario.PlanSuscripcion.BASICO); // (Forzamos el plan)

                // Hashear la NUEVA contraseña
                usuarioExistente.setPasswordHash(passwordEncoder.encode(usuarioNuevosDatos.getPasswordHash()));

                // Reactivar
                usuarioExistente.setActivo(true);
                // Opcional: Actualizar la fecha de registro/reactivación
                // usuarioExistente.setFechaRegistro(LocalDateTime.now());

                return usuarioRepository.save(usuarioExistente); // Guardamos el usuario ACTUALIZADO
            }

        } else {
            // --- EL EMAIL NO EXISTE ---
            // Caso 3: Es un usuario completamente nuevo.
            //CORRECCIÓN DE SEGURIDAD (Forzar rol Y plan por defecto)
            usuarioNuevosDatos.setTipo(Usuario.TipoUsuario.AGRICULTOR);
            usuarioNuevosDatos.setSuscriptionPlan(Usuario.PlanSuscripcion.BASICO);
            // Hashear la contraseña
            usuarioNuevosDatos.setPasswordHash(passwordEncoder.encode(usuarioNuevosDatos.getPasswordHash()));

            return usuarioRepository.save(usuarioNuevosDatos); // Guardamos el usuario NUEVO
        }
    }

    public boolean deleteByUuid(String uuid) {
        return usuarioRepository.findByUuid(uuid).map(usuario -> {

            // 1. Verificar si el usuario ya está inactivo (opcional)
            if (usuario.getActivo() == null || !usuario.getActivo()) {
                // Ya estaba inactivo, no hacemos nada más, pero confirmamos el éxito
                return true;
            }

            // 2. Desactivación Lógica (Soft Delete)
            usuario.setActivo(false); //  CAMBIO CLAVE: Marcamos como inactivo

            // 3. Guardamos el cambio (el registro se mantiene, pero 'activo' es false)
            usuarioRepository.save(usuario);

            return true; // La operación de "eliminación lógica" fue exitosa
        }).orElse(false); // Si no se encuentra el UUID, devuelve false (404 Not Found)
    }

    public boolean deleteByEmail(String email) {
        // Busca al usuario por email para el soft delete
        return usuarioRepository.findByEmail(email).map(usuario -> {

            if (usuario.getActivo() == null || !usuario.getActivo()) {
                return true; // Ya estaba inactivo
            }

            usuario.setActivo(false); // Desactivación Lógica
            usuarioRepository.save(usuario);

            return true;
        }).orElse(false); // No se encontró el email
    }

    /**
     * (USUARIO) Actualiza solo los campos "seguros" de su propio perfil.
     * El email se obtiene del token JWT.
     */
    public Optional<Usuario> updateMiPerfil(String emailUsuario, Usuario datosActualizados) {
        return usuarioRepository.findByEmail(emailUsuario).map(usuarioExistente -> {

            // El usuario SÍ puede actualizar esto:
            if (datosActualizados.getNombre() != null) {
                usuarioExistente.setNombre(datosActualizados.getNombre());
            }
            if (datosActualizados.getApellido() != null) {
                usuarioExistente.setApellido(datosActualizados.getApellido());
            }
            if (datosActualizados.getTelefono() != null) {
                usuarioExistente.setTelefono(datosActualizados.getTelefono());
            }
            if (datosActualizados.getConfiguraciones() != null) {
                usuarioExistente.setConfiguraciones(datosActualizados.getConfiguraciones());
            }

            // El usuario NO PUEDE actualizar su rol (tipo) ni su estado (activo)
            // Tampoco su plan de suscripción (esto debería ir en un endpoint de /pagos)

            return usuarioRepository.save(usuarioExistente);
        });
    }
}
