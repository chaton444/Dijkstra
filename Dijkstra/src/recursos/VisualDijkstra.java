package recursos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.PriorityQueue;

public class VisualDijkstra extends JFrame {

    private static final int numero_cuadros = 60;
    private static final int tamaño_boton = 30;
    private Nodo[][] Nodos;
    private JButton[][] Botones;
    private Nodo InicioNodo;
    private Nodo FinNodo;
    private JButton siguienteButton;

    private JButton anteriorButton;
    private int IndiceActual = 0;
    private int PasoActual = 0;
    private boolean Finalizo = false;
    private Nodo[] CaminoCorto = new Nodo[numero_cuadros * numero_cuadros];
    private List<Pasos> pasos = new ArrayList<>();
    private boolean botonDerechoPrecionado = false;

    public VisualDijkstra() {
        Nodos = new Nodo[numero_cuadros][numero_cuadros];
        Botones = new JButton[numero_cuadros][numero_cuadros];

        setTitle("Visual Dijkstra");
        setSize(numero_cuadros * tamaño_boton, numero_cuadros * tamaño_boton + 50); // Aumenta el tamaño para incluir los nuevos botones
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(numero_cuadros, numero_cuadros));
        container.add(gridPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        container.add(controlPanel, BorderLayout.SOUTH);

        ButtonHandler handler = new ButtonHandler();

        for (int fila = 0; fila < numero_cuadros; fila++) {
            for (int col = 0; col < numero_cuadros; col++) {
                Nodos[fila][col] = new Nodo(fila, col);
                Botones[fila][col] = new JButton();
                Botones[fila][col].addActionListener(handler);
                Botones[fila][col].setBackground(new java.awt.Color(255, 255, 255)); // cambia el color del botón de inicio a rojo
                Botones[fila][col].setBorder(null);

                final int FilaFinal = fila;
                final int ColumnaFinal = col;

                Botones[fila][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            botonDerechoPrecionado = true;
                            Botones[FilaFinal][ColumnaFinal].setBackground(Color.BLACK);
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            botonDerechoPrecionado = false;
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (botonDerechoPrecionado) {
                            Botones[FilaFinal][ColumnaFinal].setBackground(Color.BLACK);
                        }
                    }
                });

                gridPanel.add(Botones[fila][col]);
            }
        }

        siguienteButton = new JButton("Siguiente");
        anteriorButton = new JButton("Anterior");

        siguienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                siguienteButtonActionPerformed(e);
            }
        });

        anteriorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                anteriorButtonActionPerformed(e);
            }
        });

        controlPanel.add(anteriorButton);
        controlPanel.add(siguienteButton);
    }

    public static void main(String[] args) {
        VisualDijkstra visualDijkstra = new VisualDijkstra();
        visualDijkstra.setVisible(true);
    }

    private void runDijkstra() {
        PriorityQueue<Nodo> queue = new PriorityQueue<>(Comparator.comparingInt(node -> node.Distancia));

        InicioNodo.Distancia = 0;
        queue.add(InicioNodo);

        Finalizo = false;
        IndiceActual = 0;
        pasos.clear(); // limpiar la lista de pasos anteriores
        CaminoCorto = new Nodo[numero_cuadros * numero_cuadros]; // initialize the shortestPath array

        while (!queue.isEmpty()) {
            Nodo actual = queue.poll();
            actual.Visto = true;

            // Si el botón no es el de inicio o finalización, cambiar su color a azul
            if (!(actual == InicioNodo || actual == FinNodo)) {

                pasos.add(new Pasos(actual.Fila, actual.columna)); // agregar un nuevo Step
            }

            if (actual == FinNodo) {
                getRuta();
                Finalizo = true;
                break;
            }

            for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int nuevaFila = actual.Fila + direction[0];
                int nuevaColumna = actual.columna + direction[1];

                if (nuevaFila < 0 || nuevaFila >= numero_cuadros || nuevaColumna < 0 || nuevaColumna >= numero_cuadros) {
                    continue;
                }

                Nodo nodoVecino = Nodos[nuevaFila][nuevaColumna];

                // No procesar vecinos con fondo negro
                if (Botones[nuevaFila][nuevaColumna].getBackground() == Color.BLACK) {
                    continue;
                }

                if (!nodoVecino.Visto && actual.Distancia + 1 < nodoVecino.Distancia) {
                    nodoVecino.Distancia = actual.Distancia + 1;
                    nodoVecino.Anterior = actual;
                    queue.add(nodoVecino);
                }
            }
        }

        // guardar el camino más corto
        Nodo node = FinNodo;
        int i = 0;
        while (node != null) {
            CaminoCorto[i++] = node;
            node = node.Anterior;
        }
    }

    public void next() {
        if (IndiceActual < CaminoCorto.length - 1) {
            Nodo current = CaminoCorto[++IndiceActual];
            Botones[current.Fila][current.columna].setBackground(Color.YELLOW);
            if (IndiceActual < CaminoCorto.length - 1) {
                current = CaminoCorto[IndiceActual + 1];
                Botones[current.Fila][current.columna].setBackground(Color.GREEN);
            } else {
                Finalizo = true;
            }
        }
    }

    public void ant() {
        if (IndiceActual > 0) {
            Nodo current = CaminoCorto[IndiceActual--];
            Botones[current.Fila][current.columna].setBackground(Color.WHITE);
            if (IndiceActual > 0) {
                current = CaminoCorto[IndiceActual - 1];
                Botones[current.Fila][current.columna].setBackground(Color.GREEN);
            }
        }
        Finalizo = false;
    }

    private void siguienteButtonActionPerformed(ActionEvent e) {
        if (PasoActual < pasos.size()) {
            Pasos step = pasos.get(PasoActual++);
            Botones[step.getX()][step.getY()].setBackground(new java.awt.Color(4, 134, 214));
            dobleSiguiente();
            dobleSiguiente();
            dobleSiguiente();
            dobleSiguiente();
        } else {
            next();
            next();
            next();
            next();
        }
    }

    public void dobleSiguiente() {
        Pasos step = pasos.get(PasoActual++);
        Botones[step.getX()][step.getY()].setBackground(new java.awt.Color(4, 134, 214));
    }

    private void anteriorButtonActionPerformed(ActionEvent e) {
        if (PasoActual > 0) {
            Pasos step = pasos.get(--PasoActual);
            Botones[step.getX()][step.getY()].setBackground(Color.WHITE);
            dobleAnt();
            dobleAnt();
            dobleAnt();
            dobleAnt();
        } else {
            ant();
            ant();
            ant();
            ant();
        }
    }

    public void dobleAnt() {
        Pasos step = pasos.get(--PasoActual);
        Botones[step.getX()][step.getY()].setBackground(Color.WHITE);
    }

    private void reset() {
        // Reiniciar todos los botones a su estado original
        for (int row = 0; row < numero_cuadros; row++) {
            for (int col = 0; col < numero_cuadros; col++) {
                Nodos[row][col].Visto = false;
                Nodos[row][col].Distancia = Integer.MAX_VALUE;
                Nodos[row][col].Anterior = null;
                Botones[row][col].setBackground(new java.awt.Color(255, 255, 255)); // cambia el color del botón de inicio a rojo
                Botones[row][col].setBorder(null);
            }
        }

        // Reiniciar los nodos de inicio y fin
        InicioNodo = null;
        FinNodo = null;

        // Reiniciar las variables utilizadas para la animación
        Finalizo = false;
        IndiceActual = 0;
        PasoActual = 0;
        CaminoCorto = new Nodo[numero_cuadros * numero_cuadros];
        pasos.clear();

    }

    private class ButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (int row = 0; row < numero_cuadros; row++) {
                for (int col = 0; col < numero_cuadros; col++) {
                    if (e.getSource() == Botones[row][col]) {
                        if (InicioNodo == null) {
                            InicioNodo = Nodos[row][col];
                            Botones[row][col].setBackground(Color.GREEN);
                        } else if (FinNodo == null) {
                            FinNodo = Nodos[row][col];
                            Botones[row][col].setBackground(Color.RED);
                            runDijkstra();
                        } else {
                            reset();
                        }
                    }
                }
            }
        }
    }

    private void getRuta() {
        Nodo actual = FinNodo;
        int indice = 0;
        while (actual.Anterior != null) {
            CaminoCorto[indice++] = actual;
            actual = actual.Anterior;
        }
        CaminoCorto[indice] = InicioNodo;
        Finalizo = true;
        IndiceActual = 0;
    }

    private void addBlackButtons() {
        for (int row = 0; row < numero_cuadros; row++) {
            for (int col = 0; col < numero_cuadros; col++) {
                if (row == 0 || row == numero_cuadros - 1 || col == 0 || col == numero_cuadros - 1) {
                    Botones[row][col].setBackground(Color.BLACK);
                }
            }
        }
    }

}
