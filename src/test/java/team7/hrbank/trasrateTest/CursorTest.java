package team7.hrbank.trasrateTest;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@SpringBootTest
@Transactional
public class CursorTest {

    @Test
    void decoding(){

        String cursor = "eyJpZCI6MjB9";
        byte[] decode = Base64.getDecoder().decode(cursor);
        String decodedString = new String(decode);
        System.out.println("Decoded String: " + decodedString);
        //{"id":20}
    }
}
