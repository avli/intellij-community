// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.openapi.wm.impl.status;

import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.impl.status.InfoAndProgressPanel.MyInlineProgressIndicator;
import com.intellij.ui.TransparentPanel;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.containers.JBIterable;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author Konstantin Bulenkov
 */
@ApiStatus.Internal
public final class PresentationModeProgressPanel {
  private final MyInlineProgressIndicator progress;
  private final JBIterable<InlineProgressIndicator.ProgressButton> myEastButtons;
  private JLabel myText;
  private JProgressBar myProgressBar;
  private JLabel myText2;
  private JPanel myRootPanel;
  private JPanel myButtonPanel;

  public PresentationModeProgressPanel(@NotNull MyInlineProgressIndicator progress) {
    this.progress = progress;
    Font font = JBUI.Fonts.label(11);
    myText.setFont(font);
    myText2.setFont(font);
    myText.setText(" ");
    myText2.setText(" ");
    myEastButtons = this.progress.createPresentationButtons();
    myButtonPanel.add(InlineProgressIndicator.createButtonPanel(myEastButtons.map(b -> b.button)));
    myRootPanel.setPreferredSize(new JBDimension(250, 60));
    myProgressBar.setPreferredSize(new Dimension(JBUIScale.scale(250), myProgressBar.getPreferredSize().height));
  }

  private static @NotNull Color getTextForeground() {
    return EditorColorsManager.getInstance().getGlobalScheme().getDefaultForeground();
  }

  void update() {
    Color color = getTextForeground();
    myText.setForeground(color);
    myText2.setForeground(color);
    myProgressBar.setForeground(color);

    if (!StringUtil.equals(myText.getText(), progress.getText())) {
      myText.setText(StringUtil.defaultIfEmpty(progress.getText(), " "));
    }
    if (!StringUtil.equals(myText2.getText(), progress.getText2())) {
      myText2.setText(StringUtil.defaultIfEmpty(progress.getText2(), " "));
    }
    if ((progress.isIndeterminate() || progress.getFraction() == 0.0) != myProgressBar.isIndeterminate()) {
      myProgressBar.setIndeterminate(progress.isIndeterminate() || progress.getFraction() == 0.0);
      myProgressBar.revalidate();
    }

    if (!myProgressBar.isIndeterminate()) {
      myProgressBar.setValue((int)(progress.getFraction() * 99) + 1);
    }

    myEastButtons.forEach(b -> b.updateAction.run());
  }

  public @NotNull JComponent getProgressPanel() {
    return myRootPanel;
  }

  private void createUIComponents() {
    myRootPanel = new TransparentPanel(0.5f) {
      @Override
      public boolean isVisible() {
        if (!progress.showInPresentationMode()) {
          return false;
        }
        UISettings ui = UISettings.getInstance();
        return ui.getPresentationMode() || !ui.getShowStatusBar() && Registry.is("ide.show.progress.without.status.bar");
      }
    };
  }
}
