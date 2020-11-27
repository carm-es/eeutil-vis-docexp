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

package es.mpt.dsic.inside.fop.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import es.mpt.dsic.inside.fop.model.documento.DocumentoEniConMAdicionales;
import es.mpt.dsic.inside.pdf.converter.PdfConverter;
import es.mpt.dsic.inside.pdf.exception.PdfConversionException;
import es.mpt.dsic.inside.security.context.AplicacionContext;
import es.mpt.dsic.inside.utils.file.FileUtil;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenido;

@Component
public class TemplateConverter {

  @Autowired(required = false)
  AplicacionContext aplicacionContext;

  private static final String CONTENT_DATA_TEMP = "Content_data";

  protected final static Log logger = LogFactory.getLog(TemplateConverter.class);

  @Autowired
  PdfConverter pdfConverter;

  /**
   * Converts a ProjectTeam object to a PDF file.
   * 
   * @param team the ProjectTeam object
   * @param xslt the stylesheet file
   * @param pdf the target PDF file
   * @throws Exception
   * @throws JAXBException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public DocumentoContenido convertDocumentToPdf(DocumentoEniConMAdicionales docEniAdicionales,
      byte[] jrxml) throws Exception {
    DocumentoContenido retorno = new DocumentoContenido();

    JasperReport report = JasperCompileManager.compileReport(new ByteArrayInputStream(jrxml));

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("data", docEniAdicionales);

    JasperPrint print = JasperFillManager.fillReport(report, params, new JREmptyDataSource());

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    JasperExportManager.exportReportToPdfStream(print, bout);

    // conversion del contenido a pdf
    byte[] contenidoConvertido =
        convertContent(docEniAdicionales.getDocumento().getContenido().getValorBinario());

    ByteArrayOutputStream pdfSalida = new ByteArrayOutputStream();
    PDFMergerUtility merger = new PDFMergerUtility();
    merger.setDestinationStream(pdfSalida);
    merger.addSource(new ByteArrayInputStream(bout.toByteArray()));
    merger.addSource(new ByteArrayInputStream(contenidoConvertido));
    merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());

    retorno.setBytesDocumento(pdfSalida.toByteArray());
    retorno.setMimeDocumento("application/pdf");

    return retorno;
  }

  private byte[] convertContent(byte[] contenido) {
    byte[] retorno = null;
    File converted = null;
    try {
      File fileIn = createFile(contenido);

      String ipOO = aplicacionContext.getAplicacionInfo().getPropiedades().get("ip.openoffice");
      String portOO = aplicacionContext.getAplicacionInfo().getPropiedades().get("port.openoffice");

      converted = pdfConverter.convertir(ipOO, portOO, fileIn, null);

      retorno = FileUtils.readFileToByteArray(converted);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (PdfConversionException e) {
      e.printStackTrace();
    } finally {
      try {
        FileUtils.forceDelete(converted);
      } catch (IOException e) {
        logger.error("Error al eliminar fichero temporal:" + converted.getAbsolutePath());
      }
    }
    return retorno;
  }

  /**
   * Escribe un fichero con el contenido y devuelve la ruta de dicho fichero.
   * 
   * @param content
   * @return
   * @throws IOException
   */
  private File createFile(byte[] content) throws IOException {
    FileOutputStream fos = null;
    try {
      String filePathIn = FileUtil.createFilePath(CONTENT_DATA_TEMP);
      fos = new FileOutputStream(filePathIn);
      fos.write(content);
      fos.close();
      return new File(filePathIn);
    } catch (IOException e) {
      throw e;
    } finally {
      if (fos != null) {
        fos.close();
      }
    }
  }

}
