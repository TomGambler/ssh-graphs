package unity.fourcircle.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import unity.fourcircle.base64.Base64;
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

    }

}
