/**
 * 
 */
package offline_work;

import other.NotImplementedException;
import model.Point;
import model.Route;
import model.myMap;

/**
 * @author Admin
 *
 * ћодуль работы с локальной Ѕƒ в офф-лайне(Class OfflineWork): 
 * обеспечивает работу с локальной Ѕƒ в офф-лайн режиме. 
 * 
 */
public class OfflineWork {

	public OfflineWork(){
		
	}
	/**
	 * метод читает (из локальной Ѕƒ) область карты, 
	 * с которой работает пользователь.
	 * 
	 * @param indexOfMap переменна€ дл€ определени€ конкретной карты.
	 * @return карта (область карты).
	 */
	public myMap readMap(int indexOfMap){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ћетод читает (из локальной Ѕƒ) массив точек, 
	 * выделенных пользователем на карте 
	 * (полученной с помощью int readMap(int indexOfMap)).
	 * 
	 * @param indexOfMap переменна€ дл€ определени€ конкретной карты.
	 * @return массив точек, обозначенных\выбранных пользователем на карте.
	 */
	public Point[] readPoint(int indexOfMap){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * метод читает (из локальной Ѕƒ) массив маршрутов, 
	 * выделенных\обозначенных пользователем на карте 
	 * (полученной с помощью int readMap(int indexOfMap)).
	 * 
	 * @param indexOfMap переменна€ дл€ определени€ конкретной карты.
	 * @return массив маршрутов, составленных пользователем на карте.
	 */
	public Route[] readRoute(int indexOfMap){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * метод записывает (в локальную Ѕƒ) область карты,
	 *  с которой на данный момент работает пользователь.
	 *  
	 * @param indexOfMap переменна€ дл€ определени€ конкретной карты. 
	 * @return 1 - успешна€ запись, 0 - ≈сли не удалось ничего записать
	 */
	public int writeMap(int indexOfMap){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * метод записывает (в локальную Ѕƒ) массив точек, 
	 * выделенных пользователем на карте.
	 * 
	 * @param indexOfMap переменна€ дл€ определени€ конкретной карты
	 * @param usrP  массив точек, выделенных\отмеченных пользователем на карте.
	 * @return 1 - успешна€ запись, 0 - ≈сли не удалось ничего записать
	 */
	public int writePoint(int indexOfMap, Point[] usrP){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * метод записывает (в локальную Ѕƒ) массив маршрутов, 
	 * выделенных\обозначенных пользователем на карте.
	 * 
	 * @param indexOfMap переменна€ дл€ определени€ конкретной карты;
	 * @param usrR массив маршрутов, выделенных\составленных пользователем на карте.
	 * @return 1 успешна€ запись, 0 - ≈сли не удалось ничего записать
	 */
	public int writeRoute(int indexOfMap, Route[] usrR){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
