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

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.demo.HintManager.Hint;
import com.formdev.flatlaf.demo.intellijthemes.IJThemesPanel;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatButton.ButtonType;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.ui.JBRCustomDecorations;
import com.formdev.flatlaf.util.SystemInfo;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;

/**
 * @author Karl Tauber
 */
class DemoFrame extends JFrame {
    private final String[] availableFontFamilyNames;
    private int initialFontMenuItemCount = -1;


    DemoFrame() {
        int tabIndex = DemoPrefs.getState().getInt(FlatLafDemo.KEY_TAB, 0);

        availableFontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames().clone();
        Arrays.sort(availableFontFamilyNames);

        initComponents();
        updateFontMenuItems();
        controlBar.initialize(this, tabbedPane);

        setIconImages(FlatSVGUtils.createWindowIconImages("/com/formdev/flatlaf/demo/FlatLaf.svg"));

        if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount() && tabIndex != tabbedPane.getSelectedIndex())
            tabbedPane.setSelectedIndex(tabIndex);

        SwingUtilities.invokeLater(this::showHints);
    }

    @Override
    public void dispose() {
        super.dispose();

        FlatUIDefaultsInspector.hide();
    }

    private void showHints() {
        Hint fontMenuHint = new Hint(
                "Use 'Font' menu to increase/decrease font size or try different fonts.",
                fontMenu, SwingConstants.BOTTOM, "hint.fontMenu", null);

        Hint optionsMenuHint = new Hint(
                "Use 'Options' menu to try out various FlatLaf options.",
                optionsMenu, SwingConstants.BOTTOM, "hint.optionsMenu", fontMenuHint);

        Hint themesHint = new Hint(
                "Use 'Themes' list to try out various themes.",
                themesPanel, SwingConstants.LEFT, "hint.themesPanel", optionsMenuHint);

        HintManager.showHint(themesHint);
    }

    private void clearHints() {
        HintManager.hideAllHints();

        Preferences state = DemoPrefs.getState();
        state.remove("hint.fontMenu");
        state.remove("hint.optionsMenu");
        state.remove("hint.themesPanel");
    }

    private void showUIDefaultsInspector() {
        FlatUIDefaultsInspector.show();
    }

    private void selectedTabChanged() {
        DemoPrefs.getState().putInt(FlatLafDemo.KEY_TAB, tabbedPane.getSelectedIndex());
    }

    private void windowDecorationsChanged() {
        boolean windowDecorations = windowDecorationsCheckBoxMenuItem.isSelected();

        // change window decoration of all frames and dialogs
        FlatLaf.setUseNativeWindowDecorations(windowDecorations);

        menuBarEmbeddedCheckBoxMenuItem.setEnabled(windowDecorations);
        unifiedTitleBarMenuItem.setEnabled(windowDecorations);
    }

    private void menuBarEmbeddedChanged() {
        UIManager.put("TitlePane.menuBarEmbedded", menuBarEmbeddedCheckBoxMenuItem.isSelected());
        FlatLaf.revalidateAndRepaintAllFramesAndDialogs();
    }

    private void unifiedTitleBar() {
        UIManager.put("TitlePane.unifiedBackground", unifiedTitleBarMenuItem.isSelected());
        FlatLaf.repaintAllFramesAndDialogs();
    }

    private void underlineMenuSelection() {
        UIManager.put("MenuItem.selectionType", underlineMenuSelectionMenuItem.isSelected() ? "underline" : null);
    }

    private void alwaysShowMnemonics() {
        UIManager.put("Component.hideMnemonics", !alwaysShowMnemonicsMenuItem.isSelected());
        repaint();
    }

    private void animatedLafChangeChanged() {
        System.setProperty("flatlaf.animatedLafChange", String.valueOf(animatedLafChangeMenuItem.isSelected()));
    }

    private void showHintsChanged() {
        clearHints();
        showHints();
    }

    private void fontFamilyChanged(ActionEvent e) {
        String fontFamily = e.getActionCommand();

        FlatAnimatedLafChange.showSnapshot();

        Font font = UIManager.getFont("defaultFont");
        Font newFont = StyleContext.getDefaultStyleContext().getFont(fontFamily, font.getStyle(), font.getSize());
        // StyleContext.getFont() may return a UIResource, which would cause loosing user scale factor on Windows
        newFont = FlatUIUtils.nonUIResource(newFont);
        UIManager.put("defaultFont", newFont);

        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    private void fontSizeChanged(ActionEvent e) {
        String fontSizeStr = e.getActionCommand();

        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) Integer.parseInt(fontSizeStr));
        UIManager.put("defaultFont", newFont);

        FlatLaf.updateUI();
    }

    private void restoreFont() {
        UIManager.put("defaultFont", null);
        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    private void incrFont() {
        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) (font.getSize() + 1));
        UIManager.put("defaultFont", newFont);

        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    private void decrFont() {
        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) Math.max(font.getSize() - 1, 10));
        UIManager.put("defaultFont", newFont);

        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    void updateFontMenuItems() {
        if (initialFontMenuItemCount < 0)
            initialFontMenuItemCount = fontMenu.getItemCount();
        else {
            // remove old font items
            for (int i = fontMenu.getItemCount() - 1; i >= initialFontMenuItemCount; i--)
                fontMenu.remove(i);
        }

        // get current font
        Font currentFont = UIManager.getFont("Label.font");
        String currentFamily = currentFont.getFamily();
        String currentSize = Integer.toString(currentFont.getSize());

        // add font families
        fontMenu.addSeparator();
        ArrayList<String> families = new ArrayList<>(Arrays.asList(
                "Arial", "Cantarell", "Comic Sans MS", "Courier New", "DejaVu Sans",
                "Dialog", "Liberation Sans", "Monospaced", "Noto Sans", "Roboto",
                "SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana"));
        if (!families.contains(currentFamily))
            families.add(currentFamily);
        families.sort(String.CASE_INSENSITIVE_ORDER);

        ButtonGroup familiesGroup = new ButtonGroup();
        for (String family : families) {
            if (Arrays.binarySearch(availableFontFamilyNames, family) < 0)
                continue; // not available

            JCheckBoxMenuItem item = new JCheckBoxMenuItem(family);
            item.setSelected(family.equals(currentFamily));
            item.addActionListener(this::fontFamilyChanged);
            fontMenu.add(item);

            familiesGroup.add(item);
        }

        // add font sizes
        fontMenu.addSeparator();
        ArrayList<String> sizes = new ArrayList<>(Arrays.asList(
                "10", "11", "12", "14", "16", "18", "20", "24", "28"));
        if (!sizes.contains(currentSize))
            sizes.add(currentSize);
        sizes.sort(String.CASE_INSENSITIVE_ORDER);

        ButtonGroup sizesGroup = new ButtonGroup();
        for (String size : sizes) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(size);
            item.setSelected(size.equals(currentSize));
            item.addActionListener(this::fontSizeChanged);
            fontMenu.add(item);

            sizesGroup.add(item);
        }

        // enabled/disable items
        boolean enabled = UIManager.getLookAndFeel() instanceof FlatLaf;
        for (Component item : fontMenu.getMenuComponents())
            item.setEnabled(enabled);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        JMenuBar menuBar1 = new JMenuBar();
        JMenuItem undoMenuItem = new JMenuItem();
        JMenuItem redoMenuItem = new JMenuItem();
        JMenuItem cutMenuItem = new JMenuItem();
        JMenuItem copyMenuItem = new JMenuItem();
        JMenuItem pasteMenuItem = new JMenuItem();
        JRadioButtonMenuItem radioButtonMenuItem1 = new JRadioButtonMenuItem();
        JRadioButtonMenuItem radioButtonMenuItem2 = new JRadioButtonMenuItem();
        JRadioButtonMenuItem radioButtonMenuItem3 = new JRadioButtonMenuItem();
        fontMenu = new JMenu();
        JMenuItem restoreFontMenuItem = new JMenuItem();
        JMenuItem incrFontMenuItem = new JMenuItem();
        JMenuItem decrFontMenuItem = new JMenuItem();
        optionsMenu = new JMenu();
        windowDecorationsCheckBoxMenuItem = new JCheckBoxMenuItem();
        menuBarEmbeddedCheckBoxMenuItem = new JCheckBoxMenuItem();
        unifiedTitleBarMenuItem = new JCheckBoxMenuItem();
        underlineMenuSelectionMenuItem = new JCheckBoxMenuItem();
        alwaysShowMnemonicsMenuItem = new JCheckBoxMenuItem();
        animatedLafChangeMenuItem = new JCheckBoxMenuItem();
        JMenuItem showHintsMenuItem = new JMenuItem();
        JMenuItem showUIDefaultsInspectorMenuItem = new JMenuItem();
        JMenu helpMenu = new JMenu();
        JMenuItem aboutMenuItem = new JMenuItem();
        JButton backButton = new JButton();
        JButton forwardButton = new JButton();
        JButton cutButton = new JButton();
        JButton copyButton = new JButton();
        JButton pasteButton = new JButton();
        JButton refreshButton = new JButton();
        JToggleButton showToggleButton = new JToggleButton();
        JPanel contentPanel = new JPanel();
        tabbedPane = new JTabbedPane();
        BasicComponentsPanel basicComponentsPanel = new BasicComponentsPanel();
        controlBar = new ControlBar();
        themesPanel = new IJThemesPanel();

        //======== this ========
        setTitle("QQ游戏大厅-连连看辅助");
        setResizable(false);
        setPreferredSize(new Dimension(560, 480));
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar1 ========
        {

            //======== fontMenu ========
            {
                fontMenu.setText("字体");

                //---- restoreFontMenuItem ----
                restoreFontMenuItem.setText("恢复默认大小");
                restoreFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                restoreFontMenuItem.addActionListener(e -> restoreFont());
                fontMenu.add(restoreFontMenuItem);

                //---- incrFontMenuItem ----
                incrFontMenuItem.setText("增大字体");
                incrFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                incrFontMenuItem.addActionListener(e -> incrFont());
                fontMenu.add(incrFontMenuItem);

                //---- decrFontMenuItem ----
                decrFontMenuItem.setText("减小字体");
                decrFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                decrFontMenuItem.addActionListener(e -> decrFont());
                fontMenu.add(decrFontMenuItem);
            }
            menuBar1.add(fontMenu);

            //======== optionsMenu ========
            {
                optionsMenu.setText("样式");

                //---- windowDecorationsCheckBoxMenuItem ----
                windowDecorationsCheckBoxMenuItem.setText("Window 窗口样式");
                windowDecorationsCheckBoxMenuItem.setSelected(true);
                windowDecorationsCheckBoxMenuItem.addActionListener(e -> windowDecorationsChanged());
                optionsMenu.add(windowDecorationsCheckBoxMenuItem);

                //---- menuBarEmbeddedCheckBoxMenuItem ----
                menuBarEmbeddedCheckBoxMenuItem.setText("Embedded menu bar");
                menuBarEmbeddedCheckBoxMenuItem.setSelected(true);
                menuBarEmbeddedCheckBoxMenuItem.addActionListener(e -> menuBarEmbeddedChanged());
                optionsMenu.add(menuBarEmbeddedCheckBoxMenuItem);

                //---- unifiedTitleBarMenuItem ----
                unifiedTitleBarMenuItem.setText("Unified window title bar");
                unifiedTitleBarMenuItem.addActionListener(e -> unifiedTitleBar());
                optionsMenu.add(unifiedTitleBarMenuItem);

                //---- underlineMenuSelectionMenuItem ----
                underlineMenuSelectionMenuItem.setText("Use underline menu selection");
                underlineMenuSelectionMenuItem.addActionListener(e -> underlineMenuSelection());
                optionsMenu.add(underlineMenuSelectionMenuItem);

                //---- alwaysShowMnemonicsMenuItem ----
                alwaysShowMnemonicsMenuItem.setText("Always show mnemonics");
                alwaysShowMnemonicsMenuItem.addActionListener(e -> alwaysShowMnemonics());
                optionsMenu.add(alwaysShowMnemonicsMenuItem);

                //---- animatedLafChangeMenuItem ----
                animatedLafChangeMenuItem.setText("Animated Laf Change");
                animatedLafChangeMenuItem.setSelected(true);
                animatedLafChangeMenuItem.addActionListener(e -> animatedLafChangeChanged());
                optionsMenu.add(animatedLafChangeMenuItem);

                //---- showHintsMenuItem ----
                showHintsMenuItem.setText("Show hints");
                showHintsMenuItem.addActionListener(e -> showHintsChanged());
                optionsMenu.add(showHintsMenuItem);

                //---- showUIDefaultsInspectorMenuItem ----
                showUIDefaultsInspectorMenuItem.setText("Show UI Defaults Inspector");
                showUIDefaultsInspectorMenuItem.addActionListener(e -> showUIDefaultsInspector());
                optionsMenu.add(showUIDefaultsInspectorMenuItem);
            }
            menuBar1.add(optionsMenu);

            //======== helpMenu ========
            {
                helpMenu.setText("帮助");
                helpMenu.setMnemonic('H');

                //---- aboutMenuItem ----
                aboutMenuItem.setText("关于");
                aboutMenuItem.setMnemonic('A');
                aboutMenuItem.addActionListener(e ->
                        JOptionPane.showMessageDialog(this,
                                "连连看辅助仅供学习交流使用！", "关于",
                                JOptionPane.QUESTION_MESSAGE));
                helpMenu.add(aboutMenuItem);
            }
            menuBar1.add(helpMenu);
        }
        setJMenuBar(menuBar1);

        //======== toolBar1 =======
            //---- 刷新按钮 ----
           /* refreshButton.setToolTipText("Refresh");
            toolBar1.add(refreshButton);
            toolBar1.addSeparator();*/



        //======== contentPanel ========
        {
            contentPanel.setLayout(new MigLayout(
                    "insets dialog,hidemode 3",
                    // columns
                    "[grow,fill]",
                    // rows
                    "[grow,fill]"));

            //======== tabbedPane ========
            {
                tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                tabbedPane.addChangeListener(e -> selectedTabChanged());
                tabbedPane.addTab("基本功能", basicComponentsPanel);
            }
            contentPanel.add(tabbedPane, "cell 0 0");
        }
        contentPane.add(contentPanel, BorderLayout.CENTER);
        contentPane.add(controlBar, BorderLayout.SOUTH);
        contentPane.add(themesPanel, BorderLayout.EAST);

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(radioButtonMenuItem1);
        buttonGroup1.add(radioButtonMenuItem2);
        buttonGroup1.add(radioButtonMenuItem3);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        // add "Users" button to menubar
        FlatButton usersButton = new FlatButton();
        usersButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/users.svg"));
        usersButton.setButtonType(ButtonType.toolBarButton);
        usersButton.setFocusable(false);
        usersButton.addActionListener(e ->
                JOptionPane.showMessageDialog(null,
                        "Github:Arjen10", "作者", JOptionPane.INFORMATION_MESSAGE));
        menuBar1.add(Box.createGlue());
        menuBar1.add(usersButton);

        undoMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/undo.svg"));
        redoMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/redo.svg"));

        cutMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-cut.svg"));
        copyMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/copy.svg"));
        pasteMenuItem.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-paste.svg"));

        backButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/back.svg"));
        forwardButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/forward.svg"));
        cutButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-cut.svg"));
        copyButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/copy.svg"));
        pasteButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-paste.svg"));
        refreshButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/refresh.svg"));
        showToggleButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/show.svg"));

        cutMenuItem.addActionListener(new DefaultEditorKit.CutAction());
        copyMenuItem.addActionListener(new DefaultEditorKit.CopyAction());
        pasteMenuItem.addActionListener(new DefaultEditorKit.PasteAction());

        if (FlatLaf.supportsNativeWindowDecorations()) {
            if (JBRCustomDecorations.isSupported()) {
                // If the JetBrains Runtime is used, it forces the use of it's own custom
                // window decoration, which can not disabled.
                windowDecorationsCheckBoxMenuItem.setEnabled(false);
            }
        } else {
            unsupported(windowDecorationsCheckBoxMenuItem);
            unsupported(menuBarEmbeddedCheckBoxMenuItem);
            unsupported(unifiedTitleBarMenuItem);
        }

        if (SystemInfo.isMacOS)
            unsupported(underlineMenuSelectionMenuItem);

        // remove contentPanel bottom insets
        MigLayout layout = (MigLayout) contentPanel.getLayout();
        LC lc = ConstraintParser.parseLayoutConstraint((String) layout.getLayoutConstraints());
        UnitValue[] insets = lc.getInsets();
        lc.setInsets(new UnitValue[]{
                insets[0],
                insets[1],
                new UnitValue(0, UnitValue.PIXEL, null),
                insets[3]
        });
        layout.setLayoutConstraints(lc);
    }

    private void unsupported(JCheckBoxMenuItem menuItem) {
        menuItem.setEnabled(false);
        menuItem.setSelected(false);
        menuItem.setToolTipText("暂不支持你的系统。");
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenu fontMenu;
    private JMenu optionsMenu;
    private JCheckBoxMenuItem windowDecorationsCheckBoxMenuItem;
    private JCheckBoxMenuItem menuBarEmbeddedCheckBoxMenuItem;
    private JCheckBoxMenuItem unifiedTitleBarMenuItem;
    private JCheckBoxMenuItem underlineMenuSelectionMenuItem;
    private JCheckBoxMenuItem alwaysShowMnemonicsMenuItem;
    private JCheckBoxMenuItem animatedLafChangeMenuItem;
    private JTabbedPane tabbedPane;
    private ControlBar controlBar;
    IJThemesPanel themesPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
