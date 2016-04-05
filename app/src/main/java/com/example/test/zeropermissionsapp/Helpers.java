package com.example.test.zeropermissionsapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jesper Laptop on 5-4-2016.
 */
public class Helpers {

    protected String hex2DecAsString(String irData) {
        List<String> list = new ArrayList<String>(Arrays.asList(irData
                .split(" ")));
        list.remove(0); // dummy
        int frequency = Integer.parseInt(list.remove(0), 16); // frequency
        list.remove(0); // seq1
        list.remove(0); // seq2

        for (int i = 0; i < list.size(); i++) {
            list.set(i, Integer.toString(Integer.parseInt(list.get(i), 16)));
        }

        frequency = (int) (1000000 / (frequency * 0.241246));
        list.add(0, Integer.toString(frequency));

        irData = "";
        for (String s : list) {
            irData += s + ",";
        }
        return irData;
    }

        protected IRCode hex2decAsIRCode(String irData) {
            List<String> list = new ArrayList<String>(Arrays.asList(irData
                    .split(" ")));
            list.remove(0); // dummy
            int frequency = Integer.parseInt(list.remove(0), 16); // frequency
            list.remove(0); // seq1
            list.remove(0); // seq2

            for (int i = 0; i < list.size(); i++) {
                list.set(i, Integer.toString(Integer.parseInt(list.get(i), 16)));
            }

            frequency = (int) (1000000 / (frequency * 0.241246));
            list.add(0, Integer.toString(frequency));

            int[] countPattern = new int[list.size()];
            for(int i = 0;i < list.size();i++) {
                countPattern[i] = Integer.parseInt(list.get(i));
            }
            return new IRCode(frequency, countPattern);
        }

        protected IRCode count2duration(String countPattern) {
            List<String> list = new ArrayList<String>(Arrays.asList(countPattern.split(",")));
            int frequency = Integer.parseInt(list.get(0));
            int pulses = 1000000/frequency;
            int count;
            int duration;

            list.remove(0);

            for (int i = 0; i < list.size(); i++) {
                count = Integer.parseInt(list.get(i));
                duration = count * pulses;
                list.set(i, Integer.toString(duration));
            }

            int[] durationPattern = new int[list.size()];
            for(int i = 0;i < list.size();i++) {
                durationPattern[i] = Integer.parseInt(list.get(i));
            }
            return new IRCode(frequency, durationPattern);
        }

        public IRCode convertToDuration(String irData){
            String hexToDec = this.hex2DecAsString(irData);
            return this.count2duration(hexToDec);
        }

        public IRCode convertToCount(String irData){
            return this.hex2decAsIRCode(irData);
        }

    }

