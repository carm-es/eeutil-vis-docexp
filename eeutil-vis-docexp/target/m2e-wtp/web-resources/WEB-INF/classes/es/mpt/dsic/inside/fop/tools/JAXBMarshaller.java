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

package es.mpt.dsic.inside.fop.tools;

import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.springframework.stereotype.Component;

@Component
public class JAXBMarshaller {

  /**
   * Metodo para convertir un objeto Java al string XML equivalente.
   * 
   * @param object Objeto java a convertir.
   * @param clase Class java para saber el tipo.
   * @return String con el XML equivalente.
   * @throws JAXBException
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> String marshallRootElement(T object, Class<T> clase, boolean omitDeclararion)
      throws JAXBException {

    StringWriter sw = null;

    JAXBContext jc = JAXBContext.newInstance(clase);
    Marshaller marshaller = jc.createMarshaller();

    if (omitDeclararion) {
      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
    }

    sw = new StringWriter();

    JAXBIntrospector introspector = jc.createJAXBIntrospector();
    if (null == introspector.getElementName(object)) {
      JAXBElement jaxbElement = new JAXBElement(new QName("ROOT"), Object.class, object);
      marshaller.marshal(jaxbElement, sw);
    } else {
      marshaller.marshal(object, sw);
    }

    return sw.toString();
  }

}
