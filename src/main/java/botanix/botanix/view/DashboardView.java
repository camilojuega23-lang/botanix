package botanix.botanix.view;

import botanix.botanix.model.Jardinero;
import botanix.botanix.model.Plantas;
import botanix.botanix.repository.JardineroRepository;
import botanix.botanix.repository.PlantasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class DashboardView {

    @Autowired
    private PlantasRepository plantasRepository;

    @Autowired
    private JardineroRepository jardineroRepository;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        List<Plantas> plantas = plantasRepository.findAll();
        List<Jardinero> jardineros = jardineroRepository.findAll();

        int stockTotal = plantas.stream()
                .map(Plantas::getCantidad)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        int valorInventario = plantas.stream()
                .mapToInt(planta -> valor(planta.getCantidad()) * valor(planta.getPrecio()))
                .sum();

        long especiesRegistradas = plantas.stream()
                .map(Plantas::getEspecie)
                .filter(especie -> especie != null && !especie.isBlank())
                .map(String::trim)
                .distinct()
                .count();

        Map<String, Long> plantasPorEspecie = plantas.stream()
                .map(Plantas::getEspecie)
                .filter(especie -> especie != null && !especie.isBlank())
                .collect(Collectors.groupingBy(String::trim, Collectors.counting()));

        Map<String, Long> jardinerosPorRol = jardineros.stream()
                .map(Jardinero::getRol)
                .filter(rol -> rol != null && !rol.isBlank())
                .collect(Collectors.groupingBy(String::trim, Collectors.counting()));

        List<Plantas> ultimasPlantas = plantas.stream()
                .sorted(Comparator.comparing(Plantas::getId_plantas, Comparator.nullsLast(Long::compareTo)).reversed())
                .limit(5)
                .toList();

        List<Jardinero> ultimosJardineros = jardineros.stream()
                .sorted(Comparator.comparing(Jardinero::getId_jardinero, Comparator.nullsLast(Long::compareTo)).reversed())
                .limit(5)
                .toList();

        model.addAttribute("totalPlantas", plantas.size());
        model.addAttribute("totalJardineros", jardineros.size());
        model.addAttribute("stockTotal", stockTotal);
        model.addAttribute("valorInventario", valorInventario);
        model.addAttribute("especiesRegistradas", especiesRegistradas);
        model.addAttribute("plantasPorEspecie", plantasPorEspecie);
        model.addAttribute("jardinerosPorRol", jardinerosPorRol);
        model.addAttribute("ultimasPlantas", ultimasPlantas);
        model.addAttribute("ultimosJardineros", ultimosJardineros);

        return "dashboard/index";
    }

    private int valor(Integer numero) {
        return numero != null ? numero : 0;
    }
}
