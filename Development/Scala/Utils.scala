import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by yang on 8/18/16.
  */
object Utils {

  def getDate(): String = {
    new SimpleDateFormat("yyyy-MM-dd HH:MM:ss") format new Date()
  }

  /**
    * Get the format Date, like 2016-08-18(year-month-day)
    *
    * @return
    */
  def getDateWithoutTime(): String = {
    new SimpleDateFormat("yyyy-MM-dd") format new Date()
  }

  def getYearAndMonth(): String = {
    new SimpleDateFormat("yyyy-MM") format new Date()
  }
}