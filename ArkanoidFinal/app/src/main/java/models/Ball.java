package models;

import android.graphics.RectF;

public class Ball {
    RectF rect;
    private float xVelocidad;
    private float yVelocidad;
    public static float anchoBola = Paddle.alturaPantalla / 45;
    float alturaBola = anchoBola;
    float x1 = Paddle.alturaPantalla / 3;
    float y1 = Paddle.alturaPantalla / 1.5f;
    float x2 = x1 * 1.5f;
    float y2 = y1 * 0.75f;
    private int rapidoDeBola = 1;

    public Ball() {
        // La volida comienza desplazandose hacia arriba a 100 pixeles por segundo
        xVelocidad = 200;
        yVelocidad = 400;
        rect = new RectF();
    }

    public RectF getRect() {
        return rect;
    }

    public void actualizar(long fps) {
        rect.left = rect.left + (xVelocidad / fps);
        rect.top = rect.top + (yVelocidad / fps);
        rect.right = rect.left + anchoBola;
        rect.bottom = rect.top - alturaBola;
    }

    public void velocidadYInversa() {
        yVelocidad = -yVelocidad;
    }

    public void velocidadXInversa() {
        xVelocidad = -xVelocidad;
    }

    public void setVelocidadX(float mitadPaleta, float lugarGolpe, float longitud) {
        float desdeMediaDistancia = Math.abs(lugarGolpe - mitadPaleta);
        if (!(desdeMediaDistancia < longitud / 4)) {
            xVelocidad = x2;
            if (yVelocidad > 0)
                yVelocidad = y2;
            else yVelocidad = -y2;
        } else {
            xVelocidad = x1;
            if (yVelocidad> 0)
                yVelocidad = y1;
            else yVelocidad = -y1;
        }
        if (lugarGolpe < mitadPaleta) {
            velocidadXInversa();
        }

        if (getRapidoBola() == 0) {
            bolaLenta();
        } else
            if (getRapidoBola() == 2)
                bolaRapida();
    }

    public void limpiarObstaculoY(float y) {
        rect.bottom = y;
        rect.top = y;
    }

    public void limpiarObstaculoX(float x) {
        rect.left = x;
        rect.right = x;
    }

    public void reset(int x, int y) {
        rect.left = x / 2;
        rect.top = y * 0.75f;
        rect.right = x / 2 + anchoBola;
        rect.bottom = y * 0.75f - alturaBola;
        xVelocidad = x1;
        yVelocidad = -y1;
        if (rapidoDeBola == 2)
            bolaRapida();
        else
            if (rapidoDeBola == 0)
               bolaLenta();
    }

    public float getValorMitad()
    {
        return (rect.right + rect.left) / 2;
    }

    public void bolaLenta(){
        xVelocidad = xVelocidad / 1.5f;
        yVelocidad = yVelocidad / 1.5f;
    }

    public void bolaRapida() {
        xVelocidad *= 1.5;
        yVelocidad *= 1.5;
    }

    public int getRapidoBola() {
        return rapidoDeBola;
    }

    public void setRapidoBola(int rapidoBola) {
        this.rapidoDeBola = rapidoBola;
    }
}