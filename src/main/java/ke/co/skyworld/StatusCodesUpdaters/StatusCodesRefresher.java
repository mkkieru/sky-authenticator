//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.StatusCodesUpdaters;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import ke.co.skyworld.XmlReader.XmlReader;
import ke.co.skyworld.query_manager.QueryManager;

public class StatusCodesRefresher implements Runnable {
    Object limit;
    Object offset;
    long user_id;
    long program_id;
    String identifier;
    QueryManager usersDao = new QueryManager();
    List<LinkedHashMap<String, Object>> allAuthCodes;
    List<LinkedHashMap<String, Object>> confirmedBiometrics;
    LinkedHashMap<String, Object> biometricID = new LinkedHashMap();
    LinkedHashMap<String, Object> values = new LinkedHashMap();
    LinkedHashMap<String, Object> limits = new LinkedHashMap();
    //String sqlQuery = "select date_updated + make_interval(secs => time_to_live ), identifier,user_id,program_id,time_to_live,time_to_live_units,date_updated FROM public.auth_details WHERE status = 'ACTIVE' LIMIT (:limit) OFFSET (:offset)";
    String sqlQuery  = "select identifier,user_id,program_id,time_to_live,time_to_live_units,date_updated FROM public.auth_details WHERE status = 'ACTIVE' LIMIT (:limit) OFFSET (:offset)";
    String sqlQuery3 = "UPDATE public.auth_details SET auth_code = (:auth_code), time_to_live = (:time_to_live), time_remaining = (:time_remaining) , isnew = :isnew ,date_updated = CURRENT_TIMESTAMP WHERE user_id = (:user_id) and program_id = (:program_id) and identifier = (:identifier)";
    String sqlQuery4 = "select * from biometric_confirmation where status = 'VERIFIED' LIMIT (:limit) OFFSET (:offset)";
    String sqlQuery5 = "delete from biometric_confirmation where id = (:id)";
    String sqlQuery6 = "delete from public.buffer where status = (:status)";
    String sqlQuery7 = "delete from public.buffer where (extract(min from (now() - buffer.date_created)) > 1) ";

    public StatusCodesRefresher(Object limit, Object offset) throws SQLException {
        this.limit = limit;
        this.offset = offset;
        this.limits.put("limit", limit);
        this.limits.put("offset", offset);
    }

    public void run() {
        try {
            this.usersDao.delete(this.sqlQuery7);
            LinkedHashMap<String, Object> temp = new LinkedHashMap();
            temp.put("status", "ACTIVATED");
            this.usersDao.update(this.sqlQuery6, temp);
            this.allAuthCodes = this.usersDao.getAccessCodesPerLimit(this.sqlQuery, this.limits);
            this.confirmedBiometrics = this.usersDao.getAccessCodesPerLimit(this.sqlQuery4, this.limits);
            Iterator var2 = this.allAuthCodes.iterator();

            LinkedHashMap biometric;
            while(var2.hasNext()) {
                biometric = (LinkedHashMap)var2.next();
                long timeToLive = (Long)biometric.get("time_to_live");
                String timeToLiveUnits = (String)biometric.get("time_to_live_units");
                identifier = (String) biometric.get("identifier");
                this.user_id = (Long)biometric.get("user_id");
                this.program_id = (Long)biometric.get("program_id");
                Timestamp date_updated = (Timestamp)biometric.get("date_updated");
                Timestamp now = new Timestamp(System.currentTimeMillis());
                String var9 = timeToLiveUnits.toLowerCase().trim();
                byte var10 = -1;
                switch(var9.hashCode()) {
                    case 99469071:
                        if (var9.equals("hours")) {
                            var10 = 2;
                        }
                        break;
                    case 1064901855:
                        if (var9.equals("minutes")) {
                            var10 = 1;
                        }
                        break;
                    case 1970096767:
                        if (var9.equals("seconds")) {
                            var10 = 0;
                        }
                }
                updateStatusCodes();

//                switch(var10) {
//                    case 0:
//                        date_updated.setTime(date_updated.getTime() + timeToLive * 1000L);
//                        if (date_updated.before(now)) {
//                            this.updateStatusCodes();
//                        }
//                        break;
//                    case 1:
//                        date_updated.setTime(date_updated.getTime() + timeToLive * 60L * 1000L);
//                        if (date_updated.before(now)) {
//                            this.updateStatusCodes();
//                        }
//                        break;
//                    case 2:
//                        date_updated.setTime(date_updated.getTime() + timeToLive * 60L * 60L * 1000L);
//                        if (date_updated.before(now)) {
//                            this.updateStatusCodes();
//                        }
//                }
            }

            var2 = this.confirmedBiometrics.iterator();

            while(var2.hasNext()) {
                biometric = (LinkedHashMap)var2.next();
                int id = (Integer)biometric.get("id");
                this.biometricID.put("id", id);
                this.usersDao.update(this.sqlQuery5, this.biometricID);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

    }

    public static String genAuthCode(int length) {
        Random rnd = new Random();
        StringBuilder limit = new StringBuilder();
        int bound;
        int number;
        if (length == 0) {
            for(bound = 1; bound <= XmlReader.AUTH_CODE_LENGTH; ++bound) {
                limit.append("9");
            }

            bound = Integer.parseInt(limit.toString());
            number = rnd.nextInt(bound);
            return String.format("%0" + XmlReader.AUTH_CODE_LENGTH + "d", number);
        } else {
            for(bound = 1; bound <= length; ++bound) {
                limit.append("9");
            }

            bound = Integer.parseInt(limit.toString());
            number = rnd.nextInt(bound);
            return String.format("%0" + length + "d", number);
        }
    }

    public void updateStatusCodes() {
        try{
            this.values.put("auth_code", genAuthCode(0));
            this.values.put("user_id", this.user_id);
            this.values.put("identifier", this.identifier);
            this.values.put("program_id", this.program_id);
            this.values.put("time_remaining", XmlReader.AUTH_CODE_TTL);
            this.values.put("time_to_live", XmlReader.AUTH_CODE_TTL);
            this.values.put("isnew", "NO");
            this.usersDao.update(this.sqlQuery3, this.values);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
