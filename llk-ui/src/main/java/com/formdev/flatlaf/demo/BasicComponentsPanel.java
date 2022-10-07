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
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.swing.*;
import java.awt.*;

import static com.github.util.SpringBeanUtil.getBean;

/**
 * 基础面板
 *
 * @author Karl Tauber
 */
@Slf4j
class BasicComponentsPanel extends JPanel {


    BasicComponentsPanel() {
        initComponents();
    }

    private void initComponents() {

        JLabel comboBoxLabel = new JLabel();
        JCheckBox enabledCheckBox = new JCheckBox();

        JLabel fileTypeLabel = new JLabel();
        JSlider jSlider = new JSlider(0, 600, 400);

        JButton testButton = new JButton();
        JButton exportButton = new JButton();
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
            try {
                FunctionService functionService = getBean(FunctionService.class);
                functionService.lockAndUnLockCountdown();
            } catch (NullPointerException ex) {
                log.error("锁定倒计时失败！可能是没有初始化程序！");
                enabledCheckBox.setSelected(false);
            }
        });
        add(comboBoxLabel, "cell 0 4");

        //---- comboBox3 ----
        add(enabledCheckBox, "cell 1 4,growx");

        fileTypeLabel.setText("速度调整：");
        fileTypeLabel.setDisplayedMnemonic('C');
        fileTypeLabel.setLabelFor(jSlider);
        jSlider.addChangeListener(e -> {
            FunctionService functionService = getBean(FunctionService.class);
            functionService.changeSpeed(jSlider.getValue());
        });
        add(fileTypeLabel, "cell 2 4");
        add(jSlider, "cell 3 4,growx");

        testButton.setText("初始化");
        testButton.addActionListener(e -> {
            try {
                LlkInitialize.initHandleAndRect();
            } catch (NullPointerException ex) {
                log.error("初始化程序失败！检查是否打开了连连看游戏！");
            }
        });
        add(testButton, "cell 1 14,growx");

        exportButton.setText("全图秒杀");
        exportButton.addActionListener(e -> {
            ThreadPoolTaskScheduler scheduler = getBean(ThreadPoolTaskScheduler.class);
            scheduler.execute(() -> {
                try {
                    ClearService clearService = getBean(ClearService.class);
                    clearService.clearAll();
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
            });
        });
        add(exportButton, "cell 2 14,growx");

        jScrollPane.setViewportView(FlatLafDemo.jTextArea);
        add(jScrollPane, "cell 0 16 16,growx");
    }

}
