package com.mycompany.vehiculo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Se encarga de crear el informe XML de la flota y consultar sus categorías
 * de mantenimiento.
 */
public class GestorReportesXML {

    private static final Path RUTA_XML = Path.of("datos", "reports", "informe_flota.xml");

    /**
     * Crea un informe XML con los datos de cada vehículo de la lista.
     * Primero organiza la información en un documento DOM y después lo guarda
     * en la ruta definida para el informe.
     *
     * @param vehiculos lista de vehículos que se incluirán en el informe
     * @throws IOException si no se puede crear la carpeta o guardar el archivo
     */
    public void generarInforme(List<Vehiculo> vehiculos) throws IOException {
        Path directorio = RUTA_XML.getParent();
        Files.createDirectories(directorio);

        try {
            DocumentBuilder constructor = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document documento = constructor.newDocument();

            Element raiz = documento.createElement("flota");
            documento.appendChild(raiz);

            for (Vehiculo vehiculo : vehiculos) {
                Element elementoVehiculo = documento.createElement("vehiculo");
                raiz.appendChild(elementoVehiculo);

                agregarElemento(documento, elementoVehiculo, "matricula", vehiculo.getMatricula());
                agregarElemento(documento, elementoVehiculo, "marca", vehiculo.getMarca());
                agregarElemento(documento, elementoVehiculo, "modelo", vehiculo.getModelo());
                agregarElemento(documento, elementoVehiculo, "anyo", String.valueOf(vehiculo.getAnyo()));
                agregarElemento(documento, elementoVehiculo, "kilometraje", String.valueOf(vehiculo.getKilometraje()));
                agregarElemento(documento, elementoVehiculo, "categoriaMantenimiento", vehiculo.getCategoriaMantenimiento());
            }

            Transformer transformador = TransformerFactory.newInstance().newTransformer();
            transformador.setOutputProperty(OutputKeys.INDENT, "yes");
            transformador.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            try (OutputStream salida = Files.newOutputStream(RUTA_XML)) {
                transformador.transform(new DOMSource(documento), new StreamResult(salida));
            }
        } catch (ParserConfigurationException | TransformerException e) {
            throw new IOException("No se pudo generar el XML.", e);
        }
    }

    private void agregarElemento(Document documento, Element padre, String nombre, String valor) {
        Element elemento = documento.createElement(nombre);
        elemento.setTextContent(valor == null ? "" : valor);
        padre.appendChild(elemento);
    }

    /**
     * Lee el informe XML con SAX y devuelve las categorías de mantenimiento
     * encontradas. Se utiliza un conjunto para no guardar categorías repetidas.
     *
     * @return conjunto con las categorías únicas del informe
     * @throws IOException si no se puede abrir o leer el archivo XML
     * @throws SAXException si el documento no tiene un formato XML válido
     */
    public Set<String> leerCategorias() throws IOException, SAXException {
        HashSet<String> categorias = new HashSet<>();
        SAXParserFactory fabrica = SAXParserFactory.newInstance();

        try {
            SAXParser parser = fabrica.newSAXParser();
            parser.parse(RUTA_XML.toFile(), new DefaultHandler() {
                private boolean leyendoCategoria;
                private final StringBuilder texto = new StringBuilder();

                @Override
                public void startElement(String uri, String nombreLocal, String nombre,
                        Attributes atributos) {
                    if ("categoriaMantenimiento".equals(nombre)) {
                        leyendoCategoria = true;
                        texto.setLength(0);
                    }
                }

                @Override
                public void characters(char[] caracteres, int inicio, int longitud) {
                    if (leyendoCategoria) {
                        texto.append(caracteres, inicio, longitud);
                    }
                }

                @Override
                public void endElement(String uri, String nombreLocal, String nombre) {
                    if ("categoriaMantenimiento".equals(nombre)) {
                        String categoria = texto.toString().trim();
                        if (!categoria.isEmpty()) {
                            categorias.add(categoria);
                        }
                        leyendoCategoria = false;
                    }
                }
            });
        } catch (ParserConfigurationException e) {
            throw new SAXException("No se pudo preparar la lectura del XML.", e);
        }

        return categorias;
    }
}
