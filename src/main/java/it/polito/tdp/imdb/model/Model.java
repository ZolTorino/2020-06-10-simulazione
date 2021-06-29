package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;


import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	public ImdbDAO dao;
	private SimpleWeightedGraph<Actor, DefaultWeightedEdge> grafo;
	Map<Integer, Actor> idMap;
	public Model()
	{
		dao= new ImdbDAO();
	}
	
	public List<String> allGenres()
	{
		return dao.listAllGenres();
	}
	public Set<Actor> allActors()
	{
		return grafo.vertexSet();
	}
	public String creaGrafo(String g) {
		idMap=new HashMap<>();
		
		dao.getVertices(g, idMap);
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo,idMap.values());
		String s="Creato con vertici: "+grafo.vertexSet().size();
		
		LinkedList<Arco> archi = new LinkedList<>(dao.getEdges(g,idMap));
		
		for(Arco a:archi)
		{
			if(grafo.containsVertex(a.getA1()) && grafo.containsVertex(a.getA2())) {
					Graphs.addEdge(grafo, a.getA1(), a.getA2(), a.getPeso());
				
			}
		}
		s+="\n e Archi: "+grafo.edgeSet().size()+"\n";
		return s;
	}
	public List<Actor> getConnectedActors(Actor a){
		/*!!!!!!!!!*/
		ConnectivityInspector<Actor, DefaultWeightedEdge> ci = new ConnectivityInspector<Actor, DefaultWeightedEdge>(grafo);
		List<Actor> actors = new ArrayList<>(ci.connectedSetOf(a));
		actors.remove(a);
		Collections.sort(actors, (a1,a2)-> a1.getLastName().compareTo(a2.getLastName())); 
		return actors;
	}
	
	//SIMULAZIONE
	ArrayList<Actor> daIntervistare=new ArrayList<>();
	ArrayList<Actor> intervistati=new ArrayList<>();
	public String init(int n)
	{
		daIntervistare=new ArrayList<>(grafo.vertexSet());
		intervistati=new ArrayList<>();
		 Random ran = new Random();
	     int prob = ran.nextInt(daIntervistare.size());
	     intervistati.add(daIntervistare.get(prob));
	     daIntervistare.remove(prob);
	     return run(n);
	}
	public String run(int n) {
		int casuale=0;
		int conta=2;
		int pause=0;
		System.out.println("Giornate: "+n+"\n");
		for( int i=1; i<n;i++)
		{
			 Random ran = new Random();
		     int prob = ran.nextInt(100);
		     
		    if(prob<60|| casuale==1)
		    {
		    	casuale=0;
		    	conta++;
		    	 Random ran0 = new Random();
			     int prob0 = ran.nextInt(daIntervistare.size());
			     intervistati.add(daIntervistare.get(prob0));
			     System.out.println("Aggiunto 60: "+daIntervistare.get(prob0));
			     daIntervistare.remove(prob0);
		    }
		    else {
		    	conta++;
		    	double weight=0;
		    	Actor prossimo=null;
		    	for(DefaultWeightedEdge e:grafo.edgesOf(intervistati.get(intervistati.size()-1)))
		    	{
		    		if(grafo.getEdgeWeight(e)>weight&&daIntervistare.contains(Graphs.getOppositeVertex(grafo, e, intervistati.get(intervistati.size()-1))))
		    		{
		    			weight=grafo.getEdgeWeight(e);
		    			prossimo= Graphs.getOppositeVertex(grafo, e, intervistati.get(intervistati.size()-1));
		    			
		    		}
		    	}
		    	System.out.println("Aggiunto 40: "+prossimo);
		    	intervistati.add(prossimo);
    			daIntervistare.remove(prossimo);
		    }
		    if(intervistati.get(intervistati.size()-1).getGender().equals(intervistati.get(intervistati.size()-2).getGender()))
				{
		    	 Random ran1 = new Random();
			     int prob1 = ran.nextInt(100);
			     if(prob1<90&&conta>=2)
			     {
			    	 i++;
			    	 casuale=1;
			    	 pause++;
			    	 conta=0;
			     }
				}
		}
		String s= "\nPause: "+pause+" e Intervistati: "+intervistati.toString();
		return s;
	}
}
