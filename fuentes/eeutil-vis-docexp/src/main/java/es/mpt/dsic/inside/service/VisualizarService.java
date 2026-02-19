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

package es.mpt.dsic.inside.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import es.mpt.dsic.inside.utils.exception.EeutilException;
import es.mpt.dsic.inside.visualizacion.service.VisualizacionService;
import es.mpt.dsic.inside.ws.service.exception.InSideException;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenido;
import es.mpt.dsic.inside.ws.service.model.DocumentoContenidoGeneric;
import es.mpt.dsic.inside.ws.service.model.EstadoInfo;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.ItemGeneric;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;
import es.mpt.dsic.inside.ws.service.model.Plantilla;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacion;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacionGeneric;
import es.mpt.dsic.inside.ws.service.model.documento.DocumentoEniConMAdicionales;

@Service
public class VisualizarService {

  private static final String ERROR_AL_VISUALIZAR_CONTENIDO = "Error al visualizar contenido";

  protected static final Log logger = LogFactory.getLog(VisualizarService.class);

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

  public SalidaVisualizacionGeneric visualizar(ItemGeneric item, OpcionesVisualizacion opciones,
      String idAplicacion) throws EeutilException {
    try {

      DocumentoContenidoGeneric docSalida = null;
      if ("modelo1".contentEquals(opciones.getModelo())) {
        docSalida = visualizacionExpedienteService.doVisualizable(item, opciones, afirmaService,
            idAplicacion);
      } else if ("modelo2".contentEquals(opciones.getModelo())) {
        docSalida = visualizacionDocumentoService.doVisualizable(item, opciones, afirmaService,
            idAplicacion);
      } else {
        throw new InSideException("El valor del dato <modelo> es incorrecto", new EstadoInfo());
      }

      SalidaVisualizacionGeneric salida = new SalidaVisualizacionGeneric();
      salida.setDocumentoContenido(docSalida);

      return salida;

    } catch (EeutilException e) {
      throw e;
    } catch (Exception e) {
      throw new EeutilException(e.getMessage(), e);
    }
  }

  public SalidaVisualizacion visualizarContenidoOriginal(String idApp, Item item)
      throws EeutilException {
    try {
      DocumentoContenido docSalida =
          visualizacionDocumentoService.doVisualizableOriginal(item, afirmaService, idApp);

      SalidaVisualizacion salida = new SalidaVisualizacion();
      salida.setDocumentoContenido(docSalida);

      return salida;
    } catch (EeutilException e) {
      throw e;
    } catch (Exception e) {
      throw new EeutilException(e.getMessage(), e);
    }
  }

  @Deprecated
  public SalidaVisualizacion visualizarDocumentoConPlantilla(ApplicationLogin info,
      DocumentoEniConMAdicionales docEniAdicionales, String plantilla) throws InSideException {
    try {
      byte[] plantillaBytes = getPlantilla(info.getIdApplicacion(), plantilla);

      es.mpt.dsic.inside.fop.model.documento.DocumentoEniConMAdicionales modelObject =
          documentConverter.documentoEniConMAdicionalesWsToToModel(docEniAdicionales, afirmaService,
              info.getIdApplicacion());

      DocumentoContenido docSalida =
          fopConverter.convertDocumentToPdf(modelObject, plantillaBytes, info.getIdApplicacion());

      SalidaVisualizacion salida = new SalidaVisualizacion();
      salida.setDocumentoContenido(docSalida);

      return salida;
    } catch (EeutilException e) {
      EstadoInfo estadoInfo = new EstadoInfo();
      estadoInfo.setDescripcion(e.getMessage());
      throw new InSideException(ERROR_AL_VISUALIZAR_CONTENIDO, estadoInfo);
    } catch (Exception e) {
      EstadoInfo estadoInfo = new EstadoInfo();
      estadoInfo.setDescripcion(e.getMessage());
      throw new InSideException(ERROR_AL_VISUALIZAR_CONTENIDO, estadoInfo);
    }
  }

  @Deprecated
  private byte[] getPlantilla(String idAplicacion, String idPlantilla) throws InSideException {

    try {
      AppInfo aplicacion = aplicacionInfoService.getAplicacionInfo(idAplicacion);
      byte[] plantilla = aplicacion.getPlantilla(idPlantilla);
      if (plantilla == null) {
        throw new InSideException(new EstadoInfo(
            "Error al visualizar contenido. Plantilla no v�lida", null,
            "La aplicaci�n " + idAplicacion + " no tiene asociada la plantilla " + idPlantilla));
      } else {
        return plantilla;
      }
    } catch (EeutilException e) {
      throw new InSideException(e.getMessage(), e);
    }
  }

  @Deprecated
  public List<Plantilla> obtenerPlantillas(ApplicationLogin info) {

    AppInfo aplicacion = null;
    try {
      aplicacion = aplicacionInfoService.getAplicacionInfo(info.getIdApplicacion());

    } catch (EeutilException e) {
    }

    return convertPlantillaData(aplicacion != null ? aplicacion.getPlantillas() : null);
  }

  @Deprecated
  private List<Plantilla> convertPlantillaData(Map<String, byte[]> plantillas) {
    List<Plantilla> retorno = new ArrayList<>();
    if (plantillas != null) {
      for (Map.Entry<String, byte[]> mPlantilla : plantillas.entrySet()) {
        Plantilla plantilla = new Plantilla();
        plantilla.setIdenticador(mPlantilla.getKey());
        plantilla.setBytesPlantilla(mPlantilla.getValue());
        retorno.add(plantilla);
      }
    }
    return retorno;
  }

  @Deprecated
  public List<Plantilla> asociarPlantilla(ApplicationLogin info, String idPlantilla,
      byte[] plantilla) {
    AppInfo aplicacion =
        aplicacionInfoService.asociarPantilla(info.getIdApplicacion(), idPlantilla, plantilla);
    return convertPlantillaData(aplicacion.getPlantillas());
  }

  @Deprecated
  public List<Plantilla> eliminarPlantilla(ApplicationLogin info, String idPlantilla) {
    AppInfo aplicacion =
        aplicacionInfoService.eliminarPlantilla(info.getIdApplicacion(), idPlantilla);
    return convertPlantillaData(aplicacion.getPlantillas());
  }



}
