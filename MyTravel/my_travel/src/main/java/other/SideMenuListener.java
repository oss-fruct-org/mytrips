package other;
import android.support.v4.app.Fragment;
/**
 *  интерфейс, описывающий методы взаимодействия фрагмента и Activity:
 * Created by Admin on 13.05.2014.
 */
public interface SideMenuListener {
    public void startFragment(Fragment fragment);
    public boolean toggleMenu();
}
