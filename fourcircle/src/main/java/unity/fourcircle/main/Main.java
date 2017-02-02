package unity.fourcircle.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.jfree.ui.RefineryUtilities;

import unity.fourcircle.base64.Base64;
import unity.fourcircle.chart.XYChart;
import unity.fourcircle.ssh.SSHData;
import unity.fourcircle.ssh.SSHThread;

public class Main {

    public static void main(String[] args) {
        Properties prop = new Properties();
        InputStream input = null;
        String host = null;
        String userName = null;
        String password = null;

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            host = prop.getProperty("host");
            userName = prop.getProperty("userName");
            byte[] decoded = Base64.base64ToByteArray(prop.getProperty("password"));
            password = new String(decoded);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        SSHThread sshThread = new SSHThread(host, userName, password);
        sshThread.run();

        while (!sshThread.isReady()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        XYChart chart = new XYChart("XY Series Demo");
        SSHData lastData = null;
        while (true) {
            List<SSHData> currentDataList = sshThread.getSSHData();
            if (!lastData.equals(currentDataList.get(currentDataList.size() - 1))) {
                chart.setContentPane(XYChart.generateChart(sshThread.getSSHData()));
                chart.pack();
                RefineryUtilities.centerFrameOnScreen(chart);
                chart.setVisible(true);
            }
            lastData = currentDataList.get(currentDataList.size() - 1);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
