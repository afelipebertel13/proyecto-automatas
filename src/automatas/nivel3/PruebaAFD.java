package automatas.nivel3;

public class PruebaAFD {
    public static void main(String[] args) {
        AFD a = AFD.paresDeCeros();
        String[] pruebas = {"", "1", "0", "00", "1010", "10101", "0110"};
        for (String s : pruebas) {
            System.out.println("\"" + s + "\" -> " + (a.acepta(s) ? "ACEPTADA" : "RECHAZADA"));
        }
    }
}