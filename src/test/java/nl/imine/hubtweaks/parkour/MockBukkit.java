package nl.imine.hubtweaks.parkour;

import org.bukkit.Bukkit;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

public class MockBukkit {

    private static MockedStatic<Bukkit> mockedBukkit;

    public static void open() {
        mockedBukkit = mockStatic(Bukkit.class);
    }

    public static MockedStatic<Bukkit> getMock() {
        return mockedBukkit;
    }

    public static void close() {
        mockedBukkit.close();
    }
}
