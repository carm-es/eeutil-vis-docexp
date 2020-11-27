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

package es.mpt.dsic.inside.ws.service.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "opcionesLogo", propOrder = {"estamparLogo", "estamparNombreOrganismo",
    "listaCadenasNombreOrganismo", "posicion", "estamparPie", "textoPie"})
public class OpcionesLogo {

  @XmlElement(required = false, name = "estamparLogo", defaultValue = "true")
  private boolean estamparLogo;
  @XmlElement(required = false, name = "estamparNombreOrganismo", defaultValue = "true")
  private boolean estamparNombreOrganismo;
  @XmlElement(required = false, name = "listaCadenasNombreOrganismo")
  private ListaCadenas listaCadenasNombreOrganismo;
  @XmlElement(required = false, name = "posicion")
  private Integer posicion;
  @XmlElement(required = false, name = "estamparPie")
  private boolean estamparPie;
  @XmlElement(required = false, name = "textoPie")
  private String textoPie;

  public boolean isEstamparLogo() {
    return estamparLogo;
  }

  public void setEstamparLogo(boolean estamparLogo) {
    this.estamparLogo = estamparLogo;
  }

  public boolean isEstamparNombreOrganismo() {
    return estamparNombreOrganismo;
  }

  public void setEstamparNombreOrganismo(boolean estamparNombreOrganismo) {
    this.estamparNombreOrganismo = estamparNombreOrganismo;
  }

  public ListaCadenas getListaCadenasNombreOrganismo() {
    return listaCadenasNombreOrganismo;
  }

  public void setListaCadenasNombreOrganismo(ListaCadenas listaCadenasNombreOrganismo) {
    this.listaCadenasNombreOrganismo = listaCadenasNombreOrganismo;
  }

  public Integer getPosicion() {
    return posicion;
  }

  public void setPosicion(Integer posicion) {
    this.posicion = posicion;
  }

  public boolean isEstamparPie() {
    return estamparPie;
  }

  public void setEstamparPie(boolean estamparPie) {
    this.estamparPie = estamparPie;
  }

  public String getTextoPie() {
    return textoPie;
  }

  public void setTextoPie(String textoPie) {
    this.textoPie = textoPie;
  }



}
