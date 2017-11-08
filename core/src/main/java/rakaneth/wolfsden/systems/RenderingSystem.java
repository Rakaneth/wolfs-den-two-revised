package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.screens.PlayScreen;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidmath.Coord;

public class RenderingSystem extends IteratingSystem
{
	private SparseLayers display;
	private PlayScreen screen;
	
	public RenderingSystem(PlayScreen screen, SparseLayers display)
	{
		super(Family.all(Drawing.class).get());
		this.display = display;
		this.screen = screen;
	}
	
	public void processEntity(Entity entity, float dt)
	{
		Coord pos = Mapper.position.get(entity).current;
		Drawing dr = Mapper.drawing.get(entity);
		Coord cam = screen.cam();
		double[][] visible = screen.visible();
		if (visible[pos.x][pos.y] > 0.0)
		{
			display.put(pos.x-cam.x, pos.y-cam.y, dr.glyph, dr.color);
		}
	}  
}
