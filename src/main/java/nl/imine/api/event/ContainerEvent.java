package nl.imine.api.event;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class ContainerEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private Container container;

	public ContainerEvent(Container container) {
		super();
		this.container = container;
	}

	public String getContainerName() {
		return container.getTitle();
	}

	public List<Button> getButtons() {
		return container.getButtons();
	}

	public List<Button> getStaticButtons() {
		return container.getStaticButtons();
	}

	public List<Player> getVieuwers() {
		return container.getVieuwers();
	}

	public Container getContainer() {
		return container;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
