package cn.xy.zb.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

    /** 
     * 使用DecimalFormat,保留小数点后n位 
     */  
    public static Double formatDouble(double value, Integer scale) {  
        BigDecimal bd = new BigDecimal(value);  
        bd = bd.setScale(scale, RoundingMode.HALF_UP);  
        return bd.doubleValue();
    }
    
    
    public static void main(String[] args){
    	System.out.println(formatDouble(1.1231415556666, 4));
    }
    
    
}
