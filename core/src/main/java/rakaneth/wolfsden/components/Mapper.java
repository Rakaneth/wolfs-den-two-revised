package rakaneth.wolfsden.components;

import com.badlogic.ashley.core.ComponentMapper;

public class Mapper {
	public static final ComponentMapper<Drawing> drawing = ComponentMapper.getFor(Drawing.class);
	public static final ComponentMapper<Identity> identity = ComponentMapper.getFor(Identity.class);
	public static final ComponentMapper<Position> position = ComponentMapper.getFor(Position.class);
	public static final ComponentMapper<ActionStack> actions = ComponentMapper.getFor(ActionStack.class);
	public static final ComponentMapper<Stats> stats = ComponentMapper.getFor(Stats.class);
}
