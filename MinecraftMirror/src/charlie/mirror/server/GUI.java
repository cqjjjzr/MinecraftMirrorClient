package charlie.mirror.server;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.Exchanger;

import static charlie.mirror.server.MinecraftMirror.queue;

public class GUI {
    public JTable tblTasks;
    private JPanel pnlContents;

    public Vector<Vector<Object>> rootVector = new Vector<>();
    public Vector<String> columnName = new Vector<>();

    public static Exchanger<Void> exchanger = new Exchanger<>();

    public boolean status = false;

    public GUI() {
        MojangInv mojangInv = new MojangInv(new MojangClient());
        MinecraftMirror.processPool.execute(mojangInv);

        //tblTasks.setModel(new QueueModel());
        columnName.addAll(Arrays.asList("URL", "Path", "Status", "Message"));
        tblTasks.setModel(new DefaultTableModel(rootVector, columnName));
        MinecraftMirror.processPool.execute(new TableUpdater());
    }

    protected static void show() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        JFrame jFrame = new JFrame("MinecraftMirror " + MinecraftMirror.VERSION);
        jFrame.setContentPane(new GUI().pnlContents);
        jFrame.pack();
        jFrame.setBounds(50, 50, 500, 400);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }

    private class MojangInv implements Runnable {
        private MojangClient client;

        public MojangInv(MojangClient client){
            this.client = client;
        }

        @Override
        public void run() {
            while(true){
                client.update();
                try {
                    Thread.sleep(MinecraftMirror.configManager.getIntervalHour() * 1000 * 60 * 60);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private class TableUpdater implements Runnable {
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                SwingUtilities.invokeLater(() -> {
                    rootVector.clear();
                    for (DownloadTask task:queue.values()) {
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
                });
            }
        }
    }
}
