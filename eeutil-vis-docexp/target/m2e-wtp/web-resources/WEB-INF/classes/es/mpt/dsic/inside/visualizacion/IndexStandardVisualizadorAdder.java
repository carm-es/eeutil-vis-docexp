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

// import com.lowagie.text.BaseColor;
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
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import es.mpt.dsic.inside.visualizacion.exception.ContentNotAddedException;
// import com.lowagie.text.Font.FontFamily;

public class IndexStandardVisualizadorAdder {

  protected final static Log logger = LogFactory.getLog(IndexStandardVisualizadorAdder.class);

  protected float widthPercentage;
  protected int borderTableTitle;
  protected BaseColor backgroundTitle;

  protected String title;
  protected String title1Document;
  protected String title2Document;

  protected Font fontTitle;
  protected int alignmentTitle;

  // protected Font fontBase;

  protected String[] nameColumnsContent;
  protected float[] relativeWidthsContent;

  protected Font fontBaseBold;
  protected Font fontBase;

  public String defaulTittle;
  // public static String DEFAULT_TITLE1_DOCUMENT = "METADATOS DE DOCUMENTO ELECTR�NICO";
  // public static String DEFAULT_TITLE2_DOCUMENT = "FIRMAS DE DOCUMENTO ELECTR�NICO";
  public static Font DEFAULT_FONT_TITLE = FontFactory.getFont(BaseFont.HELVETICA, 12, Font.BOLD);
  public static Font DEFAULT_FONT_BASE = FontFactory.getFont(BaseFont.HELVETICA, 12);
  public static int DEFAULT_ALIGNMENT_TITLE = Element.ALIGN_CENTER;
  public static BaseColor DEFAULT_BACKGROUND_TITLE = new BaseColor(200, 200, 200);
  // public static BaseColor DEFAULT_BACKGROUND_TITLE = BaseColor.GRAY;

  public static float DEFAULT_WIDTH_PERCENTAGE = 100f;

  public static int DEFAULT_ALIGNMENT = Element.ALIGN_LEFT;

  public static int DEFAULT_NUM_COLUMNS_CONTENT = 3;
  public static int DEFAULT_NUM_COLUMNS_CONTENT_DOCUMENT = 3;
  public String[] defaultColumnsContent;
  public String[] defaultColumnsContentDocument;

  // public static float[] DEFAULT_RELATIVE_WIDTHS_CONTENT = {0.60f, 0.10f, 0.30f};
  public static float[] DEFAULT_RELATIVE_WIDTHS_CONTENT = {0.65f, 0.10f, 0.25f};

  public static Font DEFAULT_FONT_BOLD = FontFactory.getFont(BaseFont.HELVETICA, 8, Font.BOLD);
  public static Font DEFAULT_FONT = FontFactory.getFont(BaseFont.HELVETICA, 8);


  public IndexStandardVisualizadorAdder() {
    // this.title = defaulTittle;
    this.fontTitle = DEFAULT_FONT_TITLE;
    this.fontBase = DEFAULT_FONT_BASE;
    this.alignmentTitle = DEFAULT_ALIGNMENT_TITLE;
    this.backgroundTitle = DEFAULT_BACKGROUND_TITLE;

    this.widthPercentage = DEFAULT_WIDTH_PERCENTAGE;

    // this.nameColumnsContent = defaultColumnsContent;
    this.relativeWidthsContent = DEFAULT_RELATIVE_WIDTHS_CONTENT;
    this.fontBaseBold = DEFAULT_FONT_BOLD;
    this.fontBase = DEFAULT_FONT;
    // this.title1Document = DEFAULT_TITLE1_DOCUMENT;
    // this.title2Document = DEFAULT_TITLE2_DOCUMENT;

  }

  public float getWidthPercentage() {
    return widthPercentage;
  }

  public void setWidthPercentage(float widthPercentage) {
    this.widthPercentage = widthPercentage;
  }

  public int getBorderTableTitle() {
    return borderTableTitle;
  }

  public void setBorderTableTitle(int borderTableTitle) {
    this.borderTableTitle = borderTableTitle;
  }

  public BaseColor getBackgroundTitle() {
    return backgroundTitle;
  }

