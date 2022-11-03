package org.boozsoft.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectOfUtils {
    public static Map mapof(Object... args){
        HashMap hashMap = new HashMap();
        for (int i=0;i<args.length;i+=2){
            hashMap.put(args[i],args[i+1]);
        }
        return hashMap;
    }

    public static Map<String,String> mapof(String... args){
        HashMap hashMap = new HashMap();
        for (int i=0;i<args.length;i+=2){
            hashMap.put(args[i],args[i+1]);
        }
        return hashMap;
    }

    public static <K,V> Map<K,V> mapof(K k1,V v1,K k2,V v2){
        HashMap hashMap = new HashMap();
        hashMap.put(k1,v1);
        hashMap.put(k2,v2);
        return hashMap;
    }

    public static  List<String> listof(String... args){
        LinkedList<String> linkedList = new LinkedList<String>();

        for (int i=0;i<args.length;i++){
            linkedList.add(args[i]);
        }
        return linkedList;
    }




    public static  List listof(Object... args){
        LinkedList linkedList = new LinkedList();

        for (int i=0;i<args.length;i++){
            linkedList.add(args[i]);
        }
        return linkedList;
    }

}
