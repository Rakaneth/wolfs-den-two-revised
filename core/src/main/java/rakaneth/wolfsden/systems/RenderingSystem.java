package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.Mapper;
import rakaneth.wolfsden.components.Position;
import squidpony.squidgrid.gui.gdx.SparseLayers;

public class RenderingSystem extends IteratingSystem
{
	private SparseLayers display;
	public RenderingSystem(SparseLayers display)
	{
		super(Family.all(Drawing.class).get());
		this.display = display;
	}
	
	public void processEntity(Entity entity, float dt)
	{
		Position pos = Mapper.position.get(entity);
		Drawing dr = Mapper.drawing.get(entity);
		if (pos.dirty) {
			display.slide(dr.glyph, pos.prev.x, pos.prev.y, pos.current.x, pos.current.y, 0.1f, null);
			pos.prev = pos.current;
			pos.dirty = false;
		}
	}  
}
