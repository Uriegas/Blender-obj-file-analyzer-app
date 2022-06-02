package com.pm_2022.iti_27849_u1_uriegas_ibarra_jesus_eduardo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    MainActivity activity = new MainActivity();
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
//    @Test
//    public void vectorAnalysis_isCorrect() throws FileNotFoundException {
//        String file = "batman";
//        File f = new File(file + ".obj");
//        HashMap<String, Integer> actual  = activity.analyzeObj(f);
//        FileInputStream stream = new FileInputStream(new File(file + ".log"));
//        HashMap<String, Integer> expected = new HashMap<String, Integer>();
//        for(String key : actual.keySet()){
//            assertEquals(expected.get(key), actual.get(key));
//        }
//    }
}