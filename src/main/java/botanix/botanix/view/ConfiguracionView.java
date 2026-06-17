package botanix.botanix.view;

import botanix.botanix.repository.JardineroRepository;
import botanix.botanix.repository.PlantasRepository;
import botanix.botanix.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ConfiguracionView {

    @Autowired
    private PlantasRepository plantasRepository;

    @Autowired
    private JardineroRepository jardineroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/configuracion")
    public String configuracion(Model model) {

        model.addAttribute("totalPlantas",   plantasRepository.count());
        model.addAttribute("totalJardineros", jardineroRepository.count());
        model.addAttribute("totalUsuarios",  usuarioRepository.count());
        model.addAttribute("version",        "1.0.0");
        model.addAttribute("javaVersion",    System.getProperty("java.version"));
        model.addAttribute("os",             System.getProperty("os.name"));

        return "configuracion/index";
    }

    /**
     * Sube una foto de perfil y la guarda como /static/img/img.png
     * (sobreescribe la imagen que usa el sidebar).
     */
    @PostMapping("/configuracion/foto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto,
                            RedirectAttributes ra) {

        if (foto.isEmpty()) {
            ra.addFlashAttribute("error", "Debes seleccionar una imagen.");
            return "redirect:/configuracion";
        }

        String contentType = foto.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            ra.addFlashAttribute("error", "El archivo debe ser una imagen (JPG, PNG, GIF...).");
            return "redirect:/configuracion";
        }

        try {
            // Guarda en src/main/resources/static/img/img.png (funciona en modo dev)
            Path destDir = Paths.get(System.getProperty("user.dir"),
                                     "src", "main", "resources", "static", "img");
            Files.createDirectories(destDir);
            Path dest = destDir.resolve("img.png");
            foto.transferTo(dest.toFile());

            ra.addFlashAttribute("success", "Foto de perfil actualizada correctamente.");
        } catch (IOException e) {
            ra.addFlashAttribute("error", "Error al guardar la imagen: " + e.getMessage());
        }

        return "redirect:/configuracion";
    }

    @PostMapping("/configuracion/limpiar-cache")
    public String limpiarCache(RedirectAttributes ra) {
        ra.addFlashAttribute("success", "Caché del sistema limpiada correctamente.");
        return "redirect:/configuracion";
    }
}
