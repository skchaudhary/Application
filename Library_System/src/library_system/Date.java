package library_system;

import java.text.SimpleDateFormat; 
import java.util.*;
public class Date {
    public static void main(String[] args){
        Date date=new Date();
        SimpleDateFormat sm=new SimpleDateFormat("dd/MM/yyyy");
        String s=sm.format(date);
        System.out.println(s);
    }
}
