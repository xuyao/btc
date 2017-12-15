package cn.xy.btc.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

    /** 
     * 使用DecimalFormat,保留小数点后两位 
     */  
    public static Double formatDouble4(double value) {  
        BigDecimal bd = new BigDecimal(value);  
        bd = bd.setScale(4, RoundingMode.HALF_UP);  
        return bd.doubleValue();
    }
    
    
    public static void main(String[] args){
    	System.out.println(formatDouble4(1.1231415556666));
    }
    
    
}
