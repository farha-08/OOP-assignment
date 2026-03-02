// com.placement.system/Main.java
package com.placement.system;

import javax.swing.SwingUtilities;
import com.placement.system.views.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // Run GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
