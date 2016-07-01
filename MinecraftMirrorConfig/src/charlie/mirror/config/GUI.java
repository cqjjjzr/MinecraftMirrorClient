package charlie.mirror.config;

import charlie.mirror.server.ConfigManager;
import charlie.mirror.server.MinecraftMirror;

import javax.swing.*;
import java.util.prefs.Preferences;

public class GUI {
    private JTextField txtHttpRoot;
    private JPanel pnlContent;
    private JButton btnBrowse;
    private JLabel lblHttpRoot;
    private JLabel lblConnectTimeout;
    private JTextField txtConnectTimeout;
    private JLabel lblReadTimeout;
    private JTextField txtReadTimeout;
    private JLabel lblUserAgent;
    private JTextField txtUserAgent;
    private JLabel lblMaxSize;
    private JTextField txtMaxSize;
    private JButton btnSave;
    private JTextField txtInterval;
    private JLabel lblInterval;
    private JLabel lblNamingPort;
    private JLabel lblDataPort;
    private JTextField txtNamingPort;
    private JTextField txtDataPort;

    private JFileChooser jFileChooser = new JFileChooser();

    private Preferences preferences;

    public GUI() {
        this.preferences = Preferences.userNodeForPackage(ConfigManager.class);
        txtHttpRoot.setText(preferences.get("HttpRoot", "D:\\httproot"));
        txtConnectTimeout.setText(String.valueOf(preferences.getInt("ConnectTimeOut", 1000000)));
        txtReadTimeout.setText(String.valueOf(preferences.getInt("ReadTimeOut", 1000000)));
        txtUserAgent.setText(preferences.get("UserAgent", "Minecraft Mirror " + MinecraftMirror.VERSION));
        txtMaxSize.setText(String.valueOf(preferences.getInt("MaxQueue", 50)));
        txtInterval.setText(String.valueOf(preferences.getInt("IntervalHour", 1)));
        txtNamingPort.setText(String.valueOf(preferences.getInt("RemoteNamingPort", 20100)));
        txtDataPort.setText(String.valueOf(preferences.getInt("RemoteDataPort", 20200)));
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        btnBrowse.addActionListener(e -> {
            if(jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                txtHttpRoot.setText(jFileChooser.getSelectedFile().toString());
            }
        });
        btnSave.addActionListener(e -> {
            preferences.put("HttpRoot", txtHttpRoot.getText());
            preferences.putInt("ConnectTimeOut", Integer.parseInt(txtConnectTimeout.getText()));
            preferences.putInt("ReadTimeOut", Integer.parseInt(txtReadTimeout.getText()));
            preferences.put("UserAgent", txtUserAgent.getText());
            preferences.putInt("IntervalHour", Integer.parseInt(txtInterval.getText()));
            preferences.putInt("MaxSize", Integer.parseInt(txtMaxSize.getText()));
            preferences.putInt("RemoteNamingPort", Integer.parseInt(txtNamingPort.getText()));
            preferences.putInt("RemoteDataPort", Integer.parseInt(txtDataPort.getText()));
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ignored) {}
        JFrame frame = new JFrame("Minecraft Mirror Config");
        frame.setContentPane(new GUI().pnlContent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
