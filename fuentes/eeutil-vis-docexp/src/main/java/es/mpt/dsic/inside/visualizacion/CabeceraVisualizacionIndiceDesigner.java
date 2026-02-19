/*
 * Copyright (C) 2025, Gobierno de España This program is licensed and may be used, modified and
 * redistributed under the terms of the European Public License (EUPL), either version 1.1 or (at
 * your option) any later version as soon as they are approved by the European Commission. Unless
 * required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and more details. You
 * should have received a copy of the EUPL1.1 license along with this program; if not, you may find
 * it at http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 */

package es.mpt.dsic.inside.visualizacion;

import java.util.Arrays;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import es.mpt.dsic.inside.utils.exception.EeutilException;

public class CabeceraVisualizacionIndiceDesigner implements PdfPTableDesigner {

  private float widthTabla;
  private Font fontLineasLogo = FontFactory.getFont(BaseFont.HELVETICA, 7);
  private Font fontLineasDerecha = FontFactory.getFont(BaseFont.HELVETICA, 10);
  private float[] relativeWidths = new float[] {0.8f, 3.2f, 6f};


  public CabeceraVisualizacionIndiceDesigner() {

  }

  public CabeceraVisualizacionIndiceDesigner(float widthTabla, float[] relativeWidths) {

    this.widthTabla = widthTabla;
    this.relativeWidths = relativeWidths;
    // this.widthTabla = 727f;
    // this.widthTabla = 480f;
  }

  public float getWidthTabla() {
    return widthTabla;
  }

  public void setWidthTabla(float widthTabla) {
    this.widthTabla = widthTabla;
  }

  public float[] getRelativeWidths() {
    return relativeWidths;
  }

  public void setRelativeWidths(float[] relativeWidths) {
    this.relativeWidths = relativeWidths;
  }

  public Font getFontLineasLogo() {
    return fontLineasLogo;
  }

  public void setFontLineasLogo(Font fontLineasLogo) {
    this.fontLineasLogo = fontLineasLogo;
  }

  public Font getFontLineasDerecha() {
    return fontLineasDerecha;
  }

  public void setFontLineasDerecha(Font fontLineasDerecha) {
    this.fontLineasDerecha = fontLineasDerecha;
  }



  @Override
  /**
   * params[0] = ruta del logo params[1] = String[] con l�neas del nombre del organismo params[2] =
   * String[] l�neas de la derecha
   */
  public PdfPTable designTable(Object[] params) throws EeutilException {
    if (params.length != 3) {
      throw new EeutilException("El numero de argumentos tiene que ser 3:",
          new IllegalArgumentException("El n�mero de argumentos tiene que ser 3:"));
    }
    if (!(params[0] instanceof String)) {
      throw new EeutilException("El primer argumento debe ser un String",
          new IllegalArgumentException("El primer argumento debe ser un String"));
    }
    if (!(params[1] instanceof String[])) {
      throw new EeutilException("El segundo argumento debe ser un array de Strings",
          new IllegalArgumentException("El segundo argumento debe ser un array de Strings"));
    }
    if (!(params[2] instanceof String[])) {
      throw new EeutilException("El tercer argumento debe ser un array de Strings",
          new IllegalArgumentException("El tercer argumento debe ser un array de Strings"));
    }

    PdfPTable tabla = new PdfPTable(3);
    tabla.setTotalWidth(widthTabla);
    try {
      // tabla.setWidths(new float[]{0.8f, 3.2f, 6f});
      tabla.setWidths(relativeWidths);
    } catch (DocumentException e) {
      throw new EeutilException("No se puede dibujar la tabla ", e);
    }

    Image img = null;
    try {
      img = Image.getInstance((String) params[0]);
    } catch (Exception e) {
      throw new EeutilException(
          "No se puede obtener la imagen a partir de: " + (String) params[0] + " " + e.getMessage(),
          e);
    }
    tabla.addCell(celdaLogo(img));
    tabla.addCell(celdaTextoLogo((String[]) params[1]));
    String[] lineasDerecha = (String[]) params[2];

    // Todas las cadenas menos la �ltima se escribir�n en una tabla, a la derecha.
    tabla
        .addCell(celdaTablaDerecha(Arrays.copyOfRange(lineasDerecha, 0, lineasDerecha.length - 1)));

    // La �ltima cadena se pinta en una nueva celda (con colspan) de la tabla general, para
    // conseguir que se pinte la l�nea recta.
    tabla.addCell(celdaAbajo(lineasDerecha[lineasDerecha.length - 1], 0.5f));


    // Todas las cadenas menos la �ltima se escribir�n en una tabla, a la derecha.
    // tabla.addCell(celdaTablaDerecha (Arrays.copyOfRange(lineasDerecha, 0, lineasDerecha.length -
    // 1)));
    // tabla.addCell(celdaTablaDerecha (Arrays.copyOfRange(lineasDerecha, 0, lineasDerecha.length -
    // 2)));

    // La �ltima cadena se pinta en una nueva celda (con colspan) de la tabla general, para
    // conseguir que se pinte la l�nea recta.
    // tabla.addCell(celdaAbajo(lineasDerecha[lineasDerecha.length-2], 0.5f));
    //

    return tabla;
  }


