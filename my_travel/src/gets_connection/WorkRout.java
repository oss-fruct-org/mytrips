package gets_connection;

import model.Point;
import model.Route;
import other.NotImplementedException;

/**
 * @author Admin
 *
 * Обеспечивает манипулирование маршрутами в базе данных
 *
 */
public class WorkRout {

	public WorkRout(){
		
	}
	
	/**
	 * 
	 * Метод осуществляет добавление маршрута в базу данных
	 * 
	 * @param a Маршрут, который следует добавить в базу данных
	 * @return 0 - Если маршрут удачно добавлен в базу данных
	 */
	public int addRoute(Route a){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 
	 * Метод осуществляет удаление маршрута из базы данных
	 * 
	 * @param a Маршрут, который следует удалить из базы данных
	 * @return 0 - Если маршрут удачно удален из базы данных
	 */
	public int removeRoute(Route a){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 
	 * Метод осуществляет изменение названия маршрута
	 * 
	 * @param a Маршрут, название которого следует изменить
	 * @param name Новое название маршрута
	 * @return 0 - Если название маршрута удачно изменено
	 */
	public int updateRoute(Route a, String name){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 
	 * Метод осуществляет добавление точки в заданное место в маршруте
	 * 
	 * @param a Маршрут, в который следует добавить точку
	 * @param b Точка, после которой следует добавить точку
	 * @param c Точка, которую мы добавляем в маршрут
	 * @return 0 - Если точка удачно добавлена в маршрут
	 */
	public int addPointToRoute(Route a, Point b, Point c){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 
	 * Метод осуществляет удаление точки из маршрута 
	 * и перестроение маршрута
	 * 
	 * @param a Маршрут с которого удаляем точку
	 * @param b Точка, которую следует удалить из маршрута
	 * @return 0 - Если точка удачно удалена из маршрута
	 */
	public int removePointFromRoute(Route a, Point b){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
