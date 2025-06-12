package expendiocrudproyecto.utilidades;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import expendiocrudproyecto.modelo.pojo.*;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javafx.scene.control.Alert;

public class GeneradorReportesPDF {
    private static final String DIRECTORIO_REPORTES = "reportes";

    private static final Font FONT_TITULO = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font FONT_SUBTITULO = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font FONT_NORMAL = new Font(Font.FontFamily.HELVETICA, 12);
    private static final Font FONT_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font FONT_PIE_PAGINA = new Font(Font.FontFamily.HELVETICA, 8);

    private static File obtenerRutaCompleta(String nombreBaseArchivo) {
        File directorio = new File(DIRECTORIO_REPORTES);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        return new File(directorio, nombreBaseArchivo);
    }

    private static void agregarLogo(Document documento) {
        try {
            Image logo = Image.getInstance(GeneradorReportesPDF.class.getResource("/expendiocrudproyecto/recursos/logo.png"));
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            documento.add(logo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void agregarTitulo(Document documento, String titulo) throws DocumentException {
        Paragraph parrafoTitulo = new Paragraph(titulo, FONT_TITULO);
        parrafoTitulo.setAlignment(Element.ALIGN_CENTER);
        parrafoTitulo.setSpacingAfter(20);
        documento.add(parrafoTitulo);
    }

    private static void agregarEncabezadoTabla(PdfPTable tabla, String[] encabezados) {
        PdfPCell celdaEncabezado = new PdfPCell();
        celdaEncabezado.setBackgroundColor(BaseColor.LIGHT_GRAY);
        celdaEncabezado.setPadding(5);
        Font fontEncabezado = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        for (String encabezado : encabezados) {
            celdaEncabezado.setPhrase(new Phrase(encabezado, fontEncabezado));
            tabla.addCell(celdaEncabezado);
        }
    }

    private static void agregarPiePagina(Document documento) throws DocumentException {
        Paragraph piePagina = new Paragraph("Reporte generado el " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), FONT_PIE_PAGINA);
        piePagina.setAlignment(Element.ALIGN_CENTER);
        documento.add(piePagina);
    }

    public static void abrirArchivoPDF(String rutaArchivo) {
        try {
            File pdfFile = new File(rutaArchivo);
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Reporte generado", "El reporte se ha guardado como: " + pdfFile.getAbsolutePath());
                }
            }
        } catch (IOException ex) {
            Alertas.crearAlerta(Alert.AlertType.INFORMATION, "Reporte generado", "El reporte se ha guardado como: " + rutaArchivo);
        }
    }

    // --- Métodos de generación de reportes específicos ---

