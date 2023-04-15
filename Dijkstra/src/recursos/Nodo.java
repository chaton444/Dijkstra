package recursos;

class Nodo {

    int Fila;
    int columna;
    int Distancia;
    boolean Visto;
    Nodo Anterior;

    public Nodo(int row, int col) {
        this.Fila = row;
        this.columna = col;
        this.Distancia = Integer.MAX_VALUE;
        this.Visto = false;

    }
}
