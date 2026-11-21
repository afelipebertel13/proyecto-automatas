package automatas.nivel3;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelAFD extends JPanel {

    private final AFD afd = AFD.paresDeCeros();

    private final JTextField campoCadena = new JTextField(15);
    private final JComboBox<String> ejemplos = new JComboBox<>(new String[]{
            "(elige un ejemplo)", "", "1", "0", "00", "1010", "10101", "0110", "0001"});
    private final JLabel lblCinta = new JLabel(" ");
    private final JLabel lblResultado = new JLabel(" ", SwingConstants.CENTER);
    private final JButton btnPaso = new JButton("Paso");
    private final JButton btnEjecutar = new JButton("Ejecutar");
    private final JButton btnReiniciar = new JButton("Reiniciar");
    private final JSlider velocidad = new JSlider(100, 1500, 600);
    private final Timer temporizador;

    private final Dibujo dibujo = new Dibujo();

    private String cadena = "";
    private int posicion = 0;          // símbolos ya consumidos
    private String estadoActual;
    private boolean terminado = false;

    public PanelAFD() {
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

        JPanel info = new JPanel(new GridLayout(3, 1));
        JLabel descripcion = new JLabel("Lenguaje: " + afd.getNombre()
                + "  (alfabeto {0,1}; acepta si la cantidad de 0s es par)", SwingConstants.CENTER);
        lblCinta.setHorizontalAlignment(SwingConstants.CENTER);
        lblCinta.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
        lblResultado.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        info.add(descripcion);
        info.add(lblCinta);
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
                if (terminado) reiniciar();
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
        cadena = campoCadena.getText().trim();
        posicion = 0;
        estadoActual = afd.getEstadoInicial();
        terminado = false;
        lblResultado.setText(" ");
        lblResultado.setForeground(Color.BLACK);
        actualizarCinta();
        dibujo.repaint();
    }

    private void paso() {
        if (terminado) {
            temporizador.stop();
            btnEjecutar.setText("Ejecutar");
            return;
        }
        if (posicion == 0 && !campoCadena.getText().trim().equals(cadena)) {
            reiniciar();
        }
        if (posicion < cadena.length()) {
            char c = cadena.charAt(posicion);
            String sig = afd.siguiente(estadoActual, c);
            if (sig == null) {
                terminar(false, "RECHAZADA: el símbolo '" + c + "' no está en el alfabeto");
                return;
            }
            estadoActual = sig;
            posicion++;
        }
        if (posicion == cadena.length()) {
            boolean ok = afd.getEstadosFinales().contains(estadoActual);
            terminar(ok, ok ? "ACEPTADA" : "RECHAZADA");
        }
        actualizarCinta();
        dibujo.repaint();
    }

    private void terminar(boolean ok, String mensaje) {
        terminado = true;
        temporizador.stop();
        btnEjecutar.setText("Ejecutar");
        lblResultado.setText(mensaje);
        lblResultado.setForeground(ok ? new Color(0, 130, 0) : new Color(190, 0, 0));
        actualizarCinta();
        dibujo.repaint();
    }

    private void actualizarCinta() {
        String leido = cadena.substring(0, Math.min(posicion, cadena.length()));
        String resto = cadena.substring(Math.min(posicion, cadena.length()));
        lblCinta.setText("<html><span style='color:gray'>" + leido
                + "</span><span style='color:#0050c8;text-decoration:underline'>"
                + (resto.isEmpty() ? "" : resto.charAt(0))
                + "</span>" + (resto.isEmpty() ? "" : resto.substring(1))
                + (cadena.isEmpty() ? "(cadena vacía)" : "") + "</html>");
    }

    /** Zona donde se dibujan los estados. */
    private class Dibujo extends JPanel {
        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            List<String> estados = new ArrayList<>(afd.getEstados());
            int n = estados.size();
            int r = 40;
            int paso = getWidth() / (n + 1);
            int y = getHeight() / 2;

            // posiciones
            int[] xs = new int[n];
            for (int i = 0; i < n; i++) xs[i] = paso * (i + 1);

            // flecha de entrada al estado inicial
            int idxIni = estados.indexOf(afd.getEstadoInicial());
            g.setColor(Color.DARK_GRAY);
            g.drawLine(xs[idxIni] - r - 40, y, xs[idxIni] - r, y);
            g.fillPolygon(new int[]{xs[idxIni] - r, xs[idxIni] - r - 10, xs[idxIni] - r - 10},
                    new int[]{y, y - 5, y + 5}, 3);

            // transiciones (etiquetas simples entre estados consecutivos y bucles)
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            for (int i = 0; i < n; i++) {
                String e = estados.get(i);
                for (char s : afd.getAlfabeto()) {
                    String dest = afd.siguiente(e, s);
                    if (dest == null) continue;
                    int j = estados.indexOf(dest);
                    if (i == j) {
                        g.setColor(Color.GRAY);
                        g.drawArc(xs[i] - 20, y - r - 40, 40, 50, 0, 180);
                        g.drawString("" + s, xs[i] - 4 + (s == '0' ? -12 : 12), y - r - 44);
                    } else {
                        int dy = (i < j) ? -25 : 25;
                        g.setColor(Color.GRAY);
                        int x1 = xs[i] + (i < j ? r : -r), x2 = xs[j] + (i < j ? -r : r);
                        g.drawLine(x1, y + dy, x2, y + dy);
                        g.drawString("" + s, (x1 + x2) / 2, y + dy + (i < j ? -6 : 16));
                    }
                }
            }

            // círculos de estados
            for (int i = 0; i < n; i++) {
                String e = estados.get(i);
                boolean actual = e.equals(estadoActual);
                g.setColor(actual ? new Color(255, 215, 0) : Color.WHITE);
                g.fillOval(xs[i] - r, y - r, 2 * r, 2 * r);
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(2));
                g.drawOval(xs[i] - r, y - r, 2 * r, 2 * r);
                if (afd.getEstadosFinales().contains(e)) {
                    g.drawOval(xs[i] - r + 6, y - r + 6, 2 * r - 12, 2 * r - 12);
                }
                g.setStroke(new BasicStroke(1));
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(e, xs[i] - fm.stringWidth(e) / 2, y + 6);
            }
        }
    }
}