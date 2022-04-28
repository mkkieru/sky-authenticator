//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ke.co.skyworld.StatusCodesUpdaters;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import ke.co.skyworld.XmlReader.XmlReader;
import ke.co.skyworld.query_manager.QueryManager;

public class ThreadPools {
    public ThreadPools() {
    }

    public static void startThreads() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                XmlReader.getAccessKeys();
                String sqlQuery = "SELECT COUNT(*) FROM public.auth_details";
                long totalNumberOfRecords = 0L;

                try {
                    totalNumberOfRecords = (Long)QueryManager.getAccessCodes(sqlQuery);
                } catch (Exception var12) {
                    var12.printStackTrace();
                }

                int number_of_threads_needed = (int)totalNumberOfRecords / 200;
                number_of_threads_needed = number_of_threads_needed == 0 ? 1 : number_of_threads_needed;
                ExecutorService executorService = Executors.newFixedThreadPool(number_of_threads_needed);

                for(int i = 0; i < number_of_threads_needed; ++i) {
                    int multiplier = 200 * i;
                    StatusCodesRefresher runnable = null;

                    try {
                        runnable = new StatusCodesRefresher(200, multiplier);
                    } catch (SQLException var11) {
                        var11.printStackTrace();
                    }

                    executorService.execute(runnable);
                }

                executorService.shutdown();

                try {
                    executorService.awaitTermination(2147483647L, TimeUnit.SECONDS);
                } catch (InterruptedException var10) {
                    var10.printStackTrace();
                }

            }
        }, 2L, 1000L);
    }
}
