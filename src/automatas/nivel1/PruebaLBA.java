package automatas.nivel1;

public class PruebaLBA {

    static void correr(String entrada) {
        AutomataLBA m = AutomataLBA.anbncn();
        m.cargar(entrada);
        while (!m.isTerminada()) m.paso();
        System.out.println("\"" + entrada + "\" -> "
                + (m.isAceptada() ? "ACEPTADA" : "RECHAZADA")
                + " | pasos: " + m.getPasos());
    }

    public static void main(String[] args) {
        String[] pruebas = {"abc", "aabbcc", "aaabbbccc", "aaaabbbbcccc",
                "", "a", "aabbc", "abcc", "acb", "ba", "aabcc", "abcabc"};
        for (String s : pruebas) correr(s);
    }
}