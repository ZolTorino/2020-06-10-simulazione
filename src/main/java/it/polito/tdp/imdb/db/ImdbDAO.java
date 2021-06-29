package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Arco;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<String> listAllGenres(){
		String sql = "SELECT DISTINCT genre FROM movies_genres";
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				result.add(res.getString("genre"));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void getVertices(String g, Map<Integer, Actor>idMap){
		String sql = "SELECT a.id as id, a.first_name as name, a.last_name as surname, a.gender as gender "
				+ "FROM movies_genres AS mg, movies AS m, roles AS r, actors AS a "
				+ "WHERE m.id=mg.movie_id "
				+ "AND m.id=r.movie_id "
				+ "AND mg.genre=? "
				+ "AND a.id=r.actor_id "
				+ "GROUP BY a.id ";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, g);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("name"), res.getString("surname"),
						res.getString("gender"));
				
				idMap.put(actor.getId(), actor);
			}
			conn.close();
		
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
	}
	
	public LinkedList<Arco> getEdges(String g, Map<Integer, Actor>idMap){
		String sql = "SELECT r1.actor_id as id1, r2.actor_id as id2, COUNT(DISTINCT m1.id) as peso "
				+ "FROM movies_genres AS mg1, movies AS m1, roles AS r1,movies_genres AS mg2, movies AS m2, roles AS r2 "
				+ "WHERE m1.id=mg1.movie_id "
				+ "AND m1.id=r1.movie_id "
				+ "AND mg1.genre=? "
				+ "AND m2.id=mg2.movie_id "
				+ "AND m2.id=r2.movie_id "
				+ "AND mg2.genre=mg1.genre "
				+ "AND r1.actor_id> r2.actor_id "
				+ "AND m1.id=m2.id "
				+ "GROUP BY r1.actor_id, r2.actor_id "
				+ "";
		LinkedList<Arco> out= new LinkedList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, g);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				out.add(new Arco(idMap.get(res.getInt("id1")),idMap.get(res.getInt("id2")),res.getInt("peso")));
			}
			conn.close();
		
			return out;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
