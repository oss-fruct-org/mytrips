/**
 * 
 */
package search;

import other.NotImplementedException;
import model.Point;
import model.Route;

/**
 * @author Admin
 *
 * ѕредоставл€ет информацию о маршрутах и точках по критерию поиска
 *
 */
public class Find {

	public Find(){
		
	}
	
	/**
	 * 
	 * ћетод осуществл€ет поиск маршрутов, 
	 * которые проход€т через точки, выбранные пользователем
	 * 
	 * @param b ћассив точек, которые выбрал пользователь
	 * @return  ћассив маршрутов, которые проход€т через заданные точки, если через заданные точки не проход€т маршруты, то массив пустой
	 */
	public Route[] findRoutes(Point[] b){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *  ћетод осуществл€ет поиск близлежащих маршрутов, 
	 *  которые проход€т р€дом через точки, выбранные пользователем
	 * 
	 * @param b ћассив точек, которые выбрал пользователь
	 * @param radius ћаксимальное рассто€ние от заданных пользователем точек, 
	 * на котором будет происходить поиск близлежащих маршрутов
	 * @return ћассив близлежащих маршрутов, 
	 * которые проход€т через заданные точки, если через заданные точки и заданный радиус поиска не проход€т маршруты, 
	 * то массив пустой
	 */
	public Route[] findNRoutes(Point[] b, int radius){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * ћетод осуществл€ет поиск точек по критерию поиска
	 * 
	 * @param find  ритерий поиска
	 * @return ћассив точек, удовлетвор€ющих критерию поиска, 
	 * если точек, удовлетвор€ющих критерию поиска, не найдено, 
	 * то массив пустой
	 */
	public Point[] findPoints(String find){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * ћетод осуществл€ет поиск близлежащих точек от конкретной точки, 
	 * которые наход€тс€ в заданном радиусе поиска
	 * 
	 * @param b “очка, вокруг которой ищем
	 * @param radius –адиус поиска
	 * @return ћассив близлежащих точек, которые наход€тс€ в радиусе заданной точки, 
	 * если в радиусе заданной точки нет близлежащих точек, 
	 * то массив пустой
	 */
	public Point[] findNPoints(Point b, int radius){
		try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
