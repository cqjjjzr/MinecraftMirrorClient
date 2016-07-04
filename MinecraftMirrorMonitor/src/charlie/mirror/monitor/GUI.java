package charlie.mirror.monitor;

import charlie.mirror.server.DownloadTask;
import charlie.mirror.server.MinecraftMirror;
import charlie.mirror.server.remote.RemoteFetcher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

public class GUI {
    public JTable tblTasks;
    private JPanel pnlContents;
    private JLabel lblProgress;

    public Vector<Vector<Object>> rootVector = new Vector<>();
    public Vector<String> columnName = new Vector<>();

    public boolean status = false;
    private RemoteFetcher obj;

    public GUI(String url) throws RemoteException, NotBoundException, MalformedURLException {
        //tblTasks.setModel(new QueueModel());
        columnName.addAll(Arrays.asList("URL", "Path", "Status", "Message"));
        tblTasks.setModel(new DefaultTableModel(rootVector, columnName));
        obj = (RemoteFetcher) Naming.lookup(url);
        new Thread(new TableUpdater()).start();
    }

    protected static void show(String url) throws RemoteException, NotBoundException, MalformedURLException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        JFrame jFrame = new JFrame("MinecraftMirror " + MinecraftMirror.VERSION + " Remote Monitor");
        jFrame.setContentPane(new GUI(url).pnlContents);
        jFrame.pack();
        jFrame.setBounds(50, 50, 500, 400);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }

    private class TableUpdater implements Runnable {
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                Map<URL, DownloadTask> map;
                int full;
                try {
                    map = obj.getTasks();
                    full = obj.getFullSize();
                } catch (RemoteException e) {
                    continue;
                }
                SwingUtilities.invokeLater(() -> {
                    rootVector.clear();
                    for (DownloadTask task:map.values()) {
                        Vector<Object> sinT = new Vector<>();
                        sinT.add(task.getUrl().toString());
                        sinT.add(task.getPath().toString());
                        sinT.add(task.getStatus().toString());
                        sinT.add(task.getMessage());
                        rootVector.add(sinT);
                    }
                    try{
                        tblTasks.repaint();
                        tblTasks.updateUI();
                    } catch (Exception ignored) {}
                    if(full > 0){
                        lblProgress.setText("Remain:" + map.size() + "/" + full);
                    }
                });
            }
        }
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        String url = JOptionPane.showInputDialog("URL?", "rmi://127.0.0.1:20100/MinecraftMirror");
        GUI.show(url);
    }
}
