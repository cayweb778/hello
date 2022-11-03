//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springbooz.core.tool.node;

import java.util.List;

/**
 * ss
 */
public class ForestNodeMerger {
    /**
     *
     */
    public ForestNodeMerger() {
    }

    /**
     *
     * @param items sa
     * @param <T> sad
     * @return asd
     */
    public static <T extends INode> List<T> merge(List<T> items) {
        ForestNodeManager<T> forestNodeManager = new ForestNodeManager<T>(items);
        items.forEach((forestNode) -> {
            if (!forestNode.getParentId().equals("0")) {
                INode node = forestNodeManager.getTreeNodeAT(forestNode.getParentId());
                if (node != null) {
                    node.getChildren().add(forestNode);
                } else {
//                    forestNodeManager.addParentId(forestNode.getId());
                }
            }

        });
        return forestNodeManager.getRoot();
    }
}
