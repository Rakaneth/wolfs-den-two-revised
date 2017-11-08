package rakaneth.wolfsden;

import com.badlogic.gdx.ApplicationAdapter;

import rakaneth.wolfsden.screens.TitleScreen;
import rakaneth.wolfsden.screens.WolfScreen;
import squidpony.squidmath.StatefulRNG;

public class WolfGame extends ApplicationAdapter {
	public static final StatefulRNG rng = new StatefulRNG(0xDEADBEEF);
	private static WolfScreen curScreen;

	@Override
	public void create () 
	{
		curScreen = TitleScreen.instance;
	}

	@Override
	public void render () 
	{
		curScreen.render();
	}

	@Override
	public void resize(int width, int height) 
	{
		super.resize(width, height);
	}
	
	public static void setScreen(WolfScreen sc)
	{
		curScreen = sc;
		sc.setInput();
	}
}
