package automatas.nivel2;

public class PruebaPila {
    public static void main(String[] args) {
        String[] pruebas = {"ab", "aabb", "aaabbb", "", "a", "b", "aab", "abb", "ba", "abab"};
        for (String s : pruebas) {
            System.out.println("\"" + s + "\" -> "
                    + (AutomataPila.acepta(s) ? "ACEPTADA" : "RECHAZADA"));
        }
    }
}