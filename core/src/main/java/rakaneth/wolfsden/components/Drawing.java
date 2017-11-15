package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class Drawing implements Component
{
  public char  glyph;
  public Color color;
  public int layer;

  public Drawing(char g, Color color)
  {
    glyph = g;
    this.color = color;
    layer = 3;
  }
  
  public Drawing(char g, Color color, int layer)
  {
    this(g, color);
    this.layer = layer;
  }
}
