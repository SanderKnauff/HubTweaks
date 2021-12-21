package nl.imine.api.gui;

import java.util.ArrayList;
import java.util.List;

import nl.imine.hubtweaks.HubTweaksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import nl.imine.api.event.ContainerBuildInventoryEvent;
import nl.imine.api.event.ContainerConstructEvent;
import nl.imine.api.event.ContainerUpdateEvent;
import nl.imine.api.gui.button.BrowseDirection;
import nl.imine.api.gui.button.ButtonChangePage;

public abstract class Container {

	private final List<Inventory> openInvs = new ArrayList<>();

	private final List<Button> buttons = new ArrayList<>();
	private final List<Button> staticButtons = new ArrayList<>();

	private final String title;

	private InventorySorter sort;
	private boolean autoResize;
	private int maxScreenSize;
	private int openPage = 0;
	private int scheduler = -1;

	protected Container(String title) {
		this(title, 9);
	}

	protected Container(String title, int maxSize) {
		this(title, maxSize, true);
	}

	protected Container(String title, int maxSize, boolean autoResize) {
		this(title, maxSize, autoResize, true);
	}

	protected Container(String title, int maxSize, boolean autoResize, boolean defaultButtons) {
		this.title = title;
		this.maxScreenSize = getInventorySize(maxSize);
		this.autoResize = autoResize;
		if (defaultButtons) {
			staticButtons.add(getDefaultPreviousButton(this));
			staticButtons.add(getDefaultNextButton(this));
		}
		Bukkit.getPluginManager().callEvent(new ContainerConstructEvent(this));
	}

	public void open(Player player, int page) {
		openPage = page;

		Inventory inv;
		if (buttons.size() < maxScreenSize && staticButtons.isEmpty()) {
			inv = Bukkit.createInventory(null, maxScreenSize, title);
		} else {
			inv = Bukkit.createInventory(null, maxScreenSize + 9, title);
		}
		openInvs.add(inv);
		buildInventory(inv);

		player.openInventory(inv);
	}

	public void open(Player player) {
		open(player, 0);
	}

	public void changeToPage(int page) {
		openPage = page;
		buildInventory();
	}

	public void refresh() {
		Bukkit.getPluginManager().callEvent(new ContainerUpdateEvent(this));
		buildInventory();
	}

	public void setRefreshRate(long refreshRate) {
		if (scheduler > -1) {
			Bukkit.getScheduler().cancelTask(scheduler);
		}
		if (refreshRate > -1) {
			scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(HubTweaksPlugin.getInstance(), () -> {
				refresh();
			} , 0L, refreshRate);
		}
	}

	private void buildInventory() {
		openInvs.forEach(inv -> buildInventory(inv));
	}

	private void buildInventory(Inventory inv) {
		inv.clear();
		GuiManager.getInstance().ensureContainer(this);
		ContainerBuildInventoryEvent event = new ContainerBuildInventoryEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		buttons.forEach(b -> b.onRefresh(event));
		staticButtons.forEach(b -> b.onRefresh(event));
		if (sort != null) {
			sort.sort(buttons);
		}
		for (Button button : new ArrayList<>(buttons)) {
			if (button.getSlot() >= (openPage * maxScreenSize) && button.getSlot() < ((openPage + 1) * maxScreenSize)) {
				inv.setItem(button.getSlot() - (openPage * maxScreenSize), button.getItemStack());
			}
		}
		for (Button staticButton : new ArrayList<>(staticButtons)) {
			inv.setItem(getLastRowSlot(Math.abs(staticButton.getSlot() % 9)), staticButton.getItemStack());
		}
	}

	public void close() {
		new ArrayList<>(openInvs).forEach(inv -> {
			new ArrayList<>(inv.getViewers()).forEach(viewer -> {
				viewer.closeInventory();
			});
		});
	}

	public boolean hasButton(Button b) {
		boolean ret = false;
		for (Button button : buttons) {
			if (button.equals(b)) {
				ret = true;
				break;
			}
		}
		for (Button staticButton : staticButtons) {
			if (staticButton.equals(b)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	public Button getButton(int slot, int page) {
		Button ret = null;
		if (slot >= maxScreenSize) {
			for (Button staticButton : staticButtons) {
				if (staticButton.getSlot() == (slot - maxScreenSize)) {
					ret = staticButton;
					break;
				}
			}
		}
		if (ret != null) {
			return ret;
		}
		int id = slot + (maxScreenSize * page);
		for (Button b : buttons) {
			if (b.getSlot() == id) {
				ret = b;
				break;
			}
		}
		return ret;
	}

	public Button getStaticButton(int slot) {
		return staticButtons.get(slot);
	}

	public List<Inventory> getOpenInventories() {
		return new ArrayList<>(openInvs);
	}

	public void removeInventory(Inventory inv) {
		openInvs.remove(inv);
	}

	public void addButton(Button button) {
		buttons.add(button);
		buildInventory();
	}

	public void addStaticButton(Button button) {
		staticButtons.add(button);
		buildInventory();
	}

	public void clearButtons() {
		buttons.clear();
	}

	public List<Button> getButtons() {
		return new ArrayList<>(buttons);
	}

	public List<Button> getStaticButtons() {
		return new ArrayList<>(staticButtons);
	}

	public String getTitle() {
		return title;
	}

	public int getFreeSlot(int since) {
		ArrayList<Integer> nr = new ArrayList<>();
		for (int i = since; i <= buttons.size(); i++) {
			nr.add(i);
		}
		int maxSize = nr.size();
		for (Button b : this.getButtons()) {
			if (b.getSlot() < maxSize) {
				nr.remove((Integer) b.getSlot());
			}
		}
		if (nr.isEmpty()) {
			return 0;
		}
		return (nr.get(0) == null ? 0 : nr.get(0));
	}

	public int getFreeSlot() {
		return getFreeSlot(0);
	}

	public void setMaxScreenSize(int maxScreenSize) {
		this.maxScreenSize = getInventorySize(maxScreenSize);
	}

	public int getMaxScreenSize() {
		return this.maxScreenSize;
	}

	public InventorySorter getSorter() {
		return sort;
	}

	public void setSorter(InventorySorter sort) {
		this.sort = sort;
	}

	public int getPageAmount() {
		int max = 0;
		for (Button button : buttons) {
			max = Math.max(button.getSlot(), max);
		}
		return max / maxScreenSize;
	}

	public int getPageSlot(int slot, int page) {
		return slot + (page * this.getMaxScreenSize());
	}

	public int getLastRowSlot(int slot) {
		return (maxScreenSize) + slot;
	}

	public int getOpenPage() {
		return openPage;
	}

	public List<Player> getVieuwers() {
		List<Player> ret = new ArrayList<>();
		getOpenInventories().forEach(inv -> {
			inv.getViewers().forEach(pl -> ret.add((Player) pl));
		});
		return ret;
	}

	public static int getInventorySize(int maxSize) {
		return ((double) maxSize < 45) ? (((maxSize - 1) / 9) + 1) * 9 : 45;
	}

	public static Button getDefaultPreviousButton(Container c) {
		return new ButtonChangePage(c, 3, BrowseDirection.PREVIOUS);
	}

	public static Button getDefaultNextButton(Container c) {
		return new ButtonChangePage(c, 5, BrowseDirection.NEXT);
	}

	@Override
	public String toString() {
		return "Container:{Title:" + title + ",MaxSize:" + maxScreenSize + ",AutoResize:" + autoResize + "}";
	}
}
