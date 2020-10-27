package com.adlitteram.emailcruncher.gui.widgets;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import javax.swing.JComponent;

public class MultilineLabel extends JComponent {

    private String text;
    private final Insets margin = new Insets(5, 5, 5, 5);
    private int maxWidth = Integer.MAX_VALUE;
    private boolean justify;
    private final FontRenderContext frc = new FontRenderContext(null, false, false);

    public MultilineLabel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        var old = this.text;
        this.text = text;
        firePropertyChange("text", old, this.text);
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        if (maxWidth <= 0) {
            throw new IllegalArgumentException();
        }
        var old = this.maxWidth;
        this.maxWidth = maxWidth;
        firePropertyChange("maxWidth", old, this.maxWidth);
    }

    public boolean isJustified() {
        return justify;
    }

    public void setJustified(boolean justify) {
        var old = this.justify;
        this.justify = justify;
        firePropertyChange("justified", old, this.justify);
    }

    @Override
    public Dimension getPreferredSize() {
        return paintOrGetSize(null, getMaxWidth());
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintOrGetSize((Graphics2D) g, getWidth());
    }

    private Dimension paintOrGetSize(Graphics2D g, int width) {
        var insets = getInsets();
        width -= insets.left + insets.right + margin.left + margin.right;
        float w = insets.left + insets.right + margin.left + margin.right;
        float x = insets.left + margin.left;
        float y = insets.top + margin.top;

        if ((width > 0) && (text != null) && (text.length() > 0)) {

            float max = 0;
            var strs = text.split("\n");
            for (var str : strs) {
                var as = new AttributedString(str);
                as.addAttribute(TextAttribute.FONT, getFont());
                var aci = as.getIterator();
                var lbm = new LineBreakMeasurer(aci, frc);
                while (lbm.getPosition() < aci.getEndIndex()) {
                    var textLayout = lbm.nextLayout(width);
                    if (g != null) {
                        if (isJustified() && (textLayout.getVisibleAdvance() > 0.80 * width)) {
                            textLayout = textLayout.getJustifiedLayout(width);
                        }
                        textLayout.draw(g, x, y + textLayout.getAscent());
                    }
                    y += textLayout.getDescent() + textLayout.getLeading() + textLayout.getAscent();
                    max = Math.max(max, textLayout.getVisibleAdvance());
                }
            }
            w += max;
        }
        return new Dimension((int) Math.ceil(w), (int) Math.ceil(y) + insets.bottom + margin.bottom);
    }
}
