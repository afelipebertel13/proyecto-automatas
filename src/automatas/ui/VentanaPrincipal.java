package automatas.ui;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenido = new JPanel(cartas);

    public VentanaPrincipal() {
        setTitle("Jerarquía de Chomsky - Simulador de Autómatas");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel menu = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        menu.add(crearBoton("Nivel 3: Regular (AFD)", "N3"));
        menu.add(crearBoton("Nivel 2: Libre de contexto (Pila)", "N2"));
        menu.add(crearBoton("Nivel 1: Sensible al contexto (LBA)", "N1"));
        menu.add(crearBoton("Nivel 0: Máquina de Turing", "N0"));
        add(menu, BorderLayout.NORTH);

        contenido.add(new automatas.nivel3.PanelAFD(), "N3");
        contenido.add(marcador("Nivel 2 - por construir"), "N2");
        contenido.add(marcador("Nivel 1 - por construir"), "N1");
        contenido.add(marcador("Nivel 0 - por construir"), "N0");
        add(contenido, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto, String nombre) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> cartas.show(contenido, nombre));
        return b;
    }

    private JPanel marcador(String texto) {
        JPanel p = new JPanel(new GridBagLayout());
        p.add(new JLabel(texto));
        return p;
    }
}