/**
 * 
 */
package other;

/**
 * @author Admin
 *
 * Класс для генерации исключения для не реализованных методов
 * Применять в качесте заглушки
 *
 */
public class NotImplementedException extends Exception {
	 
	private static final long serialVersionUID = 1L;
	private String message = null;

    public NotImplementedException() {
    }

    public NotImplementedException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return (this.message == null) ? "Don\'t forget to create this method "
                : "Don\'t forget to create this method " + this.message;
    }
}
