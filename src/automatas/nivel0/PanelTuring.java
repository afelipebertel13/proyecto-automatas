package automatas.nivel0;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PanelTuring extends JPanel {

    private final JComboBox<String> programas = new JComboBox<>(new String[]{
            "Palíndromos {a,b}", "Suma en unario"});
    private final JTextField campoCadena = new JTextField(15);
    private final JComboBox<String> ejemplos = new JComboBox<>();
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

    private MaquinaTuring maquina;
    private boolean cargando = false;

    public PanelTuring() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        arriba.add(new JLabel("Programa:"));
        arriba.add(programas);
        arriba.add(new JLabel("Entrada:"));
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
        programas.addActionListener(e -> cambiarPrograma());
        ejemplos.addActionListener(e -> {
            if (!cargando && ejemplos.getSelectedIndex() > 0) {
                campoCadena.setText((String) ejemplos.getSelectedItem());
                reiniciar();
            }
        });

        cambiarPrograma();
    }

    private void cambiarPrograma() {
        cargando = true;
        ejemplos.removeAllItems();
        ejemplos.addItem("(elige un ejemplo)");
        String[] lista;
        if (programas.getSelectedIndex() == 0) {
            lista = new String[]{"a", "aba", "abba", "baab", "ababa", "ab", "aab", "abab"};
        } else {
            lista = new String[]{"111+11", "1+1", "1111+111", "11+", "+11"};
        }
        for (String s : lista) ejemplos.addItem(s);
        campoCadena.setText(lista[0]);
        cargando = false;
        reiniciar();
    }

    private void reiniciar() {
        temporizador.stop();
        btnEjecutar.setText("Ejecutar");
        maquina = programas.getSelectedIndex() == 0
                ? MaquinaTuring.palindromos() : MaquinaTuring.sumaUnaria();
        maquina.cargar(campoCadena.getText().trim());
        lblDescripcion.setText(maquina.getNombre() + ": " + maquina.getDescripcion());
        lblAlfabeto.setText("Alfabeto de la cinta: " + maquina.getAlfabetoCinta()
                + "   (_ = blanco)");
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
            String msg = ok ? "ACEPTADA" : "RECHAZADA";
            if (programas.getSelectedIndex() == 1) {
                msg += "  -  Resultado en la cinta: " + maquina.contenidoCinta()
                        + " (" + maquina.contenidoCinta().length() + ")";
            }
            lblResultado.setText(msg);
            lblResultado.setForeground(ok ? new Color(0, 130, 0) : new Color(190, 0, 0));
        }
        dibujo.repaint();
    }

    /** Dibuja la cinta, el cabezal y el diagrama de estados. */
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
            int celda = 34;
            int cy = 15;
            int cx = getWidth() / 2;
            int cab = maquina.getCabezal();
            int visibles = getWidth() / celda / 2 + 2;

            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
            FontMetrics fm = g.getFontMetrics();
            for (int k = -visibles; k <= visibles; k++) {
                int pos = cab + k;
                int x = cx + k * celda - celda / 2;
                boolean actual = (k == 0);
                char c = maquina.leer(pos);
                g.setColor(actual ? new Color(255, 230, 120)
                        : c == MaquinaTuring.BLANCO ? new Color(245, 245, 245) : new Color(200, 230, 255));
                g.fillRect(x, cy, celda, celda);
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(actual ? 3 : 1));
                g.drawRect(x, cy, celda, celda);
                g.setStroke(new BasicStroke(1));
                String s = "" + c;
                g.drawString(s, x + (celda - fm.stringWidth(s)) / 2, cy + celda / 2 + 6);
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                g.setColor(Color.GRAY);
                String p = "" + pos;
                g.drawString(p, x + (celda - g.getFontMetrics().stringWidth(p)) / 2, cy + celda + 11);
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
            }

            // cabezal (flecha bajo la celda central)
            int ay = cy + celda + 16;
            g.setColor(new Color(200, 0, 0));
            g.fillPolygon(new int[]{cx, cx - 9, cx + 9}, new int[]{ay, ay + 14, ay + 14}, 3);
            g.fillRect(cx - 3, ay + 14, 6, 8);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            g.drawString("cabezal", cx + 14, ay + 18);
            g.setColor(Color.BLACK);
            g.drawString("Estado activo: " + maquina.getEstado()
                    + "     Pasos: " + maquina.getPasos(), 12, ay + 18);
        }

        private void dibujarDiagrama(Graphics2D g) {
            List<String> estados = new ArrayList<>(maquina.getEstados());
            int n = estados.size();
            int top = 125;
            int alto = getHeight() - top - 10;
            if (alto < 80) return;

            int r = 24;
            int porFila = Math.min(n, 5);
            int filas = (n + porFila - 1) / porFila;
            int[] xs = new int[n], ys = new int[n];
            for (int i = 0; i < n; i++) {
                int fila = i / porFila, col = i % porFila;
                int enFila = Math.min(porFila, n - fila * porFila);
                xs[i] = (col + 1) * getWidth() / (enFila + 1);
                ys[i] = top + (fila + 1) * alto / (filas + 1);
            }

            // agrupar etiquetas por par (desde, hacia)
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
                    g.drawString(texto, xs[i] - g.getFontMetrics().stringWidth(texto) / 2,
                            ys[i] - r - 28);
                } else {
                    double ang = Math.atan2(ys[j] - ys[i], xs[j] - xs[i]);
                    // desplazamiento perpendicular para separar ida y vuelta
                    double off = (maquina.listarReglas().stream()
                            .anyMatch(x -> x[0].equals(par[1]) && x[4].equals(par[0]))) ? 8 : 0;
                    double px = -Math.sin(ang) * off, py = Math.cos(ang) * off;
                    int x1 = (int) (xs[i] + Math.cos(ang) * r + px);
                    int y1 = (int) (ys[i] + Math.sin(ang) * r + py);
                    int x2 = (int) (xs[j] - Math.cos(ang) * r + px);
                    int y2 = (int) (ys[j] - Math.sin(ang) * r + py);
                    g.drawLine(x1, y1, x2, y2);
                    // punta de flecha
                    int[] ax = {x2, (int) (x2 - 9 * Math.cos(ang - 0.4)), (int) (x2 - 9 * Math.cos(ang + 0.4))};
                    int[] ay = {y2, (int) (y2 - 9 * Math.sin(ang - 0.4)), (int) (y2 - 9 * Math.sin(ang + 0.4))};
                    g.fillPolygon(ax, ay, 3);
                    g.setColor(new Color(60, 60, 160));
                    g.drawString(texto, (x1 + x2) / 2 - g.getFontMetrics().stringWidth(texto) / 2
                            + (int) (px * 1.5), (y1 + y2) / 2 - 4 + (int) (py * 1.5));
                }
            }

            // círculos
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