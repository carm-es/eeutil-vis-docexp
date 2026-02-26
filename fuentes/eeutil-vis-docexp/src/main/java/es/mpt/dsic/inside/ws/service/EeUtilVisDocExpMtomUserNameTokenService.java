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
import es.mpt.dsic.inside.ws.service.model.ItemMtom;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;
import es.mpt.dsic.inside.ws.service.model.Plantilla;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacion;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacionMtom;
import es.mpt.dsic.inside.ws.service.model.documento.DocumentoEniConMAdicionales;

@WebService
public interface EeUtilVisDocExpMtomUserNameTokenService {

  @WebMethod(operationName = "visualizar", action = "urn:visualizar")
  @WebResult(name = "salidaVisualizar", partName = "salidaVisualizar")
  public SalidaVisualizacionMtom visualizar(
      @WebParam(name = "item") @XmlElement(required = true, name = "item") ItemMtom item,
      @WebParam(name = "opcionesVisualizacion") @XmlElement(required = true,
          name = "opcionesVisualizacion") OpcionesVisualizacion opcionesVisualizacion)
      throws InSideException;


}
