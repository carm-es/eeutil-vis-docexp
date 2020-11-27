/*
 * Copyright (C) 2012-13 MINHAP, Gobierno de España This program is licensed and may be used,
 * modified and redistributed under the terms of the European Public License (EUPL), either version
 * 1.1 or (at your option) any later version as soon as they are approved by the European
 * Commission. Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * more details. You should have received a copy of the EUPL1.1 license along with this program; if
 * not, you may find it at http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 */

package es.mpt.dsic.inside.visualizacion;

import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
// import com.lowagie.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
// import es.mpt.dsic.inside.ws.service.foliado.FoliadoItem;
// import es.mpt.dsic.inside.ws.service.foliado.IndexAdder;
import es.mpt.dsic.inside.visualizacion.exception.ContentNotAddedException;
// import com.lowagie.text.BaseColor;


public class IndexVisualizadorAdder {

  protected final static Log logger = LogFactory.getLog(IndexVisualizadorAdder.class);

  protected int numFilasHoja;

  protected String title;
  protected Font fontTitle;
  protected Font fontBase;
  protected int alignmentTitle;
  // protected BaseColor backgroundTitle;
  protected BaseColor backgroundTitle;
  protected float paddingTopTitle;
  protected float paddingLeftTitle;

  protected float widthPercentage;

  protected int alignment;

  public static int DEFAULT_NUM_FILAS_HOJA = 30;

  public static String DEFAULT_TITLE = "VISUALIZACI�N DEL �NDICE DE UN EXPEDIENTE";
  // public static Font DEFAULT_FONT_TITLE = new Font(FontFamily.HELVETICA, 18);
  public static Font DEFAULT_FONT_TITLE = FontFactory.getFont(BaseFont.HELVETICA, 18);
  // public static Font DEFAULT_FONT_BASE = new Font(FontFamily.HELVETICA, 12);
  public static Font DEFAULT_FONT_BASE = FontFactory.getFont(BaseFont.HELVETICA, 12);
  public static int DEFAULT_ALIGNMENT_TITLE = Element.ALIGN_LEFT;
  // public static BaseColor DEFAULT_BACKGROUND_TITLE = BaseColor.WHITE;
  public static BaseColor DEFAULT_BACKGROUND_TITLE = BaseColor.WHITE;


  public static float DEFAULT_PADDING_TOP_TITLE = 35f;
  public static float DEFAULT_PADDING_LEFT_TITLE = 45f;

  public static float DEFAULT_WIDTH_PERCENTAGE = 100f;

  public static int DEFAULT_ALIGNMENT = Element.ALIGN_LEFT;

  public IndexVisualizadorAdder() {

    this.title = DEFAULT_TITLE;
    this.fontTitle = DEFAULT_FONT_TITLE;
    this.fontBase = DEFAULT_FONT_BASE;
    this.alignmentTitle = DEFAULT_ALIGNMENT_TITLE;
    this.backgroundTitle = DEFAULT_BACKGROUND_TITLE;
    this.paddingTopTitle = DEFAULT_PADDING_TOP_TITLE;
    this.paddingLeftTitle = DEFAULT_PADDING_LEFT_TITLE;
    this.widthPercentage = DEFAULT_WIDTH_PERCENTAGE;
    this.numFilasHoja = DEFAULT_NUM_FILAS_HOJA;

  }

  public int getNumFilasHoja() {
    return numFilasHoja;
  }

