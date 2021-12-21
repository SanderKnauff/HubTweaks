package nl.imine.api.gui;

import java.util.ArrayList;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class GuiManager {

	private static GuiManager manager;

	private final ArrayList<Container> containers = new ArrayList<>();

	public static void init(Plugin plugin) {
		GuiManager.manager = new GuiManager();
		GuiListener.init(plugin);
	}

	private GuiManager() {
	}

	public static GuiManager getInstance() {
		return manager;
	}

	public ArrayList<Container> getContainers() {
		return containers;
	}

	/**
	 * Registers containers to the manager. This can be used to create custom
	 * containers capable of storing personalized data. Note that registering
	 * containers created with GuiManager#createContainer() is unneccesary and
	 * will not have any effect.
	 *
	 * @param container
	 *            The container to register.
	 * @return The registered container. This is the same instance as the one
	 *         from the passed through argument.
	 */
	public Container registeredContainer(Container container) {
		ensureContainer(container);
		return container;
	}

	public Container createContainer(String name) {
		return registeredContainer(new GenericContainer(name));
	}

	public Container createContainer(String name, int size) {
		return registeredContainer(new GenericContainer(name, size));
	}

	public Container createContainer(String name, int size, boolean autoResize) {
		return registeredContainer(new GenericContainer(name, size, autoResize));
	}

	public Container createContainer(String name, int size, boolean autoResize, boolean defaultButtons) {
		return registeredContainer(new GenericContainer(name, size, autoResize, defaultButtons));
	}

	public boolean isGuiInventory(Inventory inv) {
		for (Container c : containers) {
			for (Inventory i : c.getOpenInventories()) {
				if (i.equals(inv)) {
					return true;
				}
			}
		}
		return false;
	}

	public Container getInventoryContainer(Inventory inv) {
		for (Container c : containers) {
			for (Inventory i : c.getOpenInventories()) {
				if (i.equals(inv)) {
					return c;
				}
			}
		}
		return null;
	}

	public void ensureContainer(Container container) {
		if (!containers.contains(container)) {
			containers.add(container);
		}

	}

	public void removeContainer(Container container) {
		containers.remove(container);
	}

	private class GenericContainer extends Container {

		public GenericContainer(String title) {
			super(title);
		}

		public GenericContainer(String title, int maxSize) {
			super(title, maxSize);
		}

		public GenericContainer(String title, int maxSize, boolean autoResize) {
			super(title, maxSize, autoResize);
		}

		public GenericContainer(String title, int maxSize, boolean autoResize, boolean defaultButtons) {
			super(title, maxSize, autoResize, defaultButtons);
		}
	}
}
