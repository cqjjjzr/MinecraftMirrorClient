import charlie.mirror.client.MirrorList;
import charlie.mirror.client.MojangClient;

import javax.swing.*;
import javax.swing.event.ListDataListener;

public class MCMirrorExample {
    private JList<String> lstMirrors;
    private JPanel pnlContents;
    private JPanel pnlExample;
    private JTextField txtVersion;
    private JTextField txtLibrary;
    private JTextField txtAssets;
    private JButton btnAdd;
    private JButton btnRemove;
    private JTextField txtResult;
    private JButton btnVersion;
    private JButton btnLibrary;
    private JButton btnAssets;
    private JScrollPane scpList;

    private MirrorList list = new MirrorList();
    private MojangClient client = new MojangClient(list);

    public MCMirrorExample() {
        lstMirrors.setModel(new ListModel<String>() {
            @Override
            public int getSize() {
                return list.size();
            }

            @Override
            public String getElementAt(int index) {
                return list.elementAt(index).toString();
            }

            @Override
            public void addListDataListener(ListDataListener l) {}

            @Override
            public void removeListDataListener(ListDataListener l) {}
        });
        lstMirrors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        btnAdd.addActionListener(e -> {
            String str = JOptionPane.showInputDialog(null, "JSON:", "Add mirror", JOptionPane.QUESTION_MESSAGE);
            list.add(str);
            lstMirrors.repaint();
            lstMirrors.updateUI();
        });
        btnRemove.addActionListener(e -> {
            if(lstMirrors.getSelectedValue().equals("Mojang")){
                JOptionPane.showMessageDialog(null, "Can't remove Mojang source!", "Error", JOptionPane.ERROR_MESSAGE);
            }else{
                list.remove(lstMirrors.getSelectedValue());
                lstMirrors.repaint();
                lstMirrors.updateUI();
            }
        });
        btnVersion.addActionListener(e -> updateVersion());
        btnLibrary.addActionListener(e -> updateLibrary());
        btnAssets.addActionListener(e -> updateAssets());
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("MCMirrorExample");
        frame.setContentPane(new MCMirrorExample().pnlContents);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(100, 100, 700, 500);
        frame.setVisible(true);
    }

    public void updateVersion(){
        txtResult.setText(client.getVersion(txtVersion.getText()));
        if(JOptionPane.showConfirmDialog(null, "Is this result OK?", "Result", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION && !txtResult.getText().startsWith("https://launcher.mojang.com")){
            client.addErrorURL(txtResult.getText());
            updateVersion();
        }
    }

    public void updateLibrary(){
        txtResult.setText(client.getLibraries(txtLibrary.getText()));
        if(JOptionPane.showConfirmDialog(null, "Is this result OK?", "Result", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION && !txtResult.getText().startsWith("https://libraries.mojang.net")){
            client.addErrorURL(txtResult.getText());
            updateLibrary();
        }
    }

    public void updateAssets(){
        txtResult.setText(client.getAssets(txtAssets.getText()));
        if(JOptionPane.showConfirmDialog(null, "Is this result OK?", "Result", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION && !txtResult.getText().startsWith("https://launcher.mojang.com")){
            client.addErrorURL(txtResult.getText());
            updateAssets();
        }
    }
}
