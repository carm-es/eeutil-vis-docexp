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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSmartCopy;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.SimpleBookmark;
import com.lowagie.text.xml.xmp.DublinCoreSchema;
import com.lowagie.text.xml.xmp.PdfSchema;
import com.lowagie.text.xml.xmp.XmpArray;
import com.lowagie.text.xml.xmp.XmpSchema;
import com.lowagie.text.xml.xmp.XmpWriter;

import es.mpt.dsic.inside.configure.ConfigureRestInfo;
import es.mpt.dsic.inside.pdf.NewPdfConversion;
import es.mpt.dsic.inside.pdf.PdfConversion;
import es.mpt.dsic.inside.pdf.PdfOptions;
import es.mpt.dsic.inside.pdf.converter.PdfConverter;
import es.mpt.dsic.inside.pdf.file.StamperWrapper;
import es.mpt.dsic.inside.utils.exception.EeutilException;
import es.mpt.dsic.inside.utils.file.FileUtil;
import es.mpt.dsic.inside.visualizacion.exception.ContentNotAddedException;

@Service
@Scope(value = "prototype")
public class ContentDocumentAdder {

  private static final String ERROR_AL_ELIMINAR_FICHERO_TEMPORAL =
      "Error al eliminar fichero temporal:";

  protected static final Log logger = LogFactory.getLog(ContentDocumentAdder.class);

  @Autowired
  PdfConversion pdfConversion;

  @Autowired
  PdfConverter pdfConverter;

  @Autowired
  NewPdfConversion newPdfConversion;

  private static final String ADDER_CONTENT_PREFFIX = "adderContent";

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

