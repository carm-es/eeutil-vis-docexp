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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import es.mpt.dsic.inside.utils.exception.EeutilException;

public class PieAdder {

  protected static final Log logger = LogFactory.getLog(PieAdder.class);

  // SEPARACIÓN DEL BORDE DE LA IZQUIERDA
  protected static final float DEFAULT_SEP_EJE_X = 30;
  // SEPARACIÓN DEL BORDE DE ARRIBA
  protected static final float DEFAULT_SEP_EJE_Y = 24;

  private static final float WIDTHTABLEHOR = 727f;
  private static final float WIDTHTABLEVER = 480f;

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

  // public byte[] addCabecera (byte[] pdfEntrada, String rutaLogo, String
  // nombreExpediente, String[] lineasOrganismo) throws ContentNotAddedException {
  // public byte[] addCabecera (byte[] pdfEntrada, String rutaLogo, String
  // nombreEntidad, String valorEntidad, String nombreDocumento, String[]
  // lineasOrganismo) throws ContentNotAddedException {
  public byte[] addPie(byte[] pdfEntrada, String texto) throws EeutilException {

    byte[] bytesSalida = null;

    ByteArrayInputStream bis = new ByteArrayInputStream(pdfEntrada);
    ByteArrayOutputStream salida = new ByteArrayOutputStream();
    PdfReader reader = null;
    PdfStamper stamp = null;

    try {
      reader = new PdfReader(bis);
      salida = new ByteArrayOutputStream();
      stamp = new PdfStamper(reader, salida, PdfWriter.VERSION_1_7);
      stamp.setFullCompression();
      stamp.setFormFlattening(true);
      stamp.setFreeTextFlattening(false);
      int n = reader.getNumberOfPages();

      PdfPTableDesigner designerHorizontal = new PieVisualizacionDesigner(WIDTHTABLEHOR);
      PdfPTableDesigner designerVertical = new PieVisualizacionDesigner(WIDTHTABLEVER);

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

        // pie.writeSelectedRows(0, -1, tablaSepEjeX, stamp.getImportedPage(reader,
        // i).getHeight() - tablaSepEjeY, stamp.getOverContent(i));
        pie.writeSelectedRows(0, -1, tablaSepEjeX, tablaSepEjeY, stamp.getOverContent(i));
      }

    } catch (NoClassDefFoundError e) {
      if (e.getMessage().contains("bouncycastle")) {

        // logger.error("El fichero esta protegido y no ha podido abrirse.");
        throw new EeutilException(
            "El fichero esta protegido y no ha podido abrirse." + e.getMessage(), e);
      } else {
        // logger.error(e.getMessage());
        throw new EeutilException(e.getMessage(), e);
      }

    }

    catch (Exception e) {
      // logger.error("No se puede agregar el pie " + e.getMessage(), e);
      throw new EeutilException("No se puede agregar el pie " + e.getMessage(), e);

    } finally {
      try {
        if (stamp != null)
          stamp.close();

        if (reader != null)
          reader.close();
        if (salida != null)
          salida.close();
      } catch (DocumentException | IOException e) {
        logger.error(e.getMessage(), e);
      }
    }

    bytesSalida = salida.toByteArray();

    return bytesSalida;

  }
}
