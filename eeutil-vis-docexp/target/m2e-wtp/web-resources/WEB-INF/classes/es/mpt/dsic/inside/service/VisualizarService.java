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

package es.mpt.dsic.inside.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.transform.TransformerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.mpt.dsic.inside.fop.converter.DocumentConverter;
import es.mpt.dsic.inside.fop.converter.TemplateConverter;
import es.mpt.dsic.inside.security.model.AppInfo;
import es.mpt.dsic.inside.security.model.ApplicationLogin;
import es.mpt.dsic.inside.security.service.AplicacionInfoService;
import es.mpt.dsic.inside.services.AfirmaService;
import es.mpt.dsic.inside.visualizacion.exception.VisualizacionException;
import es.mpt.dsic.inside.visualizacion.service.VisualizacionService;
import es.mpt.dsic.inside.ws.service.exception.InSideException;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenido;
import es.mpt.dsic.inside.ws.service.model.EstadoInfo;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;
import es.mpt.dsic.inside.ws.service.model.Plantilla;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacion;
import es.mpt.dsic.inside.ws.service.model.documento.DocumentoEniConMAdicionales;

@Service
public class VisualizarService {

  protected final static Log logger = LogFactory.getLog(VisualizarService.class);

  @Autowired(required = true)
  private VisualizacionService visualizacionExpedienteService;

  @Autowired(required = true)
  private VisualizacionService visualizacionDocumentoService;

  @Autowired
  TemplateConverter fopConverter;

  @Autowired
  private AplicacionInfoService aplicacionInfoService;

  @Autowired
  DocumentConverter documentConverter;

  @Autowired
  private AfirmaService afirmaService;

  public SalidaVisualizacion visualizar(ApplicationLogin info, Item item,
      OpcionesVisualizacion opciones) throws InSideException {
    try {

      DocumentoContenido docSalida = null;
      if ("modelo1".contentEquals(opciones.getModelo())) {
        docSalida = visualizacionExpedienteService.doVisualizable(item, opciones);
      } else if ("modelo2".contentEquals(opciones.getModelo())) {
        docSalida = visualizacionDocumentoService.doVisualizable(item, opciones);
      } else {
        throw new InSideException("El valor del dato <modelo> es incorrecto", new EstadoInfo());
      }

      SalidaVisualizacion salida = new SalidaVisualizacion();
      salida.setDocumentoContenido(docSalida);

      return salida;

    } catch (InSideException e) {
      throw e;
    } catch (VisualizacionException e) {
      logger.error(e);
      EstadoInfo estadoInfo = new EstadoInfo();
      estadoInfo.setDescripcion(e.getMessage());
      throw new InSideException("Error al visualizar contenido", estadoInfo);
    } catch (Exception e) {
      EstadoInfo estadoInfo = new EstadoInfo();
      logger.error(e);
      throw new InSideException("No se puede obtener el foliado " + e.getMessage(), estadoInfo);
    }
  }

  public SalidaVisualizacion visualizarContenidoOriginal(ApplicationLogin info, Item item)
      throws InSideException {
    try {
      DocumentoContenido docSalida = visualizacionDocumentoService.doVisualizableOriginal(item);

      SalidaVisualizacion salida = new SalidaVisualizacion();
      salida.setDocumentoContenido(docSalida);

      return salida;
    } catch (VisualizacionException e) {
      EstadoInfo estadoInfo = new EstadoInfo();
      logger.error(e);
      throw new InSideException("No se puede obtener el foliado " + e.getMessage(), estadoInfo);
    }
  }

  public SalidaVisualizacion visualizarDocumentoConPlantilla(ApplicationLogin info,
      DocumentoEniConMAdicionales docEniAdicionales, String plantilla) throws InSideException {
    try {
      byte[] plantillaBytes = getPlantilla(info.getIdApplicacion(), plantilla);

      es.mpt.dsic.inside.fop.model.documento.DocumentoEniConMAdicionales modelObject =
          documentConverter.documentoEniConMAdicionalesWsToToModel(docEniAdicionales, afirmaService,
              info.getIdApplicacion());

      DocumentoContenido docSalida = fopConverter.convertDocumentToPdf(modelObject, plantillaBytes);

      SalidaVisualizacion salida = new SalidaVisualizacion();
      salida.setDocumentoContenido(docSalida);

      return salida;
    } catch (IOException e) {
      EstadoInfo estadoInfo = new EstadoInfo();
      estadoInfo.setDescripcion(e.getMessage());
      throw new InSideException("Error al visualizar contenido", estadoInfo);
    } catch (TransformerException e) {
      EstadoInfo estadoInfo = new EstadoInfo();
      estadoInfo.setDescripcion(e.getMessage());
      throw new InSideException("Error al visualizar contenido", estadoInfo);
    } catch (Exception e) {
      EstadoInfo estadoInfo = new EstadoInfo();
      estadoInfo.setDescripcion(e.getMessage());
      throw new InSideException("Error al visualizar contenido", estadoInfo);
    }
  }

  private byte[] getPlantilla(String idAplicacion, String idPlantilla) throws InSideException {
    AppInfo aplicacion = aplicacionInfoService.getAplicacionInfo(idAplicacion);
    byte[] plantilla = aplicacion.getPlantilla(idPlantilla);
    if (plantilla == null) {
      throw new InSideException(
          new EstadoInfo("Error al visualizar contenido. Plantilla no v�lida", null,
              "La aplicaci�n " + idAplicacion + " no tiene asociada la plantilla " + idPlantilla));
    } else {
      return plantilla;
    }
  }

  public List<Plantilla> obtenerPlantillas(ApplicationLogin info) throws InSideException {
    AppInfo aplicacion = aplicacionInfoService.getAplicacionInfo(info.getIdApplicacion());
    return convertPlantillaData(aplicacion.getPlantillas());
  }

  private List<Plantilla> convertPlantillaData(Map<String, byte[]> plantillas) {
    List<Plantilla> retorno = new ArrayList<Plantilla>();
    if (plantillas != null) {
      for (String identificador : plantillas.keySet()) {
        Plantilla plantilla = new Plantilla();
        plantilla.setIdenticador(identificador);
        plantilla.setBytesPlantilla(plantillas.get(identificador));
        retorno.add(plantilla);
      }
    }
    return retorno;
  }

  public List<Plantilla> asociarPlantilla(ApplicationLogin info, String idPlantilla,
      byte[] plantilla) throws InSideException {
    AppInfo aplicacion =
        aplicacionInfoService.asociarPantilla(info.getIdApplicacion(), idPlantilla, plantilla);
    return convertPlantillaData(aplicacion.getPlantillas());
  }

  public List<Plantilla> eliminarPlantilla(ApplicationLogin info, String idPlantilla)
      throws InSideException {
    AppInfo aplicacion =
        aplicacionInfoService.eliminarPlantilla(info.getIdApplicacion(), idPlantilla);
    return convertPlantillaData(aplicacion.getPlantillas());
  }

}
