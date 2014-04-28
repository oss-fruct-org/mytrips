/**
 * 
 */
package view;

import other.NotImplementedException;
import model.Point;
import model.myMap;

/**
 * @author Admin
 * 
 * Предоставляет отображение списка точек 
 *
 */
public class ViewListOfPoints {

	public ViewListOfPoints(){
		
	}
	
	/**
	 * Предоставляет отображение списка точек 
	 * 
	 * @param points массив точек для отображения
	 */
	public void ShowListOfPoints(Point[] points){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * 
	 * отображение списка близлежащих точек 
	 * и опорной точки(для которой остальные будут ближайшими)
	 * 
	 * @param radius радиус для отображения
	 * @param points массив ближайших точек для отображения
	 * @param mainPoint точка вокруг которой будет отображаться соседнии
	 */
	public void ShowListOfNPoints(double radius, Point[] points, Point mainPoint){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
}
