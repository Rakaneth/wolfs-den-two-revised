package rakaneth.wolfsden;
import java.util.HashMap;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.utils.JsonWriter;

import rakaneth.wolfsden.components.ActionStack;
import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.Position;
import rakaneth.wolfsden.components.Stats;
import squidpony.DataConverter;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidmath.Coord;

public class CreatureBuilder
{
	private Engine engine;
	private static final String fileName = "data/creatures.js";
	private HashMap<String, CreatureBase> creatures;
	private SparseLayers display;

	
	@SuppressWarnings("unchecked")
	public CreatureBuilder(Engine engine, SparseLayers display)
	{
		this.engine = engine;
		DataConverter converter = new DataConverter(JsonWriter.OutputType.javascript);
		creatures = converter.fromJson(HashMap.class, CreatureBase.class, Gdx.files.internal(fileName));
		System.out.println(creatures.get("wolf").glyph);
		this.display = display;
	}
	
	public Entity build(String id, WolfMap map)
	{
		CreatureBase base = creatures.get(id);
		Color color = Colors.get(base.color);
		Entity creature = new Entity();
		Coord pos = map.getEmpty();
		creature.add(new Position(pos, map));
		creature.add(new Drawing(display.glyph(base.glyph, color, pos.x, pos.y)));
		creature.add(new Stats(base.str, base.stam, base.spd, base.skl));
		creature.add(new ActionStack());
		engine.addEntity(creature);
		return creature;
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
