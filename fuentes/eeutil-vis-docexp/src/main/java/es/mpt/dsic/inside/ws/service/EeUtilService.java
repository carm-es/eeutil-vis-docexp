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

package es.mpt.dsic.inside.ws.service;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import es.mpt.dsic.inside.security.model.ApplicationLogin;
import es.mpt.dsic.inside.ws.service.exception.InSideException;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;
import es.mpt.dsic.inside.ws.service.model.Plantilla;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacion;
import es.mpt.dsic.inside.ws.service.model.documento.DocumentoEniConMAdicionales;


@WebService
public interface EeUtilService {


  @WebMethod(operationName = "visualizar", action = "urn:visualizar")
  @WebResult(name = "salidaVisualizar", partName = "salidaVisualizar")
  public SalidaVisualizacion visualizar(
      @WebParam(name = "aplicacionInfo") @XmlElement(required = true,
          name = "aplicacionInfo") ApplicationLogin info,
      @WebParam(name = "item") @XmlElement(required = true, name = "item") Item item,
      @WebParam(name = "opcionesVisualizacion") @XmlElement(required = true,
          name = "opcionesVisualizacion") OpcionesVisualizacion opcionesVisualizacion)
      throws InSideException;


  @WebMethod(operationName = "visualizarContenidoOriginal",
      action = "urn:visualizarContenidoOriginal")
  @WebResult(name = "salidaVisualizar", partName = "salidaVisualizar")
  public SalidaVisualizacion visualizarContenidoOriginal(
      @WebParam(name = "aplicacionInfo") @XmlElement(required = true,
          name = "aplicacionInfo") ApplicationLogin info,
      @WebParam(name = "item") @XmlElement(required = true, name = "item") Item item)
      throws InSideException;

  @WebMethod(operationName = "obtenerPlantillas", action = "urn:obtenerPlantillas")
  @WebResult(name = "listaPlantillas", partName = "listaPlantillas")
  public List<Plantilla> obtenerPlantillas(@WebParam(name = "aplicacionInfo") @XmlElement(
      required = true, name = "aplicacionInfo") ApplicationLogin info) throws InSideException;

  @WebMethod(operationName = "asociarPlantilla", action = "urn:asociarPlantilla")
  @WebResult(name = "listaPlantillas", partName = "listaPlantillas")
  public List<Plantilla> asociarPlantilla(
      @WebParam(name = "aplicacionInfo") @XmlElement(required = true,
          name = "aplicacionInfo") ApplicationLogin info,
      @WebParam(name = "idPlantilla") @XmlElement(required = true,
          name = "idPlantilla") String idPlantilla,
      @WebParam(name = "plantillaBytes") @XmlElement(required = true,
          name = "plantillaBytes") byte[] plantilla)
      throws InSideException;


  @WebMethod(operationName = "eliminarPlantilla", action = "urn:eliminarPlantilla")
  @WebResult(name = "listaPlantillas", partName = "listaPlantillas")
  public List<Plantilla> eliminarPlantilla(
      @WebParam(name = "aplicacionInfo") @XmlElement(required = true,
          name = "aplicacionInfo") ApplicationLogin info,
      @WebParam(name = "idPlantilla") @XmlElement(required = true,
          name = "idPlantilla") String idPlantilla)
      throws InSideException;

  @WebMethod(operationName = "visualizarDocumentoConPlantilla",
      action = "urn:visualizarDocumentoConPlantilla")
  @WebResult(name = "salidaVisualizar", partName = "salidaVisualizar")
  public SalidaVisualizacion visualizarDocumentoConPlantilla(
      @WebParam(name = "aplicacionInfo") @XmlElement(required = true,
          name = "aplicacionInfo") ApplicationLogin info,
      @WebParam(name = "docEniAdicionales") @XmlElement(required = true,
          name = "docEniAdicionales") DocumentoEniConMAdicionales docEniAdicionales,
      @WebParam(name = "plantilla") @XmlElement(required = true,
          name = "plantilla") String plantilla)
      throws InSideException;
}
