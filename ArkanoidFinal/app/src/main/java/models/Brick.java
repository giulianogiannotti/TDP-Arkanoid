package models;

import android.graphics.RectF;

public class Brick {

    private RectF rect;
    private boolean esVisible;
    public int golpes;

    public Brick(int fila, int columna, int ancho, int altura) {
        esVisible = true;
        int relleno = 1;

        rect = new RectF(columna * ancho + relleno,
                fila * altura + relleno,
                columna * ancho + ancho - relleno,
                fila * altura + altura - relleno);
    }

    public RectF getRect() {
        return this.rect;
    }

    public void setInvisible() {
        esVisible = false;
    }

    public boolean getVisibilidad() {
        return esVisible;
    }
}