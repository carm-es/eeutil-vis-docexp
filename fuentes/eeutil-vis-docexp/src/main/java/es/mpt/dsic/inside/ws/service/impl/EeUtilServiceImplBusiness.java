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

package es.mpt.dsic.inside.ws.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.mpt.dsic.inside.service.VisualizarService;
import es.mpt.dsic.inside.utils.exception.EeutilException;
import es.mpt.dsic.inside.ws.service.model.Item;
import es.mpt.dsic.inside.ws.service.model.ItemGeneric;
import es.mpt.dsic.inside.ws.service.model.OpcionesVisualizacion;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacion;
import es.mpt.dsic.inside.ws.service.model.SalidaVisualizacionGeneric;

@Component
public class EeUtilServiceImplBusiness {

  @Autowired
  VisualizarService visualizarService;

  public SalidaVisualizacionGeneric visualizar(ItemGeneric item, OpcionesVisualizacion opciones,
      String idAplicacion) throws EeutilException {

    try {
      return visualizarService.visualizar(item, opciones, idAplicacion);
    } catch (EeutilException e) {

      throw new EeutilException(e.getMessage(), e);
    } catch (Exception e) {
      throw new EeutilException(e.getMessage(), e);
    }
  }

  public SalidaVisualizacion visualizarContenidoOriginal(String idApp, Item item)
      throws EeutilException {

    try {
      return visualizarService.visualizarContenidoOriginal(idApp, item);

    } catch (EeutilException e) {

      throw new EeutilException(e.getMessage(), e);
    } catch (Exception e) {

      throw new EeutilException(e.getMessage(), e);
    }
  }

}
