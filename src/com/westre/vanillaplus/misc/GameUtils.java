package com.westre.vanillaplus.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameUtils {
	
	public static <T> List<T> copyIterator(Iterator<T> iter) {
	    List<T> copy = new ArrayList<T>();
	    while (iter.hasNext())
	        copy.add(iter.next());
	    return copy;
	}
	
	public static String parseTime(long time) {
        long gameTime = time;
        long hours = gameTime / 1000 + 6;
        long minutes = (gameTime % 1000) * 60 / 1000; 
        String ampm = "AM";
        if (hours >= 12) {
            hours -= 12; ampm = "PM"; 
        }
 
        if (hours >= 12) {
            hours -= 12; ampm = "AM"; 
        }
 
        if (hours == 0) hours = 12;
 
        String mm = "0" + minutes; 
        mm = mm.substring(mm.length() - 2, mm.length());
 
        return hours + ":" + mm + " " + ampm;
    }
	
}
