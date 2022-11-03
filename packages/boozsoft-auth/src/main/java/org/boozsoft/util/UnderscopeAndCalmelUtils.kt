package org.boozsoft.util;

import com.google.common.base.CaseFormat

object UnderscopeAndCalmelUtils {
    fun dbRowsToCamel(map: Map<String, Any>): HashMap<String, Any?> {
        val converter = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL)
        var newMap = HashMap<String, Any?>()
        map.keys.forEach { newMap.put(converter.convert(it).toString(), map[it] as Any?) }
        return newMap
    }
//    fun dbRowsToCamel2(map: Map<String, Any>): HashMap<String, Any?> {
//        val converter = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL)
//        var newMap = HashMap<String, Any?>()
//        map.keys.forEach { newMap.put(converter.convert(it).toString(), map[it] as Any?) }
//        return newMap
//    }
}
