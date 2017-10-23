package core;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateConverter
{
    public Long dd_mm_yyyy_to_unix(String date)
    {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date dateForm;
        try {
            dateForm = format.parse(date);
            return dateForm.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String diff_from_epoch (int years)
    {
        return unix_to_CET(31560952*1000L*years);
    }

    public Long add_years_unix(Long startUnix, int years)
    {
        return (startUnix+(31556952*1000L*years));
    }

    public String unix_to_CET(Long time)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S zzz");
        //Long date = dd_mm_yyyy_to_unix("24-05-1995");
        Date d = new Date(time);
        return format.format(d);
    }

    public String unix_to_dd_mm_yyyy(String time)
    {
        Long unixTime = unixStringToLong(time);
        Date date = new Date(unixTime * 1000);
        String str = new SimpleDateFormat("dd-MM-yyyy").format(date);
        return str;
    }

    public Integer differenceInDays(String payDay)
    {
        Date repayDay = new Date ();
        //multiply the timestampt with 1000 as java expects the time in milliseconds
        repayDay.setTime(unixStringToLong(payDay)*1000);

        Date currentDate = new Date ();
        currentDate.setTime(currentDate.getTime());

        if (currentDate.getTime()>repayDay.getTime()) return null;

        int diffInDays = (int)( (repayDay.getTime()- currentDate.getTime() )
                / (1000 * 60 * 60 * 24) );
        return diffInDays;
    }

    private Long unixStringToLong(String time)
    {
        return (Long.parseLong(time)/1000);
    }
}