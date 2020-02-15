package ru.job4j.tracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.job4j.tracker.dao.ITracker;
import ru.job4j.tracker.dao.Item;
import ru.job4j.tracker.dao.TrackerList;
import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.input.StubInput;
import ru.job4j.tracker.input.ValidateInput;
import ru.job4j.tracker.start.StartUI;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * @author Yury Matskevich
 * @since 0.1
 */
public abstract class StartUITest {
	private final PrintStream stdout = System.out;
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();

	@Before
	public void loadOutput() {
		System.setOut(new PrintStream(this.out));
	}

	@After
	public void backOutput() {
		System.setOut(this.stdout);
	}

	/**
	 * Добовление заявки
	 */
	@Test
	public void whenUserAddItemThenTrackerHasNewItemWithSameName() {
		ITracker tracker = getTracker();
		Input input =
				new ValidateInput(
						new StubInput(
								new String[]{
										"0",
										"test name",
										"desc",
										"6"
								}
						)
				);
		new StartUI(input, tracker).init();
		assertThat(tracker.findAll().get(0).getName(), is("test name"));
	}

	/**
	 * Редактирование заявки
	 */
	@Test
	public void whenUpdateThenTrackerHasUpdatedValue() {
		ITracker tracker = getTracker();
		Item item = tracker.add(new Item("test name", "desc", 123L));
		Input input =
				new ValidateInput(
						new StubInput(
								new String[]{
										"2",
										item.getId(),
										"test name1",
										"desc",
										"6"
								}
						)
				);
		new StartUI(input, tracker).init();
		assertThat(tracker.findById(item.getId()).getName(), is("test name1"));
	}

	/**
	 * Удаление заявки
	 */
	@Test
	public void whenDeleteThenTrackerHasNotDeletedItem() {
		ITracker tracker = getTracker();
		Input input =
				new ValidateInput(
						new StubInput(
								new String[]{
										"0",
										"test name1",
										"desc1",
										"0",
										"test name2",
										"desc2",
										"6"
								}
						)
				);
		new StartUI(input, tracker).init();
		Item item = tracker.findAll().get(0);
		tracker.delete(item.getId());
		assertThat(tracker.findAll().get(0).getName(), is("test name2"));
	}

	/**
	 * Просмотр заявок в трекере
	 */
	@Test
	public void whenShowAllTheItems() {
		ITracker tracker = getTracker();
		Input input =
				new ValidateInput(
						new StubInput(
								new String[]{
										"0",
										"test name1",
										"desc1",
										"0",
										"test name2",
										"desc2",
										"1",
										"6"
								}
						)
				);
		new StartUI(input, tracker).init();
		tracker.findAll();
		String string = new String(out.toByteArray());
		String subString1 = String.format("Имя заявки: test name1%nОписание заявки: desc1%n");
		String subString2 = String.format("Имя заявки: test name2%nОписание заявки: desc2%n");
		assertFalse(string.lastIndexOf(subString1) < 0);
		assertFalse(string.lastIndexOf(subString2) < 0);
	}

	/**
	 * Поиск завявки по Id
	 */
	@Test
	public void whenFindItemById() {
		ITracker tracker = getTracker();
		Item item = tracker.add(new Item("test name", "desc", 123L));
		Input input =
				new ValidateInput(
						new StubInput(
								new String[]{
										"4",
										item.getId(),
										"6"
								}
						)
				);
		new StartUI(input, tracker).init();
		String string = new String(out.toByteArray());
		String subString = String.format(
				"Id заявки: %s%nИмя заявки: test name%nОписание заявки: desc%n",
				item.getId()
		);
		assertFalse(string.lastIndexOf(subString) < 0);
	}

	/**
	 * Поиск завявки по имени
	 */
	@Test
	public void whenFindItemByName() {
		ITracker tracker = getTracker();
		Item item = tracker.add(new Item("test name", "desc", 123L));
		Input input =
				new ValidateInput(
						new StubInput(
								new String[]{
										"5",
										item.getName(),
										"6"
								}
						)
				);
		new StartUI(input, tracker).init();
		String string = new String(out.toByteArray());
		String subString = String.format(
				"Id заявки: %s%nИмя заявки: %s%nОписание заявки: desc%n",
				item.getId(),
				item.getName()
		);
		assertFalse(string.lastIndexOf(subString) < 0);
	}

	protected abstract ITracker getTracker();
}
