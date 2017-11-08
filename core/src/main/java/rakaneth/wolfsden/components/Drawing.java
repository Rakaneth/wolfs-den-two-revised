package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class Drawing implements Component {
	public char glyph;
	public Color color;
	
	public Drawing(char g, Color color)
	{
		glyph = g;
		this.color = color;
	}
}
