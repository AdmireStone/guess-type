package org.vlis.operations.event.typeguess;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.vlis.operations.event.typeguess.SpeculateKeyTypeCounter.KeyTypeCounter.SmallKeyTypeCounter;


/**
 * @author thinking_fioa , E-mail: thinking_fioa@163.com Create Time : 2016年6月29日 下午4:10:27
 *
 */
public class SpeculateKeyTypeCounter {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(SpeculateKeyTypeCounter.class);
    
    private Map<String, SmallKeyTypeCounter> counterContainer = new HashMap<String, SmallKeyTypeCounter>();
    private final KeyTypeCounter keyTypeCounter ;
    public SpeculateKeyTypeCounter(){
        keyTypeCounter = new KeyTypeCounter();
        counterContainer.put(keyTypeCounter.BOOLEAN_TYPE.getType(), keyTypeCounter.BOOLEAN_TYPE);
        counterContainer.put(keyTypeCounter.DATE_TYPE.getType(), keyTypeCounter.DATE_TYPE);
        counterContainer.put(keyTypeCounter.STRING_TYPE.getType(), keyTypeCounter.STRING_TYPE);
        counterContainer.put(keyTypeCounter.DOUBLE_TYPE.getType(), keyTypeCounter.DOUBLE_TYPE);
        counterContainer.put(keyTypeCounter.LONG_TYPE.getType(), keyTypeCounter.LONG_TYPE);
    }
    
    public void distinguishKeyAndCount( String unKnowKeyType){
        if(null == unKnowKeyType){
            return ;
        }
        distinguishKey0(unKnowKeyType);
    }
    
    private void distinguishKey0( String unKnowKeyType ){
        
        if(DistinguishKeyTypeHelper.getInstance().isBooleanType(unKnowKeyType)){
            //Boolean increase 
           // LOGGER.info(" distinguishKey increase --  Boolean : " + unKnowKeyType);
            counterContainer.get(keyTypeCounter.BOOLEAN_TYPE.getType()).increase();
        }else if(DistinguishKeyTypeHelper.getInstance().isDateType(unKnowKeyType)){
            //Date increase
            //LOGGER.info(" distinguishKey increase --  Date : " + unKnowKeyType);
            counterContainer.get(keyTypeCounter.DATE_TYPE.getType()).increase();
        }else if(DistinguishKeyTypeHelper.getInstance().isLongType(unKnowKeyType)){
            // Integer
            //LOGGER.info(" distinguishKey increase --  Integer : " + unKnowKeyType);
            counterContainer.get(keyTypeCounter.LONG_TYPE.getType()).increase();
        }else if(DistinguishKeyTypeHelper.getInstance().isDoubleType(unKnowKeyType)){
            // Double
            //LOGGER.info(" distinguishKey increase --  Double : " + unKnowKeyType);
            counterContainer.get(keyTypeCounter.DOUBLE_TYPE.getType()).increase();
        }else {
            //String
            //LOGGER.info(" distinguishKey increase --  String : " + unKnowKeyType);
            counterContainer.get(keyTypeCounter.STRING_TYPE.getType()).increase();
        }
    }
    
//    public String getKeyType(){
//        
//        if( 0 != counterContainer.get(keyTypeCounter.STRING_TYPE.getType()).getTypeCount() ){
//            //String
//            return keyTypeCounter.STRING_TYPE.getType();
//        }else if( 0 != counterContainer.get(keyTypeCounter.DATE_TYPE.getType()).getTypeCount() ){
//            //Date
//            return keyTypeCounter.DATE_TYPE.getType();
//        }else if(0 != counterContainer.get(keyTypeCounter.BOOLEAN_TYPE.getType()).getTypeCount() ){
//            //Boolean 
//            return keyTypeCounter.BOOLEAN_TYPE.getType();
//        }else if( 0 != counterContainer.get(keyTypeCounter.DOUBLE_TYPE.getType()).getTypeCount() ){
//            //Double 
//            return keyTypeCounter.DOUBLE_TYPE.getType();
//        }else if( 0 != counterContainer.get(keyTypeCounter.LONG_TYPE.getType()).getTypeCount() ){
//            //Long
//            return keyTypeCounter.LONG_TYPE.getType();
//        }else{
//            throw new IllegalStateException();
//        }
//    }
    /**
     * using different number to represent each data type 
     * the mapping relationship followed the definition of database
     * @return the attribute type 
     */
    public int getKeyType(){
        
        if( 0 != counterContainer.get(keyTypeCounter.STRING_TYPE.getType()).getTypeCount() ){
            //String
            return 4;
        }else if( 0 != counterContainer.get(keyTypeCounter.DATE_TYPE.getType()).getTypeCount() ){
            //Date
            return 5;
        }else if(0 != counterContainer.get(keyTypeCounter.BOOLEAN_TYPE.getType()).getTypeCount() ){
            //Boolean 
            return 1;
        }else if( 0 != counterContainer.get(keyTypeCounter.DOUBLE_TYPE.getType()).getTypeCount() ){
            //Double 
            return 3;
        }else if( 0 != counterContainer.get(keyTypeCounter.LONG_TYPE.getType()).getTypeCount() ){
            //Long
            return 2;
        }else{
            throw new IllegalStateException();
        }
    }
    @SuppressWarnings("unused")
    private Map<Integer, String> sortMapByKey(Map<Integer, String> oriMap) {  
        if (oriMap == null || oriMap.isEmpty()) {  
            return null;  
        }  
        Map<Integer, String> sortedMap = new TreeMap<Integer, String>(new Comparator<Integer>() {  
            
            public int compare(Integer key1, Integer key2) {  
                return key2 - key1;  // increase progressively
            }});  
        sortedMap.putAll(oriMap);  
        return sortedMap;  
    }  
    
    class KeyTypeCounter {
        
        private final SmallKeyTypeCounter BOOLEAN_TYPE = new SmallKeyTypeCounter("Boolean");
        private final SmallKeyTypeCounter DATE_TYPE = new SmallKeyTypeCounter("Date");
        private final SmallKeyTypeCounter STRING_TYPE = new SmallKeyTypeCounter("String");
        private final SmallKeyTypeCounter DOUBLE_TYPE = new SmallKeyTypeCounter("Double");
        private final SmallKeyTypeCounter LONG_TYPE = new SmallKeyTypeCounter("Long");
        
        private KeyTypeCounter(){}

         class SmallKeyTypeCounter{
            private String type;
            private int count;
            
            private SmallKeyTypeCounter(final String type){
                this.type = type;
                this.count = 0;
            }
            
            public void increase(){
                this.count = this.count +1;
            }
            
            public String getType(){
                return this.type;
            }
            
            public int getTypeCount(){
                return this.count;
            }
        }
    }
}
