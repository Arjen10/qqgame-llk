/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.demo;

import java.awt.*;
import javax.swing.*;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.SystemInfo;
import com.github.conf.LlkInitialize;
import com.github.util.SpringBeanUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author Karl Tauber
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.github"})
public class FlatLafDemo {
    static final String PREFS_ROOT_PATH = "/flatlaf-demo";
    static final String KEY_TAB = "tab";

    static boolean screenshotsMode = Boolean.parseBoolean(System.getProperty("flatlaf.demo.screenshotsMode"));

    static JTextArea jTextArea = new JTextArea();

    static {
        jTextArea.setEditable(false);
        jTextArea.setRows(9);
        jTextArea.setBackground(Color.WHITE);
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(FlatLafDemo.class, args);
        SpringBeanUtil.setApplicationContext(run);
        // on macOS enable screen menu bar
        if (SystemInfo.isMacOS && System.getProperty("apple.laf.useScreenMenuBar") == null)
            System.setProperty("apple.laf.useScreenMenuBar", "true");

        if (FlatLafDemo.screenshotsMode && !SystemInfo.isJava_9_orLater && System.getProperty("flatlaf.uiScale") == null)
            System.setProperty("flatlaf.uiScale", "2x");

        SwingUtilities.invokeLater(() -> {
            DemoPrefs.init(PREFS_ROOT_PATH);
            // application specific UI defaults
            FlatLaf.registerCustomDefaultsSource("com.formdev.flatlaf.demo");
            // set look and feel
            DemoPrefs.setupLaf(args);
            // install inspectors
            FlatInspector.install("ctrl shift alt X");
            FlatUIDefaultsInspector.install("ctrl shift alt Y");
            // create frame
            var frame = new DemoFrame();
            if (FlatLafDemo.screenshotsMode)
                frame.setSize(new Dimension(2500, 840));
            // show frame
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}
