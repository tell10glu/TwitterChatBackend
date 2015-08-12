package responses;


/**
 * Bu sınıfta sadece error tipleri belirtilicek. Bu errorlere göre client tarafında handle işlemi yapılıcak.
 * bu hatalar için Exceptionlar oluşturulup diğer tarafa gönderilebilir.
 * @author abdullahtellioglu
 *
 */
public class ErrorTypes {
	public static String UNKNOWN_ERROR = "error:0";
	public static String WRONG_USER_NAME ="error:1";
	public static String WRONG_PARAMETER = "error:2";
	public static String WRONG_USER_LOGIN = "error:3";
	public static String WRONG_USER_ID = "error:4";
	public static String WRONG_DEVICE_ID ="error:5";
	public static String EMPTY_VALUES = "error:5";
	public static String EMPTY_USER_NAME = "error:6";
	public static String EMPTY_PASSWORD = "error:7";
	public static String EMPTY_EMAIL_ADDRESS = "error:8";
	public static String EMPTY_USER_ID = "error:9";
	public static String MESSAGE_SEND_FAIL = "{error='MESSAGE_SEND_FAIL'}";
}
