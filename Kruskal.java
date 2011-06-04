import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 
 * @author Camilo Nova
 * @version 1.0
 */
public class Kruskal extends JApplet {

	int n;

	int m;

	int num;

	int den;

	int u;

	int usel;

	int proceso;

	Nodo nodos[];

	Enlace enlaces[];

	int idx[];

	JPanel panel;

	JButton calcularBtn;

	JButton reiniciarBtn;

	private Graphics graphics;

	public void init() {
		nodos = new Nodo[100];
		enlaces = new Enlace[200];
		idx = new int[200];
		panel = new JPanel(true);

		JPanel buttonsPanel = new JPanel();
		calcularBtn = new JButton("Calcular");
		reiniciarBtn = new JButton("Recargar");

		panel.setBackground(Color.lightGray);

		calcularBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (proceso == 1) {
					iniciar();
					proceso++;
				} else if (usel >= n - 1) {
					terminar();
					proceso = 1;
				} else if (proceso == 3) {
					filtrarEnlaces();
					proceso--;
				} else {
					enlaces[idx[u]].seleccionado = 1;
					proceso = 3;
				}
				dibujarGrafo();
			}

		});
		reiniciarBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				calcularBtn.setEnabled(true);
				panel.paint(getGraphics());
				crearNodosAleatorios();
				dibujarGrafo();
			}

		});

		crearNodosAleatorios();

		buttonsPanel.add(calcularBtn);
		buttonsPanel.add(reiniciarBtn);
		add(panel, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);

	}

	void dibujarGrafo() {
		graphics = panel.getGraphics();
		for (int i = 0; i < n; i++)
			dibujarNodo(graphics, nodos[i]);

		for (int i = 0; i < m; i++)
			dibujarEnlace(graphics, enlaces[i]);
	}

	int retornarNodo(String nombreNodo) {
		for (int i = 0; i < n; i++)
			if (nodos[i].nombre.equals(nombreNodo))
				return i;

		return -1;
	}

	void crearNodosAleatorios() {
		n = (int) (10D + Math.random() * 10D) / 2;
		m = 0;
		int nodo = 1;
		int arista = 1;
		for (int i = 0; i < n; i++) {
			Nodo node = new Nodo();
			node.nombre = String.valueOf(nodo);
			double ang = (double) (360 / n) * (double) nodo;
			node.x = 200 + (int) Math.round(170D * Math
					.cos((ang * 6.2831853071795862D) / 360D));
			node.y = 200 + (int) Math.round(170D * Math
					.sin((ang * 6.2831853071795862D) / 360D));
			nodo++;
			nodos[i] = node;
		}

		for (int j = 0; j < n; j++) {
			for (int i = j + 1; i < n; i++)
				if (Math.random() * 10D <= 6D) {
					Enlace edge = new Enlace();
					arista++;
					edge.positivo = i;
					edge.negativo = j;
					edge.longitud = 1 + (int) (Math.random() * 20D);
					enlaces[m] = edge;
					m++;
				}

		}

		for (int i = 0; i < m; i++)
			enlaces[i].seleccionado = -1;

		den = 0;
		num = 128;
		for (int i = 0; i < m; i++)
			if (enlaces[i].longitud > den)
				den = enlaces[i].longitud;

		iniciar();
		proceso = 2;
	}

	int partition(int izquierdo, int derecho) {
		int pivot = enlaces[idx[(izquierdo + derecho) / 2]].longitud;
		while (izquierdo <= derecho) {
			while (enlaces[idx[izquierdo]].longitud < pivot)
				izquierdo++;
			while (enlaces[idx[derecho]].longitud > pivot)
				derecho--;

			if (izquierdo <= derecho) {
				int i = izquierdo++;
				int j = derecho--;
				int k = idx[i];
				idx[i] = idx[j];
				idx[j] = k;
			}
		}
		return izquierdo;
	}

	void qsort(int izquierdo, int derecho) {
		if (izquierdo >= derecho)
			return;

		int i = partition(izquierdo, derecho);
		qsort(izquierdo, i - 1);
		qsort(i, derecho);
	}

	void iniciar() {
		for (int i = 0; i < m; i++)
			idx[i] = i;

		for (int i = 0; i < m; i++)
			enlaces[i].seleccionado = -1;

		qsort(0, m - 1);
		for (int i = 0; i < m; i++)
			enlaces[i].seleccionado = -1;

		for (int i = 0; i < n; i++) {
			nodos[i].set = i;
			nodos[i].primero = i;
			nodos[i].siguiente = -1;
		}

		usel = u = 0;
	}

	void filtrarEnlaces() {
		int vl = enlaces[idx[u]].positivo;
		int vr = enlaces[idx[u]].negativo;
		if (nodos[vl].set == nodos[vr].set) {
			enlaces[idx[u++]].seleccionado = -2;
			return;
		}
		usel++;
		enlaces[idx[u++]].seleccionado = 2;
		int i;
		for (i = vl; nodos[i].siguiente >= 0; i = nodos[i].siguiente)
			;
		nodos[i].siguiente = nodos[vr].primero;
		int j = nodos[vl].primero;
		int k = nodos[vl].set;
		for (i = nodos[vr].primero; i >= 0; i = nodos[i].siguiente) {
			nodos[i].primero = j;
			nodos[i].set = k;
		}

	}

	void terminar() {
		for (; u < m; u++)
			enlaces[idx[u]].seleccionado = -2;

		calcularBtn.setEnabled(false);
	}

	int[] retornarCoordenadas(int a, int b, int w, int h) {
		int x[] = new int[2];
		if (Math.abs(w * b) >= Math.abs(h * a)) {
			x[0] = ((b >= 0 ? 1 : -1) * a * h) / b / 2;
			x[1] = ((b >= 0 ? 1 : -1) * h) / 2;
		} else {
			x[0] = ((a >= 0 ? 1 : -1) * w) / 2;
			x[1] = ((a >= 0 ? 1 : -1) * b * w) / a / 2;
		}
		return x;
	}

	void dibujarNodo(Graphics g, Nodo nodo) {
		int x = nodo.x;
		int y = nodo.y;
		int w = g.getFontMetrics().stringWidth(nodo.nombre) + 10;
		int h = g.getFontMetrics().getHeight() + 4;
		nodo.ancho = w;
		nodo.alto = h;
		g.setColor(Color.black);
		g.drawOval((x - w / 2) - 1, (y - h / 2) - 1, w + 1, h + 1);
		g.setColor(panel.getBackground());
		g.fillOval(x - w / 2, y - h / 2, w, h);
		g.setColor(Color.WHITE);
		g.drawString(nodo.nombre, x - (w - 10) / 2, (y - (h - 4) / 2)
				+ g.getFontMetrics().getAscent());
	}

	void dibujarEnlace(Graphics g, Enlace enlace) {
		Nodo nodoInicial = nodos[enlace.positivo];
		Nodo nodoTerminal = nodos[enlace.negativo];
		int a = nodoInicial.x - nodoTerminal.x;
		int b = nodoInicial.y - nodoTerminal.y;
		int x1[] = retornarCoordenadas(-a, -b, nodoInicial.ancho, nodoInicial.alto);
		int x2[] = retornarCoordenadas(a, b, nodoTerminal.ancho, nodoTerminal.alto);

		if (enlace.seleccionado == -1)
			g.setColor(Color.blue);
		else if (enlace.seleccionado == -2)
			g.setColor(Color.gray);
		else if (enlace.seleccionado == 1)
			g.setColor(Color.orange);
		else
			g.setColor(Color.green);
		g.drawLine(nodoInicial.x + x1[0], nodoInicial.y + x1[1], nodoTerminal.x + x2[0], nodoTerminal.y + x2[1]);

		int w = g.getFontMetrics().stringWidth(String.valueOf(enlace.longitud));
		int h = g.getFontMetrics().getHeight();

		g.setColor(panel.getBackground());
		g.fillRect(((nodoInicial.x + nodoTerminal.x) - w) / 2, ((nodoInicial.y + nodoTerminal.y) - h) / 2, w, h);

		if (enlace.seleccionado == -1)
			g.setColor(Color.white);
		else if (enlace.seleccionado == -2)
			g.setColor(Color.darkGray);
		else if (enlace.seleccionado == 1)
			g.setColor(Color.orange);
		else
			g.setColor(Color.red);
		
		g.drawString(String.valueOf(enlace.longitud), ((nodoInicial.x + nodoTerminal.x) - w) / 2,
				((nodoInicial.y + nodoTerminal.y) - h) / 2 + g.getFontMetrics().getAscent());
	}

	private class Nodo {
		int x;

		int y;

		int set;

		int primero;

		int siguiente;

		int ancho;

		int alto;

		String nombre;
	}

	private class Enlace {
		int positivo;

		int negativo;

		int longitud;

		int seleccionado;

		String nombre;
	}	
}
