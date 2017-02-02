package unity.fourcircle.ssh;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import unity.fourcircle.UserAuthKI.MyUserInfo;

public class SSHThread extends Thread {

    private String host;
    private String userName;
    private String password;
    private List<SSHData> dataList;

    public SSHThread(String host, String userName, String password) {
        this.host = host;
        this.userName = userName;
        this.password = password;
        this.dataList = new LinkedList<SSHData>();
    }

    public List<SSHData> getSSHData() {
        return dataList;
    }

    private void addSSHData(SSHData data) {
        dataList.add(data);
        if (dataList.size() > 100) {
            dataList.remove(0);
        }
    }

    @Override
    public void run() {
        try {
            JSch jsch = new JSch();

            Session session = jsch.getSession(userName, host, 22);

            UserInfo ui = new MyUserInfo();
            session.setPassword(password);
            session.setUserInfo(ui);

            System.out.println("Trying to connect...");
            session.connect();
            System.out.println("Connection successful.");

            Channel channel = session.openChannel("shell");
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            OutputStream toServer = channel.getOutputStream();
            channel.connect();
            String command = "sudo su - hpscp";
            System.out.println("Executing " + command);
            toServer.write((command + "\r\n").getBytes());
            toServer.flush();
            System.out.println("Executed.");
            Thread.sleep(1000);
            command = "./license.sh";
            System.out.println("Executing " + command);
            toServer.write((command + "\r\n").getBytes());
            toServer.flush();
            System.out.println("Executed.");

            System.out.println("Reading script output...");
            String line = "";

            while (line != null) {
                line = fromServer.readLine();
                System.out.println(line);
                String[] data = line.split("\t");
                String date = null;
                String time = null;
                String licenseCount = null;
                String direction = null;
                try {
                    String[] dateArray = data[0].split("m");
                    date = dateArray[1];
                    time = data[1];
                    licenseCount = data[2];
                    direction = data[4];
                } catch (ArrayIndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }
                if (direction.compareTo("in") == 0) {
                    SSHData sshData = new SSHData();
                    sshData.setDateString(date);
                    sshData.setTimeString(time);
                    sshData.setLicenseNumber(Long.parseLong(licenseCount));
                    addSSHData(sshData);
                }

            }

            channel.disconnect();
            session.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
