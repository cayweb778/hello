package org.boozsoft.util;

import cn.hutool.core.bean.BeanUtil;
import lombok.SneakyThrows;
import org.springbooz.core.tool.node.ForestNodeMerger;
import org.springbooz.core.tool.node.INode;

import java.util.List;
import java.util.stream.Collectors;

public class TreeUtils {
    @SneakyThrows
    public static <T> T castTreeObject(Object source, Class<T> clz) {
        Object vo = clz.getDeclaredConstructor().newInstance();
        BeanUtil.copyProperties(source, vo);
        return (T) vo;
    }

    public static  <T extends INode,S> List<T> castTreeList(List<S> soruce, Class<T> clz) {
        List list = soruce.stream()
                .map(item -> TreeUtils.castTreeObject(item, clz))
                .collect(Collectors.toList());
        return ForestNodeMerger.merge(list);
    }

}
