package demo;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.xml.bind.UnmarshalException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;

public class Jaxb2MarshallerTests {

    @Test
    public void contextLoads() throws Exception {
        String content =
                "<!DOCTYPE  foo [<!ENTITY companyname \"Contoso Inc.\">]>" +
                "<root>&companyname;</root>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(content.getBytes("UTF-8"));
        try {

            SourceHttpMessageConverter<Source> converter = new SourceHttpMessageConverter<Source>();
            converter.setSupportDtd(false);

//            DOMSource result = (DOMSource) converter.read(DOMSource.class, inputMessage);

//            SAXSource result = (SAXSource) converter.read(SAXSource.class, inputMessage);
//            InputSource inputSource = result.getInputSource();
//            XMLReader reader = result.getXMLReader();
//            reader.parse(inputSource);

            StAXSource result = (StAXSource) converter.read(StAXSource.class, inputMessage);
            XMLStreamReader streamReader = result.getXMLStreamReader();
            assertTrue(streamReader.hasNext());
            streamReader.next();
            streamReader.next();
            String s = streamReader.getLocalName();
            assertEquals("root", s);
            s = streamReader.getElementText();

            System.out.println(result);
        }
        catch (HttpMessageNotReadableException e) {
            System.out.println(e.getCause().getClass());
            assertThat(e.getCause(), is(instanceOf(UnmarshalException.class)));
            assertThat(e.getCause().getCause(), is(instanceOf(SAXParseException.class)));
            e.printStackTrace();
        }
    }

}
