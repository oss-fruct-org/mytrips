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
 * ������ ������ � ��������� �� � ���-�����(Class OfflineWork): 
 * ������������ ������ � ��������� �� � ���-���� ������. 
 * 
 */
public class OfflineWork {

	public OfflineWork(){
		
	}
	/**
	 * ����� ������ (�� ��������� ��) ������� �����, 
	 * � ������� �������� ������������.
	 * 
	 * @param indexOfMap ���������� ��� ����������� ���������� �����.
	 * @return ����� (������� �����).
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
	 * ����� ������ (�� ��������� ��) ������ �����, 
	 * ���������� ������������� �� ����� 
	 * (���������� � ������� int readMap(int indexOfMap)).
	 * 
	 * @param indexOfMap ���������� ��� ����������� ���������� �����.
	 * @return ������ �����, ������������\��������� ������������� �� �����.
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
	 * ����� ������ (�� ��������� ��) ������ ���������, 
	 * ����������\������������ ������������� �� ����� 
	 * (���������� � ������� int readMap(int indexOfMap)).
	 * 
	 * @param indexOfMap ���������� ��� ����������� ���������� �����.
	 * @return ������ ���������, ������������ ������������� �� �����.
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
	 * ����� ���������� (� ��������� ��) ������� �����,
	 *  � ������� �� ������ ������ �������� ������������.
	 *  
	 * @param indexOfMap ���������� ��� ����������� ���������� �����. 
	 * @return 1 - �������� ������, 0 - ���� �� ������� ������ ��������
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
	 * ����� ���������� (� ��������� ��) ������ �����, 
	 * ���������� ������������� �� �����.
	 * 
	 * @param indexOfMap ���������� ��� ����������� ���������� �����
	 * @param usrP  ������ �����, ����������\���������� ������������� �� �����.
	 * @return 1 - �������� ������, 0 - ���� �� ������� ������ ��������
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
	 * ����� ���������� (� ��������� ��) ������ ���������, 
	 * ����������\������������ ������������� �� �����.
	 * 
	 * @param indexOfMap ���������� ��� ����������� ���������� �����;
	 * @param usrR ������ ���������, ����������\������������ ������������� �� �����.
	 * @return 1 �������� ������, 0 - ���� �� ������� ������ ��������
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
