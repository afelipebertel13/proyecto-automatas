package automatas.nivel1;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PanelLBA extends JPanel {

    private final JTextField campoCadena = new JTextField(15);
    private final JComboBox<String> ejemplos = new JComboBox<>(new String[]{
            "(elige un ejemplo)", "abc", "aabbcc", "aaabbbccc", "aaaabbbbcccc",
            "aabbc", "abcc", "acb", "ba", "aabcc", "abcabc"});
    private final JLabel lblDescripcion = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel lblAlfabeto = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel lblAccion = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel lblResultado = new JLabel(" ", SwingConstants.CENTER);
    private final JButton btnPaso = new JButton("Paso");
    private final JButton btnEjecutar = new JButton("Ejecutar");
    private final JButton btnReiniciar = new JButton("Reiniciar");
    private final JSlider velocidad = new JSlider(50, 1500, 500);
    private final Timer temporizador;
    private final Dibujo dibujo = new Dibujo();

    private AutomataLBA maquina;

    public PanelLBA() {
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
        lblAlfabeto.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        lblAccion.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
        lblResultado.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        info.add(lblDescripcion);
        info.add(lblAlfabeto);
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
                if (maquina.isTerminada()) reiniciar();
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

        campoCadena.setText("aabbcc");
        reiniciar();
    }

    private void reiniciar() {
        temporizador.stop();
        btnEjecutar.setText("Ejecutar");
        maquina = AutomataLBA.anbncn();
        maquina.cargar(campoCadena.getText().trim());
        lblDescripcion.setText(maquina.getNombre() + ": " + maquina.getDescripcion());
        lblAlfabeto.setText("Alfabeto de la cinta: " + maquina.getAlfabetoCinta()
                + "   |   Memoria: " + (maquina.getLimiteDer() + 1)
                + " celdas (longitud de la entrada + 2 delimitadores)");
        lblAccion.setText("Listo para empezar");
        lblResultado.setText(" ");
        lblResultado.setForeground(Color.BLACK);
        dibujo.repaint();
    }

    private void paso() {
        if (maquina.isTerminada()) {
            temporizador.stop();
            btnEjecutar.setText("Ejecutar");
            return;
        }
        maquina.paso();
        lblAccion.setText("Paso " + maquina.getPasos() + ": " + maquina.getUltimaAccion());
        if (maquina.isTerminada()) {
            temporizador.stop();
            btnEjecutar.setText("Ejecutar");
            boolean ok = maquina.isAceptada();
            lblResultado.setText(ok ? "ACEPTADA" : "RECHAZADA");
            lblResultado.setForeground(ok ? new Color(0, 130, 0) : new Color(190, 0, 0));
        }
        dibujo.repaint();
    }

    /** Dibuja la cinta acotada, el cabezal y el diagrama de estados. */
    private class Dibujo extends JPanel {
        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            dibujarCinta(g);
            dibujarDiagrama(g);
        }

        private void dibujarCinta(Graphics2D g) {
            int total = maquina.getLimiteDer() + 1;
            int celda = Math.max(18, Math.min(34, (getWidth() - 40) / total));
            int anchoTotal = celda * total;
            int x0 = (getWidth() - anchoTotal) / 2;
            int cy = 15;

            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, Math.max(12, celda / 2 + 2)));
            FontMetrics fm = g.getFontMetrics();
            for (int pos = 0; pos < total; pos++) {
                int x = x0 + pos * celda;
                char c = maquina.leer(pos);
                boolean borde = (pos == 0 || pos == total - 1);
                boolean actual = (pos == maquina.getCabezal());
                Color fondo;
                if (actual) fondo = new Color(255, 230, 120);
                else if (borde) fondo = new Color(170, 170, 170);
                else if (c == 'X' || c == 'Y' || c == 'Z') fondo = new Color(215, 195, 240);
                else fondo = new Color(200, 230, 255);
                g.setColor(fondo);
                g.fillRect(x, cy, celda, celda);
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(actual ? 3 : 1));
                g.drawRect(x, cy, celda, celda);
                g.setStroke(new BasicStroke(1));
                String s = "" + c;
                g.drawString(s, x + (celda - fm.stringWidth(s)) / 2, cy + celda / 2 + fm.getAscent() / 2 - 2);
            }

            // cabezal
            int cx = x0 + maquina.getCabezal() * celda + celda / 2;
            int ay = cy + celda + 6;
            g.setColor(new Color(200, 0, 0));
            g.fillPolygon(new int[]{cx, cx - 9, cx + 9}, new int[]{ay, ay + 14, ay + 14}, 3);
            g.fillRect(cx - 3, ay + 14, 6, 8);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            g.drawString("cabezal", cx + 14, ay + 18);
            g.setColor(Color.BLACK);
            g.drawString("Estado activo: " + maquina.getEstado()
                    + "     Pasos: " + maquina.getPasos(), 12, ay + 18);
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            g.drawString("Gris = delimitadores (no se pueden sobrepasar)   Morado = símbolos marcados",
                    12, ay + 34);
        }

        private void dibujarDiagrama(Graphics2D g) {
            List<String> estados = new ArrayList<>(maquina.getEstados());
            int n = estados.size();
            int top = 130;
            int alto = getHeight() - top - 10;
            if (alto < 80) return;

            int r = 24;
            int porFila = Math.min(n, 4);
            int filas = (n + porFila - 1) / porFila;
            int[] xs = new int[n], ys = new int[n];
            for (int i = 0; i < n; i++) {
                int fila = i / porFila, col = i % porFila;
                int enFila = Math.min(porFila, n - fila * porFila);
                xs[i] = (col + 1) * getWidth() / (enFila + 1);
                ys[i] = top + (fila + 1) * alto / (filas + 1);
            }

            Map<String, StringBuilder> etiquetas = new LinkedHashMap<>();
            for (String[] rg : maquina.listarReglas()) {
                String clave = rg[0] + ">" + rg[4];
                etiquetas.computeIfAbsent(clave, k -> new StringBuilder())
                        .append(rg[1]).append("/").append(rg[2]).append(",").append(rg[3]).append("  ");
            }

            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            for (Map.Entry<String, StringBuilder> en : etiquetas.entrySet()) {
                String[] par = en.getKey().split(">");
                int i = estados.indexOf(par[0]), j = estados.indexOf(par[1]);
                String texto = en.getValue().toString().trim();
                g.setColor(Color.GRAY);
                if (i == j) {
                    g.drawArc(xs[i] - 14, ys[i] - r - 24, 28, 34, 0, 180);
                    g.setColor(new Color(60, 60, 160));
                    g.drawString(texto, xs[i] - g.getFontMetrics().stringWidth(texto) / 2,
                            ys[i] - r - 28);
                } else {
                    double ang = Math.atan2(ys[j] - ys[i], xs[j] - xs[i]);
                    double off = (maquina.listarReglas().stream()
                            .anyMatch(x -> x[0].equals(par[1]) && x[4].equals(par[0]))) ? 8 : 0;
                    double px = -Math.sin(ang) * off, py = Math.cos(ang) * off;
                    int x1 = (int) (xs[i] + Math.cos(ang) * r + px);
                    int y1 = (int) (ys[i] + Math.sin(ang) * r + py);
                    int x2 = (int) (xs[j] - Math.cos(ang) * r + px);
                    int y2 = (int) (ys[j] - Math.sin(ang) * r + py);
                    g.drawLine(x1, y1, x2, y2);
                    int[] ax = {x2, (int) (x2 - 9 * Math.cos(ang - 0.4)), (int) (x2 - 9 * Math.cos(ang + 0.4))};
                    int[] ay = {y2, (int) (y2 - 9 * Math.sin(ang - 0.4)), (int) (y2 - 9 * Math.sin(ang + 0.4))};
                    g.fillPolygon(ax, ay, 3);
                    g.setColor(new Color(60, 60, 160));
                    g.drawString(texto, (x1 + x2) / 2 - g.getFontMetrics().stringWidth(texto) / 2
                            + (int) (px * 1.5), (y1 + y2) / 2 - 4 + (int) (py * 1.5));
                }
            }

            for (int i = 0; i < n; i++) {
                String e = estados.get(i);
                boolean actual = e.equals(maquina.getEstado());
                g.setColor(actual ? new Color(255, 215, 0) : Color.WHITE);
                g.fillOval(xs[i] - r, ys[i] - r, 2 * r, 2 * r);
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(2));
                g.drawOval(xs[i] - r, ys[i] - r, 2 * r, 2 * r);
                if (maquina.getEstadosAceptacion().contains(e)) {
                    g.drawOval(xs[i] - r + 4, ys[i] - r + 4, 2 * r - 8, 2 * r - 8);
                }
                g.setStroke(new BasicStroke(1));
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                g.drawString(e, xs[i] - g.getFontMetrics().stringWidth(e) / 2, ys[i] + 5);
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            }
            g.setColor(Color.DARK_GRAY);
            g.drawString("Etiquetas: lee/escribe,movimiento", 12, getHeight() - 12);
        }
    }
}