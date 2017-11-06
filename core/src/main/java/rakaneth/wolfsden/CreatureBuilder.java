package rakaneth.wolfsden;
import java.util.ArrayList;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.utils.JsonWriter;

import squidpony.DataConverter;

public class CreatureBuilder
{
	private Engine engine;
	private static final String fileName = "data/creatures.js";
	private DataConverter converter;
	private ArrayList<CreatureBase> creatures;

	
	@SuppressWarnings("unchecked")
	public CreatureBuilder(Engine engine)
	{
		this.engine = engine;
		converter = new DataConverter(JsonWriter.OutputType.javascript);
		creatures = converter.fromJson(ArrayList.class, CreatureBase.class, Gdx.files.internal(fileName));
	}

	@SuppressWarnings("unused")
	private static class CreatureBase
	{
		public String name;
		public int str;
		public int stam;
		public int spd;
		public int skl;
		public char glyph;
		public String color;
	}
}
