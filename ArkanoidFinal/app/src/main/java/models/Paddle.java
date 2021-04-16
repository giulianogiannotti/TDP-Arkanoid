package models;

import android.graphics.RectF;

public class Paddle {
    // RectF tiene cuatro coordenadas
    private RectF rect;
    private float longitud;
    private float altura;

    // X es el extremo izquierdo del rectangulo que forma nuestra paleta
    private float x;

    // Y es la coordenada del tope
    private float y;

    //Esto mantendra el ancho y alto de la pantalla
    static int anchoPantalla;
    static int alturaPantalla;

    // Esto mantendra la velocidad de pixeles por segundo de la paleta
    private float velocidadPaleta;

    // Frormas en las que la paleta puede moverse
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Si nuestra paleta se mueve y donde
    private int movimientoPaleta = STOPPED;

    public Paddle(int pantallaX, int pantallaY) {
        longitud = pantallaX / 6;
        altura = pantallaY / 25;

        //Inicializa el ancho y la atura
        anchoPantalla=pantallaX;
        alturaPantalla=pantallaY;

        x = pantallaX / 2 - longitud / 2;
        y = pantallaY - altura;

        rect = new RectF(x, y, x + longitud, y + altura);
        velocidadPaleta = pantallaX / 2.5f;
    }

    public Paddle(int pantallaX, int pantallaY, int bonus) {
        longitud = pantallaX / 6;
        altura = pantallaY / 25;

        //Inicializa el ancho y la altura
        anchoPantalla=pantallaX;
        alturaPantalla=pantallaY;

        x = pantallaX / 2 - longitud / 2;
        y = pantallaY - altura ;

        rect = new RectF(x, y, x + longitud, y + altura);
        velocidadPaleta = pantallaX / 2.5f;
    }

    public RectF getRect() {
        return rect;
    }

    public void setMovimiento(int estado) {
        movimientoPaleta = estado;
    }

    public void actualizar(long fps) {
        if(x - velocidadPaleta / fps>=10 && movimientoPaleta == LEFT){
            x = x - velocidadPaleta / fps;
        }

        if(x + velocidadPaleta / fps+longitud<= anchoPantalla-10 &&  movimientoPaleta == RIGHT){
            x = x + velocidadPaleta / fps;
        }

        rect.left = x;
        rect.right = x + longitud;
    }

    public float getAltura() {
        return altura;
    }

    public float getLongitud() {
        return longitud;
    }

    public float getValorMitad(){
        return (rect.right + rect.left)/2;
    }

    public void setLongitud(float bonus, int pantallaX) {
        longitud = pantallaX / 6;
        longitud = longitud*bonus;
    }
}