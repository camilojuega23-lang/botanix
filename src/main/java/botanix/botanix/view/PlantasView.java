package botanix.botanix.view;


import botanix.botanix.model.Plantas;
import botanix.botanix.repository.PlantasRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class PlantasView {

    @Autowired
    private PlantasRepository plantasRepository;

    @GetMapping("/view/plantas")
    public String plantas(Model model) {

        model.addAttribute("plantas", plantasRepository.findAll());
        return "plantas/list";
    }

    @GetMapping("/view/plantas/form")
    public String form(Model model){

        model.addAttribute("plantas", new Plantas());
        return "plantas/form";
    }

    @PostMapping("/view/plantas/save")
    public String save(@ModelAttribute Plantas plantas, RedirectAttributes ra) {

        plantasRepository.save(plantas);
        ra.addAttribute("message", "Planta guardada correctamente");
        return "redirect:/view/plantas";
    }

    @GetMapping("/view/plantas/edit/{id}")
    public String edit (@PathVariable Long id, Model model) {

        Plantas plantas = plantasRepository.findById(id).orElse(null);
        model.addAttribute("plantas", plantas);
        return "plantas/form";
    }

    @PostMapping("/view/plantas/delete/{id}")
    public String delete (@PathVariable Long id, RedirectAttributes ra) {
        plantasRepository.deleteById(id);
        ra.addAttribute("message", "Planta eliminado exitosamente" );
        return "redirect:/view/plantas";
    }

    @GetMapping("/view/plantas/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=plantas.xlsx");

        List<Plantas> plantas = plantasRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Plantas");
            Row header = sheet.createRow(0);
            String[] columnas = {"ID", "Codigo", "Nombre", "Nombre cientifico", "Especie", "Cantidad", "Precio"};

            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int rowIndex = 1;
            for (Plantas planta : plantas) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(planta.getId_plantas() != null ? planta.getId_plantas() : 0);
                row.createCell(1).setCellValue(planta.getCodigo());
                row.createCell(2).setCellValue(planta.getNombre());
                row.createCell(3).setCellValue(planta.getNombre_cientifico());
                row.createCell(4).setCellValue(planta.getEspecie());
                row.createCell(5).setCellValue(planta.getCantidad() != null ? planta.getCantidad() : 0);
                row.createCell(6).setCellValue(planta.getPrecio() != null ? planta.getPrecio() : 0);
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/view/plantas/pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=plantas.pdf");

        List<Plantas> plantas = plantasRepository.findAll();
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        document.add(new Paragraph("Listado de Plantas", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 2f, 2.5f, 3f, 2f, 1.5f, 1.5f});

        agregarEncabezados(table, "ID", "Codigo", "Nombre", "Nombre cientifico", "Especie", "Cantidad", "Precio");

        for (Plantas planta : plantas) {
            table.addCell(valor(planta.getId_plantas()));
            table.addCell(valor(planta.getCodigo()));
            table.addCell(valor(planta.getNombre()));
            table.addCell(valor(planta.getNombre_cientifico()));
            table.addCell(valor(planta.getEspecie()));
            table.addCell(valor(planta.getCantidad()));
            table.addCell(valor(planta.getPrecio()));
        }

        document.add(table);
        document.close();
    }

    private void agregarEncabezados(PdfPTable table, String... encabezados) {
        for (String encabezado : encabezados) {
            PdfPCell cell = new PdfPCell(new Phrase(encabezado, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(cell);
        }
    }

    private String valor(Object value) {
        return value != null ? value.toString() : "";
    }

}
