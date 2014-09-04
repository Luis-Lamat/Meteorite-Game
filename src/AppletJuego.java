/**
 * AppletJuego
 *
 * Anima un elefante y con las flechas se puede mover
 *
 * @author Antonio Mejorado
 * @version 1.00 2008/6/13
 */
 
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.ImageIcon;

public class AppletJuego extends Applet implements Runnable, KeyListener,
                                        MouseListener, MouseMotionListener{
    // Se declaran las variables y objetos
    // direccion en la que se mueve el elefante
    // 1-arriba,2-abajo,3-izquierda y 4-derecha
    private int iDireccion;
    private int iMouseX;
    private int iMouseY;
    private int idX;
    private int idY;
    private int iVidas;
    private boolean bClick;
    private AudioClip aucSonidoColision;        // Objeto AudioClip sonido Raton
    private Planeta pltTierra;         // Objeto de la clase Planeta
    private Planeta pltAsteroide;      // Objeto de la clase Planeta
    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image    imaImagenApplet;   // Imagen a proyectar en Applet	
    private Graphics graGraficaApplet;  // Objeto grafico de la Imagen
	
    /** 
     * init
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inizializan las variables o se crean los objetos
     * a usarse en el <code>Applet</code> y se definen funcionalidades.
     */
    public void init() {
        // hago el applet de un tamaño 500,500
        setSize(1200,680);
        
        // posicion en 4 para que el elefante se mueva a la derecha
    	iDireccion = 4;
        
        // se posicion la Tierra en alguna parte al azar del cuadrante 
        // superior izquierdo
	int posX = (int) (Math.random() *(getWidth() / 4));    
        int posY = (int) (Math.random() *(getHeight() / 4));    
	URL urlImagenTierra = this.getClass().getResource("earth.gif");
        URL urlImagenAsteroide = this.getClass().getResource("asteroid.gif");
        
        // se crea el objeto elefante 
	pltTierra = new Planeta(posX,posY,
                Toolkit.getDefaultToolkit().getImage(urlImagenTierra));
        
        // se posicion la Tierra en alguna parte al azar del cuadrante 
        // inferior derecho
	posX = (int) ((getHeight() / 2) + Math.random() *(getHeight() / 4));    
        posY = (int) ((getHeight() / 2) + Math.random() *(getHeight() / 4));
        pltAsteroide = new Planeta(posX,posY,
                Toolkit.getDefaultToolkit().getImage(urlImagenAsteroide));
        
        // Inicializa las vidas en 4
        iVidas = 4;
        
        // inicializa velocidad del asteroide
        pltAsteroide.setVelocidad(10 - (iVidas * 2));
        
        
	//creo el sonido de la colision
	URL urlSonidoChoque = this.getClass().getResource("explosion.wav");
        aucSonidoColision = getAudioClip (urlSonidoChoque);

        // se define el background en color amarillo
	setBackground (Color.yellow);
        /* se le añade la opcion al applet de ser escuchado por los eventos
           del teclado  */
	addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }
	
    /** 
     * start
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo
     * para la animacion este metodo es llamado despues del init o 
     * cuando el usuario visita otra pagina y luego regresa a la pagina
     * en donde esta este <code>Applet</code>
     * 
     */
    public void start () {
        // Declaras un hilo
        Thread th = new Thread (this);
        // Empieza el hilo
        th.start ();
    }
	
    /** 
     * run
     * 
     * Metodo sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, que contendrá las instrucciones
     * de nuestro juego.
     * 
     */
    public void run () {
        // se realiza el ciclo del juego en este caso nunca termina
        while (true) {
            /* mientras dure el juego, se actualizan posiciones de jugadores
               se checa si hubo colisiones para desaparecer jugadores o corregir
               movimientos y se vuelve a pintar todo
            */ 
            actualiza();
            checaColision();
            repaint();
            try	{
                // El thread se duerme.
                Thread.sleep (20);
            }
            catch (InterruptedException iexError)	{
                System.out.println("Hubo un error en el juego " + 
                        iexError.toString());
            }
	}
    }
	
    /** 
     * actualiza
     * 
     * Metodo que actualiza la posicion del objeto elefante 
     * 
     */
    public void actualiza(){
        
        // si presionaron adentro de la tierra
        if (bClick && iMouseX != 0 && iMouseY != 0){
            pltTierra.setX(iMouseX - idX);
            pltTierra.setY(iMouseY - idY);
        }
        
        // el raton sigue al elefante
        if (pltAsteroide.getX() < pltTierra.getX()) {
            pltAsteroide.derecha();
        }
        else {
            pltAsteroide.izquierda();
        }
        if (pltAsteroide.getY() < pltTierra.getY()) {
            pltAsteroide.abajo();
        }
        else {
            pltAsteroide.arriba();
        } 
    }
	
    /**
     * checaColision
     * 
     * Metodo usado para checar la colision del objeto elefante
     * con las orillas del <code>Applet</code>.
     * 
     */
    public void checaColision(){
        //Colision del elefante con el Applet dependiendo a donde se mueve.

        // si esta pasando el limite superior
        if(pltTierra.getY() < 0) { 
            pltTierra.setY(0);     // se reseta la Y
        }

        // si esta pasando el limite superior
        if(pltTierra.getY() + pltTierra.getAlto() > getHeight()) {
            pltTierra.setY(getHeight() - pltTierra.getAlto()); // se reseta la Y
        }

        // si esta pasando el limite superior
        if(pltTierra.getX() < 0) {
            pltTierra.setX(0);       // se reseta la X
        }
        
        // si esta pasando el limite superior
        if(pltTierra.getX() + pltTierra.getAncho() > getWidth()) { 
            pltTierra.setX(getWidth() - pltTierra.getAncho()); // se reseta la X
        }
	
        // Checa si ambos objetos colisionan
        if (pltTierra.colisiona(pltAsteroide)){
            bClick = false;
            reposiciona();
            iVidas--;
            pltAsteroide.setVelocidad(10 - (iVidas * 2));
            
            //Solo pone el sonido cuando el juego esta activo
            if (iVidas >= 0) 
                aucSonidoColision.play();
        }
    }
    

    /**
     * reposiciona 
     * 
     * cambia las posiciones de la Tierra y del Asteroide , el elefante
     * debe posicionarse en el cuadrante inferior derecho y 
     * el raton en el cuadrante superior izquierdo
     * 
     */

    public void reposiciona() {
        pltAsteroide.setX((int) (Math.random() *(getWidth() / 4)));
        pltAsteroide.setY((int) (Math.random() *(getHeight() / 4)));
        
        pltTierra.setX((int) ((getWidth() / 2) + Math.random() *(getWidth() / 4)));
        pltTierra.setY((int) ((getHeight() / 2) + Math.random() *(getHeight() / 4)));
    }
    
    /**
     * update
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo lo que hace es actualizar el contenedor y 
     * define cuando usar ahora el paint
     * @param graGrafico es el <code>objeto grafico</code> usado para dibujar.
     * 
     */
    public void update (Graphics graGrafico){
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null){
                imaImagenApplet = createImage (this.getSize().width, 
                        this.getSize().height);
                graGraficaApplet = imaImagenApplet.getGraphics ();
        }

        // creo imagen para el background
        URL urlImagenSpace = this.getClass().getResource("space.jpg");
        Image imaImagenEspacio = Toolkit.getDefaultToolkit().getImage(urlImagenSpace);

        // Despliego la imagen
        graGraficaApplet.drawImage(imaImagenEspacio, 0, 0, 
                getWidth(), getHeight(), this);

        // Actualiza el Foreground.
        graGraficaApplet.setColor (getForeground());
        paint(graGraficaApplet);

        // Dibuja la imagen actualizada
        graGrafico.drawImage (imaImagenApplet, 0, 0, this);
    }

    /**
     * keyPressed
     * 
     * Metodo sobrescrito de la interface <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al dejar presionada
     * alguna tecla.
     * @param keyEvent es el <code>evento</code> generado al presionar.
     * 
     */
    public void keyPressed(KeyEvent keyEvent) {
        // si presiono flecha para arriba
        if(keyEvent.getKeyCode() == KeyEvent.VK_UP) {    
                iDireccion = 1;  // cambio la dirección arriba
        }
        // si presiono flecha para abajo
        else if(keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {    
                iDireccion = 2;   // cambio la direccion para abajo
        }
        // si presiono flecha a la izquierda
        else if(keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {    
                iDireccion = 3;   // cambio la direccion a la izquierda
        }
        // si presiono flecha a la derecha
        else if(keyEvent.getKeyCode() == KeyEvent.VK_RIGHT){    
                iDireccion = 4;   // cambio la direccion a la derecha
        }
    }
    
    /**
     * keyTyped
     * 
     * Metodo sobrescrito de la interface <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al presionar una 
     * tecla que no es de accion.
     * @param e es el <code>evento</code> que se genera en al presionar.
     * 
     */
    public void keyTyped(KeyEvent keyEvent){
    	// no hay codigo pero se debe escribir el metodo
    }
    
    /**
     * keyReleased
     * Metodo sobrescrito de la interface <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al soltar la tecla.
     * @param e es el <code>evento</code> que se genera en al soltar las teclas.
     */
    public void keyReleased(KeyEvent keyEvent){

    }
    
    /**
     * paint
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo se dibuja la imagen con la posicion actualizada,
     * ademas que cuando la imagen es cargada te despliega una advertencia.
     * @param g es el <code>objeto grafico</code> usado para dibujar.
     * 
     */
    public void paint(Graphics g) {
        if (iVidas > 0){
            // si la imagen ya se cargo
            if (pltTierra != null && pltAsteroide != null) {
                    //Dibuja la imagen de la tierra en la posicion actualizada
                    g.drawImage(pltTierra.getImagen(), pltTierra.getX(),
                            pltTierra.getY(), this);
                    g.drawImage(pltAsteroide.getImagen(), pltAsteroide.getX(),
                            pltAsteroide.getY(), this);

            } // sino se ha cargado se dibuja un mensaje 
            else {
                    //Da un mensaje mientras se carga el dibujo	
                    g.drawString("No se cargo la imagen..", 20, 20);
            }

            g.setColor(Color.WHITE);
            g.drawString("X drag: " + String.valueOf(iMouseX), 15, 20);
            g.drawString("Y drag: " + String.valueOf(iMouseY), 15, 40);
            g.drawString("Clicked: " + String.valueOf(bClick), 15, 60);
            g.drawString("Posicion Planeta: X = " + 
                    String.valueOf(pltTierra.getX()) + " | Y = " +
                    String.valueOf(pltTierra.getY()), 15, 80);
            g.drawString("Diferenciales: dX = " + 
                    String.valueOf(idX) + " | dY = " +
                    String.valueOf(idY), 15, 100);
            g.drawString("Vidas: " + String.valueOf(iVidas), 15, 120);
        }
        else {
            g.setColor(Color.WHITE);
            g.drawString("GAME OVER", 550, 330);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int iX = e.getX();
        int iY = e.getY();
        
        if (pltTierra.colisiona(iX, iY)){
            bClick = true;
            idX = iX - pltTierra.getX();
            idY = iY - pltTierra.getY();
        }
    } 
    @Override
    public void mouseDragged(MouseEvent e) {
        if (bClick){
            iMouseX = e.getX();
            iMouseY = e.getY();
            System.out.println(String.valueOf(iMouseX) + " " + String.valueOf(iMouseY));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        bClick = false;
        iMouseX = 0;
        iMouseY = 0;
    }
    
    // Metodos del mouse no utilizados
    public void mouseMoved(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseClicked(MouseEvent e) {

    }
    public void mouseExited(MouseEvent e) {
    } 
}