import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

public class XmlComposer implements IComposer {
    private Logger toLog = LogManager.getLogger("TestBean");

    @Override
    public void compose(List<Integer> list) {
        String filename = "1.xml";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document doc = factory.newDocumentBuilder().newDocument(); // Создаем новый xml документ

            Element root = doc.createElement("entries"); // Создаем корневой элемент
            for (Integer i : list) { // В цикле добавляем строки со значениями из БД
                Element entry = doc.createElement("entry");
                Element field = doc.createElement("field");
                field.setTextContent(i.toString());
                entry.appendChild(field);
                root.appendChild(entry);
            }
            doc.appendChild(root);
            doc.normalizeDocument();

            File file = new File(filename);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(file)); // Записываем файл на диск

        } catch (ParserConfigurationException | TransformerException e) {
            toLog.error(e.toString());
        }

    }
}
