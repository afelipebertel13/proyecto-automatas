package automatas.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class Estilo {

    public static final Color FONDO = new Color(238, 243, 252);
    public static final Color BARRA = new Color(30, 45, 90);
    public static final Color BOTON = new Color(52, 105, 200);

    
    public static void aplicar(Container contenedor) {
        for (Component hijo : contenedor.getComponents()) {
            if (hijo instanceof JComboBox) continue;
            if (hijo instanceof JButton) {
                estilizarBoton((JButton) hijo, BOTON);
            } else if (hijo instanceof JSlider) {
                ((JSlider) hijo).setOpaque(false);
            } else if (hijo instanceof JPanel) {
                ((JPanel) hijo).setBackground(FONDO);
            }
            if (hijo instanceof Container) aplicar((Container) hijo);
        }
    }

    public static void estilizarBoton(JButton b, Color base) {
        b.setUI(new BotonRedondo(base));
        b.setForeground(Color.WHITE);
        b.setFont(b.getFont().deriveFont(Font.BOLD));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static class BotonRedondo extends BasicButtonUI {
        private final Color base;

        BotonRedondo(Color base) {
            this.base = base;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel m = b.getModel();
            Color color = m.isPressed() ? base.darker()
                    : m.isRollover() ? base.brighter() : base;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
            g2.dispose();
            super.paint(g, c);
        }
    }
}