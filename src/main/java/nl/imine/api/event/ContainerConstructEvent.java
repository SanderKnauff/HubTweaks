package nl.imine.api.event;

import nl.imine.api.gui.Button;
import nl.imine.api.gui.Container;
import org.bukkit.event.HandlerList;

public class ContainerConstructEvent extends ContainerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public ContainerConstructEvent(Container container) {
		super(container);
	}

	public void addButton(Button b) {
		getContainer().addButton(b);
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
