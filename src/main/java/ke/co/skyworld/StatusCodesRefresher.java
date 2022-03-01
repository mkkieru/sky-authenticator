package ke.co.skyworld;

import ke.co.skyworld.query_manager.Query_manager;

import java.sql.Timestamp;
import java.util.*;

public class StatusCodesRefresher implements Runnable {

    Object limit;
    Object offset;
    long user_id;
    long program_id;
    Query_manager usersDao = new Query_manager();
    List<LinkedHashMap<String, Object>> allAuthCodes;
    LinkedHashMap<String, Object> values = new LinkedHashMap<>();
    LinkedHashMap<String, Object> limits = new LinkedHashMap<>();

    String sqlQuery = "SELECT * FROM public.auth_details WHERE status = 'ACTIVE' LIMIT (:limit) OFFSET (:offset)";
    String sqlQuery3 = "UPDATE public.auth_details SET auth_code = (:auth_code) , date_updated =CURRENT_TIMESTAMP WHERE user_id = (:user_id) and program_id = (:program_id);";

    public StatusCodesRefresher(Object limit, Object offset) {
        this.limit = limit;
        this.offset = offset;

        limits.put("limit", limit);
        limits.put("offset", offset);
    }

    @Override
    public void run() {

        try {

            allAuthCodes = usersDao.getAccessCodesPerLimit(sqlQuery, limits);

            for (LinkedHashMap<String, Object> codes : allAuthCodes) {

                long timeToLive = (long) codes.get("time_to_live");
                String timeToLiveUnits = (String) codes.get("time_to_live_units");

                user_id = (long) codes.get("user_id");
                program_id = (long) codes.get("program_id");

                Timestamp date_updated = (Timestamp) codes.get("date_updated");
                Timestamp now = new Timestamp(System.currentTimeMillis());

                switch (timeToLiveUnits.toLowerCase().trim()) {
                    case "seconds":

                        date_updated.setTime(date_updated.getTime() + ((timeToLive) * 1000));

                        if (date_updated.before(now)) {

                            updateStatusCodes();
                        }
                        break;
                    case "minutes":

                        date_updated.setTime(date_updated.getTime() + ((timeToLive) * 60 * 1000));

                        if (date_updated.before(now)) {

                            updateStatusCodes();
                        }
                        break;
                    case "hours":

                        date_updated.setTime(date_updated.getTime() + ((timeToLive) * 60 * 60 * 1000));

                        if (date_updated.before(now)) {

                            updateStatusCodes();
                        }
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String genAuthCode() {

        Random rnd = new Random();

        StringBuilder limit = new StringBuilder();

        for (int i = 1; i <= XmlReader.AUTH_CODE_LENGTH; i++) {
            limit.append("9");
        }

        int bound = Integer.parseInt(limit.toString());
        int number = rnd.nextInt(bound);

        // this will convert any number sequence into 6 character.
        return String.format("%0" + XmlReader.AUTH_CODE_LENGTH + "d", number);
    }

    public void updateStatusCodes() throws Exception {
        values.put("auth_code", genAuthCode());
        values.put("user_id", user_id);
        values.put("program_id", program_id);

        usersDao.update(sqlQuery3, values);
    }

}