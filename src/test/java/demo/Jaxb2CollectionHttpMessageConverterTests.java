package demo;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.xml.Jaxb2CollectionHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;

public class Jaxb2CollectionHttpMessageConverterTests {

    Jaxb2CollectionHttpMessageConverter<Collection<RootElement>> converter = new Jaxb2CollectionHttpMessageConverter<Collection<RootElement>>() {

        protected XMLInputFactory createXmlInputFactory() {
            XMLInputFactory inputFactory = super.createXmlInputFactory();
            inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
            return inputFactory;
        }

    };

    @Test
    public void contextLoads() throws Exception {
        String content =
                "<!DOCTYPE  foo [<!ENTITY companyname \"Contoso Inc.\">]>" +
                "<list><rootElement><external>&companyname;</external></rootElement></list>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(content.getBytes("UTF-8"));
        try {
            Type type = new ParameterizedTypeReference<List<RootElement>>() {}.getType();
            List<RootElement> rootElementList = (List<RootElement>) converter.read(type, null, inputMessage);
            System.out.println(rootElementList);
        }
        catch (HttpMessageNotReadableException e) {
            System.out.println(e.getCause().getClass());
            assertThat(e.getCause(), is(instanceOf(UnmarshalException.class)));
            //assertThat(e.getCause().getCause(), is(instanceOf(SAXParseException.class)));
            e.printStackTrace();
        }
    }

    @XmlRootElement
    public static class RootElement {
        private String external;

        public String getExternal() {
            return external;
        }

        public void setExternal(String external) {
            this.external = external;
        }

        @Override
        public String toString() {
            return "RootElement{" +
                    "external='" + external + '\'' +
                    '}';
        }
    }
}
