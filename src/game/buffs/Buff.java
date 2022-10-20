package game.buffs;
import java.util.ArrayList;
import java.util.function.Consumer;

import entities.*;

/**
*
* @author Lukas Meeder
* @version 1.0 (20.10.2022)
*
*/

public class Buff {
	// Buff Class
	public Buff(int id, String name, String description, Consumer<Entity> funktion, boolean isDebuff, int interval, int duration, Entity entity)
	{
		int existingBuffCount = 0;
				
		// Check if a buff with that id already exists on the player
		for(Buff checkedBuff : Buff.allBuffs)
		{
			if(id == checkedBuff.getId())
			{
				existingBuffCount++;
			}
		}
		
		if(existingBuffCount <= 0) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.appliedTo = entity;
			this.funktion = funktion;
			this.isDebuff = isDebuff;
			this.interval = interval;
			this.duration = duration;
			this.lastTimestamp = System.currentTimeMillis();
			this.creationTimestamp = System.currentTimeMillis();
			allBuffs.add(this);
			System.out.println("Created a new Buff with id " + this.id);
		} else {
			System.err.println("Buff with id " + id + " already exists. Skipping.");
		}
	}
	
	public Buff(int id, String name, String description, Consumer<Entity> funktion, Consumer<Entity> funktionOff, boolean isDebuff, int interval, int duration, Entity entity)
	{
		int existingBuffCount = 0;
				
		// Check if a buff with that id already exists on the player
		for(Buff checkedBuff : Buff.allBuffs)
		{
			if(id == checkedBuff.getId())
			{
				existingBuffCount++;
			}
		}
		
		if(existingBuffCount <= 0) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.appliedTo = entity;
			this.funktion = funktion;
			this.funktionOff = funktionOff;
			this.isDebuff = isDebuff;
			this.interval = interval;
			this.duration = duration;
			this.lastTimestamp = System.currentTimeMillis();
			this.creationTimestamp = System.currentTimeMillis();
			allBuffs.add(this);
			System.out.println("Created a new Buff with id " + this.id);
		} else {
			System.err.println("Buff with id " + id + " already exists. Skipping.");
		}
	}
	
	private static ArrayList<Buff> allBuffs = new ArrayList<Buff>();
	private static ArrayList<Buff> buffsToBeRemoved = new ArrayList<Buff>();
	
	private int id;
	private boolean isDebuff = false;
	private String name = "Default buff name";
	private String description = "Default buff description";
	private Consumer<Entity> funktion;
	private Consumer<Entity> funktionOff;
	private int interval = 0;
	private int duration = 10;
	private Entity appliedTo;
	private long lastTimestamp;
	private long creationTimestamp;
	
	// Buff Method etc..
// Methods
	
	
	public String getName()
	{
		return this.name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription()
	{
		return this.description;
	}
	
	public static ArrayList<Buff> getAllBuffs(){
		return allBuffs;
	}
	
	public static ArrayList<Buff> getBuffsToBeRemoved(){
		return buffsToBeRemoved;
	}

	public Entity getAppliedTo() {
		return appliedTo;
	}

	public void setAppliedTo(Entity appliedTo) {
		this.appliedTo = appliedTo;
	}
	
	public boolean isDebuff() {
		return isDebuff;
	}

	public void setDebuff(boolean isDebuff) {
		this.isDebuff = isDebuff;
	}
	
	public Consumer<Entity> getFunktionOff() {
		return funktionOff;
	}

	public void setFunktionOff(Consumer<Entity> funktionOff) {
		this.funktionOff = funktionOff;
	}

	public void executeFunktion()
	{
		if(funktion != null) {
			if(interval == 0) { // If no interval
				funktion.accept(this.appliedTo);
			} else 
			{
				if((System.currentTimeMillis() - lastTimestamp) > interval) // If intervall is hit
				{
					funktion.accept(this.appliedTo);
					this.lastTimestamp = System.currentTimeMillis();
				}
			}
			
		} else {
			System.err.println("Tried to execute function of Buff " + getId());
		}
		
		if((System.currentTimeMillis() - this.creationTimestamp) > duration)
		{
			if(this.funktionOff != null)
			{
				executeEndFunction();
			}
			buffsToBeRemoved.add(this);
		}
	}
	
	public void executeEndFunction()
	{
		if(funktionOff != null)
		{
			funktionOff.accept(this.appliedTo);
		} else 
		{
			System.out.println("Buff with id " + this.getId() + " does not have an ending function!");
		}
	}
	
	
}
