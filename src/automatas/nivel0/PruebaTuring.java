package automatas.nivel0;

public class PruebaTuring {

    static void correr(MaquinaTuring m, String entrada) {
        m.cargar(entrada);
        while (!m.isTerminada()) m.paso();
        System.out.println(m.getNombre() + " | \"" + entrada + "\" -> "
                + (m.isAceptada() ? "ACEPTADA" : "RECHAZADA")
                + " | cinta: \"" + m.contenidoCinta() + "\" | pasos: " + m.getPasos());
    }

    public static void main(String[] args) {
        String[] pals = {"", "a", "ab", "aba", "abba", "aab", "abab", "baab"};
        for (String s : pals) correr(MaquinaTuring.palindromos(), s);

        String[] sumas = {"111+11", "1+1", "+11", "11+"};
        for (String s : sumas) correr(MaquinaTuring.sumaUnaria(), s);
    }
}