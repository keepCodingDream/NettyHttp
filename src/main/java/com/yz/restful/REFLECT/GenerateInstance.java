package com.yz.restful.REFLECT;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;


public class GenerateInstance {
     public <T> Object generate(Map<String,?> map,Class<T> class1s) throws Exception{
    	  Object obj = class1s.newInstance();
    	  Field[] fields = class1s.getDeclaredFields();
    	  for(Field item:fields){
    		  PropertyDescriptor pd = new PropertyDescriptor(item.getName(), class1s);
    		  Method method=pd.getWriteMethod();
    		  for(String key:map.keySet()){
    			  if(key.equals(item.getName())){
    				  method.invoke(obj, map.get(key));
    			  }
    		  }
    		  
    	  }
    	 return obj;
     }
}
