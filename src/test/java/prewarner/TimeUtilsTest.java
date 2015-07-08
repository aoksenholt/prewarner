package prewarner;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TimeUtilsTest {

    @Test
    public void testExcelToMsec() throws Exception {
        assertEquals(0.5 * 86400 * 1000, TimeUtils.excelToMsec(0.5), 1000);
    }

    @Test
    public void listTest() {
        List<Integer> l = new ArrayList<Integer>() {{ add(2); }};
        assertEquals(1, l.size());
        System.out.println(l.subList(0,1));

        // System.out.println(l.subList(0,2));
        // return l.subList(0, number > lastList.size()? lastList.size() : number);

    }
}