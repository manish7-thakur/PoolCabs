package com.poolcabs.util;

import com.poolcabs.generated.directionservice.DirectionsResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/**
 *
 */
public class JAXBHelper {

    /**
     * Unmarshal a String containing xml for a given schema into type <T>
     *
     * @param <T> the return Type.
     * @param s the string to unmarshal
     * @param schema the schame
     * @param schemaPackage
     * @return the created <T>
     * @throws nl.coin.overstapdrempels.exception.OpzegdrempelsException
     */
    public static <T> T unmarshall(InputStream s, Schema schema, String schemaPackage) {
        T type = null;
        try {
            JAXBContext context = JAXBContext.newInstance(schemaPackage);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            type = (T) unmarshaller.unmarshal(s);
        } catch (Exception ex) {
            Logger.getLogger(JAXBHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return type;
    }

    public static <T> Schema getSchemaFor(Class<T> type) throws IOException, SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        String schema = null;
        if (DirectionsResponse.class.equals(type)) {
            schema = "/xsd/direction_service.xsd";
        } 

        URL url = JAXBHelper.class.getClassLoader().getResource(schema);
        return sf.newSchema(url);
    }
}
