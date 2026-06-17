package botanix.botanix.view;

import botanix.botanix.model.Jardinero;
import botanix.botanix.model.Plantas;
import botanix.botanix.repository.JardineroRepository;
import botanix.botanix.repository.PlantasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ReportesView {

    @Autowired
    private PlantasRepository plantasRepository;

    @Autowired
    private JardineroRepository jardineroRepository;

    @GetMapping("/reportes")
    public String reportes(Model model) {

        List<Plantas> plantas = plantasRepository.findAll();
        List<Jardinero> jardineros = jardineroRepository.findAll();

        // === MÉTRICAS GENERALES ===
        int totalPlantas = plantas.size();
        int totalJardineros = jardineros.size();

        int stockTotal = plantas.stream()
                .mapToInt(p -> p.getCantidad() != null ? p.getCantidad() : 0)
                .sum();

        int valorInventario = plantas.stream()
                .mapToInt(p -> valor(p.getCantidad()) * valor(p.getPrecio()))
                .sum();

        OptionalDouble precioPromedio = plantas.stream()
                .filter(p -> p.getPrecio() != null)
                .mapToInt(Plantas::getPrecio)
                .average();

        int plantaMasValiosa = plantas.stream()
                .filter(p -> p.getPrecio() != null)
                .mapToInt(Plantas::getPrecio)
                .max()
                .orElse(0);

        // === DISTRIBUCIÓN POR ESPECIE ===
        Map<String, Long> plantasPorEspecie = plantas.stream()
                .filter(p -> p.getEspecie() != null && !p.getEspecie().isBlank())
                .collect(Collectors.groupingBy(p -> p.getEspecie().trim(), Collectors.counting()));

        // === DISTRIBUCIÓN POR ROL ===
        Map<String, Long> jardinerosPorRol = jardineros.stream()
                .filter(j -> j.getRol() != null && !j.getRol().isBlank())
                .collect(Collectors.groupingBy(j -> j.getRol().trim(), Collectors.counting()));

        // === TOP 5 PLANTAS POR VALOR (precio * cantidad) ===
        List<Plantas> topPlantas = plantas.stream()
                .sorted(Comparator.comparingInt(p -> -(valor(p.getPrecio()) * valor(p.getCantidad()))))
                .limit(5)
                .collect(Collectors.toList());

        // === PLANTAS CON BAJO STOCK (< 10 unidades) ===
        List<Plantas> bajoStock = plantas.stream()
                .filter(p -> p.getCantidad() != null && p.getCantidad() < 10)
                .sorted(Comparator.comparingInt(Plantas::getCantidad))
                .collect(Collectors.toList());

        // === CANTIDAD TOTAL POR ESPECIE ===
        Map<String, Integer> stockPorEspecie = plantas.stream()
                .filter(p -> p.getEspecie() != null && p.getCantidad() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getEspecie().trim(),
                        Collectors.summingInt(Plantas::getCantidad)
                ));

        long especiesUnicas = plantasPorEspecie.size();
        long rolesUnicos = jardinerosPorRol.size();

        // Máximos para las barras de distribución (calculados en server para evitar SpEL de streams)
        long maxPorEspecie = plantasPorEspecie.values().stream().max(Long::compareTo).orElse(1L);
        long maxPorRol     = jardinerosPorRol.values().stream().max(Long::compareTo).orElse(1L);

        model.addAttribute("totalPlantas", totalPlantas);
        model.addAttribute("totalJardineros", totalJardineros);
        model.addAttribute("stockTotal", stockTotal);
        model.addAttribute("valorInventario", valorInventario);
        model.addAttribute("precioPromedio", precioPromedio.isPresent() ? (int) precioPromedio.getAsDouble() : 0);
        model.addAttribute("plantaMasValiosa", plantaMasValiosa);
        model.addAttribute("plantasPorEspecie", plantasPorEspecie);
        model.addAttribute("jardinerosPorRol", jardinerosPorRol);
        model.addAttribute("topPlantas", topPlantas);
        model.addAttribute("bajoStock", bajoStock);
        model.addAttribute("stockPorEspecie", stockPorEspecie);
        model.addAttribute("especiesUnicas", especiesUnicas);
        model.addAttribute("rolesUnicos", rolesUnicos);
        model.addAttribute("maxPorEspecie", maxPorEspecie);
        model.addAttribute("maxPorRol", maxPorRol);

        return "reportes/index";
    }

    private int valor(Integer n) {
        return n != null ? n : 0;
    }
}
