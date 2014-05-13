package model;

import java.util.List;

/**
 * @author Admin
 *
 */
public class Route {

	/**
	 * Реализовать конструктор
	 */
	public Route(){
		
	}
	
	public int id;
	
	public String name;
	
	public String description;
	
	public List<Point> routePoints;
	
	public List<Point> closePoints;
}
