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

import com.github.ClearService;
import com.github.FunctionService;
import com.github.conf.LlkInitialize;
import net.miginfocom.swing.MigLayout;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.swing.*;

import static com.github.util.SpringBeanUtil.getBean;

/**
 * 基础面板
 *
 * @author Karl Tauber
 */
class BasicComponentsPanel extends JPanel {

    private static final FunctionService FUNCTION_SERVICE = getBean(FunctionService.class);

    private static final  ClearService CLEAR_SERVICE = getBean(ClearService.class);

    private static final ThreadPoolTaskScheduler SCHEDULER = getBean(ThreadPoolTaskScheduler.class);

    BasicComponentsPanel() {
        initComponents();
    }

    private void initComponents() {

        JLabel comboBoxLabel = new JLabel();
        JCheckBox enabledCheckBox = new JCheckBox();

        JLabel fileTypeLabel = new JLabel();
        JSlider jSlider = new JSlider(0,500,400);

        JButton testButton = new JButton();
        JButton exportButton = new JButton();

        JTextArea textArea = new JTextArea();
        JScrollPane jScrollPane = new JScrollPane();

        //======== this ========
        setLayout(new MigLayout(
                "insets dialog,hidemode 3",
                // columns
                "[sizegroup 1]" +
                        "[sizegroup 1]" +
                        "[sizegroup 1]" +
                        "[]" +
                        "[]",
                // rows
                "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]" +
                        "[]para" +
                        "[]" +
                        "[]"));

        //---- 数据库类型 ----
        comboBoxLabel.setText("锁定倒计时：");
        comboBoxLabel.setDisplayedMnemonic('C');
        comboBoxLabel.setLabelFor(enabledCheckBox);
        enabledCheckBox.addActionListener(e -> {
            FUNCTION_SERVICE.lockAndUnLockCountdown();
        });
        add(comboBoxLabel, "cell 0 4");

        //---- comboBox3 ----
        add(enabledCheckBox, "cell 1 4,growx");

        fileTypeLabel.setText("速度调整：");
        fileTypeLabel.setDisplayedMnemonic('C');
        fileTypeLabel.setLabelFor(jSlider);
        jSlider.addChangeListener(e -> {
            FUNCTION_SERVICE.changeSpeed(jSlider.getValue());
        });
        add(fileTypeLabel, "cell 2 4");
        add(jSlider, "cell 3 4,growx");

        testButton.setText("初始化");
        testButton.addActionListener(e -> LlkInitialize.initHandleAndRect());
        add(testButton, "cell 1 14,growx");

        exportButton.setText("全图秒杀");
        exportButton.addActionListener(e -> {
            SCHEDULER.execute(CLEAR_SERVICE::clearAll);
        });
        add(exportButton, "cell 2 14,growx");

        jScrollPane.setViewportView(textArea);
        add(jScrollPane, "cell 2 16,growx");
    }

}
