/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package client.view;

import client.controller.ClientCtr;
import client.controller.GameCtr;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import server.dao.PlayerDAO;
import shared.model.ObjectWrapper;
import shared.model.Player;

public class MainFrm extends javax.swing.JFrame {

    private ClientCtr mySocket;
    private JList<String> playerList;
    private DefaultListModel<String> playerListModel;
    private String enemy;

    public MainFrm(ClientCtr socket) {
        mySocket = socket;
        initComponents();
        
        lblPlayer.setText(mySocket.getUsername());

        playerListModel = new DefaultListModel<>();
        playerList = new JList<>(playerListModel);
        playerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                enemy = playerList.getSelectedValue();
                System.out.println(enemy);
            }
        });

        scrollWaitingList.setViewportView(playerList);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            System.out.println("In main form: " + mySocket.getUsername());
            mySocket.sendData(new ObjectWrapper(ObjectWrapper.UPDATE_WAITING_LIST_REQUEST, ""));
            System.out.println("Send update waiting list successfully");
        }
        super.setVisible(visible);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnRequest = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        scrollWaitingList = new javax.swing.JScrollPane();
        lblPlayer = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnRequest.setText("Request");
        btnRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRequestActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnClose.setText("Logout");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        lblPlayer.setText("name player");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollWaitingList, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnClose)
                            .addComponent(btnRefresh)
                            .addComponent(btnRequest))
                        .addGap(31, 31, 31))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(btnRequest)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRefresh)
                .addGap(28, 28, 28)
                .addComponent(btnClose)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPlayer, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(scrollWaitingList, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        mySocket.sendData(new ObjectWrapper(ObjectWrapper.UPDATE_WAITING_LIST_REQUEST, ""));
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        try {
            mySocket.sendData(new ObjectWrapper(ObjectWrapper.EXIT_MAIN_FORM));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mySocket.getLoginFrm().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRequestActionPerformed
        if (enemy != null) {
            try {
                mySocket.sendData(new ObjectWrapper(ObjectWrapper.SEND_PLAY_REQUEST, enemy));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a player", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnRequestActionPerformed

    public void receivedDataProcessing(ObjectWrapper data) {
        switch (data.getPerformative()) {
            case ObjectWrapper.SERVER_INFORM_CLIENT_WAITING:
                playerListModel.clear();

                String dataUsernameList = (String) data.getData();
                String[] usernameList = dataUsernameList.split("\\|\\|");

                for (String username : usernameList) {
                    if (!username.equals(mySocket.getUsername())) {
                        playerListModel.addElement(username);
                    }
                }
                break;
            case ObjectWrapper.RECEIVE_PLAY_REQUEST:
                String usernameEnemy = (String) data.getData();
                int k = JOptionPane.showConfirmDialog(this, usernameEnemy + " wants to play with you \n Do you accept", "Game Request", JOptionPane.YES_NO_OPTION);
                if (k == 1) {
                    mySocket.sendData(new ObjectWrapper(ObjectWrapper.REJECTED_PLAY_REQUEST));
                } else if (k == 0) {
                    mySocket.sendData(new ObjectWrapper(ObjectWrapper.ACCEPTED_PLAY_REQUEST));
                }
                break;
            case ObjectWrapper.SERVER_SET_GAME_READY:
//                SetShipFrm setShipFrm = new SetShipFrm(mySocket);
//                mySocket.setSetShipFrm(setShipFrm);

                GameCtr gameCtr = new GameCtr(mySocket);
                mySocket.setGameCtr(gameCtr);
                SetShipFrm setShipFrm = new SetShipFrm(gameCtr);
                gameCtr.setSetShipFrm(setShipFrm);

                gameCtr.getSetShipFrm().setVisible(true);
                this.dispose();
                break;
//            case ObjectWrapper.ENEMY_IN_GAME_ERROR:
//                JOptionPane.showMessageDialog(this, "Người chơi đã trong một trận đấu khác", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                break;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRequest;
    private javax.swing.JLabel lblPlayer;
    private javax.swing.JScrollPane scrollWaitingList;
    // End of variables declaration//GEN-END:variables
}
