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

package es.mpt.dsic.inside.visualizacion.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import es.mpt.dsic.inside.security.context.AplicacionContext;
import es.mpt.dsic.inside.visualizacion.CabeceraAdder;
import es.mpt.dsic.inside.visualizacion.ContentDocumentAdder;
import es.mpt.dsic.inside.visualizacion.IndexStandardVisualizadorAdder;
import es.mpt.dsic.inside.visualizacion.PieAdder;
import es.mpt.dsic.inside.visualizacion.VisualizacionItem;
import es.mpt.dsic.inside.visualizacion.VisualizacionUtils;
import es.mpt.dsic.inside.visualizacion.exception.ContentNotAddedException;
import es.mpt.dsic.inside.visualizacion.exception.VisualizacionException;
import es.mpt.dsic.inside.visualizacion.service.VisualizacionService;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenido;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;

@Service("VisualizacionDocumentoService")
public class VisualizacionDocumentoServiceImpl implements VisualizacionService {

  protected final static Log logger = LogFactory.getLog(VisualizacionDocumentoServiceImpl.class);

  private IndexStandardVisualizadorAdder indexVisualizadorAdder;
  private CabeceraAdder cabeceraAdder;

  @Autowired
  private ContentDocumentAdder contentDocumentAdder;
  private PieAdder pieAdder;

  @Autowired(required = false)
  private AplicacionContext aplicacionContext;

  private String tDocElectronico;
  private String tTipoFirma;
  private String tFirmante;
  private String tFechaFirma;
  private String tValorCsv;
  private String tRegulacionCsv;
  private String tVisualizacionDocElectronico;

  @Override
  public DocumentoContenido doVisualizable(Item item, OpcionesVisualizacion oVisualizacion)
      throws VisualizacionException {
    DocumentoContenido dc = null;

    try {

      String rutaLogo = aplicacionContext.getAplicacionInfo().getPropiedades().get("rutaLogo");

      List<VisualizacionItem> visItems = VisualizacionUtils.obtenerVisualizacionItems(item);

      indexVisualizadorAdder.setTitle(tDocElectronico.toUpperCase());
      indexVisualizadorAdder.setNameColumnsContent(new String[] {tTipoFirma.toUpperCase(),
          tFirmante.toUpperCase() + " / " + tValorCsv.toUpperCase(),
          tFechaFirma.toUpperCase() + " / " + tRegulacionCsv.toUpperCase()});
      indexVisualizadorAdder.setRelativeWidthsContent(new float[] {0.30f, 0.35f, 0.35f});

      Document doc = indexVisualizadorAdder.createVerticalDocument();

      ByteArrayOutputStream outSalida = new ByteArrayOutputStream();

      PdfWriter pw = PdfWriter.getInstance(doc, outSalida);
      pw.setLinearPageMode();
      doc.open();

      indexVisualizadorAdder.addVerticalContent(visItems, doc, pw);

      contentDocumentAdder
          .setIpOO(aplicacionContext.getAplicacionInfo().getPropiedades().get("ip.openoffice"));
      contentDocumentAdder
          .setPortOO(aplicacionContext.getAplicacionInfo().getPropiedades().get("port.openoffice"));
      contentDocumentAdder.addContent(item.getDocumentoContenido().getBytesDocumento(), doc, pw);

      doc.close();
      pw.close();

      byte[] pdfResult = outSalida.toByteArray();

      dc = new DocumentoContenido();

      if (rutaLogo != null && oVisualizacion.getOpcionesLogo() != null
          && oVisualizacion.getOpcionesLogo().isEstamparLogo()
          && oVisualizacion.getOpcionesLogo().isEstamparNombreOrganismo()
          && oVisualizacion.getOpcionesLogo().getListaCadenasNombreOrganismo() != null) {
        cabeceraAdder.setTablaSepEjeX(doc.leftMargin());
        cabeceraAdder.setTablaSepEjeY(15);
        // cabeceraAdder.setWidthTable(480f);
        // cabeceraAdder.setRelativeWidths(new float[] {1f, 2f, 5f});

        // byte[] pdfConLogo =
        // cabeceraAdder.addCabecera(outSalida.toByteArray(),
        pdfResult = cabeceraAdder.addCabecera(pdfResult, rutaLogo, tVisualizacionDocElectronico,
            tDocElectronico + ": " + visItems.get(0).getNombre(),
            (String[]) oVisualizacion.getOpcionesLogo().getListaCadenasNombreOrganismo()
                .getCadenas().toArray(new String[] {}));
      }

      if (oVisualizacion.getOpcionesLogo() != null
          && oVisualizacion.getOpcionesLogo().isEstamparPie()
          && oVisualizacion.getOpcionesLogo().getTextoPie() != null
          && !oVisualizacion.getOpcionesLogo().getTextoPie().contentEquals("")) {

        pieAdder.setTablaSepEjeX(doc.leftMargin());
        pieAdder.setTablaSepEjeY(doc.bottomMargin());

        pdfResult = pieAdder.addPie(pdfResult, oVisualizacion.getOpcionesLogo().getTextoPie());

      }

      dc.setBytesDocumento(pdfResult);
      dc.setMimeDocumento("application/pdf");
      outSalida.close();

      return dc;
    } catch (VisualizacionException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Error al obtener Visualizacion " + e.getMessage(), e);
      throw new VisualizacionException(
          "Ha sido imposible obtener Visualizacion " + item.getNombre(), e);
    }
  }

