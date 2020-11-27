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
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import es.mpt.dsic.inside.security.context.AplicacionContext;
import es.mpt.dsic.inside.visualizacion.CabeceraAdder;
import es.mpt.dsic.inside.visualizacion.IndexStandardVisualizadorAdder;
import es.mpt.dsic.inside.visualizacion.PieAdder;
import es.mpt.dsic.inside.visualizacion.VisualizacionItem;
import es.mpt.dsic.inside.visualizacion.VisualizacionUtils;
import es.mpt.dsic.inside.visualizacion.exception.VisualizacionException;
import es.mpt.dsic.inside.visualizacion.service.VisualizacionService;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenido;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;

@Service("VisualizacionExpedienteService")
public class VisualizacionExpedienteServiceImpl implements VisualizacionService {

  protected final static Log logger = LogFactory.getLog(VisualizacionExpedienteServiceImpl.class);

  private IndexStandardVisualizadorAdder indexVisualizadorAdder;
  private CabeceraAdder cabeceraAdder;
  private PieAdder pieAdder;

  @Autowired(required = false)
  private AplicacionContext aplicacionContext;

  private String tExpedienteElectronico;
  private String tDocIndice;

  // private String rutaLogo;

  @Override
  public DocumentoContenido doVisualizable(Item item, OpcionesVisualizacion oVisualizacion)
      throws VisualizacionException {
    DocumentoContenido dc = null;

    try {
      String rutaLogo = aplicacionContext.getAplicacionInfo().getPropiedades().get("rutaLogo");

      List<VisualizacionItem> visItems = VisualizacionUtils.obtenerVisualizacionItems(item);

      Document doc = indexVisualizadorAdder.createDocument();

      ByteArrayOutputStream outSalida = new ByteArrayOutputStream();

      PdfWriter pw = PdfWriter.getInstance(doc, outSalida);
      pw.setLinearPageMode();
      doc.open();

      indexVisualizadorAdder.setTitle(indexVisualizadorAdder.getDefaulTittle());
      indexVisualizadorAdder
          .setNameColumnsContent(indexVisualizadorAdder.getDefaultColumnsContent());
      indexVisualizadorAdder.addContent(visItems, doc, pw);

      doc.close();
      pw.close();

      dc = new DocumentoContenido();

      byte[] pdfResult = outSalida.toByteArray();

      if (rutaLogo != null && oVisualizacion.getOpcionesLogo() != null
          && oVisualizacion.getOpcionesLogo().isEstamparLogo()
          && oVisualizacion.getOpcionesLogo().isEstamparNombreOrganismo()
          && oVisualizacion.getOpcionesLogo().getListaCadenasNombreOrganismo() != null) {
        cabeceraAdder.setTablaSepEjeX(doc.leftMargin());
        cabeceraAdder.setTablaSepEjeY(15);
        // cabeceraAdder.setWidthTable(727f);
        // cabeceraAdder.setRelativeWidths(new float[] {0.8f, 3.2f,
        // 6f});

        // byte[] pdfConLogo =
        // cabeceraAdder.addCabecera(outSalida.toByteArray(),
        pdfResult = cabeceraAdder.addCabecera(pdfResult, rutaLogo,
            tExpedienteElectronico + ": " + visItems.get(0).getNombre(), tDocIndice,
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

  public String gettExpedienteElectronico() {
    return tExpedienteElectronico;
  }

  public void settExpedienteElectronico(String tExpedienteElectronico) {
    this.tExpedienteElectronico = tExpedienteElectronico;
  }

  public String gettDocIndice() {
    return tDocIndice;
  }

  public void settDocIndice(String tDocIndice) {
    this.tDocIndice = tDocIndice;
  }

  @Override
  public DocumentoContenido doVisualizableOriginal(Item item) throws VisualizacionException {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * public String getRutaLogo() { return rutaLogo; }
   * 
   * public void setRutaLogo(String rutaLogo) { this.rutaLogo = rutaLogo; }
   */

}
