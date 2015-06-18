package demo;

import org.junit.Test;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.UnmarshalException;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = DemoApplication.class)
public class DemoApplicationTests {

    //    @Autowired
    //    @Qualifier("httpMessageConverter")
    HttpMessageConverter<Object> converter = new Jaxb2RootElementHttpMessageConverter() {
        @Override
        protected Source processSource(Source source) {
            if (source instanceof StreamSource) {
                StreamSource streamSource = (StreamSource) source;
                InputSource inputSource = new InputSource(streamSource.getInputStream());
                try {
                    XMLReader ex = XMLReaderFactory.createXMLReader();
                    ex.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                    return new SAXSource(ex, inputSource);
                } catch (SAXException var6) {
                    this.logger.warn("Processing of external entities could not be disabled", var6);
                    return source;
                }
            } else {
                return source;
            }
        }
    };

    @Test
    public void contextLoads() throws Exception {
        String content = "<!DOCTYPE foo><rootElement><external>a</external></rootElement>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(content.getBytes("UTF-8"));
        try {
            RootElement rootElement = (RootElement) converter.read(RootElement.class, inputMessage);
            System.out.println(rootElement);
            fail();
        } catch (HttpMessageNotReadableException e) {
            System.out.println(e.getCause().getClass());
            assertThat(e.getCause(), is(instanceOf(UnmarshalException.class)));
            assertThat(e.getCause().getCause(), is(instanceOf(SAXParseException.class)));
            e.printStackTrace();
        }
    }

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
