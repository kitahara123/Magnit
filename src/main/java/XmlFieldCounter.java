import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlFieldCounter implements IParser {
    private Logger toLog = LogManager.getLogger("TestBean");

    @Override
    public void parse() {
        String fileName = "2.xml";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fileName); // Создаем документ из файла 2.xml
            NodeList nl = doc.getFirstChild().getChildNodes(); // Забираем все entry
            long summ = 0;
            for (int i = 1; i <= nl.getLength(); i++) { // Бижим по списку entry
                Node xmlNode = nl.item(i);
                Node item = null;

                if (xmlNode != null && xmlNode.getAttributes() != null) { // Костыль. Почему-то при парсинге на каждую полную ноду появилась пустая. Их пропускаем.
                    item = xmlNode.getAttributes().getNamedItem("field");
                }

                if (item != null) {
                    String str = item.getNodeValue();
                    summ = summ + Integer.parseInt(str); // Суммируем значения
                }
            }
            toLog.info("result: " + summ); // Выводим результат, ура


        } catch (Exception e) {
            toLog.error(e.toString());
        }
    }
}
