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


package es.mpt.dsic.inside.fop.model.documento;

import java.util.List;


public class TipoDocumento {

  protected TipoContenido contenido;
  protected TipoMetadatos metadatos;
  protected List<TipoFirmasElectronicas> firmas;

  /**
   * Gets the value of the contenido property.
   * 
   * @return possible object is {@link TipoContenido }
   * 
   */
  public TipoContenido getContenido() {
    return contenido;
  }

  /**
   * Sets the value of the contenido property.
   * 
   * @param value allowed object is {@link TipoContenido }
   * 
   */
  public void setContenido(TipoContenido value) {
    this.contenido = value;
  }

  /**
   * Gets the value of the metadatos property.
   * 
   * @return possible object is {@link TipoMetadatos }
   * 
   */
  public TipoMetadatos getMetadatos() {
    return metadatos;
  }

  /**
   * Sets the value of the metadatos property.
   * 
   * @param value allowed object is {@link TipoMetadatos }
   * 
   */
  public void setMetadatos(TipoMetadatos value) {
    this.metadatos = value;
  }

  /**
   * La firma es obligatoria para el documento administrativo electr�nico y para todo aquel
   * documento electr�nico susceptible de ser incorporado en un expediente electr�nico.
   * 
   * @return possible object is {@link Firmas }
   * 
   */
  public List<TipoFirmasElectronicas> getFirmas() {
    return firmas;
  }

  /**
   * Sets the value of the firmas property.
   * 
   * @param value allowed object is {@link Firmas }
   * 
   */
  public void setFirmas(List<TipoFirmasElectronicas> value) {
    this.firmas = value;
  }

}
