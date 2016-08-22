package org.vlis.operations.event.typeguess;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * @author thinking_fioa , E-mail: thinking_fioa@163.com Create Time : 2016年5月29日下午4:02:41
 *
 */
public enum KeyType {
    BOOLEAN_TYPE("Boolean"){
        public Object transform(String value){
            try{
                return Boolean.valueOf(value);
            }catch(NumberFormatException ex){
                LOGGER.error(" Key Transform  number , value : " +value +", Type : Boolean");
                ex.printStackTrace();
                //TODO: recorde mysql 
            }
            return null;
        }
    },
    DATE_TYPE("Date"){
        public Object transform(String value){
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sdf.format(sdf.parse(value));
            }catch(ParseException ex){
                LOGGER.error(" Key Transform  number , value : " +value +", Type : Date");
                ex.printStackTrace();
              //TODO: recorde mysql 
            }catch(NumberFormatException ex){
                LOGGER.error(" Key Transform  number , value : " +value +", Type : Date");
                ex.printStackTrace();
              //TODO: recorde mysql 
            }
            return null;
        }
    },
    STRING_TYPE("String"){
        public Object transform(String value){
            try{
                return String.valueOf(value);
            }catch(NumberFormatException ex){
                LOGGER.error(" Key Transform  number , value : " +value +", Type : String");
                ex.printStackTrace();
              //TODO: recorde mysql 
            }
            return null;
        }
    },
    DOUBLE_TYPE("Double"){
        public Object transform(String value){
            try{
                return Double.valueOf(value);
            }catch(NumberFormatException ex){
                LOGGER.error(" Key Transform  number , value : " +value +", Type : Double");
                ex.printStackTrace();
              //TODO: recorde mysql 
            }
            return null;
        }
    },
    LONG_TYPE("Long"){
        public Object transform(String value){
            try{
                return Long.valueOf(value);
            }catch(NumberFormatException ex){
                LOGGER.error(" Key Transform  number , value : " +value +", Type : Integer");
                ex.printStackTrace();
              //TODO: recorde mysql 
            }
            return null;
        }
    };
    
    private static final Logger LOGGER = LogManager.getLogger(KeyType.class);
    private String type;
    
    KeyType(String type){
        this.type = type;
    }
    
    public abstract Object transform(String value);
    
    public String getType(){
        return this.type;
    }
}
