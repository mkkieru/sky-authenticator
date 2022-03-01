package ke.co.skyworld;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class Test {

    public static void main(String[] args) {


        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp test = new Timestamp(System.currentTimeMillis());
        test.setTime( now.getTime() + (60 * 1000));
        System.out.println(test);

        /*
        String username = "Mkkieru" ;
        String password = "5e45e4e92b4edfaee04f3c4b02ddfbe7d54e81d4c057ee9714c2198d05fd324c";
        String auth = Hashing.sha256().hashString(username+password, StandardCharsets.UTF_8).toString();
        System.out.println("Mkkieru : "+auth);

        String username2 = "mkkieru" ;
        String password2 = "5e45e4e92b4edfaee04f3c4b02ddfbe7d54e81d4c057ee9714c2198d05fd324c";
        String auth2 = Hashing.sha256().hashString(username2+password2, StandardCharsets.UTF_8).toString();
        System.out.println("mkkieru : "+auth2);

        Mkkieru : 26ce9166aa3d86a91f5d404a48fc8c0355aeeab206acc039ed2423b71bb6be63
        mkkieru : 0fa010d3f075ef0e01d4342fd0e87347a337832f2c67e930ddc61f222c38247e
         */

    }
}
