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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import es.mpt.dsic.inside.pdf.PdfConversion;
import es.mpt.dsic.inside.pdf.PdfOptions;
import es.mpt.dsic.inside.pdf.converter.PdfConverter;
import es.mpt.dsic.inside.pdf.exception.PdfConversionException;
import es.mpt.dsic.inside.utils.file.FileUtil;
import es.mpt.dsic.inside.visualizacion.exception.ContentNotAddedException;

@Service
@Scope(value = "prototype")
public class ContentDocumentAdder {

  protected final static Log logger = LogFactory.getLog(ContentDocumentAdder.class);

  @Autowired
  PdfConversion pdfConversion;

  @Autowired
  PdfConverter pdfConverter;

  private static String ADDER_CONTENT_PREFFIX = "adderContent";

  private String ipOO;
  private String portOO;

  public String getIpOO() {
    return ipOO;
  }

  public void setIpOO(String ipOO) {
    this.ipOO = ipOO;
  }

  public String getPortOO() {
    return portOO;
  }

  public void setPortOO(String portOO) {
    this.portOO = portOO;
  }


  public void addContent(byte[] document, Document doc, PdfWriter pw)
      throws ContentNotAddedException {
    FileInputStream fis = null;
    File fileIn = null;
    File converted = null;
    String pathResult = null;
    try {
      pathResult = FileUtil.createFilePath(ADDER_CONTENT_PREFFIX);

      fileIn = createFile(document);

      converted = pdfConverter.convertir(ipOO, portOO, fileIn, null);

      fis = new FileInputStream(converted);
      PdfReader reader = new PdfReader(fis);

      PdfOptions options = PdfOptions.createDefault();
      options.setPrintPageNumbers(false);
      options.setPagePositionY(40);
      options.setPagePositionX(60);

      pdfConversion.addPdfScaled(doc, converted, pathResult, pw, reader, options, null);

    } catch (RuntimeException | PdfConversionException | DocumentException | IOException e) {
      throw new ContentNotAddedException("No se puede añadir el contenido ", e);
    } finally {
      try {
        try {
          FileUtils.forceDelete(fileIn);
        } catch (Exception e) {
          logger.warn("Error al eliminar fichero temporal:" + fileIn.getAbsolutePath());
        }
        try {
          FileUtils.forceDelete(new File(pathResult));
        } catch (Exception e) {
          logger.warn("Error al eliminar fichero temporal:" + pathResult);
        }
        try {
          FileUtils.forceDelete(converted);
        } catch (Exception e) {
          logger.warn("Error al eliminar fichero temporal:" + pathResult);
        }
        if (fis != null) {
          fis.close();
        }
      } catch (IOException e) {
        throw new ContentNotAddedException("No se puede añadir el contenido ", e);
      }
    }

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
      String filePathIn = FileUtil.createFilePath(ADDER_CONTENT_PREFFIX);
      fos = new FileOutputStream(filePathIn);
      fos.write(content);
      fos.close();
      return new File(filePathIn);
    } catch (RuntimeException e) {
      throw e;
    } finally {
      if (fos != null) {
        fos.close();
      }
    }
  }

}
