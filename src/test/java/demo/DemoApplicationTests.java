package demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXParseException;

import javax.xml.bind.UnmarshalException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DemoApplication.class)
public class DemoApplicationTests {

    @Autowired
    @Qualifier("httpMessageConverter")
    HttpMessageConverter<Object> converter;

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
