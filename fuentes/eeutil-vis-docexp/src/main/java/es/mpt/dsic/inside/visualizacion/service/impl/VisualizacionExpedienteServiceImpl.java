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

package es.mpt.dsic.inside.visualizacion.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.istack.ByteArrayDataSource;

import es.mpt.dsic.inside.security.context.AplicacionContext;
import es.mpt.dsic.inside.services.AfirmaService;
import es.mpt.dsic.inside.utils.exception.EeutilException;
import es.mpt.dsic.inside.visualizacion.CabeceraAdder;
import es.mpt.dsic.inside.visualizacion.IndexStandardVisualizadorAdder;
import es.mpt.dsic.inside.visualizacion.PieAdder;
import es.mpt.dsic.inside.visualizacion.VisualizacionItem;
import es.mpt.dsic.inside.visualizacion.VisualizacionUtils;
import es.mpt.dsic.inside.visualizacion.service.VisualizacionService;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenido;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenidoGeneric;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.ItemGeneric;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;

@Service("VisualizacionExpedienteService")
public class VisualizacionExpedienteServiceImpl implements VisualizacionService {

  protected static final Log logger = LogFactory.getLog(VisualizacionExpedienteServiceImpl.class);

  private IndexStandardVisualizadorAdder indexVisualizadorAdder;
  private CabeceraAdder cabeceraAdder;
  private PieAdder pieAdder;

  @Autowired(required = false)
  private AplicacionContext aplicacionContext;

  private String tExpedienteElectronico;
  private String tDocIndice;

  // private String rutaLogo;

  @Override
  public DocumentoContenidoGeneric doVisualizable(ItemGeneric item,
      OpcionesVisualizacion oVisualizacion, AfirmaService afirmaService, String idAplicacion)
      throws EeutilException {
    DocumentoContenidoGeneric dc = null;
    // ByteArrayOutputStream outSalida=null;

    try (ByteArrayOutputStream outSalida = new ByteArrayOutputStream();) {
      String rutaLogo = aplicacionContext.getAplicacionInfo().getPropiedades().get("rutaLogo");

      List<VisualizacionItem> visItems = VisualizacionUtils.obtenerVisualizacionItems(item);

      // Document doc = indexVisualizadorAdder.createDocument();

      // outSalida = new ByteArrayOutputStream();

      // Document doc=null;

      try (Document doc = indexVisualizadorAdder.createDocument();) {
        // doc = indexVisualizadorAdder.createDocument();
        PdfWriter pw = PdfWriter.getInstance(doc, outSalida);
        pw.setLinearPageMode();
        pw.setPdfVersion(PdfWriter.PDF_VERSION_1_7);
        doc.open();

        indexVisualizadorAdder.setTitle(indexVisualizadorAdder.getDefaulTittle());
        indexVisualizadorAdder
            .setNameColumnsContent(indexVisualizadorAdder.getDefaultColumnsContent());
        indexVisualizadorAdder.addContent(visItems, doc, pw);

        if (doc != null) {
          doc.close();
        }



        dc = new DocumentoContenidoGeneric();

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

        dc.setDocumento(new ByteArrayDataSource(pdfResult, "application/pdf"));
        dc.setMimeDocumento("application/pdf");

      }



    } catch (EeutilException e) {
      throw e;
    } catch (Exception e) {
      // logger.error("Error al obtener Visualizacion " + e.getMessage(), e);
      throw new EeutilException(
          "Ha sido imposible obtener Visualizacion " + item.getNombre() + " " + e.getMessage(), e);
    }

    return dc;
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
  public DocumentoContenido doVisualizableOriginal(Item item, AfirmaService afirmaService,
      String idAplicacion) throws EeutilException {
    throw new UnsupportedOperationException(
        "La operacion no esta definida para esta implementacion");
  }



}
