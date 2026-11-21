package automatas.nivel3;

import java.util.*;

public class AFD {
    private final String nombre;
    private final Set<String> estados = new LinkedHashSet<>();
    private final Set<Character> alfabeto = new LinkedHashSet<>();
    private final Map<String, Map<Character, String>> transiciones = new HashMap<>();
    private String estadoInicial;
    private final Set<String> estadosFinales = new HashSet<>();

    public AFD(String nombre) {
        this.nombre = nombre;
    }

    public void agregarEstado(String e, boolean inicial, boolean final_) {
        estados.add(e);
        transiciones.putIfAbsent(e, new HashMap<>());
        if (inicial) estadoInicial = e;
        if (final_) estadosFinales.add(e);
    }

    public void agregarTransicion(String desde, char simbolo, String hacia) {
        alfabeto.add(simbolo);
        transiciones.get(desde).put(simbolo, hacia);
    }

    /** Devuelve el siguiente estado, o null si no hay transición. */
    public String siguiente(String estado, char simbolo) {
        return transiciones.get(estado).get(simbolo);
    }

    /** Recorre la cadena y devuelve la lista de estados visitados. */
    public List<String> recorrido(String cadena) {
        List<String> ruta = new ArrayList<>();
        String actual = estadoInicial;
        ruta.add(actual);
        for (char c : cadena.toCharArray()) {
            actual = siguiente(actual, c);
            if (actual == null) break;
            ruta.add(actual);
        }
        return ruta;
    }

    public boolean acepta(String cadena) {
        List<String> ruta = recorrido(cadena);
        if (ruta.size() != cadena.length() + 1) return false;
        return estadosFinales.contains(ruta.get(ruta.size() - 1));
    }

    public String getNombre() { return nombre; }
    public Set<String> getEstados() { return estados; }
    public Set<Character> getAlfabeto() { return alfabeto; }
    public String getEstadoInicial() { return estadoInicial; }
    public Set<String> getEstadosFinales() { return estadosFinales; }

    public static AFD paresDeCeros() {
        AFD a = new AFD("Número par de 0s");
        a.agregarEstado("q0", true, true);
        a.agregarEstado("q1", false, false);
        a.agregarTransicion("q0", '0', "q1");
        a.agregarTransicion("q0", '1', "q0");
        a.agregarTransicion("q1", '0', "q0");
        a.agregarTransicion("q1", '1', "q1");
        return a;
    }
}