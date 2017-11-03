package rakaneth.wolfsden.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import rakaneth.wolfsden.components.Drawing;
import rakaneth.wolfsden.components.WolfMap;
import rakaneth.wolfsden.screens.WolfScreen;

public class RenderingSystem extends IteratingSystem
{
	private WolfScreen curScreen;
	public RenderingSystem(WolfScreen startScreen)
	{
		super(Family.all(Drawing.class).get());
		curScreen = startScreen;
	}
	
	public void setScreen(WolfScreen screen)
	{
		curScreen = screen;
	}
	
	public void processEntity(Entity entity, float dt)
	{
		curScreen.render();
	}  
}
