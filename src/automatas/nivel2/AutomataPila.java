package automatas.nivel2;

import java.util.*;

public class AutomataPila {

    /** Una "foto" del autómata en un momento dado. */
    public static class Config {
        public final String estado;
        public final int posicion;
        public final List<Character> pila; // el último elemento es el tope
        public final String accion;

        public Config(String estado, int posicion, List<Character> pila, String accion) {
            this.estado = estado;
            this.posicion = posicion;
            this.pila = new ArrayList<>(pila);
            this.accion = accion;
        }
    }

    private final String cadena;
    private final List<Character> pila = new ArrayList<>();
    private String estado = "q0";
    private int posicion = 0;
    private boolean terminado = false;
    private boolean aceptada = false;
    private String mensaje = "";

    public AutomataPila(String cadena) {
        this.cadena = cadena;
        pila.add('$'); // marcador de fondo de pila
    }

    /** Ejecuta un paso y devuelve la configuración resultante. */
    public Config paso() {
        if (terminado) return actual("");

        String accion;
        if (posicion < cadena.length()) {
            char c = cadena.charAt(posicion);
            if (c == 'a' && estado.equals("q0")) {
                pila.add('A');
                posicion++;
                accion = "Lee 'a': apila A";
            } else if (c == 'b' && (estado.equals("q0") || estado.equals("q1"))
                    && pila.get(pila.size() - 1) == 'A') {
                pila.remove(pila.size() - 1);
                estado = "q1";
                posicion++;
                accion = "Lee 'b': desapila A";
            } else {
                return rechazar("Lee '" + c + "': no hay transición posible (rechaza)");
            }
        } else {
            // fin de la cadena
            boolean pilaVacia = pila.size() == 1 && pila.get(0) == '$';
            if (estado.equals("q1") && pilaVacia) {
                estado = "q2";
                terminado = true;
                aceptada = true;
                mensaje = "ACEPTADA";
                return actual("Fin de cadena con pila vacía: acepta");
            }
            return rechazar("Fin de cadena: la pila no quedó vacía o faltan símbolos");
        }
        return actual(accion);
    }

    private Config rechazar(String motivo) {
        terminado = true;
        aceptada = false;
        mensaje = "RECHAZADA";
        return actual(motivo);
    }

    private Config actual(String accion) {
        return new Config(estado, posicion, pila, accion);
    }

    public boolean isTerminado() { return terminado; }
    public boolean isAceptada() { return aceptada; }
    public String getMensaje() { return mensaje; }
    public String getCadena() { return cadena; }
    public Config configuracionActual() { return actual("Inicio"); }

    /** Ejecuta la cadena completa y dice si se acepta. */
    public static boolean acepta(String cadena) {
        AutomataPila ap = new AutomataPila(cadena);
        while (!ap.isTerminado()) ap.paso();
        return ap.isAceptada();
    }
}