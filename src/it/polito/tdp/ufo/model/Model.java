package it.polito.tdp.ufo.model;

import java.time.Year;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.ufo.db.SightingsDAO;


public class Model {
	
	// PER LA RICORSIONE
	
	// 1. Struttura dati "finale"
	private List<String> ottima; // dove sara' presente il percorso massimo
	
	// ottima e' una lista di stati (String) in cui c'e' lo stato di partenza
	// e un insieme di altri stati (non ripetuti)
	
	// 2. Struttura dati parziale -> lista definita nel metodo ricorsivo
	
	// 3. Condizione di terminazione
	// dopo un determinato nodo, non ci sono piu' successori che non ho (gia') considerato
	
	// 4. Generare una nuova soluzione a partire da una soluzione parziale
	// dato l'ultimo nodo inserito in parziale, considero tutti i successori di quel nodo
	// che non ho ancora considerato
	
	// 5. filtro
	// alla fine, ritornero' una sola soluzione -> quella per cui la size() e' massima
	
	// 6. Livello di ricorsione
	// lunghezza del percorso parziale
	
	// 7. caso iniziale
	// parziale contiene il mio stato di partenza
	
	
	private SightingsDAO dao;
	private List<String> stati; // e' come se fosse l'idMap
								// xke' in questo caso  i vertici sono delle stringhe semplici
	private Graph<String, DefaultEdge> grafo; // lo inizializziamo in creaGrafo() cosi' ogni volta ne creiamo uno nuovo
	
	
	public Model() {
		this.dao = new SightingsDAO();
	}
	
	public List<AnnoCount> getAnni() {		
		return this.dao.getAnni();
	}

	public void creaGrafo(Year anno) {
		this.grafo = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		this.stati = this.dao.getStati(anno);
		Graphs.addAllVertices(this.grafo, this.stati);
	
		// Soluzione "semplice" -> doppio ciclo, controllo esistenza arco
		// perche' i vertici sono sicuramente <= 52
		
		for(String s1 : this.grafo.vertexSet()) {
			for(String s2 : this.grafo.vertexSet()) {
				if(!s1.equals(s2)) {
					if(this.dao.esisteArco(s1, s2, anno)) {
						this.grafo.addEdge(s1, s2); // orientato
					}
				}
			}
		}
		
		System.out.println("Grafo creato!");
		System.out.println("# vertici: "+ this.grafo.vertexSet().size());
		System.out.println("# archi: "+ this.grafo.edgeSet().size());
	}

	public int getNvertici() {
		return this.grafo.vertexSet().size();
	}

	public int getNarchi() {
		return this.grafo.edgeSet().size();
	}

	public List<String> getStati() {
		return this.stati;
	}
	
	public List<String> getSuccessori(String stato) {
		return Graphs.successorListOf(this.grafo, stato);
	}
	
	public List<String> getPredecessori(String stato) {
		return Graphs.predecessorListOf(this.grafo, stato);
	}
	
	public List<String> getRaggiungibili(String stato) {
		List<String> raggiungibili = new LinkedList<>();
		DepthFirstIterator<String,DefaultEdge> dp = new DepthFirstIterator<String,DefaultEdge>(this.grafo,stato);
		
		dp.next(); // scarta il primo elemento perche' vuole la lista dei raggiungibili
		
		while (dp.hasNext()) {
			raggiungibili.add(dp.next());
		}
		
		return raggiungibili;
	}
	
	public List<String> getPercorsoMassimo(String partenza) {
		
		// Tutte le volte che chiamo dall'esterno questo metodo
		// creo this.ottima
		this.ottima = new LinkedList<String>();
		
		// La soluzione parziale, lista di stringhe, la creo direttamente qua dentro
		List<String> parziale = new LinkedList<String>();
				
		parziale.add(partenza); // 7.
		
		cercaPercorso(parziale);
				
		
		return this.ottima;
	}

	private void cercaPercorso(List<String> parziale) {
		
		// vedere se la soluzione corrente e' migliore dell'ottima corrente
		if(parziale.size()>ottima.size()) {
			this.ottima = new LinkedList<String>(parziale); // clono la lista
		}
		
		// prendo tutti i successori dell'ultimo nodo inserito nella parziale
		// ottengo tutti i candidati
		List<String> candidati = this.getSuccessori(parziale.get(parziale.size()-1));
		
		for(String candidato : candidati) {
			if(!parziale.contains(candidato)) { // condizione di terminazione implicita
				// e' un candidato che non ho ancora considerato
				parziale.add(candidato);
				this.cercaPercorso(parziale);
				parziale.remove(parziale.size()-1);
				// parziale.remove(candidato);
			}
		}		
	}
	
}
