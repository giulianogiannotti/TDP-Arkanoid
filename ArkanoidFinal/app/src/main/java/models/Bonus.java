package models;

import android.graphics.RectF;

public class Bonus {
    private RectF rect;
    private float longitud;
    private float yVelocidad;
    public int tipo;

    public Bonus(RectF Rect){
        longitud = (Rect.right - Rect.left) / 3;
        rect = new RectF(Rect.left + longitud, Rect.top / 4, Rect.right - longitud, Rect.bottom / 4);
        yVelocidad = 200;
    }

    public RectF getRect() {
        return this.rect;
    }

    public void actualizar(long fps) {
        rect.top = rect.top + (yVelocidad / fps);
        rect.bottom = rect.top - longitud + (yVelocidad / fps);
    }

}
