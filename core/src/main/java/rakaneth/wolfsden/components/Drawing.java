package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;

import squidpony.squidgrid.gui.gdx.SColor;

public class Drawing implements Component
{
  public char   glyph;
  public SColor color;
  public int    layer;

  public Drawing(char g, SColor color)
  {
    glyph = g;
    this.color = color;
    layer = 3;
  }

  public Drawing(char g, SColor color, int layer)
  {
    this(g, color);
    this.layer = layer;
  }
}
