package automatas.nivel2;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelPila extends JPanel {

    private final JTextField campoCadena = new JTextField(15);
    private final JComboBox<String> ejemplos = new JComboBox<>(new String[]{
            "(elige un ejemplo)", "ab", "aabb", "aaabbb", "aaaabbbb",
            "aab", "abb", "ba", "abab", "a", "b"});
    private final JLabel lblCinta = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel lblAccion = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel lblResultado = new JLabel(" ", SwingConstants.CENTER);
    private final JButton btnPaso = new JButton("Paso");
    private final JButton btnEjecutar = new JButton("Ejecutar");
    private final JButton btnReiniciar = new JButton("Reiniciar");
    private final JSlider velocidad = new JSlider(100, 1500, 600);
    private final Timer temporizador;
    private final Dibujo dibujo = new Dibujo();

    private AutomataPila automata;
    private AutomataPila.Config config;

    public PanelPila() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        arriba.add(new JLabel("Cadena:"));
        arriba.add(campoCadena);
        arriba.add(ejemplos);
        arriba.add(btnPaso);
        arriba.add(btnEjecutar);
        arriba.add(btnReiniciar);
        arriba.add(new JLabel("Velocidad:"));
        velocidad.setInverted(true);
        arriba.add(velocidad);

        JPanel info = new JPanel(new GridLayout(4, 1));
        JLabel descripcion = new JLabel(
                "Lenguaje: a^n b^n, n >= 1  (apila una A por cada 'a', desapila una por cada 'b')",
                SwingConstants.CENTER);
        lblCinta.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
        lblAccion.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 15));
        lblResultado.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        info.add(descripcion);
        info.add(lblCinta);
        info.add(lblAccion);
        info.add(lblResultado);

        add(arriba, BorderLayout.NORTH);
        add(dibujo, BorderLayout.CENTER);
        add(info, BorderLayout.SOUTH);

        temporizador = new Timer(velocidad.getValue(), e -> paso());
        velocidad.addChangeListener(e -> temporizador.setDelay(velocidad.getValue()));

        btnPaso.addActionListener(e -> paso());
        btnEjecutar.addActionListener(e -> {
            if (temporizador.isRunning()) {
                temporizador.stop();
                btnEjecutar.setText("Ejecutar");
            } else {
                if (automata.isTerminado()) reiniciar();
                temporizador.start();
                btnEjecutar.setText("Pausar");
            }
        });
        btnReiniciar.addActionListener(e -> reiniciar());
        ejemplos.addActionListener(e -> {
            if (ejemplos.getSelectedIndex() > 0) {
                campoCadena.setText((String) ejemplos.getSelectedItem());
                reiniciar();
            }
        });

        reiniciar();
    }

    private void reiniciar() {
        temporizador.stop();
        btnEjecutar.setText("Ejecutar");
        automata = new AutomataPila(campoCadena.getText().trim());
        config = automata.configuracionActual();
        lblAccion.setText("Listo para empezar");
        lblResultado.setText(" ");
        lblResultado.setForeground(Color.BLACK);
        actualizarCinta();
        dibujo.repaint();
    }

    private void paso() {
        if (automata.isTerminado()) {
            temporizador.stop();
            btnEjecutar.setText("Ejecutar");
            return;
        }
        config = automata.paso();
        lblAccion.setText(config.accion);
        if (automata.isTerminado()) {
            temporizador.stop();
            btnEjecutar.setText("Ejecutar");
            lblResultado.setText(automata.getMensaje());
            lblResultado.setForeground(automata.isAceptada()
                    ? new Color(0, 130, 0) : new Color(190, 0, 0));
        }
        actualizarCinta();
        dibujo.repaint();
    }

    private void actualizarCinta() {
        String cad = automata.getCadena();
        int pos = Math.min(config.posicion, cad.length());
        String leido = cad.substring(0, pos);
        String resto = cad.substring(pos);
        lblCinta.setText("<html><span style='color:gray'>" + leido
                + "</span><span style='color:#0050c8;text-decoration:underline'>"
                + (resto.isEmpty() ? "" : resto.charAt(0))
                + "</span>" + (resto.isEmpty() ? "" : resto.substring(1))
                + (cad.isEmpty() ? "(cadena vacía)" : "") + "</html>");
    }

    /** Dibuja el diagrama de estados y la pila como columna. */
    private class Dibujo extends JPanel {
        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Diagrama de estados (izquierda)
            String[] nombres = {"q0", "q1", "q2"};
            int r = 32;
            int zonaAncho = getWidth() / 2;
            int[] xs = {zonaAncho / 6 + 20, zonaAncho / 2, zonaAncho * 5 / 6 - 10};
            int cy = getHeight() / 2;

            // flecha de entrada
            g.setColor(Color.DARK_GRAY);
            g.drawLine(xs[0] - r - 30, cy, xs[0] - r, cy);

            // transiciones
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g.drawLine(xs[0] + r, cy, xs[1] - r, cy);
            g.drawString("b, A/ε", (xs[0] + xs[1]) / 2 - 18, cy - 6);
            g.drawLine(xs[1] + r, cy, xs[2] - r, cy);
            g.drawString("fin, $", (xs[1] + xs[2]) / 2 - 18, cy - 6);
            // bucles
            g.drawArc(xs[0] - 18, cy - r - 30, 36, 40, 0, 180);
            g.drawString("a, /A", xs[0] - 18, cy - r - 34);
            g.drawArc(xs[1] - 18, cy - r - 30, 36, 40, 0, 180);
            g.drawString("b, A/ε", xs[1] - 20, cy - r - 34);

            // círculos
            for (int i = 0; i < 3; i++) {
                boolean actual = nombres[i].equals(config.estado);
                g.setColor(actual ? new Color(255, 215, 0) : Color.WHITE);
                g.fillOval(xs[i] - r, cy - r, 2 * r, 2 * r);
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(2));
                g.drawOval(xs[i] - r, cy - r, 2 * r, 2 * r);
                if (i == 2) g.drawOval(xs[i] - r + 5, cy - r + 5, 2 * r - 10, 2 * r - 10);
                g.setStroke(new BasicStroke(1));
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(nombres[i], xs[i] - fm.stringWidth(nombres[i]) / 2, cy + 6);
            }

            // Pila (columna a la derecha)
            int anchoCaja = 90, altoCaja = 34;
            int px = getWidth() * 3 / 4 - anchoCaja / 2;
            int base = getHeight() - 40;
            List<Character> pila = new ArrayList<>(config.pila);
            int maxVisibles = Math.max(1, (base - 40) / altoCaja);

            g.setColor(Color.BLACK);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            g.drawString("PILA", px + 25, 22);

            // marco de la pila
            g.setColor(Color.DARK_GRAY);
            g.drawLine(px - 8, base, px + anchoCaja + 8, base);
            g.drawLine(px - 8, base - maxVisibles * altoCaja, px - 8, base);
            g.drawLine(px + anchoCaja + 8, base - maxVisibles * altoCaja, px + anchoCaja + 8, base);

            for (int i = 0; i < pila.size() && i < maxVisibles; i++) {
                int y = base - (i + 1) * altoCaja;
                boolean tope = (i == pila.size() - 1);
                char s = pila.get(i);
                g.setColor(s == '$' ? new Color(210, 210, 210)
                        : tope ? new Color(130, 200, 255) : new Color(190, 225, 255));
                g.fillRect(px, y + 2, anchoCaja, altoCaja - 4);
                g.setColor(Color.BLACK);
                g.drawRect(px, y + 2, anchoCaja, altoCaja - 4);
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
                g.drawString("" + s, px + anchoCaja / 2 - 6, y + altoCaja / 2 + 7);
                if (tope) {
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
                    g.drawString("<- tope", px + anchoCaja + 14, y + altoCaja / 2 + 5);
                }
            }
        }
    }
}