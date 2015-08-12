import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Created by abdullahtellioglu on 06/08/15.
 */
public class DateUtils {
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static Date convertDateFromString(String date){
        Date d;
        try{
            d = new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(date);

        }catch (Exception ex){
            ex.printStackTrace();
            d = new Date();
        }
        return d;
    }
    public static String convertDateToString(Date date){
        String d;
        SimpleDateFormat dateFormat=new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        try{
            d = dateFormat.format(date);
        }catch (Exception ex){
            ex.printStackTrace();
            d = dateFormat.format(new Date());
        }
        return d;
    }
}
