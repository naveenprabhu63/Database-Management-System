package com.crime;

import javax.swing.*;
import java.awt.*;

public class DarkMode {
    private static boolean dark = false;
    public static void toggle(JFrame frame) {
        dark = !dark;
        try {
            if (dark) {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                UIManager.put("Panel.background", new Color(40, 40, 40));
                UIManager.put("OptionPane.background", new Color(40, 40, 40));
                UIManager.put("OptionPane.messageForeground", Color.WHITE);
                UIManager.put("TextField.background", new Color(60, 60, 60));
                UIManager.put("TextField.foreground", Color.WHITE);
                UIManager.put("Button.background", new Color(70, 70, 70));
                UIManager.put("Button.foreground", Color.WHITE);
                UIManager.put("Table.background", new Color(50, 50, 50));
                UIManager.put("Table.foreground", Color.WHITE);
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            SwingUtilities.updateComponentTreeUI(frame);
            frame.repaint();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
