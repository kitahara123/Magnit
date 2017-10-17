import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import java.io.InputStream;

public class XSLTConverter implements IConverter {
    private Logger toLog = LogManager.getLogger("TestBean");

    @Override
    public void convert() {
        String fileNameFrom = "1.xml";
        String fileNameTo = "2.xml";
        InputStream is = TestBean.class.getResourceAsStream("style.xsl"); // Забираем заранее подготовленную таблицу XSLT стилей из корня jar

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fileNameFrom); // Создаем документ из файла 1.xml

            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(is)); // Преобразуем один формат в другой с помощью XSLT
            transformer.transform(new DOMSource(doc), new StreamResult(fileNameTo)); // Сохраняем файл на диск как 2.xml

        } catch (Exception e) {
            toLog.error(e.toString());
        }


    }
}
