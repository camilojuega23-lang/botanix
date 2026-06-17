package botanix.botanix.view;

import botanix.botanix.model.Usuario;
import botanix.botanix.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UsuarioView {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/dashboard";
        }

        return "usuario/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo, @RequestParam String contrasena, HttpSession session, RedirectAttributes ra) {

        Optional<Usuario> resultado = usuarioRepository.findByCorreo(correo.trim());

        if (resultado.isPresent()) {
            Usuario usuario = resultado.get();
            if (passwordEncoder.matches(contrasena,  usuario.getContrasena())) {
                session.setAttribute("usuarioLogueado", usuario.getNombre());
                session.setAttribute("usuarioId", usuario.getId_usuario());
                session.setAttribute("usuarioCorreo", usuario.getCorreo());
                return "redirect:/dashboard";
            }
        }

        ra.addFlashAttribute("error", "Correo o contrasena incorrectos");
        return "redirect:/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/dashboard";
        }

        return "usuario/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@RequestParam String nombre,
                                   @RequestParam String correo,
                                   @RequestParam String contrasena,
                                   @RequestParam String confirmarContrasena,
                                   RedirectAttributes ra) {
        String nombreNormalizado = nombre.trim();
        String correoNormalizado = correo.trim();

        if (nombreNormalizado.isEmpty() || correoNormalizado.isEmpty() || contrasena.isBlank()) {
            ra.addFlashAttribute("error", "Completa todos los campos requeridos");
            return "redirect:/registro";
        }

        if (!contrasena.equals(confirmarContrasena)) {
            ra.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/registro";
        }

        if (contrasena.length() < 6) {
            ra.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            return "redirect:/registro";
        }

        if (usuarioRepository.findByCorreo(correoNormalizado).isPresent()) {
            ra.addFlashAttribute("error", "Ya existe un usuario con ese correo");
            return "redirect:/registro";
        }

        Usuario usuario = Usuario.builder()
                .nombre(nombreNormalizado)
                .correo(correoNormalizado)
                .contrasena(passwordEncoder.encode(contrasena))
                .build();

        usuarioRepository.save(usuario);
        ra.addFlashAttribute("success", "Usuario registrado. Ahora puedes iniciar sesion");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
