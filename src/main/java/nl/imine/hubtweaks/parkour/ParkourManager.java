package nl.imine.hubtweaks.parkour;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

import nl.imine.hubtweaks.HubTweaksPlugin;
import nl.imine.hubtweaks.parkour.model.ParkourLevel;
import nl.imine.hubtweaks.parkour.model.ParkourPlayer;
import nl.imine.hubtweaks.parkour.model.ParkourTiming;
import nl.imine.hubtweaks.util.ColorUtil;
import nl.imine.hubtweaks.util.LocationUtil;
import nl.imine.hubtweaks.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

public class ParkourManager implements Listener {

	public static final int LEVEL_EQUIPMENT = -1;
	public static final int LEVEL_BASE = 0;

	private final Logger logger;
	private final ParkourLevelRepository parkourLevelRepository;
	private final ParkourPlayerRepository parkourPlayerRepository;
	private final ParkourGoalRepository parkourGoalRepository;

	public ParkourManager(Logger logger, ParkourLevelRepository parkourLevelRepository, ParkourPlayerRepository parkourPlayerRepository, ParkourGoalRepository parkourGoalRepository) {
		this.logger = logger;
		this.parkourLevelRepository = parkourLevelRepository;
		this.parkourPlayerRepository = parkourPlayerRepository;
		this.parkourGoalRepository = parkourGoalRepository;
	}

	public void init(Plugin plugin) {
		parkourLevelRepository.loadAll();
		parkourPlayerRepository.loadAll();
		parkourGoalRepository.loadAll();
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		Bukkit.getServer().getPluginManager().registerEvents(new ParkourAntiCheat(parkourPlayerRepository), plugin);
	}

	@EventHandler
	public void onPressurePlateInteract(PlayerInteractEvent evt) {
		if (!evt.getAction().equals(Action.PHYSICAL) || evt.getClickedBlock() == null) {
			return;
		}

		final var oGoal = parkourGoalRepository.findOne(evt.getClickedBlock().getLocation());
		if (oGoal.isEmpty()) {
			return;
		}
		var goal = oGoal.get();

		final var player = parkourPlayerRepository.findOne(evt.getPlayer().getUniqueId()).orElseGet(() -> {
			final ParkourPlayer ret = new ParkourPlayer(evt.getPlayer().getUniqueId(), null, new HashMap<>());
			parkourPlayerRepository.addOne(ret);
			logger.info("Created ParkourPlayer for %s (%s)".formatted(evt.getPlayer().getName(), evt.getPlayer().getUniqueId()));
			return ret;
		});

		if (goal.level().level() == LEVEL_EQUIPMENT) {
			givePlayerEquipment(player, true);
			logger.info(() -> "Gave %s (%s) their equipment".formatted(evt.getPlayer().getName(), evt.getPlayer().getUniqueId()));
			return;
		}

		if (goal.level().level() == LEVEL_BASE) {
			player.restartParkour();
			evt.getPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
			logger.info(() -> "Restarting parkour for %s (%s)".formatted(evt.getPlayer().getName(), evt.getPlayer().getUniqueId()));
			return;
		}

		if (player.hasCheated()) {
			logger.info(() -> "%s (%s) hit a parkour goal, but were flagged for cheating".formatted(evt.getPlayer().getName(), evt.getPlayer().getUniqueId()));
			return;
		}
		registerSegment(player, goal.level());

		final var oHighestLevel = player.getHighestLevel();
		if (oHighestLevel.isEmpty()) {
			parkourLevelRepository.findOne((short) 0).ifPresent(player::setHighestLevel);
			logger.info(() -> "%s (%s) had ".formatted(evt.getPlayer().getName(), evt.getPlayer().getUniqueId()));
			return;
		}
		final var highestLevel = oHighestLevel.get();

		if (goal.level().equals(parkourLevelRepository.getHighestLevel().orElse(null))) {
			if (!player.hasTouchedPlated()) {
				logger.info(() -> "%s (%s) Finished the parkour without hitting any plates!".formatted(evt.getPlayer().getName(), evt.getPlayer().getUniqueId()));
				parkourLevelRepository.getAll().stream().filter(ParkourLevel::bonusLevel).min(Comparator.comparingInt(ParkourLevel::level)).ifPresent(level -> {
					player.setHighestLevel(level);
					parkourPlayerRepository.addOne(player);
					for (int i = 0; i < 10; i++) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(HubTweaksPlugin.getInstance(), () -> LocationUtil.firework(
							evt.getPlayer().getLocation(),
							FireworkEffect.builder()
								.with(FireworkEffect.Type.BALL_LARGE)
								.withColor(Color.PURPLE)
								.withFade(Color.GREEN, Color.LIME, Color.YELLOW, Color.ORANGE, Color.RED)
								.build(),
							20L), 20L * (i + 5));
					}
					Bukkit.getOnlinePlayers()
						.forEach(pl -> PlayerUtil.sendActionMessage(pl, ColorUtil.replaceColors("&d&l%s &r&5has reached the end of the parkour!", evt.getPlayer().getName())));
				});
			} else {
				Bukkit.getOnlinePlayers()
					.forEach(pl -> PlayerUtil.sendActionMessage(pl, ColorUtil.replaceColors("&c&l%s &r&5has reached the end of the parkour!", evt.getPlayer().getName())));
			}
		} else if (highestLevel.level() < goal.level().level()) {
			logger.info(() -> "%s (%s) reached parkour level %s!".formatted(evt.getPlayer().getName(), evt.getPlayer().getUniqueId(), goal.level()));
			player.setHighestLevel(goal.level());
			parkourPlayerRepository.addOne(player);
		}
		givePlayerEquipment(player, false);
		player.setTouchedPlated(true);

