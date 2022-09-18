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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.*;

/**
 * 最下面得工具栏
 *
 * @author Karl Tauber
 */
class ControlBar extends JPanel {
    private DemoFrame frame;
    private JTabbedPane tabbedPane;

    ControlBar() {
        initComponents();

        // remove top insets
        MigLayout layout = (MigLayout) getLayout();
        LC lc = ConstraintParser.parseLayoutConstraint((String) layout.getLayoutConstraints());
        UnitValue[] insets = lc.getInsets();
        lc.setInsets(new UnitValue[]{
                new UnitValue(0, UnitValue.PIXEL, null),
                insets[1],
                insets[2],
                insets[3]
        });
        layout.setLayoutConstraints(lc);

        UIScale.addPropertyChangeListener(e -> {
            // update info label because user scale factor may change
            updateInfoLabel();
        });
    }

    @Override
    public void updateUI() {
        super.updateUI();

        if (infoLabel != null)
            updateInfoLabel();
    }

    //这个是键盘监听
    void initialize(DemoFrame frame, JTabbedPane tabbedPane) {
        this.frame = frame;
        this.tabbedPane = tabbedPane;

        // register Alt+UP and Alt+DOWN to switch to previous/next theme
        ((JComponent) frame.getContentPane()).registerKeyboardAction(
                e -> frame.themesPanel.selectPreviousTheme(),
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_DOWN_MASK),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ((JComponent) frame.getContentPane()).registerKeyboardAction(
                e -> frame.themesPanel.selectNextTheme(),
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_DOWN_MASK),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        // update info label when moved to another screen
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                updateInfoLabel();
            }
        });
    }

    private void updateInfoLabel() {
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        infoLabel.setText(String.format("(JavaVersion %s %s )", javaVersion, javaVendor));
    }

    private void enabledChanged() {
        enabledDisable(tabbedPane, enabledCheckBox.isSelected());

        // repainting whole tabbed pane is faster than repainting many individual components
        tabbedPane.repaint();
    }

    private void enabledDisable(Container container, boolean enabled) {
        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                enabledDisable((JPanel) c, enabled);
                continue;
            }

            c.setEnabled(enabled);

            if (c instanceof JScrollPane) {
                Component view = ((JScrollPane) c).getViewport().getView();
                if (view != null)
                    view.setEnabled(enabled);
            } else if (c instanceof JTabbedPane) {
                JTabbedPane tabPane = (JTabbedPane) c;
                int tabCount = tabPane.getTabCount();
                for (int i = 0; i < tabCount; i++) {
                    Component tab = tabPane.getComponentAt(i);
                    if (tab != null)
                        tab.setEnabled(enabled);
                }
            }

            if (c instanceof JToolBar)
                enabledDisable((JToolBar) c, enabled);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        separator1 = new JSeparator();
        enabledCheckBox = new JCheckBox();
        infoLabel = new JLabel();

        //======== this ========
        setLayout(new MigLayout(
                "insets dialog",
                // columns
                "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[grow,fill]" +
                        "[button,fill]",
                // rows
                "[bottom]" +
                        "[]"));
        add(separator1, "cell 0 0 5 1");

        //---- enabledCheckBox ----
        enabledCheckBox.setText("锁定");
        enabledCheckBox.setMnemonic('E');
        enabledCheckBox.setSelected(false);
        enabledCheckBox.addActionListener(e -> enabledChanged());
        add(enabledCheckBox, "cell 2 1");

        //---- infoLabel ----
        infoLabel.setText("text");
        add(infoLabel, "cell 3 1,alignx center,growx 0");

        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JSeparator separator1;
    private JCheckBox enabledCheckBox;
    private JLabel infoLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
