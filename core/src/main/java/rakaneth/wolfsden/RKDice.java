package rakaneth.wolfsden;

import rakaneth.wolfsden.WolfGame;

import squidpony.squidmath.Dice;

public class RKDice
{
	private static Dice	dice = new Dice(WolfGame.rng);
	private int					roll;
	private int					keep;

	/**
	 * Constructor for manual building. Creates a 1k1 roll.
	 */
	public RKDice()
	{
		setRoll(1);
		setKeep(1);
	}

	/**
	 * Constructor providing initial roll and keep values. This is the most
	 * commonly-used constructor.
	 * 
	 * @param roll
	 *          The number of dice to roll; the left side of the k.
	 * @param keep
	 *          The number of dice to keep; the right side of the k.
	 */
	public RKDice(int roll, int keep)
	{
		setRoll(roll);
		setKeep(keep);
	}

	/**
	 * Constructor taking a dice string in the form of XkY.
	 * 
	 * @param diceString
	 *          The string to convert. Must be in XkY form.
	 */
	public RKDice(String diceString)
	{
		this();
		String[] parts = diceString.split("k");
		if (parts.length == 2)
		{
			setRoll(Integer.parseInt(parts[0]));
			setKeep(Integer.parseInt(parts[1]));
		}
	}

	/**
	 * Gets the result of the roll.
	 * 
	 * @return The result of the roll of these dice.
	 */
	public int roll()
	{
		int realKeep = Math.min(roll, keep);
		return dice.bestOf(realKeep, roll, "!6");
	}
	
	/**
	 * Gets the number of successes of these dice rolled against a difficulty. <br>
	 * Every 5 over the difficulty is an additional success.
	 * 
	 * @param diff
	 * @return The number of successes rolled.
	 */
	public int roll(int diff)
	{
		int result = roll() - diff;
		return (result >= 0) ? (result / 5) + 1 : 0;
	}

	/**
	 * Adds another RKDice instance to this one. <br>
	 * The roll values are added together, then the keep values are added together.
	 * <br>
	 * The result cannot be less than 1k1.
	 * 
	 * @param rk
	 * @return a new RKDice instance that is the result of adding this to rk.
	 */
	public RKDice add(RKDice rk)
	{
		int newRoll = Math.max(1, roll + rk.roll);
		int newKeep = Math.max(1, keep + rk.keep);
		return new RKDice(newRoll, newKeep);
	}
	
	/**
	 * Statically rolls XkY without creating an instance.
	 * @param diceString The dice string to roll, in XkY format.
	 * @return The result of the roll.
	 */
	public static int roll(String diceString)
	{
		String[] parts = diceString.split("k");
		if (parts.length != 2) 
			return 0;
		else 
		{
			int roll = Integer.parseInt(parts[0]);
			int keep = Integer.parseInt(parts[1]);
			int realKeep = Math.min(roll, keep);
			return dice.bestOf(realKeep, roll,  "!6");
		}
	}
	
	/**
	 * Statically rolls XkY versus a difficulty of diff, counting successes as in {@link #roll(int)}.
	 * @param diceString The dice string to roll, in XkY format.
	 * @param diff The difficulty of the roll.
	 * @return The number of successes.
	 */
	public static int roll(String diceString, int diff)
	{
		int raw = roll(diceString) - diff;
		return (raw >= 0) ? (raw / 5) + 1 : 0;
	}

	public String toString()
	{
		return String.format("%dk%d", roll, keep);
	}

	/**
	 * Manually sets the number of dice to roll. Cannot be less than 1.
	 * 
	 * @param val
	 *          The number to set roll to.
	 */
	public void setRoll(int val)
	{
		roll = Math.max(1, val);
	}

	/**
	 * Manually sets the number of dice to keep. Cannot be less than 1.
	 * 
	 * @param val
	 *          The number to set keep to.
	 */
	public void setKeep(int val)
	{
		keep = Math.max(1, val);
	}
}
