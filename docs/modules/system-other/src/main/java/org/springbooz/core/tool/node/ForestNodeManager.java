package org.springbooz.core.tool.node;

import org.springbooz.core.tool.node.INode;

import java.util.ArrayList;
import java.util.List;

/**
 * 森林管理类  https://blog.csdn.net/u014424628/article/details/51765394?utm_source=blogxgwz2
 *
 * @param <T> ss
 * @author pangu
 * */
public class ForestNodeManager<T extends INode> {

    /**
     * 森林的所有节点
     */
    private List<T> list;

    /**
     * 森林的父节点ID
     */
    private List<String> parentIds = new ArrayList<>();

    /**
     *
     * @param items sad
     */
    public ForestNodeManager(List<T> items) {
        list = items;
    }

    /**
     * 根据节点ID获取一个节点
     *
     * @param id 节点ID
     * @return 对应的节点对象
     */
    public INode getTreeNodeAT(String id) {
        for (INode forestNode : list) {
            if (forestNode.getId().equals(id)) {
                return forestNode;
            }
        }
        return null;
    }

    /**
     * 增加父节点ID
     *
     * @param parentId sadsa
     */
    public void addParentId(String parentId) {
        parentIds.add(parentId);
    }

    /**
     * 获取树的根节点(一个森林对应多颗树)
     *
     * @return 树的根节点集合
     */
    public List<T> getRoot() {
        List<T> roots = new ArrayList<>();
        for (T forestNode : list) {
            if (forestNode.getParentId().equals("0") || parentIds.contains(forestNode.getId())) {
                roots.add(forestNode);
            }
        }
        return roots;
    }
}
