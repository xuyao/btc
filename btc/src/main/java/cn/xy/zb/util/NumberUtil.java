package cn.xy.zb.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

    /** 
     * 使用DecimalFormat,保留小数点后n位 ,直接截取，不保留小数
     */  
    public static Double formatDouble(double value, Integer scale) {  
        BigDecimal bd = new BigDecimal(value);  
        bd = bd.setScale(scale, RoundingMode.DOWN);  
        return bd.doubleValue();
    }
    
    
    /** 
     * 使用DecimalFormat,保留小数点后n位,四舍五入
     */  
    public static Double formatDoubleHP(double value, Integer scale) {  
        BigDecimal bd = new BigDecimal(value);  
        bd = bd.setScale(scale, RoundingMode.HALF_UP);  
        return bd.doubleValue();
    }
    
    
    public static double doubleAdd(double a1, double b1) {  
    	BigDecimal a2 = new BigDecimal(Double.toString(a1));  
    	BigDecimal b2 = new BigDecimal(Double.toString(b1));  
    	return a2.add(b2).doubleValue();  
	}
    
    public static double doubleSub(double a1, double b1) {  
    	BigDecimal a2 = new BigDecimal(Double.toString(a1));  
    	BigDecimal b2 = new BigDecimal(Double.toString(b1));  
    	return a2.subtract(b2).doubleValue();  
	}
    
    public static double doubleMul(double a1, double b1) {  
    	BigDecimal a2 = new BigDecimal(Double.toString(a1));  
    	BigDecimal b2 = new BigDecimal(Double.toString(b1));  
    	return a2.multiply(b2).doubleValue();  
	}
    
    public static double doubleDiv(double a1, double b1, int scale) {
        if (scale < 0) {  
            throw new IllegalArgumentException("error");  
        }
        BigDecimal a2 = new BigDecimal(Double.toString(a1));  
        BigDecimal b2 = new BigDecimal(Double.toString(b1));  
        return a2.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
    }
    
    
    public static void main(String[] args){
    	System.out.println(formatDouble(450.161, 2));
    }
    
    
}
