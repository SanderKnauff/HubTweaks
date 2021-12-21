package nl.imine.api.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InventorySorter {

	protected String name;

	public InventorySorter(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void sort(List<Button> buttons) {
		List<Integer> ints = new ArrayList<>();
		buttons.forEach(button -> ints.add(button.getSlot()));
		ints.sort(new IntSort());
		buttons.sort(new Sort());
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).setSlot(ints.get(i));
		}
	}

	public int compare(Button b1, Button b2) {
		return b1.getName().compareToIgnoreCase(b2.getName());
	}

	private class IntSort implements Comparator<Integer> {
		@Override
		public int compare(Integer i1, Integer i2) {
			return i1 - i2;
		}
	}

	private class Sort implements Comparator<Button> {
		@Override
		public int compare(Button b1, Button b2) {
			return InventorySorter.this.compare(b1, b2);
		}
	}
}
