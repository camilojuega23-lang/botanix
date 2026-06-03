package botanix.botanix.view;

import botanix.botanix.model.Jardinero;
import botanix.botanix.repository.JardineroRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class JardineroView {

    @Autowired
    private JardineroRepository jardineroRepository;

    @GetMapping("/view/jardinero")
    public String jardinero(Model model) {

        model.addAttribute("jardinero", jardineroRepository.findAll());
        return "jardinero/list";
    }

    @GetMapping("/view/jardinero/form")
    public String form(Model model){

        model.addAttribute("jardinero", new Jardinero());
        return "jardinero/form";
    }

    @PostMapping("/view/jardinero/save")
    public String save(@ModelAttribute Jardinero jardinero, RedirectAttributes ra) {

        jardineroRepository.save(jardinero);
        ra.addAttribute("message", "Jardinero guardada correctamente");
        return "redirect:/view/jardinero";
    }

    @GetMapping("/view/jardinero/edit/{id}")
    public String edit (@PathVariable Long id, Model model) {

        Jardinero jardinero = jardineroRepository.findById(id).orElse(null);
        model.addAttribute("jardinero", jardinero);
        return "jardinero/form";
    }

    @PostMapping("/view/jardinero/delete/{id}")
    public String delete (@PathVariable Long id, RedirectAttributes ra) {
        jardineroRepository.deleteById(id);
        ra.addAttribute("message", "Jardinero eliminado exitosamente" );
        return "redirect:/view/jardinero";
    }

    @GetMapping("/view/jardinero/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=jardineros.xlsx");

        List<Jardinero> jardineros = jardineroRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Jardineros");
            Row header = sheet.createRow(0);
            String[] columnas = {"ID", "Nombre", "Documento", "Direccion", "Rol"};

            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
            }

            int rowIndex = 1;
            for (Jardinero jardinero : jardineros) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(jardinero.getId_jardinero() != null ? jardinero.getId_jardinero() : 0);
                row.createCell(1).setCellValue(jardinero.getNombre());
                row.createCell(2).setCellValue(jardinero.getDocumento());
                row.createCell(3).setCellValue(jardinero.getDireccion());
                row.createCell(4).setCellValue(jardinero.getRol());
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/view/jardinero/pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=jardineros.pdf");

        List<Jardinero> jardineros = jardineroRepository.findAll();
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        document.add(new Paragraph("Listado de Jardineros", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.2f, 2.5f, 2.5f, 3f, 2f});

        agregarEncabezados(table, "ID", "Nombre", "Documento", "Direccion", "Rol");

        for (Jardinero jardinero : jardineros) {
            table.addCell(valor(jardinero.getId_jardinero()));
            table.addCell(valor(jardinero.getNombre()));
            table.addCell(valor(jardinero.getDocumento()));
            table.addCell(valor(jardinero.getDireccion()));
            table.addCell(valor(jardinero.getRol()));
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
