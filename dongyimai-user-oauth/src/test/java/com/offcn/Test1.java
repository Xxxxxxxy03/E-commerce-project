package com.offcn;

import org.junit.jupiter.api.Test;

import java.util.*;

public class Test1 {
    @Test
    public void test01(){
        HashMap<String, String> map = new HashMap<>();
        map.put("a","1");
        map.put("b","2");
        map.put("c","3");
        ArrayList<Map.Entry<String, String>> list = new ArrayList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }

        });
        for (Map.Entry<String, String> entry : list) {
            System.out.println(entry);
        }
        for (Map.Entry<String, String> entry : list) {
            String key = entry.getKey();
            String value = entry.getValue();

        }
    }
}