  /**
   * 
   * @param document
   * @param doc
   * @param pw
   * @return devuelve el path del fichero generado.
   * @throws ContentNotAddedException
   */
  public String addContent(byte[] document, Document doc, PdfWriter pw, String mimeType,
      String idApp) throws EeutilException {
    File fileIn = null;
    File converted = null;

    String strFilePathResult = null;

    PdfReader reader = null;
    try {
      strFilePathResult = FileUtil.createFilePath(ADDER_CONTENT_PREFFIX);
      fileIn = createFile(document);

      converted = pdfConverter.convertir(ipOO, portOO, fileIn, mimeType, idApp,
          ConfigureRestInfo.getBaseUrlRestIgae(), ConfigureRestInfo.getTokenNameRestIgae(),
          ConfigureRestInfo.getTokenValueRestIgae());


      try (FileInputStream fis = new FileInputStream(converted);) {
        reader = new PdfReader(fis);

        PdfOptions options = PdfOptions.createDefault();
        options.setPrintPageNumbers(false);
        options.setPagePositionY(40);
        options.setPagePositionX(60);

        String sTitle = reader.getInfo().get(PdfName.TITLE.toString()) == null ? "Reporte EEUTILS"
            : reader.getInfo().get(PdfName.TITLE.toString());
        String sLanguage = reader.getCatalog().getAsString(PdfName.LANG) == null ? "es"
            : reader.getCatalog().getAsString(PdfName.LANG).toString();
        List aBookMarks = SimpleBookmark.getBookmarkList(reader);

        pdfConversion.addPdfScaled(doc, converted, strFilePathResult, pw, reader, options, null,
            sTitle, sLanguage, aBookMarks);
        pw.open();
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

    } catch (RuntimeException | IOException | EeutilException e) {
      throw new EeutilException(e.getMessage(), e);
    } finally {

      try {
        if (fileIn != null && fileIn.exists()) {
          FileUtils.forceDelete(fileIn);
        }
      } catch (Exception e) {
        logger.error(ERROR_AL_ELIMINAR_FICHERO_TEMPORAL + fileIn.getAbsolutePath(), e);
        throw new EeutilException(ERROR_AL_ELIMINAR_FICHERO_TEMPORAL + fileIn.getAbsolutePath(), e);
      }

      if (reader != null)
        reader.close();
      /*
       * try { FileUtils.forceDelete(new File(pathResult)); } catch (Exception e) {
       * logger.error(ERROR_AL_ELIMINAR_FICHERO_TEMPORAL + pathResult); }
       */ try {
        if (converted != null && converted.exists()) {
          FileUtils.forceDelete(converted);
        }
      } catch (Exception e) {
        logger.error(ERROR_AL_ELIMINAR_FICHERO_TEMPORAL + converted, e);
        throw new EeutilException(ERROR_AL_ELIMINAR_FICHERO_TEMPORAL + converted, e);
      }

    }

    return strFilePathResult;

  }

  /**
   * Convierte un Documento a PDF y lo redimensiona.
   * 
   * @param inputStreamDocument InputStream del documento a tratar.
   * @param idApp Id de la aplicaci�n para realizar la llamada a obtenerInformacionFirma.
   * @return File Documento PDF resultante. Ojo, no se borra el fichero, se necesitara para mas
   *         adelante.
   * @throws EeutilException
   */
  public Object[] convertContent(File fileIn, String idApp, String mimeType)
      throws EeutilException {
    File converted = null;
    File fileFixedRotations = null;
    // File fRescaled= null;
    Object[] objConverted = null;
    StamperWrapper stamperWrapper = null;
    try {
      converted = pdfConverter.convertir(ipOO, portOO, fileIn, mimeType, idApp,
          ConfigureRestInfo.getBaseUrlRestIgae(), ConfigureRestInfo.getTokenNameRestIgae(),
          ConfigureRestInfo.getTokenValueRestIgae());

      // fileFixedRotations= newPdfConversion.fixedFilesWithRotations(converted);

      fileFixedRotations = converted;

      stamperWrapper = new StamperWrapper();
      String fileOut = stamperWrapper.createFilePrefix(NewPdfConversion.REDUCED_PREFIX, ".pdf");

      stamperWrapper.createStamperWrapper(fileFixedRotations, fileOut);

      objConverted = newPdfConversion.resizePdf(stamperWrapper, 50, 10, -1);

      // fRescaled= (File) objConverted[0];

    } catch (RuntimeException | EeutilException e) {
      throw new EeutilException("No se puede a�adir el contenido " + e.getMessage(), e);
    } finally {
      try {
        if (fileIn != null && fileIn.exists()) {
          FileUtils.forceDelete(fileIn);
        }
        if (fileFixedRotations != null && fileFixedRotations.exists()) {
          FileUtils.forceDelete(fileFixedRotations);
        }
      } catch (IOException e) {
        // logger.error(ERROR_AL_ELIMINAR_FICHERO + pdfReduced.getAbsolutePath(),e);
        throw new EeutilException(
            "ERROR_AL_ELIMINAR_FICHERO " + (fileIn != null ? fileIn.getAbsolutePath() : ""), e);
      }
    }

    return objConverted;
  }


  public byte[] mergePdfs(byte[] primero, FileInputStream segundo, Document docSalida,
      String sTitle, String sLanguage, List aBookmarks, List<Integer> listaOrientacionPaginas)
      throws EeutilException {

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PdfReader reader = null;
    PdfReader reader2 = null;

    try {

      docSalida.setPageSize(PageSize.A4);

      PdfCopy copy = new PdfSmartCopy(docSalida, out);
      docSalida.open();

      reader = new PdfReader(primero);

      docSalida.addTitle(sTitle);

      addTitleXMPMetadata(sTitle, copy);


      for (int i = 0; i < reader.getNumberOfPages();) {
        copy.addPage(copy.getImportedPage(reader, ++i));
      }

      // TODO: copy.addDocument(reader);


      // crearPaginasVaciasTodasPaginas(listaOrientacionPaginas, docSalida);

      reader2 = new PdfReader(segundo);

      for (int i = 1; i <= reader2.getNumberOfPages(); i++) {


        Rectangle rectangle = reader2.getPageSizeWithRotation(i);

        if (rectangle.getHeight() >= rectangle.getWidth()) {
          copy.addPage(PageSize.A4, 1);
        } else {
          copy.addPage(PageSize.A4.rotate(), 1);
        }


        // copy.addPage(copy.getImportedPage(reader2, ++i));

      }

      // TODO: copy.addDocument(reader2);


      copy.getExtraCatalog().put(PdfName.LANG, new PdfString(sLanguage));

      if (aBookmarks != null && !aBookmarks.isEmpty()) {

        // modificar los bookmarks en visualizar para que los atributos Page del hashmap que tienen
        // valor "1 Fit" ... sume el numero de paginas
        // equivalentes a las paginas que se generan de expedientes.
        try {
          add1PageBookMark(aBookmarks);
        } catch (Exception e) {
          // Si obtenemos una excepcion con la adicion de 1 de las paginas no pasa nada
        }
        copy.setOutlines(aBookmarks);
      }


      out.flush();

    } catch (NoClassDefFoundError e) {
      if (e.getMessage().contains("bouncycastle")) {

        // logger.error("El fichero esta protegido y no ha podido abrirse.");
        throw new EeutilException(
            "El fichero esta protegido y no ha podido abrirse." + e.getMessage(), e);
      } else {

        throw new EeutilException(e.getMessage(), e);
      }

    }

    catch (Exception e) {
      throw new EeutilException(
          "Se ha producido un error en la contatenacion de PDFs." + " " + e.getMessage(), e);
    } finally {

      if (reader != null)
        reader.close();
      if (reader2 != null)
        reader2.close();
      if (docSalida != null && docSalida.isOpen())
        docSalida.close();

      try {
        if (out != null)
          out.close();
      } catch (IOException e) {
        throw new EeutilException(e.getMessage(), e);
      }
    }

    return out.toByteArray();
  }



  /**
   * Escribe un fichero con el contenido y devuelve la ruta de dicho fichero.
   * 
   * @param content
   * @return
   * @throws IOException
   */
  public File createFile(byte[] content) throws EeutilException {

    try {

      String filePathIn = FileUtil.createFilePath(ADDER_CONTENT_PREFFIX);
      try (FileOutputStream fos = new FileOutputStream(filePathIn);) {
        fos.write(content);
        return new File(filePathIn);
      }

    } catch (IOException e) {
      throw new EeutilException(e.getMessage(), e);
    }

  }


  private void addTitleXMPMetadata(String sTitle, PdfCopy copy) throws IOException {
    ByteArrayOutputStream os = null;
    XmpWriter xmp = null;

    try {

      os = new ByteArrayOutputStream();


      xmp = new XmpWriter(os);
      XmpSchema dc = new DublinCoreSchema();
      XmpArray array = new XmpArray(XmpArray.ALTERNATIVE);
      array.add(sTitle);
      // SetProperty(TITLE, array);
      dc.setProperty(DublinCoreSchema.TITLE, sTitle);
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
      copy.setXmpMetadata(os.toByteArray());
    } finally {

      if (os != null)
        os.close();

    }
  }

  /****
   * Anade una pagina a los atributos Page (ej Page="1 Fit" x el hecho de que se crea una pagina
   * nueva para visualizar)
   * 
   * @param aBookMarks
   */
  private void add1PageBookMark(List aBookMarks) {
    if (aBookMarks != null && !aBookMarks.isEmpty()) {
      Iterator itBookMark = aBookMarks.iterator();

      while (itBookMark.hasNext()) {
        Object elementI = itBookMark.next();

        elementHashMapModifyPagePlusOne(elementI);


      }


    }



  }

  private void elementHashMapModifyPagePlusOne(Object elementI) {
    if (elementI instanceof HashMap) {
      Map mBookMarkLevel2 = (Map) elementI;

      if (mBookMarkLevel2 != null && !mBookMarkLevel2.isEmpty()) {
        Set setBookMarkLevel2 = mBookMarkLevel2.entrySet();

        for (Object mValue : mBookMarkLevel2.entrySet()) {
          Map.Entry mEntry = (Entry) mValue;


          if (mEntry.getValue() instanceof List) {
            List lEntryArray = (List) mEntry.getValue();

            if (lEntryArray != null && !lEntryArray.isEmpty()) {
              for (Object objArray : lEntryArray) {
                if (objArray instanceof HashMap) {
                  elementHashMapModifyPagePlusOne(objArray);
                }
              }
            }
          }

          if (mEntry.getValue() instanceof HashMap) {
            elementHashMapModifyPagePlusOne(mEntry);
          }


          else if (mEntry.getValue() instanceof String) {
            if (mEntry.getKey().equals("Page")) {
              mEntry.setValue(add1StringPage((String) mEntry.getValue()));
            }
          }

        }

      }
    }
  }


  private String add1StringPage(String valueMap) {
    int index = valueMap.indexOf(" ");

    String parteNumerica = null;
    String parteRestante = null;

    parteNumerica = valueMap.substring(0, valueMap.indexOf(" "));
    parteRestante = valueMap.substring(valueMap.indexOf(" "), valueMap.length());
    parteNumerica = String.valueOf(Integer.valueOf(parteNumerica) + 1);


    return parteNumerica + parteRestante;
  }



  /**
   * @param listaOrientacionPaginas
   * @return
   * @throws IOException
   */
  // private void crearPaginasVaciasTodasPaginas(List listaOrientacionPaginas, Document document)
  // throws IOException {
  // File pdfBlank;
  // //Creating PDF document object
  // //PDDocument document = new PDDocument();
  //
  //
  // for(int i=0;i<listaOrientacionPaginas.size();i++)
  // {
  //
  // //Add an empty page to it
  // document.setPageSize(PageSize.A4);
  // document.newPage();
  //
  // if(listaOrientacionPaginas.get(i).equals(PageFormat.LANDSCAPE))
  // {
  // document.setPageSize(PageSize.A4.rotate());
  // document.newPage();
  // }
  // else
  // {
  // document.setPageSize(PageSize.A4);
  // document.newPage();
  // }
  //
  // }
  //
  // }

}
