# Simulador de la Jerarquía de Chomsky

Aplicación de escritorio con interfaz gráfica (Java Swing) que permite visualizar y simular un modelo computacional por cada nivel de la Jerarquía de Chomsky.

**Autor:** Andrés Felipe betancur bertel (afelipebertel13)
**Curso:** Automatas, gramatica y lenguajes. 

## Módulos

| Nivel                         | Lenguaje y Modelo                    |Qué se visualiza                                                |
|-------------------------------|-------------------|------------------|
| 3 | Regular                   | Autómata finito determinista (AFD)   | Diagrama de estados y estado actual símbolo a símbolo          |
| 2 | Libre de contexto         | Autómata de pila                     | Diagrama de estados y la pila como columna que crece y decrece |
| 1 | Sensible al contexto      | Autómata linealmente acotado (LBA)   | Cinta acotada con delimitadores ⊢ y ⊣ y cabezal bidireccional  |
| 0 | Recursivamente enumerable | Máquina de Turing                    | Cinta extendible, alfabeto de la cinta, cabezal y estado activo|

## Requisitos

- JDK 17 o superior (desarrollado y probado con JDK 25).
- No se necesitan librerías externas: Swing viene incluido en el JDK.

Para comprobar que Java está instalado:

```
java -version
javac -version
```

## Cómo ejecutar

1. Clonar el repositorio:

```
git clone https://github.com/afelipebertel13/proyecto-automatas.git
cd proyecto-automatas
```

2. Compilar y ejecutar.

**En PowerShell (Windows):**

```
mkdir out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out automatas.Main
```

**En Git Bash, Linux o Mac:**

```
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out automatas.Main
```

**Con VS Code:** abrir la carpeta, instalar *Extension Pack for Java*, abrir `src/automatas/Main.java` y pulsar **Run**.

> La opción `-encoding UTF-8` es necesaria para que los delimitadores ⊢ y ⊣ se compilen bien.

## Cómo usar la aplicación

1. En la parte superior se elige el nivel con los cuatro botones.
2. Se escribe una cadena en el campo de texto o se elige una de la lista de ejemplos.
3. Controles disponibles en todos los módulos:
   - **Paso:** avanza un solo paso.
   - **Ejecutar / Pausar:** corre la simulación de forma animada.
   - **Reiniciar:** vuelve al estado inicial con la cadena actual.
   - **Velocidad:** deslizador que regula la animación.
4. Al terminar se muestra **ACEPTADA** (verde) o **RECHAZADA** (rojo).

## Cadenas de prueba

Todas estas cadenas también están en el menú de ejemplos de cada módulo.

### Nivel 3: AFD (número par de 0s, alfabeto {0,1})

- Aceptadas: `1`, `00`, `1010`, `10101`, `0110` (y la cadena vacía)
- Rechazadas: `0`, `0001`, `100`

### Nivel 2: Autómata de pila (aⁿbⁿ, n ≥ 1)

- Aceptadas: `ab`, `aabb`, `aaabbb`, `aaaabbbb`
- Rechazadas: `aab`, `abb`, `ba`, `abab`, `a`, `b`

### Nivel 1: LBA (aⁿbⁿcⁿ, n ≥ 1)

- Aceptadas: `abc`, `aabbcc`, `aaabbbccc`, `aaaabbbbcccc`
- Rechazadas: `aabbc`, `abcc`, `acb`, `ba`, `aabcc`, `abcabc`

### Nivel 0: Máquina de Turing

**Programa "Palíndromos {a,b}"**

- Aceptadas: `a`, `aba`, `abba`, `baab`, `ababa`
- Rechazadas: `ab`, `aab`, `abab`

**Programa "Suma en unario"** (entrada `1^a+1^b`, deja `1^(a+b)` en la cinta)

| Entrada    | Resultado en la cinta  |
|---------   |----------------------  |
| `111+11`   | `11111`                |
| `1+1`      | `11`                   |
| `1111+111` | `1111111`              |

## Estructura del proyecto

```
src/automatas/
├── Main.java                 Punto de entrada
├── ui/VentanaPrincipal.java  Ventana con el menú de niveles
├── nivel3/                   AFD.java, PanelAFD.java, PruebaAFD.java
├── nivel2/                   AutomataPila.java, PanelPila.java, PruebaPila.java
├── nivel1/                   AutomataLBA.java, PanelLBA.java, PruebaLBA.java
└── nivel0/                   MaquinaTuring.java, PanelTuring.java, PruebaTuring.java
```

La lógica de cada modelo está separada de su panel gráfico. Las clases `Prueba...` permiten verificar cada modelo por consola, sin interfaz.

## Notas del diagrama de la máquina de Turing

Las etiquetas de las flechas tienen el formato `lee/escribe,movimiento`, donde el movimiento es `L` (izquierda), `R` (derecha) o `S` (quedarse), y `_` es el símbolo blanco. Por ejemplo, `a/_,R` significa: leer `a`, escribir blanco y mover a la derecha.