  private PdfPCell celdaAbajo(String texto, float borderTop) {
    PdfPCell cellTxt = new PdfPCell();
    cellTxt.setColspan(3);
    cellTxt.setBorder(Rectangle.NO_BORDER);
    if (borderTop != 0.0f) {
      cellTxt.setBorderWidthTop(borderTop);
      // cellTxt.setBorderWidthTop(0.5f);
    }
    Paragraph p = new Paragraph(texto, fontLineasDerecha);
    p.setAlignment(Element.ALIGN_RIGHT);
    cellTxt.addElement(p);
    return cellTxt;


  }

  private PdfPCell celdaLogo(Image img) {
    PdfPCell cellImg = new PdfPCell();
    cellImg.setBorder(Rectangle.NO_BORDER);
    img.scalePercent(25);
    cellImg.addElement(img);
    return cellImg;
  }

  private PdfPCell celdaTextoLogo(String[] lineasLogo) {
    PdfPCell cellTxt = new PdfPCell();
    cellTxt.setBorder(Rectangle.NO_BORDER);
    cellTxt.setPaddingTop(25f);

    Paragraph p = new Paragraph(construirTexto(lineasLogo), fontLineasLogo);
    p.setLeading(8f);
    p.setAlignment(Element.ALIGN_LEFT);
    cellTxt.addElement(p);
    return cellTxt;

  }

  private String construirTexto(String[] lineasLogo) {
    StringBuilder sb = new StringBuilder("");
    for (int i = 0; i < lineasLogo.length; i++) {
      sb.append(lineasLogo[i] + System.getProperty("line.separator"));
    }
    return sb.toString();
  }

  private PdfPCell celdaTablaDerecha(String[] lineasDerecha) {

    PdfPTable table = new PdfPTable(1);

    PdfPCell cellTxt2 = new PdfPCell();
    cellTxt2.setBorder(Rectangle.NO_BORDER);
    // cellTxt.setPaddingTop(10f);
    cellTxt2.addElement(Chunk.NEWLINE);
    table.addCell(cellTxt2);


    for (String linea : lineasDerecha) {
      PdfPCell cellTxt = new PdfPCell();
      cellTxt.setBorder(Rectangle.NO_BORDER);
      // cellTxt.setPaddingTop(10f);
      Paragraph p = new Paragraph(linea, fontLineasDerecha);
      p.setAlignment(Element.ALIGN_RIGHT);
      cellTxt.addElement(p);
      table.addCell(cellTxt);
    }
    PdfPCell celdaTabla = new PdfPCell(table);
    celdaTabla.setBorder(Rectangle.NO_BORDER);
    return celdaTabla;
  }


}
