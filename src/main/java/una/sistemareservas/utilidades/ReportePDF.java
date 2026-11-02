package una.sistemareservas.utilidades;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public final class ReportePDF {

    private ReportePDF() {
    }


    public static void exportarTabla(Window ventana, String nombreArchivo, String titulo, String subtitulo,
                                     String[] encabezados, List<String[]> filas) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar " + titulo);
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        selector.setInitialFileName(nombreArchivo);

        File archivo = selector.showSaveDialog(ventana);
        if (archivo == null) {
            return;
        }

        try (FileOutputStream salida = new FileOutputStream(archivo)) {
            Document documento = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(documento, salida);
            documento.open();

            documento.add(new Paragraph(titulo));
            if (subtitulo != null && !subtitulo.isBlank()) {
                documento.add(new Paragraph(subtitulo));
            }
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(encabezados.length);
            tabla.setWidthPercentage(100);
            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Phrase(encabezado));
                celda.setBackgroundColor(new BaseColor(200, 200, 200));
                tabla.addCell(celda);
            }
            for (String[] fila : filas) {
                for (String valor : fila) {
                    tabla.addCell(valor == null || valor.isEmpty() ? " " : valor);
                }
            }
            documento.add(tabla);
            documento.close();

            Dialogos.informacion("Reporte", "¡El reporte PDF se ha guardado exitosamente!");
        } catch (Exception e) {
            Dialogos.error("Ocurrió un error al generar el PDF: " + e.getMessage());
        }
    }
}