  public void setNumFilasHoja(int numFilasHoja) {
    this.numFilasHoja = numFilasHoja;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Font getFontTitle() {
    return fontTitle;
  }

  public void setFontTitle(Font fontTitle) {
    this.fontTitle = fontTitle;
  }

  public Font getFontBase() {
    return fontBase;
  }

  public void setFontBase(Font fontBase) {
    this.fontBase = fontBase;
  }

  public int getAlignmentTitle() {
    return alignmentTitle;
  }

  public void setAlignmentTitle(int alignmentTitle) {
    this.alignmentTitle = alignmentTitle;
  }

  public BaseColor getBackgroundTitle() {
    return backgroundTitle;
  }

  public void setBackgroundTitle(BaseColor backgroundTitle) {
    this.backgroundTitle = backgroundTitle;
  }

  public float getPaddingTopTitle() {
    return paddingTopTitle;
  }

  public void setPaddingTopTitle(float paddingTopTitle) {
    this.paddingTopTitle = paddingTopTitle;
  }

  public float getPaddingLeftTitle() {
    return paddingLeftTitle;
  }

  public void setPaddingLeftTitle(float paddingLeftTitle) {
    this.paddingLeftTitle = paddingLeftTitle;
  }

  public void addContent(List<VisualizacionItem> listaItems, Document doc, PdfWriter pw)
      throws ContentNotAddedException {

    try {
      doc.newPage();
      if (title != null) {
        printTitle(doc, pw);
      }

      printIndex(listaItems, doc, pw);

    } catch (DocumentException e) {
      logger.error("No se puede índice de la visualización " + e.getMessage(), e);
      throw new ContentNotAddedException("No se puede índice de la visualización ", e);
    }

  }

  /**
   * Pinta el título
   * 
   * @param doc Documento abierto
   * @param pw PdfWriter del documento abierto
   * @throws DocumentException si no se puede pintar el título
   */

  protected void printTitle(Document doc, PdfWriter pw) throws DocumentException {

    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(widthPercentage);

    PdfPCell cell = new PdfPCell();
    cell.setBorder(PdfPCell.NO_BORDER);
    cell.setBackgroundColor(backgroundTitle);
    cell.setPaddingTop(paddingTopTitle);
    cell.setPaddingLeft(paddingLeftTitle);

    Paragraph p = new Paragraph(title, fontTitle);
    p.setAlignment(alignmentTitle);
    cell.addElement(p);

    table.addCell(cell);

    try {
      doc.add(table);
      doc.add(Chunk.NEWLINE);

    } catch (DocumentException e) {
      logger.error("No se puede imprimir el tÃ­tulo del Ã­ndice " + e.getMessage());
      throw e;
    }
  }

  /**
   * Pinta el índice
   * 
   * @param listaItems Lista de elementos del documento
   * @param doc Documento abierto
   * @param pw PdfWriter del documento abierto
   * @throws DocumentException si no se puede pintar.
   */
  protected void printIndex(List<VisualizacionItem> listaItems, Document doc, PdfWriter pw)
      throws DocumentException {

    int filas = 1;

    for (int i = 0; i < listaItems.size(); i++) {

      VisualizacionItem item = listaItems.get(i);

      // Pintamos nueva pÃ¡gina cuando sea necesario
      if (filas % numFilasHoja == 0) {
        doc.newPage();
      }


      /***************
       * TAMAÑO DE FUENTE DEPENDIENDO DE LA PROFUNDIDAD Y COLOR DE LAS FILAS INTERCALADO // El color
       * de fila BaseColor colorCell = backgroundRows [i % backgroundRows.length] ;
       * 
       * // Fuente de la lÃ­nea, mÃ¡s pequeÃ±a cuanto mayor sea la profundidad del elemento en el
       * Ã­ndice (subapartados letra mÃ¡s pequeÃ±a). Font font = new Font (fontBase.getFamily(),
       * fontBase.getSize() - (item.getProfundidadEnIndice() + 2));
       * 
       * PdfPTable fila = fila (item, offset, font, colorCell, doc, pw); FIN TAMAÑO DE FUENTE
       * DEPENDIENDO DE LA PROFUNDIDAD Y COLOR DE LAS FILAS INTERCALADO
       **********************/

      PdfPTable tablaItem = tablaItem(item, fontBase, BaseColor.WHITE);
      filas += tablaItem.getRows().size();

      PdfPTable tablaPropiedades = tablaPropiedades(item, fontBase, BaseColor.WHITE);

      // Si es la primera fila, dejamos un espacio antes.
      if (i == 0) {
        tablaItem.setSpacingBefore(30f);
      }
      try {
        doc.add(tablaItem);
        if (tablaPropiedades != null) {
          doc.add(tablaPropiedades);
          filas += tablaPropiedades.getRows().size();
        }
      } catch (DocumentException de) {
        logger.error("No se puede aÃ±adir la fila " + item.getNombre());
        throw de;
      }


    }
  }


  /**
   * Crea una tabla que se corresponderÃ­a con una fila del Ã­ndice
   * 
   * @param item Contiene informaciÃ³n sobre el elemento del Ã­ndice
   * @param offset NÃºmero de pÃ¡ginas de desplazamiento
   * @param font Fuente
   * @param bgColor Color del fondo de la fila
   * @param pw PdfWriter del documento
   * @return tabla que se corresponde con una fila del Ã­ndice
   */
  protected PdfPTable tablaItem(final VisualizacionItem item, final Font font,
      final BaseColor bgColor) throws DocumentException {
    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(widthPercentage);
    table.setHorizontalAlignment(alignment);

    Paragraph par = new Paragraph(item.getNombre(), font);
    par.setIndentationLeft(20 * (item.getProfundidad() - 1));
    par.setAlignment(Element.ALIGN_LEFT);
    PdfPCell celda = new PdfPCell();
    celda.setBackgroundColor(bgColor);
    celda.setBorder(PdfPCell.NO_BORDER);
    celda.addElement(par);
    table.addCell(celda);

    return table;

  }

  /**
   * Crea la tabla de las propiedades
   * 
   * @param item Item del que se quieren pintar las propiedades
   * @param font
   * @param bgColor
   * @return
   * @throws DocumentException
   */
  protected PdfPTable tablaPropiedades(final VisualizacionItem item, final Font font,
      final BaseColor bgColor) throws DocumentException {
    PdfPTable tabla = null;

    if (item.getPropiedades() != null) {
      tabla = new PdfPTable(2);
      for (Entry<String, String> propiedad : item.getPropiedades()) {
        PdfPCell clave = new PdfPCell();
        clave.setBackgroundColor(bgColor);
        clave.setBorder(PdfPCell.NO_BORDER);
        Paragraph paragClave = new Paragraph(propiedad.getKey(), font);
        paragClave.setAlignment(Element.ALIGN_LEFT);
        paragClave.setIndentationLeft(20 * (item.getProfundidad() - 1) + 20);
        clave.addElement(paragClave);
        tabla.addCell(clave);

        PdfPCell valor = new PdfPCell();
        valor.setBackgroundColor(bgColor);
        valor.setBorder(PdfPCell.NO_BORDER);
        Paragraph paragValor = new Paragraph(propiedad.getValue(), font);
        paragClave.setAlignment(Element.ALIGN_LEFT);
        valor.addElement(paragValor);
        tabla.addCell(valor);
      }
    }

    return tabla;
  }
}
