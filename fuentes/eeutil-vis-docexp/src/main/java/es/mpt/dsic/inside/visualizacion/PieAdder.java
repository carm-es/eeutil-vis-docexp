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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// import com.lowagie.text.BaseColor;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import es.mpt.dsic.inside.visualizacion.exception.ContentNotAddedException;

public class PieAdder {


  protected final static Log logger = LogFactory.getLog(PieAdder.class);


  // SEPARACIÓN DEL BORDE DE LA IZQUIERDA
  protected static float DEFAULT_SEP_EJE_X = 30;
  // SEPARACIÓN DEL BORDE DE ARRIBA
  protected static float DEFAULT_SEP_EJE_Y = 24;

  private static float widthTableHor = 727f;
  private static float widthTableVer = 480f;

  /*
   * private static float[] relativeWidthsHor = {0.8f, 3.2f, 6f}; private static float[]
   * relativeWidthsVer = {1f, 2f, 5f};
   * 
   * private String tPagina;
   */


  // Indica el desplazamiento, con respecto al borde de la derecha.
  protected float tablaSepEjeX;
  // Indica el desplazamiento, con respecto al borde de arriba.
  protected float tablaSepEjeY;

  protected float widthTable;

  // protected float[] relativeWidths;

  public float getTablaSepEjeX() {
    return tablaSepEjeX;
  }

  public void setTablaSepEjeX(float tablaSepEjeX) {
    this.tablaSepEjeX = tablaSepEjeX;
  }

  public float getTablaSepEjeY() {
    return tablaSepEjeY;
  }

  public void setTablaSepEjeY(float tablaSepEjeY) {
    this.tablaSepEjeY = tablaSepEjeY;
  }

  public float getWidthTable() {
    return widthTable;
  }

  public void setWidthTable(float widthTable) {
    this.widthTable = widthTable;
  }

  /*
   * public float[] getRelativeWidths() { return relativeWidths; } public void
   * setRelativeWidths(float[] relativeWidths) { this.relativeWidths = relativeWidths; }
   */
  public PieAdder() {

    tablaSepEjeX = DEFAULT_SEP_EJE_X;
    tablaSepEjeY = DEFAULT_SEP_EJE_Y;

  }



  // public byte[] addCabecera (byte[] pdfEntrada, String rutaLogo, String nombreExpediente,
  // String[] lineasOrganismo) throws ContentNotAddedException {
  // public byte[] addCabecera (byte[] pdfEntrada, String rutaLogo, String nombreEntidad, String
  // valorEntidad, String nombreDocumento, String[] lineasOrganismo) throws ContentNotAddedException
  // {
  public byte[] addPie(byte[] pdfEntrada, String texto) throws ContentNotAddedException {

    byte[] bytesSalida = null;

    ByteArrayInputStream bis = new ByteArrayInputStream(pdfEntrada);
    ByteArrayOutputStream salida = new ByteArrayOutputStream();
    try {
      PdfReader reader = new PdfReader(bis);
      salida = new ByteArrayOutputStream();
      PdfStamper stamp = new PdfStamper(reader, salida);
      stamp.setFullCompression();
      stamp.setFormFlattening(false);
      int n = reader.getNumberOfPages();

      PdfPTableDesigner designerHorizontal = new PieVisualizacionDesigner(widthTableHor);
      PdfPTableDesigner designerVertical = new PieVisualizacionDesigner(widthTableVer);

      PdfPTable pie;

      for (int i = 1; i <= n; i++) {

        Object[] params = {texto};
        // Vertical
        if (stamp.getImportedPage(reader, i).getHeight() > stamp.getImportedPage(reader, i)
            .getWidth()) {
          pie = designerVertical.designTable(params);
          // Horizontal
        } else {
          pie = designerHorizontal.designTable(params);
        }

        // pie.writeSelectedRows(0, -1, tablaSepEjeX, stamp.getImportedPage(reader, i).getHeight() -
        // tablaSepEjeY, stamp.getOverContent(i));
        pie.writeSelectedRows(0, -1, tablaSepEjeX, tablaSepEjeY, stamp.getOverContent(i));
      }

      stamp.close();
      reader.close();
      salida.close();

      bytesSalida = salida.toByteArray();

    } catch (Exception e) {
      logger.error("No se puede agregar el pie " + e.getMessage(), e);
      throw new ContentNotAddedException("No se puede agregar el pie " + e.getMessage());

    }



    return bytesSalida;

  }
}
