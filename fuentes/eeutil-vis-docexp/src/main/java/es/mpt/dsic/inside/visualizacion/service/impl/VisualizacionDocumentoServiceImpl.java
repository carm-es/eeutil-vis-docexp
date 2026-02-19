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

import java.awt.geom.AffineTransform;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSmartCopy;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.SimpleBookmark;
import com.lowagie.text.xml.xmp.DublinCoreSchema;
import com.lowagie.text.xml.xmp.PdfSchema;
import com.lowagie.text.xml.xmp.XmpArray;
import com.lowagie.text.xml.xmp.XmpSchema;
import com.lowagie.text.xml.xmp.XmpWriter;
import com.sun.istack.ByteArrayDataSource;

import es.mpt.dsic.inside.configure.ConfigureLibreofficeMime;
import es.mpt.dsic.inside.model.InformacionFirmaAfirma;
import es.mpt.dsic.inside.model.ResultadoValidacionInfoAfirma;
import es.mpt.dsic.inside.pdf.file.StamperWrapper;
import es.mpt.dsic.inside.security.context.AplicacionContext;
import es.mpt.dsic.inside.services.AfirmaService;
import es.mpt.dsic.inside.util.CodigosError;
import es.mpt.dsic.inside.utils.exception.EeutilException;
import es.mpt.dsic.inside.utils.file.FileUtil;
import es.mpt.dsic.inside.utils.io.IOUtil;
import es.mpt.dsic.inside.utils.mime.MimeUtil;
import es.mpt.dsic.inside.utils.pdf.PdfEncr;
import es.mpt.dsic.inside.utils.xml.XMLUtil;
import es.mpt.dsic.inside.visualizacion.CabeceraAdder;
import es.mpt.dsic.inside.visualizacion.ContentDocumentAdder;
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

@Service("VisualizacionDocumentoService")
public class VisualizacionDocumentoServiceImpl implements VisualizacionService {

  private static final String MSG_ERROR_FORMATO_INVALIDO =
      "El fichero incluido en la peticion no es un formato valido para la operacion que intenta realizar, mas informacion en https://administracionelectronica.gob.es/ctt/inside .";

  private static final String APPLICATION_PDF_MIME = "application/pdf";

  private static final String LANG_PARAM = "/Lang";

  private static final String TITLE_PARAM = "/Title";

  private static final String TEXT_TCN_MIME = "text/tcn";

  private static final String ERROR_AL_OBTENER_VISUALIZACION = "Error al obtener Visualizacion ";

  private static final String HA_SIDO_IMPOSIBLE_OBTENER_VISUALIZACION =
      "Ha sido imposible obtener Visualizacion ";

  protected static final Log logger = LogFactory.getLog(VisualizacionDocumentoServiceImpl.class);

  private static final String ERROR_AL_ELIMINAR_FICHERO_TEMPORAL =
      "Error al eliminar fichero temporal:";