  public IndexStandardVisualizadorAdder getIndexVisualizadorAdder() {
    return indexVisualizadorAdder;
  }

  public void setIndexVisualizadorAdder(IndexStandardVisualizadorAdder indexVisualizadorAdder) {
    this.indexVisualizadorAdder = indexVisualizadorAdder;
  }

  public CabeceraAdder getCabeceraAdder() {
    return cabeceraAdder;
  }

  public void setCabeceraAdder(CabeceraAdder cabeceraAdder) {
    this.cabeceraAdder = cabeceraAdder;
  }

  public PieAdder getPieAdder() {
    return pieAdder;
  }

  public void setPieAdder(PieAdder pieAdder) {
    this.pieAdder = pieAdder;
  }

  public ContentDocumentAdder getContentDocumentAdder() {
    return contentDocumentAdder;
  }

  public void setContentDocumentAdder(ContentDocumentAdder contentDocumentAdder) {
    this.contentDocumentAdder = contentDocumentAdder;
  }

  public String gettDocElectronico() {
    return tDocElectronico;
  }

  public void settDocElectronico(String tDocElectronico) {
    this.tDocElectronico = tDocElectronico;
  }

  public String gettTipoFirma() {
    return tTipoFirma;
  }

  public void settTipoFirma(String tTipoFirma) {
    this.tTipoFirma = tTipoFirma;
  }

  public String gettFirmante() {
    return tFirmante;
  }

  public void settFirmante(String tFirmante) {
    this.tFirmante = tFirmante;
  }

  public String gettFechaFirma() {
    return tFechaFirma;
  }

  public void settFechaFirma(String tFechaFirma) {
    this.tFechaFirma = tFechaFirma;
  }

  public String gettValorCsv() {
    return tValorCsv;
  }

  public void settValorCsv(String tValorCsv) {
    this.tValorCsv = tValorCsv;
  }

  public String gettRegulacionCsv() {
    return tRegulacionCsv;
  }

  public void settRegulacionCsv(String tRegulacionCsv) {
    this.tRegulacionCsv = tRegulacionCsv;
  }

  public String gettVisualizacionDocElectronico() {
    return tVisualizacionDocElectronico;
  }

  public void settVisualizacionDocElectronico(String tVisualizacionDocElectronico) {
    this.tVisualizacionDocElectronico = tVisualizacionDocElectronico;
  }

  @Override
  public DocumentoContenido doVisualizableOriginal(Item item) throws VisualizacionException {

    try {
      DocumentoContenido dc = new DocumentoContenido();

      Document doc = indexVisualizadorAdder.createVerticalDocument();

      ByteArrayOutputStream outSalida = new ByteArrayOutputStream();

      PdfWriter pw = PdfWriter.getInstance(doc, outSalida);
      pw.setLinearPageMode();
      doc.open();

      contentDocumentAdder
          .setIpOO(aplicacionContext.getAplicacionInfo().getPropiedades().get("ip.openoffice"));
      contentDocumentAdder
          .setPortOO(aplicacionContext.getAplicacionInfo().getPropiedades().get("port.openoffice"));
      contentDocumentAdder.addContent(item.getDocumentoContenido().getBytesDocumento(), doc, pw);

      doc.close();
      pw.close();

      byte[] pdfResult = outSalida.toByteArray();

      dc.setBytesDocumento(pdfResult);
      dc.setMimeDocumento("application/pdf");
      outSalida.close();

      return dc;
    } catch (NumberFormatException e) {
      logger.error("Error al obtener Visualizacion " + e.getMessage(), e);
      throw new VisualizacionException(
          "Ha sido imposible obtener Visualizacion " + item.getNombre(), e);
    } catch (DocumentException e) {
      logger.error("Error al obtener Visualizacion " + e.getMessage(), e);
      throw new VisualizacionException(
          "Ha sido imposible obtener Visualizacion " + item.getNombre(), e);
    } catch (ContentNotAddedException e) {
      logger.error("Error al obtener Visualizacion " + e.getMessage(), e);
      throw new VisualizacionException(
          "Ha sido imposible obtener Visualizacion " + item.getNombre(), e);
    } catch (IOException e) {
      logger.error("Error al obtener Visualizacion " + e.getMessage(), e);
      throw new VisualizacionException(
          "Ha sido imposible obtener Visualizacion " + item.getNombre(), e);
    }
  }

  /*
   * public String getRutaLogo() { return rutaLogo; }
   * 
   * 
   * public void setRutaLogo(String rutaLogo) { this.rutaLogo = rutaLogo; }
   */

}