    public static String generarReporteVentasPeriodo(List<ReporteVentasPeriodo> ventas, LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String nombreBase = "Reporte_Ventas_Periodo_" + fechaInicio + "_a_" + fechaFin + ".pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase);
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF));
        documento.open();

        agregarLogo(documento);
        agregarTitulo(documento, "REPORTE DE VENTAS POR PERIODO");

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        agregarEncabezadoTabla(tabla, new String[]{"Periodo", "Total Pedidos", "Total Ventas"});

        for (ReporteVentasPeriodo venta : ventas) {
            tabla.addCell(venta.getPeriodo());
            tabla.addCell(String.valueOf(venta.getTotalPedidos()));
            tabla.addCell("$" + String.format("%.2f", venta.getTotalVentas()));
        }
        documento.add(tabla);

        agregarPiePagina(documento);
        documento.close();
        return archivoPDF.getAbsolutePath();
    }

    public static String generarReporteVentasProducto(List<ReporteVentasProducto> productos) throws Exception {
        String nombreBase = "Reporte_Ventas_Por_Producto.pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase);
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF));
        documento.open();

        agregarLogo(documento);
        agregarTitulo(documento, "REPORTE DE VENTAS POR PRODUCTO");

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        agregarEncabezadoTabla(tabla, new String[]{"Producto", "Precio Unitario", "Cantidad Vendida", "Total"});

        for (ReporteVentasProducto producto : productos) {
            tabla.addCell(producto.getNombreProducto());
            tabla.addCell("$" + String.format("%.2f", producto.getPrecioUnitario()));
            tabla.addCell(String.valueOf(producto.getCantidadVendida()));
            tabla.addCell("$" + String.format("%.2f", producto.getTotalVentas()));
        }
        documento.add(tabla);

        agregarPiePagina(documento);
        documento.close();
        return archivoPDF.getAbsolutePath();
    }

    public static String generarReporteStockMinimo(List<ReporteStockMinimo> productos) throws Exception {
        String nombreBase = "Reporte_Stock_Minimo.pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase);
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF));
        documento.open();

        agregarLogo(documento);
        agregarTitulo(documento, "REPORTE DE PRODUCTOS CON STOCK MÍNIMO");

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        agregarEncabezadoTabla(tabla, new String[]{"Producto", "Stock Actual", "Stock Mínimo", "Precio Unitario"});

        for (ReporteStockMinimo producto : productos) {
            tabla.addCell(producto.getNombreProducto());
            tabla.addCell(String.valueOf(producto.getStockActual()));
            tabla.addCell(String.valueOf(producto.getStockMinimo()));
            tabla.addCell("$" + String.format("%.2f", producto.getPrecioUnitario()));
        }
        documento.add(tabla);

        agregarPiePagina(documento);
        documento.close();
        return archivoPDF.getAbsolutePath();
    }

    public static String generarReporteProductosMasVendidos(List<ReporteProductosMasVendidos> productos) throws Exception {
        String nombreBase = "Reporte_Productos_Mas_Vendidos.pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase);
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF));
        documento.open();

        agregarLogo(documento);
        agregarTitulo(documento, "REPORTE DE PRODUCTOS MÁS VENDIDOS");

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        agregarEncabezadoTabla(tabla, new String[]{"Producto", "Total Vendido", "Total Ingresos"});

        for (ReporteProductosMasVendidos producto : productos) {
            tabla.addCell(producto.getNombreProducto());
            tabla.addCell(String.valueOf(producto.getCantidadVendida()));
            tabla.addCell("$" + String.format("%.2f", producto.getTotalVentas()));
        }
        documento.add(tabla);

        agregarPiePagina(documento);
        documento.close();
        return archivoPDF.getAbsolutePath();
    }

    public static String generarReporteProductosMenosVendidos(List<ReporteProductosMenosVendidos> productos) throws Exception {
        String nombreBase = "Reporte_Productos_Menos_Vendidos.pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase);
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF));
        documento.open();

        agregarLogo(documento);
        agregarTitulo(documento, "REPORTE DE PRODUCTOS MENOS VENDIDOS");

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        agregarEncabezadoTabla(tabla, new String[]{"Producto", "Total Vendido", "Total Ingresos", "Observación"});

        for (ReporteProductosMenosVendidos producto : productos) {
            tabla.addCell(producto.getNombreProducto());
            tabla.addCell(String.valueOf(producto.getCantidadVendida()));
            tabla.addCell("$" + String.format("%.2f", producto.getTotalVentas()));
            tabla.addCell(producto.getObservacion());
        }
        documento.add(tabla);

        agregarPiePagina(documento);
        documento.close();
        return archivoPDF.getAbsolutePath();
    }

    public static String generarReporteProductosNoVendidos(List<ReporteProductosNoVendidosCliente> productos) throws Exception {
        String nombreBase = "Reporte_Productos_No_Vendidos_Cliente.pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase);
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF));
        documento.open();

        agregarLogo(documento);
        agregarTitulo(documento, "REPORTE DE PRODUCTOS NO VENDIDOS A CLIENTE");

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        agregarEncabezadoTabla(tabla, new String[]{"Producto", "Precio Unitario", "Recomendación"});

        for (ReporteProductosNoVendidosCliente producto : productos) {
            tabla.addCell(producto.getNombreProducto());
            tabla.addCell("$" + String.format("%.2f", producto.getPrecioUnitario()));
            tabla.addCell(producto.getRecomendacion());
        }
        documento.add(tabla);

        agregarPiePagina(documento);
        documento.close();
        return archivoPDF.getAbsolutePath();
    }

    public static String generarReportePromocionCliente(List<ReporteProductoMasVendidoCliente> productos) throws Exception {
        String nombreBase = "Reporte_Promocion_Cliente.pdf";
        File archivoPDF = obtenerRutaCompleta(nombreBase);
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, new FileOutputStream(archivoPDF));
        documento.open();

        agregarLogo(documento);
        agregarTitulo(documento, "REPORTE DE PROMOCIONES POR CLIENTE");

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        agregarEncabezadoTabla(tabla, new String[]{"Cliente", "Producto Favorito", "Cantidad Comprada"});

        for (ReporteProductoMasVendidoCliente producto : productos) {
            tabla.addCell(producto.getNombreCliente());
            tabla.addCell(producto.getNombreProducto());
            tabla.addCell(String.valueOf(producto.getCantidadComprada()));
        }
        documento.add(tabla);

        agregarPiePagina(documento);
        documento.close();
        return archivoPDF.getAbsolutePath();
    }
}