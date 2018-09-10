package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;
// This validator performs JAXB unmarshalling with validation
// against a schema
// This version is thread-safe when using the JAXB reference implementation
// (for which the documentation specifies JAXBContext is thread-safe while
// Marshaller and Unmarshaller are not)

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Provider
@Consumes({"application/xml", "text/xml"})
public class NfvValidationProvider implements MessageBodyReader<JAXBElement<?>> {
    final String jaxbPackage = "it.polito.dp2.NFV.sol3.service.ServiceXML";
    Logger logger;
    String responseBodyTemplate;
    JAXBContext jaxbContext;
    Schema schema;

    public NfvValidationProvider() {
        logger = Logger.getLogger(NfvValidationProvider.class.getName());

        try {
            //InputStream schemaStream1 = NfvValidationProvider.class.getResourceAsStream("/xsd/nfvInfo.xsd");
            InputStream schemaStream2 = NfvValidationProvider.class.getResourceAsStream("/xsd/NfvDeclaration.xsd");
            if (schemaStream2 == null) {
                logger.log(Level.SEVERE, "xml schema file Not found.");
                throw new IOException();
            }
            jaxbContext = JAXBContext.newInstance(jaxbPackage);
            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            //schema = sf.newSchema(new Source[] {new StreamSource(schemaStream1), new StreamSource(schemaStream2)});
            schema = sf.newSchema(new StreamSource(schemaStream2));

            InputStream templateStream = NfvValidationProvider.class.getResourceAsStream("/html/BadRequestBodyTemplate.html");
            if (templateStream == null) {
                logger.log(Level.SEVERE, "html template file Not found.");
                throw new IOException();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(templateStream));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            responseBodyTemplate = out.toString();

            logger.log(Level.INFO, "Nfv deployer initialized successfully");
        } catch (SAXException | JAXBException | IOException se) {
            logger.log(Level.SEVERE, "Error parsing xml directory file. Service will not work properly.", se);
        }
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return JAXBElement.class.equals(type) || jaxbPackage.equals(type.getPackage().getName());
    }

    @Override
    public JAXBElement<?> readFrom(Class<JAXBElement<?>> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                                   MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);
            return (JAXBElement<?>) unmarshaller.unmarshal(entityStream);
        } catch (JAXBException ex) {
            logger.log(Level.WARNING, "Request body validation error.", ex);
            Throwable linked = ex.getLinkedException();
            String validationErrorMessage = "Request body validation error";
            if (linked != null && linked instanceof SAXParseException)
                validationErrorMessage += ": " + linked.getMessage();
            BadRequestException bre = new BadRequestException("Request body validation error");
            String responseBody = responseBodyTemplate.replaceFirst("___TO_BE_REPLACED___", validationErrorMessage);
            Response response = Response.fromResponse(bre.getResponse()).entity(responseBody).build();
            throw new BadRequestException("Request body validation error", response);
        }
    }

}
