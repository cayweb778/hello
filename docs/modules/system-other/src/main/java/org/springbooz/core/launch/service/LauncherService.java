//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springbooz.core.launch.service;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.Ordered;

public interface LauncherService extends Ordered, Comparable<LauncherService> {
    void launcher(SpringApplicationBuilder builder, String appName, String profile);

    default int getOrder() {
        return -2147483648;
    }

    default int compareTo(LauncherService o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }
}
