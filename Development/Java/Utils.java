public class Utils{

    /*
     * 获取当前日期，例如 2017-3-31
    */
    public static String getCurrentDate(){
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
    }
}