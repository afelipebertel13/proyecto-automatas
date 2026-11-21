package automatas.nivel1;

import java.util.*;

/** Autómata linealmente acotado: la cinta mide exactamente |entrada| + 2 celdas. */
public class AutomataLBA {

    public static final char IZQ = '⊢';
    public static final char DER = '⊣';

    private static class Transicion {
        final char escribe, mueve;
        final String destino;
        Transicion(char escribe, char mueve, String destino) {
            this.escribe = escribe;
            this.mueve = mueve;
            this.destino = destino;
        }
    }

    private final String nombre;
    private final String descripcion;
    private final String estadoInicial;
    private final Set<String> estados = new LinkedHashSet<>();
    private final Set<String> estadosFinales = new HashSet<>();
    private final Set<Character> alfabetoCinta = new LinkedHashSet<>();
    private final Map<String, Transicion> tabla = new LinkedHashMap<>(); // clave "estado|simbolo"

    private char[] cinta = new char[0];
    private int cabezal;
    private String estado;
    private boolean terminada, aceptada;
    private String ultimaAccion = "";
    private int pasos;

    public AutomataLBA(String nombre, String descripcion, String estadoInicial) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estadoInicial = estadoInicial;
        this.estado = estadoInicial;
        estados.add(estadoInicial);
    }

    public void agregarFinal(String e) {
        estados.add(e);
        estadosFinales.add(e);
    }

    public void regla(String desde, char lee, char escribe, char mueve, String hacia) {
        estados.add(desde);
        estados.add(hacia);
        alfabetoCinta.add(lee);
        alfabetoCinta.add(escribe);
        tabla.put(desde + "|" + lee, new Transicion(escribe, mueve, hacia));
    }

    public void cargar(String entrada) {
        cinta = new char[entrada.length() + 2];
        cinta[0] = IZQ;
        for (int i = 0; i < entrada.length(); i++) cinta[i + 1] = entrada.charAt(i);
        cinta[cinta.length - 1] = DER;
        cabezal = 0;
        estado = estadoInicial;
        terminada = false;
        aceptada = false;
        ultimaAccion = "Inicio";
        pasos = 0;
    }

    public char leer(int pos) {
        return (pos >= 0 && pos < cinta.length) ? cinta[pos] : ' ';
    }

    public void paso() {
        if (terminada) return;
        char c = cinta[cabezal];
        Transicion t = tabla.get(estado + "|" + c);
        if (t == null) {
            terminada = true;
            ultimaAccion = "No hay regla para (" + estado + ", " + c + "): rechaza";
            return;
        }
        cinta[cabezal] = t.escribe;
        int nuevaPos = cabezal + (t.mueve == 'L' ? -1 : t.mueve == 'R' ? 1 : 0);
        ultimaAccion = "(" + estado + ", " + c + ") -> escribe '" + t.escribe
                + "', mueve " + t.mueve + ", va a " + t.destino;
        estado = t.destino;
        pasos++;

        if (nuevaPos < 0 || nuevaPos >= cinta.length) {
            terminada = true;
            ultimaAccion += "  [intentó salirse de la cinta acotada]";
            return;
        }
        cabezal = nuevaPos;
        if (estadosFinales.contains(estado)) {
            terminada = true;
            aceptada = true;
            ultimaAccion += "  [estado final]";
        }
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public int getCabezal() { return cabezal; }
    public boolean isTerminada() { return terminada; }
    public boolean isAceptada() { return aceptada; }
    public String getUltimaAccion() { return ultimaAccion; }
    public int getPasos() { return pasos; }
    public Set<Character> getAlfabetoCinta() { return alfabetoCinta; }
    public Set<String> getEstadosAceptacion() { return estadosFinales; }
    public Set<String> getEstados() { return estados; }
    public int getLimiteDer() { return Math.max(0, cinta.length - 1); }

    /** Reglas como {desde, lee, escribe, mueve, hacia}. */
    public List<String[]> listarReglas() {
        List<String[]> lista = new ArrayList<>();
        for (Map.Entry<String, Transicion> en : tabla.entrySet()) {
            String[] k = en.getKey().split("\\|");
            Transicion t = en.getValue();
            lista.add(new String[]{k[0], k[1], "" + t.escribe, "" + t.mueve, t.destino});
        }
        return lista;
    }

    /** a^n b^n c^n (n >= 1). */
    public static AutomataLBA anbncn() {
        AutomataLBA m = new AutomataLBA("LBA: a^n b^n c^n",
                "Acepta a^n b^n c^n (n>=1). Cinta acotada con delimitadores, marca con X, Y, Z", "q0");
        m.agregarFinal("qA");

        m.regla("q0", IZQ, IZQ, 'R', "q1");
        m.regla("q1", 'a', 'X', 'R', "q2");
        m.regla("q1", 'Y', 'Y', 'R', "q5");
        m.regla("q2", 'a', 'a', 'R', "q2");
        m.regla("q2", 'Y', 'Y', 'R', "q2");
        m.regla("q2", 'b', 'Y', 'R', "q3");
        m.regla("q3", 'b', 'b', 'R', "q3");
        m.regla("q3", 'Z', 'Z', 'R', "q3");
        m.regla("q3", 'c', 'Z', 'L', "q4");
        m.regla("q4", 'a', 'a', 'L', "q4");
        m.regla("q4", 'b', 'b', 'L', "q4");
        m.regla("q4", 'Y', 'Y', 'L', "q4");
        m.regla("q4", 'Z', 'Z', 'L', "q4");
        m.regla("q4", 'X', 'X', 'R', "q1");
        m.regla("q5", 'Y', 'Y', 'R', "q5");
        m.regla("q5", 'Z', 'Z', 'R', "q5");
        m.regla("q5", DER, DER, 'S', "qA");
        return m;
    }
}