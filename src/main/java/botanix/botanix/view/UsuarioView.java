package botanix.botanix.view;

import botanix.botanix.model.Usuario;
import botanix.botanix.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UsuarioView {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "usuario/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo, @RequestParam String contrasena, HttpSession session, RedirectAttributes ra) {

        Optional<Usuario> resultado = usuarioRepository.findByCorreo(correo);

        if (resultado.isPresent()) {
            Usuario usuario = resultado.get();
            if (usuario.getContrasena().equals(contrasena)) {
                session.setAttribute("usuarioLogueado", usuario.getNombre());
                return "redirect:/dashboard";
            }
        }

        ra.addFlashAttribute("error", "Correo o contraseña incorrectos");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