  public void setBackgroundTitle(BaseColor backgroundTitle) {
    this.backgroundTitle = backgroundTitle;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle1Document() {
    return title1Document;
  }

  public void setTitle1Document(String title1Document) {
    this.title1Document = title1Document;
  }

  public String getTitle2Document() {
    return title2Document;
  }

  public void setTitle2Document(String title2Document) {
    this.title2Document = title2Document;
  }

  public Font getFontTitle() {
    return fontTitle;
  }

  public void setFontTitle(Font fontTitle) {
    this.fontTitle = fontTitle;
  }

  public int getAlignmentTitle() {
    return alignmentTitle;
  }

  public void setAlignmentTitle(int alignmentTitle) {
    this.alignmentTitle = alignmentTitle;
  }

  public String[] getNameColumnsContent() {
    return nameColumnsContent;
  }

  public void setNameColumnsContent(String[] nameColumnsContent) {
    this.nameColumnsContent = nameColumnsContent;
  }

  public float[] getRelativeWidthsContent() {
    return relativeWidthsContent;
  }

  public void setRelativeWidthsContent(float[] relativeWidthsContent) {
    this.relativeWidthsContent = relativeWidthsContent;
  }

  public Font getFontBaseBold() {
    return fontBaseBold;
  }

  public void setFontBaseBold(Font fontBaseBold) {
    this.fontBaseBold = fontBaseBold;
  }

  public Font getFontBase() {
    return fontBase;
  }

  public void setFontBase(Font fontBase) {
    this.fontBase = fontBase;
  }

  public Document createDocument() {
    Document doc = new Document();
    doc.setPageSize(new Rectangle(842, 595));
    doc.setMargins(75, 40, 100, 60);
    return doc;
  }

  public Document createVerticalDocument() {
    Document doc = new Document();
    doc.setPageSize(new Rectangle(595, 842));
    doc.setMargins(75, 40, 100, 60);
    return doc;
  }

  protected void printTitleIndex(Document doc, PdfWriter pw) throws DocumentException {
    try {
      printTitle(doc, pw, title);
    } catch (DocumentException e) {
      logger.error("No se puede imprimir el tÃÂ­tulo del ÃÂ­ndice " + e.getMessage());
      throw e;
    }

  }

  protected void printTitle1Document() {

  }

  protected void printTitle(Document doc, PdfWriter pw, String title) throws DocumentException {
    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(widthPercentage);

    PdfPCell cell = new PdfPCell();
    cell.setBorderWidth(0.5f);
    cell.setBackgroundColor(backgroundTitle);
    cell.setPaddingTop(-3f);
    cell.setPaddingLeft(0f);


    Paragraph p = new Paragraph(title, fontTitle);
    p.setAlignment(alignmentTitle);
    cell.addElement(p);

    table.addCell(cell);

    try {
      doc.add(table);
      doc.add(Chunk.NEWLINE);
    } catch (DocumentException e) {
      logger.error("No se puede imprimir el titulo  " + title + ", " + e.getMessage());
      throw e;
    }
  }

  /**
   * Pinta el tÃ­tulo
   * 
   * @param doc Documento abierto
   * @param pw PdfWriter del documento abierto
   * @throws DocumentException si no se puede pintar el tÃ­tulo
   */

  /*
   * protected void printTitle (Document doc, PdfWriter pw) throws DocumentException {
   * 
   * PdfPTable table = new PdfPTable (1); table.setWidthPercentage(widthPercentage);
   * 
   * PdfPCell cell = new PdfPCell(); cell.setBorderWidth(0.5f);
   * cell.setBackgroundColor(backgroundTitle); cell.setPaddingTop(-3f); cell.setPaddingLeft(0f);
   * 
   * 
   * Paragraph p = new Paragraph(title, fontTitle); p.setAlignment(alignmentTitle);
   * cell.addElement(p);
   * 
   * table.addCell(cell);
   * 
   * try { doc.add(table); doc.add(Chunk.NEWLINE);
   * 
   * } catch (com.lowagie.text.DocumentException e) { logger.error
   * ("No se puede imprimir el tÃÂ­tulo del ÃÂ­ndice " + e.getMessage()); throw e; } }
   */

  public void addContent(List<VisualizacionItem> listaItems, Document doc, PdfWriter pw)
      throws ContentNotAddedException {

    try {
      doc.newPage();
      if (title != null) {
        printTitle(doc, pw, title);
      }

      printPreambulo(listaItems.get(0), doc);

      printIndex(listaItems, doc, pw);

    } catch (DocumentException e) {
      logger.error("No se puede índice de la visualización " + e.getMessage(), e);
      throw new ContentNotAddedException("No se puede índice de la visualización ", e);
    }

  }

  public void addVerticalContent(List<VisualizacionItem> listaItems, Document doc, PdfWriter pw)
      throws ContentNotAddedException {
    try {
      doc.newPage();
      if (title != null) {
        printTitle(doc, pw, title);
      }

      printPreambulo(listaItems.get(0), doc);

      printMoreData(listaItems, doc, pw);

    } catch (DocumentException e) {
      logger.error("No se puede a�adir el contenido " + e.getMessage(), e);
      throw new ContentNotAddedException("No se puede a�adir el contenido ", e);
    }

  }

  /*
   * Pinta el índice
   * 
   * @param listaItems Lista de elementos del documento
   * 
   * @param doc Documento abierto
   * 
   * @param pw PdfWriter del documento abierto
   * 
   * @throws DocumentException si no se puede pintar.
   */
  protected void printIndex(List<VisualizacionItem> listaItems, Document doc, PdfWriter pw)
      throws DocumentException {

    PdfPTable tableContenido = new PdfPTable(nameColumnsContent.length);
    tableContenido.setWidthPercentage(widthPercentage);
    tableContenido.setWidths(relativeWidthsContent);

    // Cabecera - primera celda (identificador del expediente)
    String idExp = nameColumnsContent[0] + ": " + listaItems.get(0).getNombre();
    tableContenido.addCell(createCellCabecera(idExp, Element.ALIGN_LEFT));

    // Cabecera - resto de celdas de la cabecera
    for (int i = 1; i < nameColumnsContent.length; i++) {
      tableContenido.addCell(createCellCabecera(nameColumnsContent[i], Element.ALIGN_CENTER));
    }

    // Contenido
    // Recorremos la lista de Items, ignoramos el primero porque ya lo hemos pintado en el pre�mbulo

    for (int i = 1; i < listaItems.size(); i++) {
      VisualizacionItem visItem = listaItems.get(i);
      Font font = null;

      if (visItem.getPropiedades() == null) {
        font = fontBaseBold;
      } else {
        font = fontBase;
      }

      tableContenido.addCell(createCeldaDescripcionContenido(
          visItem.marcadoresToString(true) + " - " + visItem.getNombre(), visItem.getProfundidad(),
          font, Element.ALIGN_LEFT));


      for (int j = 1; j < nameColumnsContent.length; j++) {
        String texto = "";
        if (visItem.getPropiedades() != null) {
          texto = visItem.getPropiedades().get(j - 1).getValue();
        }
        tableContenido.addCell(createCeldaPropiedad(texto, font, Element.ALIGN_CENTER));
      }
    }

    doc.add(tableContenido);
  }

  protected void printMoreData(List<VisualizacionItem> listaItems, Document doc, PdfWriter pw)
      throws DocumentException {

    PdfPTable tableContenido = new PdfPTable(nameColumnsContent.length);
    tableContenido.setWidthPercentage(widthPercentage);
    tableContenido.setWidths(relativeWidthsContent);


    // Cabecera - resto de celdas de la cabecera
    for (int i = 0; i < nameColumnsContent.length; i++) {
      tableContenido.addCell(createCellCabecera(nameColumnsContent[i], Element.ALIGN_LEFT));
    }

    // Contenido
    // Recorremos la lista de Items, ignoramos el primero porque ya lo hemos pintado en el pre�mbulo

    for (int i = 1; i < listaItems.size(); i++) {
      VisualizacionItem visItem = listaItems.get(i);
      Font font = null;

      if (visItem.getPropiedades() == null) {
        font = fontBaseBold;
      } else {
        font = fontBase;
      }

      // tableContenido.addCell(createCeldaDescripcionContenido(visItem.marcadoresToString(true) + "
      // - " + visItem.getNombre(), visItem.getProfundidad(), font));
      tableContenido.addCell(createCeldaSimple(visItem.getNombre(), font, Element.ALIGN_LEFT));


      for (int j = 1; j < nameColumnsContent.length; j++) {
        String texto = "";
        if (visItem.getPropiedades() != null) {
          texto = visItem.getPropiedades().get(j - 1).getValue();
        }
        tableContenido.addCell(createCeldaSimple(texto, font, Element.ALIGN_LEFT));
      }
    }

    doc.add(tableContenido);
  }



  private void printPreambulo(VisualizacionItem item, Document doc) throws DocumentException {
    if (item.getPropiedades() != null) {

      PdfPTable tablePreambulo = new PdfPTable(2);
      tablePreambulo.setWidthPercentage(widthPercentage);
      tablePreambulo.setWidths(new float[] {0.25f, 0.75f});

      for (Entry<String, String> propiedad : item.getPropiedades()) {

        PdfPCell celdaClave = new PdfPCell();
        celdaClave.setBorder(PdfPCell.NO_BORDER);
        celdaClave.setPaddingLeft(4f);
        celdaClave.setPaddingTop(-1f);
        Paragraph p = new Paragraph(propiedad.getKey() + ":", fontBaseBold);
        p.setAlignment(Element.ALIGN_LEFT);

        celdaClave.addElement(p);
        tablePreambulo.addCell(celdaClave);

        PdfPCell celdaValor = new PdfPCell();
        celdaValor.setBorder(PdfPCell.NO_BORDER);
        celdaValor.setPaddingTop(-2f);
        Paragraph p1 = new Paragraph(propiedad.getValue(), fontBase);
        p1.setAlignment(Element.ALIGN_LEFT);
        celdaValor.addElement(p1);
        tablePreambulo.addCell(celdaValor);

        tablePreambulo.setSpacingAfter(30f);
      }

      doc.add(tablePreambulo);
    }
  }

  private PdfPCell createCellCabecera(String texto, int alignment) {
    PdfPCell cabecera = new PdfPCell();
    cabecera.setBorder(PdfPCell.NO_BORDER);
    cabecera.setBackgroundColor(backgroundTitle);
    cabecera.setPaddingTop(-2f);
    if (alignment == Element.ALIGN_LEFT) {
      cabecera.setPaddingLeft(6f);
    }


    Paragraph p = new Paragraph(texto, fontBaseBold);
    p.setAlignment(alignment);
    cabecera.addElement(p);
    return cabecera;
  }



  private PdfPCell createCeldaDescripcionContenido(String texto, int profundidad, Font font,
      int alignment) {
    PdfPCell celda = new PdfPCell();
    celda.setBorder(PdfPCell.NO_BORDER);

    celda.setPaddingLeft(8f * profundidad);
    if (font.getStyle() == Font.BOLD) {
      celda.setPaddingTop(0f);
    } else {
      celda.setPaddingTop(-2f);
    }

    Paragraph p = new Paragraph(texto, font);
    // p.setAlignment(Element.ALIGN_LEFT);
    p.setAlignment(alignment);

    celda.addElement(p);
    return celda;
  }

  private PdfPCell createCeldaSimple(String texto, Font font, int alignment) {
    PdfPCell celda = new PdfPCell();
    celda.setBorder(PdfPCell.NO_BORDER);

    // celda.setPaddingTop(-2f);
    if (alignment == Element.ALIGN_LEFT) {
      celda.setPaddingLeft(6f);
    }
    Paragraph p = new Paragraph(texto, font);
    p.setAlignment(alignment);

    celda.addElement(p);
    return celda;
  }

  private PdfPCell createCeldaPropiedad(String texto, Font font, int alignment) {
    PdfPCell celda = new PdfPCell();
    celda.setBorder(PdfPCell.NO_BORDER);
    if (font.getStyle() == Font.BOLD) {
      celda.setPaddingTop(0f);
    } else {
      celda.setPaddingTop(-2f);
    }
    // celda.setPaddingTop(-6f);

    Paragraph p = new Paragraph(texto, font);
    // p.setAlignment(Element.ALIGN_CENTER);
    p.setAlignment(alignment);

    celda.addElement(p);
    return celda;
  }

  public String getDefaulTittle() {
    return defaulTittle;
  }

  public void setDefaulTittle(String defaulTittle) {
    this.defaulTittle = defaulTittle;
  }

  public String[] getDefaultColumnsContent() {
    return defaultColumnsContent;
  }

  public void setDefaultColumnsContent(String[] defaultColumnsContent) {
    String[] cadenas = null;
    if (defaultColumnsContent != null) {
      cadenas = new String[defaultColumnsContent.length];
      for (int iCnt = 0; iCnt < defaultColumnsContent.length; iCnt++) {
        cadenas[iCnt] = defaultColumnsContent[iCnt].toUpperCase();
      }
    }
    this.defaultColumnsContent = cadenas;
  }

  public String[] getDefaultColumnsContentDocument() {
    return defaultColumnsContentDocument;
  }

  public void setDefaultColumnsContentDocument(String[] defaultColumnsContentDocument) {
    this.defaultColumnsContentDocument = defaultColumnsContentDocument;
  }

}
