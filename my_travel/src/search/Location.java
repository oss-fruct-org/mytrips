/**
 * 
 */
package search;

import other.NotImplementedException;
import model.Point;

/**
 * @author Admin
 *
 *  Предоставляет информацию об местоположении устройства
 *  
 */
public class Location {

	public Location(){
		
	}
	
	/**
	 * Метод определяет твоё местоположение
	 * @return Точка, в которой вы находитесь
	 */
	public Point whereAmI(){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
