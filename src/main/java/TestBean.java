import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestBean {
    private static Logger toLog = LogManager.getLogger("TestBean");
    private String url;
    private String batchPerformanceOptions = "?useServerPrepStmts=false&rewriteBatchedStatements=true";
    private String user;
    private String password;
    private int rowNum;

    public static void main(String[] args) {
        long before = System.currentTimeMillis();
        TestBean bean = new TestBean();

        if (args.length < 4) {
            toLog.error("Одного или более аргумента не хватает.");
            System.exit(1);
        }

        // Задаем параметры
        bean.setUrl(args[0]); // Url БД
        bean.setUser(args[1]); // Имя пользователя БД
        bean.setPassword(args[2]); // Пароль
        bean.setRowNum(args[3]); // Колличество строк которое будем вставлять

        toLog.info(args[0] + " " + args[1] + " " + args[2] + " " + args[3]);
        bean.clearTable(); // Очищаем таблицу
        bean.executeInsert(); // Выполняем вставку заданного количества строк

        IComposer xmlComposer = new XmlComposer();
        xmlComposer.compose(bean.executeSelect()); // Забираем данные из БД и формируем 1.xml файл

        IConverter XSLTConverter = new XSLTConverter();
        XSLTConverter.convert(); // Форматируем 1.xml файл в 2.xml при помощи XSLT

        IParser xmlFieldCounter = new XmlFieldCounter();
        xmlFieldCounter.parse(); // Парсим 2.xml и выводим сумму значений

        double estimateTime = System.currentTimeMillis() - before; // Считаем время затраченное на работу
        toLog.info("took " + estimateTime / 1000);
    }


    public TestBean() {

    }

    private Connection con;

    public void clearTable() { // Очищаем таблицу перед вставкой
        Statement stmt;
        String query = "TRUNCATE TABLE TEST";
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            stmt.executeQuery(query);

        } catch (SQLException e) {
            toLog.error(e.toString());
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                toLog.error(e.toString());
            }
        }
    }

    public void executeInsert() { //  Вставляем строки в БД
        PreparedStatement ps = null;
        String query = "insert into TEST (FIELD) VALUES (?)";
        try {
            con = DriverManager.getConnection(url, user, password);
            ps = con.prepareStatement(query);

            int i;
            for (i = 1; i <= rowNum; i++) {
                ps.setInt(1, i);
                ps.addBatch();

                if (i % 1000 == 0) { // Делаем вставку каждую 1000 строк
                    ps.executeBatch();
                }
            }
            ps.executeBatch();

        } catch (SQLException e) {
            toLog.error(e.toString());
        } finally {
            try {
                con.close();
                if (ps != null) ps.close();
            } catch (Exception e) {
                toLog.error(e.toString());
            }
        }
    }

    public List<Integer> executeSelect() { // Забираем строки из БД
        List<Integer> res = new ArrayList<>();
        Statement stmt = null;
        String query = "select field from TEST";
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                res.add(rs.getInt(1));
            }

        } catch (SQLException e) {
            toLog.error(e.toString());
        } finally {
            try {
                con.close();
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                toLog.error(e.toString());
            }
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestBean testBean = (TestBean) o;

        if (rowNum != testBean.rowNum) return false;
        if (!url.equals(testBean.url)) return false;
        if (!batchPerformanceOptions.equals(testBean.batchPerformanceOptions)) return false;
        if (!user.equals(testBean.user)) return false;
        if (!password.equals(testBean.password)) return false;
        return con.equals(testBean.con);

    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + batchPerformanceOptions.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + rowNum;
        result = 31 * result + con.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "url='" + url + '\'' +
                ", batchPerformanceOptions='" + batchPerformanceOptions + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", rowNum=" + rowNum +
                ", con=" + con +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url + batchPerformanceOptions;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(String rowNum) {
        try {
            this.rowNum = Integer.parseInt(rowNum);
        } catch (NumberFormatException e) {
            toLog.error(e.toString());
        }
    }
}
