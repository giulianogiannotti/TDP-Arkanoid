package activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.nati.arkanoid.R;
import java.util.Random;

import models.Ball;
import models.Bonus;
import models.Brick;
import models.Paddle;

public class MainActivity extends Activity {

    ArkanoidView arkanoidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializo vista
        arkanoidView = new ArkanoidView(this);
        setContentView(arkanoidView);
    }

    class ArkanoidView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder surfaceHolder;

        // cuando jugarJuego se esta ejecutando o no
        volatile boolean jugarJuego;

        // El juego esta pausado al inicio, debemos mover algo
        boolean pausa = true;

        Canvas canvas;
        Paint paint;

        // rastrea la velocidad de fotogramas del juego
        long fps;

        // calculate the fps
        private long tiempoDelFrame;
        int pantallaX;
        int pantallaY;

        MediaPlayer mp1 = MediaPlayer.create(MainActivity.this, R.raw.a1);
        MediaPlayer mp2 = MediaPlayer.create(MainActivity.this, R.raw.a2);
        MediaPlayer mp3 = MediaPlayer.create(MainActivity.this, R.raw.a3);
        MediaPlayer mp4 = MediaPlayer.create(MainActivity.this, R.raw.levelstart);

        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.back1);

        Brick[] ladrillos = new Brick[25];
        int numLadrillos = 0;


        int puntaje = 0;
        int vidas = 3;

        Ball bola;
        Paddle paleta;
        Bonus premio;

        boolean flag = false;

        //Detalles de display
        Display display = getWindowManager().getDefaultDisplay();

        //Cargo la resolucion en un objeto Point
        Point tamaño = new Point();

        int nivel = 1;
        boolean termino = false;

        public ArkanoidView(Context context) {
            super(context);

            surfaceHolder = getHolder();
            paint = new Paint();

            display.getSize(tamaño);

            pantallaX = tamaño.x;
            pantallaY = tamaño.y;

            paleta = new Paddle(pantallaX, pantallaY);
            bola = new Ball();
            crearLadrillos();
        }

        private void resetJuego() {
                puntaje = 0;
                vidas = 3;
                nivel = 1;
            crearLadrillos();
            mp4.start();
            bitmap = BitmapFactory.decodeResource(res, R.drawable.back1);
        }

        public void crearLadrillos() {
            bola.reset(pantallaX, pantallaY);
            paleta = new Paddle(pantallaX, pantallaY);

            int ladrilloAncho = pantallaX;
            int ladrilloAltura = pantallaY / 12;
            numLadrillos = 0;

            int maxColumnas;
            int maxFilas;

            Random ra = new Random();

            switch(nivel){
                case 1:
                   maxColumnas = 8;
                   maxFilas = 4;
                    for (int columna = 0; columna < maxColumnas; columna++) {
                        for (int fila= 1; fila < maxFilas; fila++) {
                            ladrillos[numLadrillos] = new Brick(fila, columna, ladrilloAncho / 8, ladrilloAltura);
                            if (ladrillos[numLadrillos].getRect().left > 0 && ladrillos[numLadrillos].getRect().right < pantallaX) {
                                ladrillos[numLadrillos].golpes = maxFilas - fila;
                                numLadrillos++;
                            }
                        }
                    }
                    break;
                case 2:
                    maxColumnas=7;
                    maxFilas = 4;
                        for (int fila = 0; fila < maxFilas; fila++) {
                            for (int columna = 0; columna < maxColumnas; columna++) {
                            ladrillos[numLadrillos] = new Brick(fila, columna, ladrilloAncho / 8, ladrilloAltura);
                            if (ladrillos[numLadrillos].getRect().left > 0 && ladrillos[numLadrillos].getRect().right < pantallaX) {
                                ladrillos[numLadrillos].golpes =columna+1;
                                numLadrillos++;
                            }
                        }
                    maxColumnas--;
                    }
                    break;
                default:
                    maxColumnas = 8;
                    maxFilas = 3;
                    for (int columna = 0; columna < maxColumnas; columna++) {
                        for (int row = 0; row < maxFilas; row++) {
                            ladrillos[numLadrillos] = new Brick(row, columna, ladrilloAncho / 8, ladrilloAltura);
                            if (ladrillos[numLadrillos].getRect().left > 0 && ladrillos[numLadrillos].getRect().right < pantallaX) {
                                int k = ra.nextInt(5);
                                ladrillos[numLadrillos].golpes = k + 1;
                                numLadrillos++;
                            }
                        }
                    }
                    break;
            }
        }

        @Override
        public void run() {
            mp4.start();
            while (jugarJuego) {
                if (!termino) {
                    long inicioFrame = System.currentTimeMillis();
                    if (!pausa) {
                        paleta.actualizar(fps);
                        bola.actualizar(fps);
                        if (flag) {
                            premio.actualizar(fps);
                            chocaConPremio();
                        }

                        chocaConLadrillos();
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        bolaChocando(bola);
                        bolaChocaAbajoPantalla(bola);
                    }
                    draw();
                    tiempoDelFrame = System.currentTimeMillis() -inicioFrame;
                    if (tiempoDelFrame >= 1) {
                        fps = 1000 / tiempoDelFrame;
                    }
                }
            }
        }

        private void bolaChocando(Ball b) {
            chocandoConPaleta(b);
            bolaChocaTopePantalla(b);
            bolaChocaParedIzquierda(b);
            bolaChocaParedDerecha(b);
        }

        private void bolaChocaParedDerecha(Ball b) {
            if (b.getRect().right > pantallaX - 20) {
                b.velocidadXInversa();
                b.limpiarObstaculoX(pantallaX - 44);
            }
        }

        private void bolaChocaParedIzquierda(Ball b) {
            if (b.getRect().left < 0) {
                b.velocidadXInversa();
                b.limpiarObstaculoX(2);
            }
        }

        private void bolaChocaTopePantalla(Ball b) {
            if (b.getRect().top < 0) {
                b.velocidadYInversa();
                b.limpiarObstaculoY(24);
            }
        }

        private void bolaChocaAbajoPantalla(Ball b) {
            if (b.getRect().bottom + paleta.getAltura() / 3 > pantallaY) {
                b.velocidadYInversa();
                b.limpiarObstaculoY(pantallaY - 2);
                    vidas--;

                    mp3.start();
                flag = false;
                bola.setRapidoBola(1);
                    b.reset(pantallaX, pantallaY);
                    paleta = new Paddle(pantallaX, pantallaY);
                    pausa = true;
            }
        }

        private void chocandoConPaleta(Ball b) {
            if (intersects(paleta.getRect(), b.getRect())) {
                float mitadPaleta = paleta.getValorMitad();
                float mitadBola = b.getValorMitad();
                b.setVelocidadX(mitadPaleta, mitadBola, paleta.getLongitud());
                b.velocidadYInversa();
                b.limpiarObstaculoY(paleta.getRect().top - 4);
                mp1.start();
            }
        }

        private void chocaConPremio() {
            if (intersects(paleta.getRect(), premio.getRect())) {
                flag = false;
                switch(premio.tipo){
                    case 1:
                         paleta.setLongitud(2f,pantallaX);
                         break;
                    case 2:
                        paleta.setLongitud(0.5f,pantallaX);
                        break;
                    case 3:
                        if (bola.getRapidoBola() != 0){
                            bola.setRapidoBola(0);
                            bola.bolaLenta();
                        }
                        break;
                    case 4:
                        if (bola.getRapidoBola() != 2){
                            bola.setRapidoBola(2);
                            bola.bolaRapida();
                        }
                        break;
                    case 5:
                        vidas++;
                        break;
                    case 6:
                        siguienteNivel();
                        break;
                    default:
                        puntaje += 20;
                        break;
                }
            }

            //Si el bonus no se recoge
            else
                if (premio.getRect().bottom  > pantallaY) {
                    flag=false;
            }
        }

        public boolean intersects(RectF a, RectF b) {
            return a.left < b.right && b.left < a.right
                    && a.top < b.bottom + Ball.anchoBola && b.top < a.bottom + Ball.anchoBola;
        }

        public int chocaLadrilloEnCostado(RectF a, RectF b) {
            if (a.left < b.right && b.left < a.right)
                return 1;
            else return 0;
        }

        public int chocaLadrilloPorDebajo(RectF a, RectF b) {
            if (a.top < b.bottom + Ball.anchoBola && b.top < a.bottom + Ball.anchoBola)
                return 2;
            else
                return 0;
        }

        int[] hit_point = new int[24];

        private void chocaConLadrillos() {
            for (int i = 0; i < numLadrillos; i++) {
                if (ladrillos[i].getVisibilidad()) {
                    if (intersects(ladrillos[i].getRect(), bola.getRect())) {
                        ladrillos[i].golpes--;
                        if (ladrillos[i].golpes == 0) {
                            ladrillos[i].setInvisible();
                        } else {
                            añadirColorLadrillos(i);
                        }

                        if (hit_point[i] == 2) {
                            bola.velocidadXInversa();
                        } else {
                            bola.velocidadYInversa();
                        }
                        puntaje += 10;

                        if (!flag){
                            Random r = new Random();
                            int x = r.nextInt(50);
                            if (x < 50) {
                                premio = new Bonus(ladrillos[i].getRect());
                                flag = true;

                                //Probabilidad de premios
                                if (x < 10) premio.tipo = 1;
                                else if (x < 20) premio.tipo = 2;
                                else if (x < 30) premio.tipo = 3;
                                else if (x < 40) premio.tipo = 4;
                                else if (x < 41) premio.tipo = 5;
                                else if (x < 42) premio.tipo = 6;
                                else premio.tipo = 7;
                            }
                        }


                        mp2.start();
                        break;
                    } else {
                        if (chocaLadrilloEnCostado(ladrillos[i].getRect(), bola.getRect()) != 0) {
                            hit_point[i] = 1;
                        }
                        if (chocaLadrilloPorDebajo(ladrillos[i].getRect(), bola.getRect()) != 0) {
                            hit_point[i] = 2;
                        }
                    }
                }
            }
        }

        private void añadirColorLadrillos(int i) {
            switch (ladrillos[i].golpes) {
                case 1:
                    paint.setColor(Color.argb(255, 255, 0, 255));
                    break;
                case 2:
                    paint.setColor(Color.argb(255, 102, 51, 0));
                    break;
                case 3:
                    paint.setColor(Color.argb(255, 255, 0, 0));
                    break;
                case 4:
                    paint.setColor(Color.argb(255, 0, 255, 0));
                    break;
                case 5:
                    paint.setColor(Color.argb(255, 0, 255, 255));
                    break;
                case 6:
                    paint.setColor(Color.argb(255, 255, 255, 0));
                    break;
                default:
                    paint.setColor(Color.argb(255, 120, 120, 120));
                    break;
            }
            canvas.drawRect(ladrillos[i].getRect(), paint);
        }

        // Dibuja la pantalla actualizada
        public void draw() {
            if (surfaceHolder.getSurface().isValid()) {
                canvas = surfaceHolder.lockCanvas();

                canvas.drawColor(Color.argb(255, 21, 168, 209));
                canvas.drawBitmap(bitmap, 0, 0, null);
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawRect(paleta.getRect(), paint);
                canvas.drawOval(bola.getRect(), paint);
                paint.setColor(Color.argb(255, 90, 240, 70));

                if (flag){
                    switch (premio.tipo){
                        case 1:
                            paint.setColor(Color.argb(255, 255, 255, 255));
                            break;
                        case 2:
                            paint.setColor(Color.argb(255, 255, 0, 191));
                            break;
                        case 3:
                            paint.setColor(Color.argb(255, 0, 0, 0));
                            break;
                        case 4:
                            paint.setColor(Color.argb(255, 102, 51, 0));
                            break;
                        case 5:
                            paint.setColor(Color.argb(255, 255, 0, 0));
                            break;
                        case 6:
                            paint.setColor(Color.argb(255, 255, 255, 0));
                            break;
                        default:
                            paint.setColor(Color.argb(255, 64, 255, 0));
                    }
                canvas.drawRect(premio.getRect(), paint);
                }

                for (int i = 0; i < numLadrillos; i++) {
                    if (ladrillos[i].getVisibilidad()) {
                        añadirColorLadrillos(i);
                    }
                }

                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(70);
                canvas.drawText("Nivel: " + nivel + "   Puntaje: " + puntaje + "   Vidas: " + vidas, 10, 50, paint);

                // Gano
                if (ganaNivel()) {
                    siguienteNivel();
                }

                // Perdio
                else if (vidas < 1) {
                    paint.setTextSize(90);
                    pausa = true;
                    termino = true;
                    canvas.drawText("Has perdido!", 10, pantallaY / 2, paint);
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }

        private void siguienteNivel() {
            paint.setTextSize(90);
            pausa = true;
            nivel++;
            bola.setRapidoBola(1);
            flag = false;
            mp4.start();

            if (nivel % 3 == 2)
                bitmap = BitmapFactory.decodeResource(res, R.drawable.back2);
            else if (nivel % 3 == 0)
                bitmap = BitmapFactory.decodeResource(res, R.drawable.back3);
            else
                bitmap = BitmapFactory.decodeResource(res, R.drawable.back1);
            if (nivel == 4) {
                canvas = surfaceHolder.lockCanvas();
                paint.setTextSize(90);
                pausa=true;
                termino = true;

                canvas.drawText("Has ganado! Fin del juego", 10, pantallaY / 2, paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
                resetJuego();
            } else {
                crearLadrillos();
            }
        }

        private boolean ganaNivel() {
            for (int i = 0; i < numLadrillos; i++) {
                if (ladrillos[i].getVisibilidad()) {
                    return false;
                }
            }
            return true;
        }

        public void pause() {
            jugarJuego = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "Joining thread!");
            }
        }

        public void resume() {
            jugarJuego = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    pausa= false;
                    if (termino) {
                        termino = false;
                        jugarJuego = true;
                        pausa = false;
                        flag = false;
                        if (vidas == 0) {
                            resetJuego();
                        }
                        else crearLadrillos();
                    }
                    if (motionEvent.getX() >pantallaX / 2)
                        paleta.setMovimiento(paleta.RIGHT);
                    else
                        paleta.setMovimiento(paleta.LEFT);
                    break;
                case MotionEvent.ACTION_UP:
                    paleta.setMovimiento(paleta.STOPPED);
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        arkanoidView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        arkanoidView.pause();
    }
}