  public static final String ADDER_CONTENT_PREFFIX = "adderContent";

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
  public DocumentoContenidoGeneric doVisualizable(ItemGeneric item,
      OpcionesVisualizacion oVisualizacion, AfirmaService afirmaService, String idAplicacion)
      throws EeutilException {

    DocumentoContenidoGeneric dc = null;
    File documentoConvertidoPdf = null;
    File documentoConvertido1PaginaMas = null;
    StamperWrapper stamperWrapper = null;
    Object[] objContent = null;
    File fileIn = null;
    File fcabeceraPieVisualizar = null;
    File fResultadoFinal = null;
    boolean bTieneRotaciones = false;

    try (ByteArrayOutputStream outSalida = new ByteArrayOutputStream();) {

      String rutaLogo = aplicacionContext.getAplicacionInfo().getPropiedades().get("rutaLogo");

      List<VisualizacionItem> visItems = VisualizacionUtils.obtenerVisualizacionItems(item);

      indexVisualizadorAdder.setTitle(tDocElectronico.toUpperCase());
      indexVisualizadorAdder.setNameColumnsContent(new String[] {tTipoFirma.toUpperCase(),
          tFirmante.toUpperCase() + " / " + tValorCsv.toUpperCase(),
          tFechaFirma.toUpperCase() + " / " + tRegulacionCsv.toUpperCase()});
      indexVisualizadorAdder.setRelativeWidthsContent(new float[] {0.30f, 0.35f, 0.35f});

      Document doc = null;
      PdfWriter pw = null;

      try {

        doc = indexVisualizadorAdder.createVerticalDocument();

        pw = PdfWriter.getInstance(doc, outSalida);
        pw.setLinearPageMode();
        pw.setPdfVersion(PdfWriter.PDF_VERSION_1_7);
        doc.open();

        indexVisualizadorAdder.addVerticalContent(visItems, doc);

      } finally {
        if (doc != null) {
          doc.close();
        }
        if (pw != null) {
          pw.close();
        }
      }

      byte[] verticalContent = outSalida.toByteArray();

      contentDocumentAdder
          .setIpOO(aplicacionContext.getAplicacionInfo().getPropiedades().get("ip.openoffice"));
      contentDocumentAdder
          .setPortOO(aplicacionContext.getAplicacionInfo().getPropiedades().get("port.openoffice"));

      String[] aResultado = workFlowMimeLibreofficeItemGeneric(idAplicacion, item,
          ADDER_CONTENT_PREFFIX, afirmaService);

      // filepath
      fileIn = new File(aResultado[0]);

      // String mimeFinal = aResultado[1];

      File entradaSinSeguridad = null;

      // si el mime es de pdf le quitamos la seguridad al fichero.
      /*
       * if (mimeFinal.equals("application/pdf")) { // String rutaSinSeguridad =
       * FileUtil.createFilePath("sinSeguridad") + ".pdf";
       * 
       * // quitamos seguridad al pdf original // entradaSinSeguridad =
       * copiarPdfEliminandoSeguridad(fileIn.toPath(), // rutaSinSeguridad); entradaSinSeguridad =
       * fileIn;
       * 
       * } // si no es pdf no se hace el proceso de quitar la seguridad puesto que la // conversion
       * no lleva seguridad ya. else { entradaSinSeguridad = fileIn; }
       */

      entradaSinSeguridad = fileIn;

      // le pasamos el mime calculado antes le pasabamos null a libreoffice
      objContent = contentDocumentAdder.convertContent(entradaSinSeguridad,
          aplicacionContext.getAplicacionId(), aResultado[1]);

      stamperWrapper = (StamperWrapper) objContent[0];
      documentoConvertidoPdf = new File(stamperWrapper.getNameFileFileOutPrefix());

      stamperWrapper.closeAll();

      Document docSalida = indexVisualizadorAdder.createVerticalDocument();

      // Generamos cabecerapie

      // String sRutaPrimeraPagina=FileUtil.createFilePath("cabeceraPieVisualizar");
      // FileUtils.writeByteArrayToFile(new File(sRutaPrimeraPagina),
      // verticalContent);

      bTieneRotaciones = (boolean) objContent[5];

      byte[] pdfResultCabeceraPie = null;

      try (FileInputStream isdocumentoConvertidoPdf = new FileInputStream(documentoConvertidoPdf)) {
        pdfResultCabeceraPie = contentDocumentAdder.mergePdfs(verticalContent,
            isdocumentoConvertidoPdf, docSalida, (String) objContent[3], (String) objContent[2],
            (List) objContent[4], (List) objContent[1]);
      }

      dc = new DocumentoContenidoGeneric();

      if (rutaLogo != null && oVisualizacion.getOpcionesLogo() != null
          && oVisualizacion.getOpcionesLogo().isEstamparLogo()
          && oVisualizacion.getOpcionesLogo().isEstamparNombreOrganismo()
          && oVisualizacion.getOpcionesLogo().getListaCadenasNombreOrganismo() != null) {
        cabeceraAdder.setTablaSepEjeX(doc.leftMargin());
        cabeceraAdder.setTablaSepEjeY(0);
        // cabeceraAdder.setWidthTable(480f);
        // cabeceraAdder.setRelativeWidths(new float[] {1f, 2f, 5f});

        // byte[] pdfConLogo =
        // cabeceraAdder.addCabecera(outSalida.toByteArray(),
        pdfResultCabeceraPie = cabeceraAdder.addCabecera(pdfResultCabeceraPie, rutaLogo,
            tVisualizacionDocElectronico, tDocElectronico + ": " + visItems.get(0).getNombre(),
            (String[]) oVisualizacion.getOpcionesLogo().getListaCadenasNombreOrganismo()
                .getCadenas().toArray(new String[] {}));
      }

      if (oVisualizacion.getOpcionesLogo() != null
          && oVisualizacion.getOpcionesLogo().isEstamparPie()
          && oVisualizacion.getOpcionesLogo().getTextoPie() != null
          && !oVisualizacion.getOpcionesLogo().getTextoPie().contentEquals("")) {

        pieAdder.setTablaSepEjeX(doc.leftMargin());
        pieAdder.setTablaSepEjeY(35.0f);

        String textoPie = oVisualizacion.getOpcionesLogo().getTextoPie();

        if (textoPie != null && textoPie.length() > 90) {
          textoPie = textoPie.substring(0, 90) + "...";
        }

        pdfResultCabeceraPie = pieAdder.addPie(pdfResultCabeceraPie, textoPie);

      }

      HashMap<String, String> mDatosAccesibilidad = new HashMap<>();
      mDatosAccesibilidad.put(TITLE_PARAM, objContent[3].toString());
      mDatosAccesibilidad.put(LANG_PARAM, objContent[2].toString());

      String spdfResultCabeceraPie = FileUtil.createFilePath("cabeceraPieVisualizar");
      fcabeceraPieVisualizar = new File(spdfResultCabeceraPie);
      FileUtils.writeByteArrayToFile(fcabeceraPieVisualizar, pdfResultCabeceraPie);

      String sresultadoFinal = null;

      if (!bTieneRotaciones) {

        // anadir una pagina al principio al newreduced documentoConvertidoPdf
        sresultadoFinal = stampReducidoYInfoAccesibilidad(new File(spdfResultCabeceraPie),
            documentoConvertidoPdf, mDatosAccesibilidad, (List) objContent[4]);
      } else {
        // anadir una pagina al principio al newreduced documentoConvertidoPdf
        documentoConvertido1PaginaMas = manipulatePdf(documentoConvertidoPdf.toPath().toString(),
            documentoConvertidoPdf + "1PAG");
        sresultadoFinal = stampReducidoYInfoAccesibilidad(documentoConvertido1PaginaMas,
            new File(spdfResultCabeceraPie), mDatosAccesibilidad, (List) objContent[4]);
      }

      fResultadoFinal = new File(sresultadoFinal);
      dc.setDocumento(new ByteArrayDataSource(Files.readAllBytes(fResultadoFinal.toPath()),
          APPLICATION_PDF_MIME));
      dc.setMimeDocumento(APPLICATION_PDF_MIME);

      // return dc;
    } catch (EeutilException e) {
      throw new EeutilException(
          HA_SIDO_IMPOSIBLE_OBTENER_VISUALIZACION + item.getNombre() + " " + e.getMessage(), e);
    } catch (Exception e) {
      throw new EeutilException(
          HA_SIDO_IMPOSIBLE_OBTENER_VISUALIZACION + item.getNombre() + " " + e.getMessage(), e);
    } finally {
      try {
        if (documentoConvertidoPdf != null && documentoConvertidoPdf.exists()) {
          FileUtils.forceDelete(documentoConvertidoPdf);
        }
      } catch (IOException e) {
        if (documentoConvertidoPdf != null && documentoConvertidoPdf.exists()) {
          throw new EeutilException(ERROR_AL_ELIMINAR_FICHERO_TEMPORAL
              + documentoConvertidoPdf.getAbsolutePath() + " " + e.getMessage(), e);
        }

      }
      try {
        if (documentoConvertido1PaginaMas != null && documentoConvertido1PaginaMas.exists()) {
          FileUtils.forceDelete(documentoConvertido1PaginaMas);
        }
      } catch (IOException e) {
        if (documentoConvertido1PaginaMas != null && documentoConvertido1PaginaMas.exists()) {
          throw new EeutilException(ERROR_AL_ELIMINAR_FICHERO_TEMPORAL
              + documentoConvertido1PaginaMas.getAbsolutePath() + " " + e.getMessage(), e);
        }

      }
      try {
        if (fileIn != null && fileIn.exists()) {
          FileUtils.forceDelete(fileIn);
        }
      } catch (IOException e) {
        if (fileIn != null && fileIn.exists()) {
          throw new EeutilException(
              ERROR_AL_ELIMINAR_FICHERO_TEMPORAL + fileIn.getAbsolutePath() + " " + e.getMessage(),
              e);
        }

      }
      try {
        if (fcabeceraPieVisualizar != null && fcabeceraPieVisualizar.exists()) {
          FileUtils.forceDelete(fcabeceraPieVisualizar);
        }
      } catch (IOException e) {
        if (fcabeceraPieVisualizar != null && fcabeceraPieVisualizar.exists()) {
          throw new EeutilException(ERROR_AL_ELIMINAR_FICHERO_TEMPORAL
              + fcabeceraPieVisualizar.getAbsolutePath() + " " + e.getMessage(), e);
        }

      }
      try {
        if (fResultadoFinal != null && fResultadoFinal.exists()) {
          FileUtils.forceDelete(fResultadoFinal);
        }
      } catch (IOException e) {
        if (fResultadoFinal != null && fResultadoFinal.exists()) {
          throw new EeutilException(ERROR_AL_ELIMINAR_FICHERO_TEMPORAL
              + fResultadoFinal.getAbsolutePath() + " " + e.getMessage(), e);
        }

      }

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
  public DocumentoContenido doVisualizableOriginal(Item item, AfirmaService afirmaService,
      String idAplicacion) throws EeutilException {

    Document doc = null;
    PdfWriter pw = null;
    String pathResult = null;
    File fPathResult = null;
    File fileIn = null;
    File entradaSinSeguridad = null;
    DocumentoContenido dc = new DocumentoContenido();

    try (ByteArrayOutputStream outSalida = new ByteArrayOutputStream();) {


      doc = indexVisualizadorAdder.createVerticalDocument();

      pw = PdfWriter.getInstance(doc, outSalida);
      pw.setLinearPageMode();
      pw.setPdfVersion(PdfWriter.PDF_VERSION_1_7);
      doc.open();

      contentDocumentAdder
          .setIpOO(aplicacionContext.getAplicacionInfo().getPropiedades().get("ip.openoffice"));
      contentDocumentAdder
          .setPortOO(aplicacionContext.getAplicacionInfo().getPropiedades().get("port.openoffice"));

      String[] aResultado =
          workFlowMimeLibreofficeItem(idAplicacion, item, ADDER_CONTENT_PREFFIX, afirmaService);

      // filepath
      fileIn = new File(aResultado[0]);

      /*
       * String mimeFinal = aResultado[1];
       * 
       * // si el mime es de pdf le quitamos la seguridad al fichero. if
       * (mimeFinal.equals("application/pdf")) { // String rutaSinSeguridad =
       * FileUtil.createFilePath("sinSeguridad") + ".pdf";
       * 
       * // quitamos seguridad al pdf original // entradaSinSeguridad =
       * copiarPdfEliminandoSeguridad(fileIn.toPath(), // rutaSinSeguridad); entradaSinSeguridad =
       * fileIn; } // si no es pdf no se hace el proceso de quitar la seguridad puesto que la //
       * conversion no lleva seguridad ya. else { entradaSinSeguridad = fileIn; }
       */
      entradaSinSeguridad = fileIn;

      if (TEXT_TCN_MIME.equals(item.getDocumentoContenido().getMimeDocumento())) {
        pathResult =
            contentDocumentAdder.addContent(Files.readAllBytes(entradaSinSeguridad.toPath()), doc,
                pw, TEXT_TCN_MIME, aplicacionContext.getAplicacionId());
      } else {
        pathResult =
            contentDocumentAdder.addContent(Files.readAllBytes(entradaSinSeguridad.toPath()), doc,
                pw, aResultado[1], aplicacionContext.getAplicacionId());
      }
      fPathResult = new File(pathResult);
      byte[] fileContent = Files.readAllBytes(fPathResult.toPath());

      // byte[] pdfResult = outSalida.toByteArray();

      dc.setBytesDocumento(fileContent);
      dc.setMimeDocumento(APPLICATION_PDF_MIME);

      // return dc;

    } catch (NumberFormatException | DocumentException | EeutilException e) {
      throw new EeutilException(
          HA_SIDO_IMPOSIBLE_OBTENER_VISUALIZACION + item.getNombre() + e.getMessage(), e);
    } catch (Exception e1) {
      // logger.error(ERROR_AL_OBTENER_VISUALIZACION + e1.getMessage(), e1);
      throw new EeutilException(
          HA_SIDO_IMPOSIBLE_OBTENER_VISUALIZACION + item.getNombre() + e1.getMessage(), e1);
    } finally {

      try {

        try {

          if (doc != null)
            doc.close();

          if (pw != null)
            pw.close();

        } catch (Exception e) {
          // sacamos la traza pero no lanzamos excepcion al estar en un finally
          // logger.error(e.getMessage(),e);
        }

        if (fPathResult != null && fPathResult.exists()) {
          // borramos el fichero temporal generado en ContentDocumentAdder.addContent si
          // existe
          FileUtils.forceDelete(new File(pathResult));
        }
        if (fileIn != null && fileIn.exists()) {
          // borramos el fichero temporal generado en ContentDocumentAdder.addContent si
          // existe
          FileUtils.forceDelete(fileIn);
        }
        if (entradaSinSeguridad != null && entradaSinSeguridad.exists()) {
          // borramos el fichero temporal generado en ContentDocumentAdder.addContent si
          // existe
          FileUtils.forceDelete(entradaSinSeguridad);
        }

      } catch (IOException e) {
        throw new EeutilException(
            "Ha sido imposible borrar el fichero " + pathResult + " " + e.getMessage(), e);
      }

    }


    return dc;

  }

  /*
   * public String getRutaLogo() { return rutaLogo; }
   * 
   * 
   * public void setRutaLogo(String rutaLogo) { this.rutaLogo = rutaLogo; }
   */

  /**
   * Workflow para sacar el tipo de mime y guardarlo en CopiaInfo y la ruta del fichero temporal
   * como return
   * 
   * @param copia
   * @param mime
   * @param prefijoFile
   * @return inputPathFile
   * @throws EeutilException
   */
  private String[] workFlowMimeLibreofficeItemGeneric(String idAplicacion, ItemGeneric item,
      String prefijoFile, AfirmaService afirmaService) throws EeutilException {

    String[] aResultado = new String[2];

    String mimeResultado = null;

    String inputPathFile = null;

    String mimeVerificarFirma = null;

    String mimeTika = null;

    InputStream istream;

    try {

      istream = item.getDocumentoContenido().getDocumento().getInputStream();
      byte[] bContenidoDocumento = IOUtils.toByteArray(istream);

      // obtenemos el mime para tika
      mimeTika = MimeUtil.getMimeType(bContenidoDocumento);

      if (validarSiMimeTablaProhibidos(mimeTika)) {
        throw new EeutilException(MSG_ERROR_FORMATO_INVALIDO
            + "Error al visualizarContenidoOriginal, el mime obtenido no es procesable: "
            + mimeTika);
      }

      // vemos si el posible mime es una posible firma
      if (MimeUtil.esMimeTikaPosibleFirma(mimeTika)) {
        aResultado = verificarFirmaYPDFEncriptadoItemGeneric(idAplicacion, mimeTika, prefijoFile,
            item, afirmaService);
        inputPathFile = aResultado[0];
        mimeVerificarFirma = aResultado[1];
      }
      // si no es una posible firma si es pdf verificamos que no esta protegido y si
      // esta generamos el inputPathFile
      else {
        mimeVerificarFirma = mimeTika;

        if (APPLICATION_PDF_MIME.equals(mimeVerificarFirma)) {
          if (PdfEncr.isProtectedPdf(bContenidoDocumento)) {
            throw new EeutilException(
                "Error al visualizar. El fichero pdf tiene password y no se puede procesar");
          }
        }

        inputPathFile = FileUtil.createFilePath(prefijoFile, bContenidoDocumento);
      }

      mimeResultado = obtenerMimeSobreMimeParamMimeTika(mimeVerificarFirma,
          item.getDocumentoContenido().getMimeDocumento());

      boolean esMimePermitido = validarSiMimeTablaPermitidos(mimeResultado);

      // verificamos si el mime esta en nuestra tabla sino lo desechamos y lanzamos
      // excepcion.
      if (!esMimePermitido) {
        throw new EeutilException(MSG_ERROR_FORMATO_INVALIDO
            + "Mime no permitido para la visualizacion de documentos: MIME: " + mimeResultado);
      }
      // ponemos como mime definitivo en el parametro el del resultado
      item.getDocumentoContenido().setMimeDocumento(mimeResultado.toLowerCase());
    } catch (Exception e) {

      // solo si hay excepcion se borra
      if (inputPathFile != null && new File(inputPathFile).exists()) {
        try {
          FileUtils.forceDelete(new File(inputPathFile));
        } catch (IOException t) {
          // logger.error(ERROR_AL_ELIMINAR_FICHERO + entrada.getAbsolutePath(),e);
          throw new EeutilException(ERROR_AL_ELIMINAR_FICHERO_TEMPORAL + inputPathFile, t);
        }
      }

      throw new EeutilException(e.getMessage(), e);
    }

    aResultado[0] = inputPathFile;
    aResultado[1] = mimeResultado;

    return aResultado;
  }

  /**
   * Workflow para sacar el tipo de mime y guardarlo en CopiaInfo y la ruta del fichero temporal
   * como return
   * 
   * @param copia
   * @param mime
   * @param prefijoFile
   * @return inputPathFile
   * @throws EeutilException
   */
  private String[] workFlowMimeLibreofficeItem(String idAplicacion, Item item, String prefijoFile,
      AfirmaService afirmaService) throws EeutilException {

    String[] aResultado = new String[2];

    String inputPathFile = null;

    String mimeVerificarFirma = null;

    String mimeResultado = null;

    String mimeTika = null;

    try {

      byte[] bContenidoDocumento = item.getDocumentoContenido().getBytesDocumento();

      // obtenemos el mime para tika
      mimeTika = MimeUtil.getMimeType(bContenidoDocumento);

      if (validarSiMimeTablaProhibidos(mimeTika)) {
        throw new EeutilException(MSG_ERROR_FORMATO_INVALIDO
            + "Error al visualizarContenidoOriginal, el mime obtenido no es procesable: "
            + mimeTika);
      }

      // vemos si el posible mime es una posible firma
      if (MimeUtil.esMimeTikaPosibleFirma(mimeTika)) {
        aResultado = verificarFirmaYPDFEncriptadoItem(idAplicacion, mimeTika, prefijoFile, item,
            afirmaService);
        inputPathFile = aResultado[0];
        mimeVerificarFirma = aResultado[1];
      }
      // si no es una posible firma si es pdf verificamos que no esta protegido y si
      // esta generamos el inputPathFile
      else {
        mimeVerificarFirma = mimeTika;
        if (APPLICATION_PDF_MIME.equals(mimeVerificarFirma)) {
          if (PdfEncr.isProtectedPdf(bContenidoDocumento)) {
            throw new EeutilException(
                "Error al visualizarContenidoOriginal. El fichero pdf tiene password y no se puede procesar");
          }
        }

        inputPathFile = FileUtil.createFilePath(prefijoFile, bContenidoDocumento);
      }

      mimeResultado = obtenerMimeSobreMimeParamMimeTika(mimeVerificarFirma,
          item.getDocumentoContenido().getMimeDocumento());

      boolean esMimePermitido = validarSiMimeTablaPermitidos(mimeResultado);

      // verificamos si el mime esta en nuestra tabla sino lo desechamos y lanzamos
      // excepcion.
      if (!esMimePermitido) {
        throw new EeutilException(MSG_ERROR_FORMATO_INVALIDO
            + "Mime no permitido para la visualizacion de documentos: MIME: " + mimeResultado);
      }
      // ponemos como mime definitivo en el parametro el del resultado
      item.getDocumentoContenido().setMimeDocumento(mimeResultado.toLowerCase());
    } catch (Exception e) {

      // solo si hay excepcion se borra
      if (inputPathFile != null && new File(inputPathFile).exists()) {
        try {
          FileUtils.forceDelete(new File(inputPathFile));
        } catch (IOException t) {
          // logger.error(ERROR_AL_ELIMINAR_FICHERO + entrada.getAbsolutePath(),e);
          throw new EeutilException(ERROR_AL_ELIMINAR_FICHERO_TEMPORAL + inputPathFile, t);
        }
      }

      throw new EeutilException(e.getMessage(), e);

    }

    aResultado[0] = inputPathFile;
    aResultado[1] = mimeResultado;

    return aResultado;
  }

  private String[] verificarFirmaYPDFEncriptadoItemGeneric(String idAplicacion, String mime,
      String prefijoFile, ItemGeneric item, AfirmaService afirmaService) throws EeutilException {

    String[] aResultado = new String[2];

    String mimeVerificarFirma = null;

    String inputPathFile = null;

    InputStream istream = null;

    try {

      istream = item.getDocumentoContenido().getDocumento().getInputStream();
      byte[] bContenidoDocumento = IOUtils.toByteArray(istream);

      // esto tiene que ir siempre antes de AOSignerFactory.getSigner
      if (APPLICATION_PDF_MIME.equalsIgnoreCase(mime)
          && PdfEncr.isProtectedPdf(bContenidoDocumento)) {
        throw new EeutilException(
            "Error al visualizar. El fichero pdf tiene password y no se puede procesar");
      }
      // comprobamos si el contenido es una firma o no. Esto es nuevo y habra que
      // validarlo.
      // AOSigner aoSigner=new
      // AOSignerWrapperEeutils().wrapperGetSigner(copia.getContenido().getContenido(),copia.getContenido().getTipoMIME());

      inputPathFile = null;

      ResultadoValidacionInfoAfirma validacionFirmaInfo = null;
      boolean esFirma = false;

      try {

        // Si no es ninguno de estos formatos no puede ser una firma.
        if (!IOUtil.esPosibleFormatoFirmaCadesXadesPadesExtendedAnalyzeBytes(bContenidoDocumento)) {
          esFirma = false;
        } else {
          validacionFirmaInfo = afirmaService.validarFirma(idAplicacion,
              Base64.encodeBase64String(bContenidoDocumento), null, null, null, null);
          esFirma = validacionFirmaInfo.isEstado();
          // no es una firma y se procesa como no firma
          if (!esFirma && validacionFirmaInfo.getDetalle() != null
              && validacionFirmaInfo.getDetalle().contains("El formato de la firma no es")) {
            esFirma = false;
          }
          // en cualquier otro caso se cuenta como una firma.
          else {
            esFirma = true;
          }
        }
      }
      // si lanza una excepcion
      catch (EeutilException t) {
        // es un formato no valido de firma (no se reconoce como firma)
        // Error al realizar la peticion a DSSAfirmaVerify HTTP response '413: Request
        // Entity Too Large' when communicating with
        // https://des-afirma.redsara.es/afirmaws/services/DSSAfirmaVerify
        // hemos hecho una personalizacion de excepciones en EEutilException para las
        // excepciones de tipo WebServiceException
        if ((t.getCOD_AFIRMA() != null && t.getCOD_AFIRMA().equals(CodigosError.COD_0003))) {
          esFirma = false;
        } else if ((t.getMSG_AFIRMA() != null
            ? t.getMSG_AFIRMA().indexOf("Request Entity Too Large") != -1
            : false)) {
          // evaluamos si es una posible firma mirando los primeros bytes
          if (IOUtil.esPosibleFormatoFirmaCadesXadesAnalyzeBytes(bContenidoDocumento)) {
            // ERROR es una posible firma
            throw t;
          } else {
            esFirma = false;
          }

        } else {
          esFirma = true;
        }
      }

      // validamos si es firma correcta
      if (esFirma) {
        // le pasamos el contenido de una firma
        InformacionFirmaAfirma infoAfirma = afirmaService.obtenerInformacionFirma(idAplicacion,
            bContenidoDocumento, false, true, true, null);

        // se comprueba si es tcn
        boolean esTcnFirmaXades = XMLUtil.comprobarFirmaXadesEsTcn(bContenidoDocumento,
            infoAfirma.getTipoDeFirma().getTipoFirma());

        if (esTcnFirmaXades) {
          // cambiamos el mime a tcn para preparar la llamada a convertir
          item.getDocumentoContenido().setMimeDocumento(TEXT_TCN_MIME);
          mimeVerificarFirma = TEXT_TCN_MIME;
        } else {
          // do nothing
          if (!TEXT_TCN_MIME.equals(item.getDocumentoContenido().getMimeDocumento())) {
            item.getDocumentoContenido()
                .setMimeDocumento(infoAfirma.getDocumentoFirmado().getTipoMIME());
            mimeVerificarFirma = infoAfirma.getDocumentoFirmado().getTipoMIME();
          }
        }

        inputPathFile =
            FileUtil.createFilePath(prefijoFile, infoAfirma.getDocumentoFirmado().getContenido());
      }

      else {
        inputPathFile = FileUtil.createFilePath(prefijoFile, bContenidoDocumento);
        mimeVerificarFirma = mime;
      }

    } catch (EeutilException | ParserConfigurationException | SAXException | IOException e) {
      throw new EeutilException(e.getMessage(), e);
    } catch (Exception e) {
      throw new EeutilException(e.getMessage(), e);
    } finally {
      if (istream != null)
        try {
          istream.close();
        } catch (IOException e) {
          throw new EeutilException(e.getMessage(), e);
        }
    }

    aResultado[0] = inputPathFile;
    aResultado[1] = mimeVerificarFirma;

    return aResultado;
  }

  private String obtenerMimeSobreMimeParamMimeTika(String mimeTika, String mimeParam)
      throws EeutilException {

    String mimeResultado = null;

    // si el mime es tcn prevalece sobre todo
    if (TEXT_TCN_MIME.equals(mimeParam)) {
      mimeResultado = mimeParam;
    } else {
      if (mimeTika == null || "".equals(mimeTika)) {
        // Error
        if (mimeParam == null || "".equals(mimeParam)) {
          throw new EeutilException("Error, no es posible obtener mime del contenido");
        }
        // sacamos el mime param
        else {
          mimeResultado = mimeParam;
        }
      }
      // si tika es distinto de null
      else {
        if (mimeTika.equals(mimeParam)) {
          mimeResultado = mimeTika;
        } else {
          // si el mimeparam es null
          if (mimeParam == null || "".equals(mimeParam)) {
            mimeResultado = mimeTika;
          }
          // si el mimeparam no es null prevalece el parametro
          else {
            mimeResultado = mimeTika.toLowerCase();
            // si el mime de tika no es valido nos quedamos con el del param
            if (!validarSiMimeTablaPermitidos(mimeResultado)) {
              mimeResultado = mimeParam;
              if (!validarSiMimeTablaPermitidos(mimeResultado)) {
                throw new EeutilException("Error, el mime no es imprimible por libreoffice: "
                    + mimeResultado + " tika: " + mimeTika + " param " + mimeParam);
              }

            }

          }
        }

      }
    }

    if (!validarSiMimeTablaPermitidos(mimeResultado)) {
      throw new EeutilException("Error, el mime no es imprimible por libreoffice: " + mimeResultado
          + " tika: " + mimeTika + " param " + mimeParam);
    }

    return mimeResultado;

  }

  private String[] verificarFirmaYPDFEncriptadoItem(String idAplicacion, String mime,
      String prefijoFile, Item item, AfirmaService afirmaService) throws EeutilException {

    String[] aResultado = new String[2];

    String mimeVerificarFirma = null;

    String inputPathFile = null;

    try {

      byte[] bContenidoDocumento = item.getDocumentoContenido().getBytesDocumento();

      // esto tiene que ir siempre antes de AOSignerFactory.getSigner
      if (APPLICATION_PDF_MIME.equalsIgnoreCase(mime)
          && PdfEncr.isProtectedPdf(bContenidoDocumento)) {
        throw new EeutilException(
            "Error al visualizarContenidoOriginal. El fichero pdf tiene password y no se puede procesar");
      }
      // comprobamos si el contenido es una firma o no. Esto es nuevo y habra que
      // validarlo.
      // AOSigner aoSigner=new
      // AOSignerWrapperEeutils().wrapperGetSigner(copia.getContenido().getContenido(),copia.getContenido().getTipoMIME());

      ResultadoValidacionInfoAfirma validacionFirmaInfo = null;
      boolean esFirma = false;

      try {
        // Si no es ninguno de estos formatos no puede ser una firma.
        if (!IOUtil.esPosibleFormatoFirmaCadesXadesPadesExtendedAnalyzeBytes(bContenidoDocumento)) {
          esFirma = false;
        } else {
          validacionFirmaInfo = afirmaService.validarFirma(idAplicacion,
              Base64.encodeBase64String(bContenidoDocumento), null, null, null, null);
          esFirma = validacionFirmaInfo.isEstado();
          // no es una firma y se procesa como no firma
          if (!esFirma && validacionFirmaInfo.getDetalle() != null
              && validacionFirmaInfo.getDetalle().contains("El formato de la firma no es")) {
            esFirma = false;
          }
          // en cualquier otro caso se cuenta como una firma.
          else {
            esFirma = true;
          }
        }
      }
      // si lanza una excepcion
      catch (EeutilException t) {
        // es un formato no valido de firma (no se reconoce como firma)
        // Error al realizar la peticion a DSSAfirmaVerify HTTP response '413: Request
        // Entity Too Large' when communicating with
        // https://des-afirma.redsara.es/afirmaws/services/DSSAfirmaVerify
        // hemos hecho una personalizacion de excepciones en EEutilException para las
        // excepciones de tipo WebServiceException
        if ((t.getCOD_AFIRMA() != null && t.getCOD_AFIRMA().equals(CodigosError.COD_0003))) {
          esFirma = false;
        } else if ((t.getMSG_AFIRMA() != null
            ? t.getMSG_AFIRMA().indexOf("Request Entity Too Large") != -1
            : false)) {
          // evaluamos si es una posible firma mirando los primeros bytes
          if (IOUtil.esPosibleFormatoFirmaCadesXadesAnalyzeBytes(bContenidoDocumento)) {
            // ERROR es una posible firma
            throw t;
          } else {
            esFirma = false;
          }

        } else {
          esFirma = true;
        }
      }

      // validamos si es firma correcta
      if (esFirma) {
        // le pasamos el contenido de una firma
        InformacionFirmaAfirma infoAfirma = afirmaService.obtenerInformacionFirma(idAplicacion,
            bContenidoDocumento, false, true, true, null);

        // se comprueba si es tcn
        boolean esTcnFirmaXades = XMLUtil.comprobarFirmaXadesEsTcn(bContenidoDocumento,
            infoAfirma.getTipoDeFirma().getTipoFirma());

        if (esTcnFirmaXades) {
          // cambiamos el mime a tcn para preparar la llamada a convertir
          item.getDocumentoContenido().setMimeDocumento("text/tcn");
          mimeVerificarFirma = "text/tcn";
        } else {
          // do nothing
          if (!"text/tcn".equals(item.getDocumentoContenido().getMimeDocumento())) {
            item.getDocumentoContenido()
                .setMimeDocumento(infoAfirma.getDocumentoFirmado().getTipoMIME());
            mimeVerificarFirma = infoAfirma.getDocumentoFirmado().getTipoMIME();
          }
        }

        inputPathFile =
            FileUtil.createFilePath(prefijoFile, infoAfirma.getDocumentoFirmado().getContenido());
      }

      else {
        inputPathFile = FileUtil.createFilePath(prefijoFile, bContenidoDocumento);
        mimeVerificarFirma = mime;
      }

    } catch (EeutilException e) {
      throw new EeutilException(e.getMessage(), e);
    } catch (ParserConfigurationException e) {
      throw new EeutilException(e.getMessage(), e);
    } catch (SAXException e) {
      throw new EeutilException(e.getMessage(), e);
    } catch (IOException e) {
      throw new EeutilException(e.getMessage(), e);
    } catch (Exception e) {
      throw new EeutilException(e.getMessage(), e);
    }

    aResultado[0] = inputPathFile;
    aResultado[1] = mimeVerificarFirma;

    return aResultado;
  }

  public boolean validarSiMimeTablaPermitidos(String mimeResultado) {
    if (ConfigureLibreofficeMime.getObjLibreofficeFormats(mimeResultado)) {
      return true;
    } else {
      return false;
    }
  }

  public boolean validarSiMimeTablaProhibidos(String mimeResultado) {
    if (ConfigureLibreofficeMime.getObjLibreofficeProhibitedFormats(mimeResultado)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @throws IOException
   * @throws DocumentException
   * @throws FileNotFoundException
   */
  private void fusionarDocumentoBlancoReducido(String rutaOrigen, String rutaDestino,
      String rutaResultado, HashMap<String, String> mAccesibilidad, List aBookmarks)
      throws IOException, DocumentException, FileNotFoundException {

    // PdfReader reader = null;
    // PdfReader reader2 = null;
    FileOutputStream fos = null;
    PdfStamper stamper = null;

    try (PdfReader reader = new PdfReader(rutaOrigen);
        PdfReader reader2 = new PdfReader(rutaDestino);) {

      // reader = new PdfReader(rutaOrigen);
      // reader2 = new PdfReader(rutaDestino);

      // boolean ponerUnaMas=false;
      // if(reader.getNumberOfPages()<reader2.getNumberOfPages())
      // {
      // ponerUnaMas=true;
      // }

      // PdfReader reader2 = new PdfReader(formulario+"newreduced.pdf");
      // PdfReader reader = new PdfReader(formulario+"copiaSimpleFirma.pdf");
      // Create a stamper
      fos = new FileOutputStream(rutaResultado);
      stamper = new PdfStamper(reader, fos, PdfWriter.VERSION_1_7);
      stamper.setViewerPreferences(PdfWriter.DisplayDocTitle);

      // Anadimos al stamp los metadatos de title y lang
      // si el titulo del pdf origen existe se anade al diccionario
      if (mAccesibilidad.get(TITLE_PARAM) != null) {

        addTitleXMPMetadata(mAccesibilidad, stamper);

      }

      if (mAccesibilidad.get(LANG_PARAM) != null && stamper.getReader().getCatalog() != null) {
        stamper.getReader().getCatalog().put(PdfName.LANG,
            new PdfString(mAccesibilidad.get(LANG_PARAM)));
      }

      if (aBookmarks != null && !aBookmarks.isEmpty()) {
        stamper.getWriter().setOutlines(aBookmarks);
      }

      // Create an imported page to be inserted
      PdfImportedPage page = null;

      // Caso en el que se a�ada al reducido en este momento la pagina en blanco y no anteriormente
      // (con las rotaciones se anade anteriormente)
      if (reader2.getNumberOfPages() > reader.getNumberOfPages()) {
        page = stamper.getImportedPage(reader2, 1);
        stamper.insertPage(1, PageSize.A4);
        stamper.getUnderContent(1).addTemplate(page, 0, 0);
        fusionarPaginasBlancasReducidas(reader2, 1, page, stamper);
      }


      for (int i = 2; i <= reader.getNumberOfPages(); i++) {

        // pasamos de ella por que es la pagina de firma
        // if(reader.getNumberOfPages()==i && ponerUnaMas)
        // {
        // continue;
        // }

        page = stamper.getImportedPage(reader2, i);
        /*
         * int orientacion=new EeUtilFirmaServiceBusiness().obtenerOrientacionPagina(reader2,i);
         * System.out.println("pagina"+i+" "+ orientacion);
         * //stamper.getUnderContent(i).addTemplate(page, 0, 0);
         * 
         * if(orientacion==PageFormat.PORTRAIT) { stamper.getUnderContent(i).addTemplate(page, 0,
         * 0); } else { Rectangle pageSize = reader2.getPageSize(i);
         * stamper.getUnderContent(i).addTemplate(page, 0, 1, -1, 0,
         * pageSize.getHeight(),pageSize.getWidth()); }
         * 
         * 
         * 
         * 
         * }catch(Exception e) {
         * 
         * }
         */

        fusionarPaginasBlancasReducidas(reader2, i, page, stamper);

      }

      // pasamos de ella por que es la pagina de firma
      // if(ponerUnaMas)
      // {
      // page = stamper.getImportedPage(reader2, reader2.getNumberOfPages());
      // stamper.insertPage(reader2.getNumberOfPages(),reader2.getPageSize(reader2.getNumberOfPages()));
      // //stamper.getUnderContent(reader2.getNumberOfPages()).addTemplate(page, 0, 0);
      // fusionarPaginasBlancasReducidas(reader2,reader2.getNumberOfPages(),page,stamper);
      // }

    } finally {
      if (stamper != null)
        stamper.close();
      if (fos != null)
        fos.close();
      // if (reader != null)
      // reader.close();
      // if (reader2 != null)
      // reader2.close();
    }

  }

  public void fusionarPaginasBlancasReducidas(PdfReader pdfReader, int i, PdfImportedPage page,
      PdfStamper stamper) {

    Rectangle pagesize = pdfReader.getPageSizeWithRotation(i);
    float oWidth = pagesize.getWidth();
    float oHeight = pagesize.getHeight();
    int rotation = pagesize.getRotation();
    // float scale = getScale(oWidth, oHeight);
    // la escala siempre es 1.0f ya que siempre estamos usando paginas de tamano
    // 10x297mm o 297x210mm (595x842 o 842x595)
    float scale = 1.0f;
    float scaledWidth = oWidth * scale;
    float scaledHeight = oHeight * scale;

    AffineTransform transform = new AffineTransform(scale, 0, 0, scale, 0, 0);
    switch (rotation) {
      case 0:
        stamper.getUnderContent(i).addTemplate(page, scale, 0, 0, scale, 0, 0);
        break;
      case 90:
        AffineTransform rotate90 = new AffineTransform(0, -1f, 1f, 0, 0, scaledHeight);
        rotate90.concatenate(transform);
        stamper.getUnderContent(i).addTemplate(page, 0, -1f, 1f, 0, 0, scaledHeight);
        break;
      case 180:
        AffineTransform rotate180 = new AffineTransform(-1f, 0, 0, -1f, scaledWidth, scaledHeight);
        rotate180.concatenate(transform);
        stamper.getUnderContent(i).addTemplate(page, -1f, 0, 0, -1f, scaledWidth, scaledHeight);
        break;
      case 270:
        AffineTransform rotate270 = new AffineTransform(0, 1f, -1f, 0, scaledWidth, 0);
        rotate270.concatenate(transform);
        stamper.getUnderContent(i).addTemplate(page, 0, 1f, -1f, 0, scaledWidth, 0);
        break;
      default:
        stamper.getUnderContent(i).addTemplate(page, scale, 0, 0, scale, 0, 0);
    }
  }

  private void addTitleXMPMetadata(HashMap<String, String> mAccesibilidad, PdfStamper stamper)
      throws IOException {
    // ByteArrayOutputStream os = null;
    XmpWriter xmp = null;

    try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {

      // os = new ByteArrayOutputStream();

      xmp = new XmpWriter(os);
      XmpSchema dc = new DublinCoreSchema();
      XmpArray array = new XmpArray(XmpArray.ALTERNATIVE);
      array.add(mAccesibilidad.get(TITLE_PARAM));
      // SetProperty(TITLE, array);
      dc.setProperty(DublinCoreSchema.TITLE, mAccesibilidad.get(TITLE_PARAM));
      // XmpArray subject = new XmpArray(XmpArray.UNORDERED);
      // subject.add("Hello World");
      // subject.add("XMP & Metadata");
      // subject.add("Metadata");
      // dc.setProperty(DublinCoreSchema.SUBJECT, subject);
      xmp.addRdfDescription(dc);
      PdfSchema pdf = new PdfSchema();
      // pdf.setProperty(PdfSchema.KEYWORDS, "Hello World, XMP, Metadata");
      pdf.setProperty(PdfSchema.VERSION, "1.7");
      xmp.addRdfDescription(pdf);
      if (xmp != null)
        xmp.close();
      stamper.setXmpMetadata(os.toByteArray());
    }
  }

  /**
   * @param pdfReduced
   * @param pdfResult
   * @return
   * @throws IOException
   * @throws DocumentException
   * @throws FileNotFoundException
   */
  private String stampReducidoYInfoAccesibilidad(File pdfReduced, File pdfResult,
      HashMap<String, String> mAccesibilidad, List aBookmarks) throws EeutilException {
    String outputResultado = FileUtil.createFilePath("resultado");

    try {
      fusionarDocumentoBlancoReducido(pdfResult.getAbsolutePath(), pdfReduced.getAbsolutePath(),
          outputResultado, mAccesibilidad, aBookmarks);
    } catch (FileNotFoundException e) {
      throw new EeutilException(e.getMessage(), e);
    } catch (IOException e) {
      throw new EeutilException(e.getMessage(), e);
    } catch (DocumentException e) {
      throw new EeutilException(e.getMessage(), e);
    }

    return outputResultado;
  }

  public File manipulatePdf(String src, String dest) throws IOException, DocumentException {

    // PdfReader reader = null;
    // FileOutputStream fos = null;
    PdfStamper stamper = null;

    File file = new File(dest);

    try (PdfReader reader = new PdfReader(src);
        FileOutputStream fos = new FileOutputStream(file);) {
      // reader = new PdfReader(src);



      // fos = new FileOutputStream(file);

      stamper = new PdfStamper(reader, new FileOutputStream(dest), PdfWriter.VERSION_1_7);

      stamper.getReader().getCatalog().put(PdfName.LANG, new PdfString("es"));

      List<Map<String, Object>> bookmarks = SimpleBookmark.getBookmarkList(reader);
      stamper.getWriter().setOutlines(bookmarks);

      stamper.getReader().getCatalog().put(PdfName.TITLE, reader.getCatalog().get(PdfName.TITLE));

      stamper.insertPage(1, PageSize.A4);

      return new File(dest);
    } finally {

      if (stamper != null)
        stamper.close();
      // if(fos!=null) fos.close();
      // if(reader!=null) reader.close();
    }
  }

  /*
   * public static void main(String args[]) throws EeutilException, IOException { //
   * System.out.println("HOLA"); // System.out.println(new
   * VisualizacionDocumentoServiceImpl().obtenerMimeSobreMimeParamMimeTika("", "mimeParam")); //
   * System.out.println(new
   * VisualizacionDocumentoServiceImpl().obtenerMimeSobreMimeParamMimeTika(null, "mimeParam")); //
   * System.out.println(new
   * VisualizacionDocumentoServiceImpl().obtenerMimeSobreMimeParamMimeTika("mikeTika", "")); //
   * System.out.println(new
   * VisualizacionDocumentoServiceImpl().obtenerMimeSobreMimeParamMimeTika("mikeTika", null)); //
   * System.out.println(new
   * VisualizacionDocumentoServiceImpl().obtenerMimeSobreMimeParamMimeTika("mikeTika",
   * "mimeParam")); // System.out.println(new
   * VisualizacionDocumentoServiceImpl().obtenerMimeSobreMimeParamMimeTika(null, "")); //
   * System.out.println(new
   * VisualizacionDocumentoServiceImpl().obtenerMimeSobreMimeParamMimeTika("", null)); //
   * System.out.println(new
   * VisualizacionDocumentoServiceImpl().obtenerMimeSobreMimeParamMimeTika(null, null)); //
   * System.out.println("ADIOS");
   * 
   * PdfReader reader = new PdfReader("D:/newreduced166116271924038.pdf");
   * 
   * File file = new File("D:/newreduced166116271924038_KK.pdf");
   * 
   * FileOutputStream fos = new FileOutputStream(file);
   * 
   * PdfStamper stamper = new PdfStamper(reader, fos);
   * 
   * stamper.insertPage(1, PageSize.A4);
   * 
   * stamper.close(); fos.close(); reader.close();
   * 
   * }
   */
}
