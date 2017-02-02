package unity.fourcircle;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class UserAuthKI {
	public static void main(String[] arg) {

		try {
			JSch jsch = new JSch();

			String host = null;
			if (arg.length > 0) {
				host = arg[0];
			} else {
				host = JOptionPane.showInputDialog("Enter username@hostname", "axhskmj" + "@10.252.105.218");
			}
			String user = host.substring(0, host.indexOf('@'));
			host = host.substring(host.indexOf('@') + 1);

			Session session = jsch.getSession(user, host, 22);

			// username and passphrase will be given via UserInfo interface.
			UserInfo ui = new MyUserInfo();
			// ui.promptPassword("Enter password");
			String passwd = "Ye0Fn4jn"; /*
										 * JOptionPane.
										 * showInputDialog("Enter password");
										 */
			session.setPassword(passwd);
			session.setUserInfo(ui);

			System.out.println("User: " + session.getUserName());
			System.out.println("Password: " + passwd);
			System.out.println("Host: " + session.getHost());

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
			Thread.sleep(2000);
			command = "./license.sh";
			System.out.println("Executing " + command);
			toServer.write((command + "\r\n").getBytes());
			toServer.flush();
			System.out.println("Executed.");

			System.out.println("Reading script output...");
			String line = "";

			while (line != null) {
				line = fromServer.readLine();
				String[] data = line.split("\t");
				System.out.println();
				String[] dateArray = data[0].split("m");
				for (String element : dateArray) {
					System.out.print(element + " ");
				}

				for (String element : data) {
					System.out.print(element + " ");
				}
				// String time = data[1];
				// String licenseCount = data[2];
				// String direction = data[4];
				// System.out.print(time + " license count: " + licenseCount + "
				// " + direction);
				System.out.println();

			}

			channel.disconnect();
			session.disconnect();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

    public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
        @Override
		public String getPassword() {
			return passwd;
		}

        @Override
		public boolean promptYesNo(String str) {
			Object[] options = { "yes", "no" };
			int foo = JOptionPane.showOptionDialog(null, str, "Warning", JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			return foo == 0;
		}

		String passwd;
		JTextField passwordField = new JPasswordField(20);

        @Override
		public String getPassphrase() {
			return null;
		}

        @Override
		public boolean promptPassphrase(String message) {
			return false;
		}

        @Override
		public boolean promptPassword(String message) {
			Object[] ob = { passwordField };
			int result = JOptionPane.showConfirmDialog(null, ob, message, JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				passwd = passwordField.getText();
				return true;
			} else {
				return false;
			}
		}

        @Override
		public void showMessage(String message) {
			JOptionPane.showMessageDialog(null, message);
		}

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		private Container panel;

        @Override
		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
				boolean[] echo) {
			/*
			 * //System.out.println("promptKeyboardInteractive");
			 * System.out.println("destination: "+destination);
			 * System.out.println("name: "+name);
			 * System.out.println("instruction: "+instruction);
			 * System.out.println("prompt.length: "+prompt.length);
			 * System.out.println("prompt: "+prompt[0]);
			 */
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts = new JTextField[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]), gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if (echo[i]) {
					texts[i] = new JTextField(20);
				} else {
					texts[i] = new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if (JOptionPane.showConfirmDialog(null, panel, destination + ": " + name, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
				String[] response = new String[prompt.length];
				for (int i = 0; i < prompt.length; i++) {
					response[i] = texts[i].getText();
				}
				return response;
			} else {
				return null; // cancel
			}
		}
	}
}