package io.coala.jetbrains.utils;

import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.markup.TextAttributes;

public class RangeMarkerTextAttributes {

  private final TextAttributes rangeMarkerTextAttributes = new TextAttributes();

  public RangeMarkerTextAttributes() {
    rangeMarkerTextAttributes
        .copyFrom(CodeInsightColors.WARNINGS_ATTRIBUTES.getDefaultAttributes());
  }

  public TextAttributes getRangeMarkerTextAttributes() {
    return this.rangeMarkerTextAttributes;
  }
}
