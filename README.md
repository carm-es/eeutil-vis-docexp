# eeutil-vis-docexp
eeutil-vis-docexp
Instalación y evolutivo de la versión distribuible de Eeutil-Vis-Docexp.

Se parte de la versión distribuible que se ofrece en el área de descargas de la Suite Inside del Centro de Transferencia Tecnológica:

https://administracionelectronica.gob.es/ctt/inside/descargas -> Versión distribuible (Código fuente) -> Distribuible eeutil-vis-docexp v4.2.0 (noviembre 2018)
Eeutils(CSV Creator) es el componente de la Suite CSV que agrupa diversas funcionalidades relacionadas con la generación de CSV y gestión de firmas e informes. Eeutils(CSV Creator) se divide en cinco módulos, entre los que se encuentra eeutil-vis-docexp, que permite visualizar cualquier documento en PDF.

### Operaciones de eeutil-vis-docexp
Se realizan las siguientes operaciones:

- **visualizarDocumentoConPlantilla**  :  Se llama en **INSIDE** al servicio **/visualizarDocumento** a través de la web, mediante la opción "Visualizar documento", pero no existe llamada SOAP como tal en INSIDE, ya que se realiza internamente a través de la operación **visualizar** de la EEUTIL. Se realiza mediante el método *InsideServiceVisualizacionImpl - port.visualizarDocumentoConPlantilla()*

- **visualizarContenidoOriginal** : Se llama en **INSIDE** a través de los servicios **/editarDocumento** y **/visualizarContenido** a través del método *InsideServiceVisualizacionImpl - port.visualizarContenidoOriginal*

- **obtenerPlantillas** :  Se llama en **INSIDE** al servicio **/visualizarDocumento** a través de la web, mediante la opción "Visualizar documento", pero no existe llamada SOAP como tal en INSIDE, ya que se realiza internamente a través de la operación **visualizar** de la EEUTIL. Se realiza mediante el método *InsideServiceVisualizacionImpl - port.obtenerPlantillas()*

- **visualizar** :  Se llama en **INSIDE** a través de los servicios **/editarDocumento** y **/visualizarContenido** a través del método *InsideServiceVisualizacionImpl - port.visualizar*
