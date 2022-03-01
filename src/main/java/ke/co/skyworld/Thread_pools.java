package ke.co.skyworld;

import ke.co.skyworld.query_manager.Query_manager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Thread_pools {

    public static void startThreads() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String sqlQuery = "SELECT COUNT(*) FROM public.auth_details";
                long totalNumberOfRecords = 0;
                int multiplier;

                try {
                    totalNumberOfRecords = (long) Query_manager.getAccessCodes(sqlQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int number_of_threads_needed = (int) totalNumberOfRecords / 200;

                number_of_threads_needed = (number_of_threads_needed == 0) ? 1 : number_of_threads_needed;

                ExecutorService executorService = Executors.newFixedThreadPool(number_of_threads_needed);

                for (int i = 0; i < number_of_threads_needed; i++) {

                    multiplier = 200 * i;

                    Runnable runnable = new StatusCodesRefresher(200, multiplier);
                    executorService.execute(runnable);

                }
            }
        }, 0, 1000);
    }
}