		logger.info(() -> "%s (%s) hit checkpoint %s at %s".formatted(evt.getPlayer().getName(), evt.getPlayer().getUniqueId(), goal.level(), goal.target().toString()));
	}

	public void registerSegment(ParkourPlayer player, ParkourLevel goal) {
		if (goal.level() <= player.getCurrentSegment()) {
			return;
		}
		Instant segmentStart = player.getSegmentStart();
		Instant segmentEnd = Instant.now();
		player.getCurrentRunTimings().put(goal, new ParkourTiming(segmentEnd, goal, segmentEnd.toEpochMilli() - segmentStart.toEpochMilli()));
		player.setSegmentStart(segmentEnd);
		player.setCurrentSegment((short) (goal.level() + 1));
		parkourPlayerRepository.addOne(player);



//		final Player player = Bukkit.getPlayer(uuid);
//		Optional<ParkourTiming> oRecordTime = timings.stream()
//				.filter(t -> t.getDestLevel().equals(timing.getDestLevel()))
//				.filter(t -> t.getStartLevel().equals(timing.getStartLevel()))
//				.sorted(
//					(ParkourTiming t1, ParkourTiming t2) -> (int) (t1.getTimeMiliseconds() - t2.getTimeMiliseconds()))
//				.findFirst();
//		ParkourLevel finalLevel = ParkourManager.getParkourInstance().getLevels().stream()
//				.filter(l -> !l.bonusLevel())
//				.sorted((ParkourLevel p1, ParkourLevel p2) -> p2.level() - p1.level()).findFirst().get();
//		long recordTime = -1;
//		boolean isEnd = (timing.getStartLevel().level() == 0 && timing.getDestLevel().equals(finalLevel));
//		if (oRecordTime.isPresent()) {
//			recordTime = oRecordTime.get().getTimeMiliseconds();
//		}
//		if (recordTime > timing.getTimeMiliseconds() || recordTime == -1) {
//			if (!isEnd) {
//				player.sendTitle("", "&7New Record: &c" + timing.getTimeMiliseconds(), 20, 60, 20);
//			} else {
//				player.sendTitle(
//					ColorUtil.replaceColors("&7New overall Record: &c%s".formatted(StringUtil.readableMiliseconds(timing.getTimeMiliseconds()))),
//					ColorUtil.replaceColors("&7Sum of best segments: &c%s".formatted(StringUtil.readableMiliseconds(getSumOfBest()))),
//					20, 60, 20
//				);
//			}
//		} else {
//			if (isEnd) {
//				player.sendTitle(
//					"",
//					ColorUtil.replaceColors("&7Final Time: &c%s".formatted(StringUtil.readableMiliseconds(timing.getTimeMiliseconds()))),
//					20, 60, 20
//				);
//			}
//		}
//		String oldTime = (recordTime == -1) ? "--:--:--" : StringUtil.readableMiliseconds(recordTime);
//		player.spigot().sendMessage(
//			ChatMessageType.ACTION_BAR,
//			TextComponent.fromLegacyText(ColorUtil.replaceColors("&7Old time: &c%s &8|| &7New Time: &c%s".formatted(oldTime, StringUtil.readableMiliseconds(timing.getTimeMiliseconds()))))
//		);
//
//
//		timings.add(timing);



	}

	private void givePlayerEquipment(ParkourPlayer player, boolean shouldGiveElytra) {
		player.getHighestLevel().filter(level -> level.level() > 0).ifPresent(level -> {
			ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
			Optional.ofNullable((LeatherArmorMeta) boots.getItemMeta()).ifPresent(meta -> {
				meta.setColor(level.reward().getColor());
				boots.setItemMeta(meta);
			});
			Optional.ofNullable(Bukkit.getPlayer(player.getUuid())).ifPresent(p -> p.getInventory().setBoots(boots));
		});

		final var hasCompletedParkour =
			player.getHighestLevel().map(ParkourLevel::level).orElse((short) 0) >=
			parkourLevelRepository.getHighestLevel().map(ParkourLevel::level).orElse((short) 0);

		if(shouldGiveElytra && hasCompletedParkour) {
			parkourLevelRepository.getAll()
				.stream()
				.max(Comparator.comparingInt(ParkourLevel::level))
				.filter(finalLevel -> player.getHighestLevel().equals(Optional.of(finalLevel)))
				.map(parkourLevel -> Bukkit.getPlayer(player.getUuid()))
				.flatMap(level -> Optional.ofNullable(Bukkit.getPlayer(player.getUuid())))
				.ifPresent(p -> p.getInventory().setChestplate(new ItemStack(Material.ELYTRA)));
		}
	}
}
