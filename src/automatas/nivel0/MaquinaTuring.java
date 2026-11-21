package automatas.nivel0;

import java.util.*;

public class MaquinaTuring {

    public static class Regla {
        public final char escribe;
        public final char mueve; // 'L', 'R' o 'S'
        public final String nuevoEstado;

        public Regla(char escribe, char mueve, String nuevoEstado) {
            this.escribe = escribe;
            this.mueve = mueve;
            this.nuevoEstado = nuevoEstado;
        }
    }

    public static final char BLANCO = '_';

    private final String nombre;
    private final String descripcion;
    private final String estadoInicial;
    private final Set<String> estadosAceptacion = new HashSet<>();
    private final Set<String> estados = new LinkedHashSet<>();
    private final Set<Character> alfabetoCinta = new LinkedHashSet<>();
    private final Map<String, Map<Character, Regla>> reglas = new HashMap<>();
    private final boolean aceptaPorEstado;

    // --- ejecución ---
    private final Map<Integer, Character> cinta = new HashMap<>();
    private int cabezal = 0;
    private String estado;
    private boolean terminada = false;
    private boolean aceptada = false;
    private String ultimaAccion = "";
    private int pasos = 0;

    public MaquinaTuring(String nombre, String descripcion, String estadoInicial, boolean aceptaPorEstado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estadoInicial = estadoInicial;
        this.estado = estadoInicial;
        this.aceptaPorEstado = aceptaPorEstado;
        estados.add(estadoInicial);
    }

    public void agregarFinal(String e) {
        estados.add(e);
        estadosAceptacion.add(e);
    }

    public void regla(String estado, char lee, char escribe, char mueve, String nuevo) {
        estados.add(estado);
        estados.add(nuevo);
        alfabetoCinta.add(lee);
        alfabetoCinta.add(escribe);
        reglas.computeIfAbsent(estado, k -> new HashMap<>())
              .put(lee, new Regla(escribe, mueve, nuevo));
    }

    public void cargar(String entrada) {
        cinta.clear();
        for (int i = 0; i < entrada.length(); i++) cinta.put(i, entrada.charAt(i));
        cabezal = 0;
        estado = estadoInicial;
        terminada = false;
        aceptada = false;
        ultimaAccion = "Inicio";
        pasos = 0;
    }

    public char leer(int pos) {
        return cinta.getOrDefault(pos, BLANCO);
    }

    public void paso() {
        if (terminada) return;
        if (pasos > 100000) {
            terminada = true;
            ultimaAccion = "Se superó el límite de pasos";
            return;
        }
        char c = leer(cabezal);
        Map<Character, Regla> tabla = reglas.get(estado);
        Regla r = (tabla == null) ? null : tabla.get(c);

        if (r == null) {
            terminada = true;
            aceptada = !aceptaPorEstado && estadosAceptacion.contains(estado);
            ultimaAccion = "No hay regla para (" + estado + ", " + c + "): se detiene";
            return;
        }

        cinta.put(cabezal, r.escribe);
        if (r.mueve == 'L') cabezal--;
        else if (r.mueve == 'R') cabezal++;
        ultimaAccion = "(" + estado + ", " + c + ") -> escribe '" + r.escribe
                + "', mueve " + r.mueve + ", va a " + r.nuevoEstado;
        estado = r.nuevoEstado;
        pasos++;

        if (estadosAceptacion.contains(estado)) {
            terminada = true;
            aceptada = true;
            ultimaAccion += "  [estado final]";
        }
    }

    /** Contenido de la cinta sin blancos en los extremos, para mostrar resultados. */
    public String contenidoCinta() {
        if (cinta.isEmpty()) return "";
        int min = Collections.min(cinta.keySet()), max = Collections.max(cinta.keySet());
        StringBuilder sb = new StringBuilder();
        for (int i = min; i <= max; i++) sb.append(leer(i));
        return sb.toString().replaceAll("^_+|_+$", "");
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
    public Set<String> getEstadosAceptacion() { return estadosAceptacion; }
    public boolean isAceptaPorEstado() { return aceptaPorEstado; }
    public Set<String> getEstados() { return estados; }

    /** Devuelve todas las reglas como lista de {desde, lee, escribe, mueve, hacia}. */
    public List<String[]> listarReglas() {
        List<String[]> lista = new ArrayList<>();
        for (String desde : estados) {
            Map<Character, Regla> t = reglas.get(desde);
            if (t == null) continue;
            for (Map.Entry<Character, Regla> en : t.entrySet()) {
                Regla r = en.getValue();
                lista.add(new String[]{desde, "" + en.getKey(), "" + r.escribe,
                        "" + r.mueve, r.nuevoEstado});
            }
        }
        return lista;
    }

    // ------------------------------------------------------------------
    // Programas predefinidos
    // ------------------------------------------------------------------

    /** Palíndromos sobre {a,b}. Acepta si llega a "qA". */
    public static MaquinaTuring palindromos() {
        MaquinaTuring m = new MaquinaTuring("Palíndromos {a,b}",
                "Acepta cadenas sobre {a,b} que se leen igual al derecho y al revés", "q0", true);
        m.agregarFinal("qA");

        m.regla("q0", 'a', BLANCO, 'R', "qa");
        m.regla("q0", 'b', BLANCO, 'R', "qb");
        m.regla("q0", BLANCO, BLANCO, 'S', "qA");

        m.regla("qa", 'a', 'a', 'R', "qa");
        m.regla("qa", 'b', 'b', 'R', "qa");
        m.regla("qa", BLANCO, BLANCO, 'L', "qa2");
        m.regla("qa2", 'a', BLANCO, 'L', "qback");
        m.regla("qa2", BLANCO, BLANCO, 'S', "qA");

        m.regla("qb", 'a', 'a', 'R', "qb");
        m.regla("qb", 'b', 'b', 'R', "qb");
        m.regla("qb", BLANCO, BLANCO, 'L', "qb2");
        m.regla("qb2", 'b', BLANCO, 'L', "qback");
        m.regla("qb2", BLANCO, BLANCO, 'S', "qA");

        m.regla("qback", 'a', 'a', 'L', "qback");
        m.regla("qback", 'b', 'b', 'L', "qback");
        m.regla("qback", BLANCO, BLANCO, 'R', "q0");
        return m;
    }

    /** Suma en unario: 111+11 -> 11111. */
    public static MaquinaTuring sumaUnaria() {
        MaquinaTuring m = new MaquinaTuring("Suma en unario",
                "Entrada: 1^a+1^b  (por ejemplo 111+11). Deja 1^(a+b) en la cinta", "s0", true);
        m.agregarFinal("sF");

        m.regla("s0", '1', '1', 'R', "s0");
        m.regla("s0", '+', '1', 'R', "s1");
        m.regla("s1", '1', '1', 'R', "s1");
        m.regla("s1", BLANCO, BLANCO, 'L', "s2");
        m.regla("s2", '1', BLANCO, 'S', "sF");
        return m;
    }
}