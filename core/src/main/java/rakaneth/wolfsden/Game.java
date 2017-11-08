package rakaneth.wolfsden;

import com.badlogic.gdx.ApplicationAdapter;

import rakaneth.wolfsden.screens.TitleScreen;
import rakaneth.wolfsden.screens.WolfScreen;
import squidpony.squidmath.StatefulRNG;

/**
 * This is a small, not-overly-simple demo that presents some important features of SquidLib and shows a faster,
 * cleaner, and more recently-introduced way of displaying the map and other text. Features include dungeon map
 * generation, field of view, pathfinding (to the mouse position), simplex noise (used for a flickering torch effect),
 * language generation/ciphering, a colorful glow effect, and ever-present random number generation (with a seed).
 * You can increase the size of the map on most target platforms (but GWT struggles with large... anything) by
 * changing gridHeight and gridWidth to affect the visible area or bigWidth and bigHeight to adjust the size of the
 * dungeon you can move through, with the camera following your '@' symbol.
 * <br>
 * The assets folder of this project, if it was created with SquidSetup, will contain the necessary font files (just one
 * .fnt file and one .png are needed, but many more are included by default). You should move any font files you don't
 * use out of the assets directory when you produce a release JAR, APK, or GWT build.
 */
public class Game extends ApplicationAdapter {
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
