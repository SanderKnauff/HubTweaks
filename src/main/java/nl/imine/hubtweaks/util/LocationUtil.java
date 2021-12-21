package nl.imine.hubtweaks.util;

import nl.imine.hubtweaks.HubTweaksPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class LocationUtil {

    public static void firework(org.bukkit.Location loc, org.bukkit.FireworkEffect effect, long delayTillDetonate) {
        org.bukkit.entity.Firework firework = (org.bukkit.entity.Firework) loc.getWorld().spawnEntity(loc,
            org.bukkit.entity.EntityType.FIREWORK);
        org.bukkit.inventory.meta.FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(effect);
        firework.setFireworkMeta(fireworkMeta);
        org.bukkit.Bukkit.getScheduler().runTaskLater(HubTweaksPlugin.getInstance(), firework::detonate, delayTillDetonate);
    }

    public static Coordinate getDirectionFromYaw(float yaw) {
        double x = 0, z = 0;
        if (yaw >= -180 && yaw < -157.5) {
            z = -1;
        } else if (yaw >= -157.5 && yaw < -112.5) {
            x = 0.5;
            z = -0.5;
        } else if (yaw >= -112.5 && yaw < -67.5) {
            x = 1;
        } else if (yaw >= -67.5 && yaw < -22.5) {
            x = 0.5;
            z = 0.5;
        } else if (yaw >= -22.5 && yaw < 22.5) {
            z = 1;
        } else if (yaw >= 22.5 && yaw < 67.5) {
            x = -0.5;
            z = 0.5;
        } else if (yaw >= 67.5 && yaw < 112.5) {
            x = -1;
        } else if (yaw >= 112.5 && yaw < 157.5) {
            x = -0.5;
            z = -0.5;
        } else if (yaw >= 157.5 && yaw < 180) {
            z = -1;
        }
        return new Coordinate(x, z);
    }

    /**
     * Calculates the distance between two points.<br>
     * This funtion calculates the squared distance by using the Pythagorean
     * Theorem.<br>
     * If you use this function to measure distance it is adviced to square
     * distance you want to compare it to as the usage of a root function as
     * this is more efficient.
     *
     * @param origin
     *            the origin you want to measeure from.
     * @param target
     *            the place you want to measure the distance to.
     * @return a double containing the squared distance between <b>origin </b>
     *         and <b>target</b>
     */
    public static double getFlatDistance(Coordinate origin, Coordinate target) {
        return Math.abs(Math.abs(Math.pow(origin.getX() - target.getX(), 2))
                        + Math.abs(Math.pow(origin.getZ() - target.getZ(), 2)));
    }

    /**
     * Calculates the distance between two points.<br>
     * This funtion calculates the squared distance by using the Pythagorean
     * Theorem.<br>
     * If you use this function to measure distance it is adviced to square
     * distance you want to compare it to as the usage of a root function as
     * this is more efficient.
     *
     * @param origin
     *            the origin you want to measeure from.
     * @param target
     *            the place you want to measure the distance to.
     * @return a double containing the squared distance between <b>origin </b>
     *         and <b>target</b>
     */
    public static double getFlatDistance(org.bukkit.Location origin, org.bukkit.Location target) {
        return getFlatDistance(new Coordinate(origin.getX(), origin.getZ()),
            new Coordinate(target.getX(), target.getZ()));
    }

    public static org.bukkit.Location safeAddLocations(org.bukkit.Location origin, org.bukkit.Location toAdd) {
        return origin.clone().add(toAdd.getX(), toAdd.getY(), toAdd.getZ());
    }

    public static Coordinate getNearestFlat(Coordinate target, Collection<Coordinate> toChooseFrom) {
        Coordinate ret = null;
        double flatDistance = Double.MAX_VALUE;
        for (Coordinate location : toChooseFrom) {
            double testDistance = getFlatDistance(location, target);
            if (flatDistance > testDistance) {
                ret = location;
                flatDistance = testDistance;
            }
        }
        return ret;
    }

    /**
     * Checks if a certain location is inside a rectangular box.<br>
     * This check is inclusive, so if the target's location os the same as one
     * of the corners this will still return <i>true</i>.
     *
     * @param target
     *            the location you want to check.
     * @param corner1
     *            the first corner of the rectanglar box.
     * @param corner2
     *            the second corner of the rectangular box.
     * @return a boolean which indicates if the location is inside the box.
     */
    public static boolean isInBox(org.bukkit.Location target, org.bukkit.Location corner1, org.bukkit.Location corner2) {
        int xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        if (xMin <= target.getBlockX() && target.getBlockX() <= xMax) {
            if (yMin <= target.getBlockY() && target.getBlockY() <= yMax) {
                if (zMin <= target.getBlockZ() && target.getBlockZ() <= zMax) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a list of Blocks inside a rectangular area.<br>
     * This scan is inclusive, so any blocks at the border of a corner will also
     * be included in the returned list.
     *
     * @param corner1
     *            the first corner of the rectanglar box.
     * @param corner2
     *            the second corner of the rectangular box.
     * @return an List of Type block containing all the blocks inside the box.
     */
    public static List<org.bukkit.block.Block> getBlocksInBox(org.bukkit.Location corner1, org.bukkit.Location corner2) {
        List<org.bukkit.block.Block> blocks = new ArrayList<>();
        int xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    blocks.add(corner1.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    /**
     * Returns a list of Blocks on the border of a rectangular area.<br>
     *
     * @param corner1
     *            the first corner of the rectanglar box.
     * @param corner2
     *            the second corner of the rectangular box.
     * @return an List of Type block containing all the blocks on the borders
     *         the box.
     */
    public static List<org.bukkit.block.Block> getBlocksInOutline(org.bukkit.Location corner1, org.bukkit.Location corner2) {
        List<org.bukkit.block.Block> blocks = new ArrayList<>();
        int xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    if ((x == xMin || x == xMax) || (y == zMin || y == yMax) || (z == zMin || z == zMax)) {
                        blocks.add(corner1.getWorld().getBlockAt(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    /**
     * Calculates the coordinates on the edge of a circle.<br>
     * The coordinates returned depend on the offset and the amount of
     * coordinates requested and will be spread evenly over the edge of the
     * circle.<br>
     * This method uses a {@link Math#sin(double)} and {@link Math#cos(double)}
     * funtion so it is not recommended to use too fast after one another.
     *
     * @param originX
     *            the x coordinate of the center of the circle.
     * @param originY
     *            the y coordinate of the center of the circle.
     * @param radius
     *            the distance of the edge from the circle to the origin of the
     *            circle.
     * @param offset
     *            the amount of degrees the returned points will be offset at.
     *            this rotates the location of the points on the edge of the
     *            circle.
     * @param amount
     *            the amount of coordinates returned.
     * @return a list of coordinates spread evenly around the edge of the
     *         circle.
     */
    public static List<Coordinate> getCoordinatesInCircle(double originX, double originY, double radius, double offset, int amount) {
        List<Coordinate> points = new ArrayList<>();
        for (double i = 0; i < amount; i++) {
            double x = originX + radius * Math.cos(Math.toRadians(((360D / amount) * i) + offset));
            double y = originY + radius * Math.sin(Math.toRadians(((360D / amount) * i) + offset));
            points.add(new Coordinate(x, y));
        }
        return points;
    }

    /**
     * Calculates the coordinates on the edge of a circle.<br>
     * The coordinates returned depend on the offset and the amount of
     * coordinates requested and will be spread evenly over the edge of the
     * circle.<br>
     * This method uses a {@link Math#sin(double)} and {@link Math#cos(double)}
     * funtion so it is not recommended to use too fast after one another.
     *
     * @param origin
     *            the coordinates of the center of the circle.
     * @param radius
     *            the distance of the edge from the circle to the origin of the
     *            circle.
     * @param offset
     *            the amount of degrees the returned points will be offset at.
     *            this rotates the location of the points on the edge of the
     *            circle.
     * @param amount
     *            the amount of coordinates returned.
     * @return a list of coordinates spread evenly around the edge of the
     *         circle.
     */
    public static List<Coordinate> getCoordinatesInCircle(Coordinate origin, double radius, double offset, int amount) {
        return getCoordinatesInCircle(origin.getX(), origin.getZ(), radius, offset, amount);
    }

    /**
     * Calculates the coordinates on the edge of a circle.<br>
     * The coordinates returned will be spread evenly over the edge of the
     * circle.<br>
     * This method uses a {@link Math#sin(double)} and {@link Math#cos(double)}
     * funtion so it is not recommended to use too fast after one another.
     *
     * @param originX
     *            the x coordinate of the center of the circle.
     * @param originY
     *            the y coordinate of the center of the circle.
     * @param radius
     *            the distance of the edge from the circle to the origin of the
     *            circle.
     * @param amount
     *            the amount of coordinates returned.
     * @return a list of coordinates spread evenly around the edge of the
     *         circle.
     */
    public static List<Coordinate> getCoordinatesInCircle(double originX, double originY, double radius, int amount) {
        return getCoordinatesInCircle(originX, originY, radius, 0, amount);
    }

    /**
     * Calculates the coordinates on the edge of a circle.<br>
     * The coordinates returned will be spread evenly over the edge of the
     * circle.<br>
     * This method uses a {@link Math#sin(double)} and {@link Math#cos(double)}
     * funtion so it is not recommended to use too fast after one another.
     *
     * @param origin
     *            the coordinates of the center of the circle.
     * @param radius
     *            the distance of the edge from the circle to the origin of the
     *            circle.
     * @param amount
     *            the amount of coordinates returned.
     * @return a list of coordinates spread evenly around the edge of the
     *         circle.
     */
    public static List<Coordinate> getCoordinatesInCircle(Coordinate origin, double radius, int amount) {
        return getCoordinatesInCircle(origin.getX(), origin.getZ(), radius, amount);
    }

    /**
     * Calculates the coordinates on the edge of an arch.<br>
     * The coordinates returned will be spread evenly over the edge of the arch.
     * <br>
     * This arech will be calculated as part of a circle with minimum and
     * maximum degree.<br>
     * This method uses a {@link Math#sin(double)} and {@link Math#cos(double)}
     * funtion so it is not recommended to use too fast after one another.
     *
     * @param originX
     *            the x coordinate of the center of the circle on which the arch
     *            will be based.
     * @param originY
     *            the y coordinate of the center of the circle on which the arch
     *            will be based.
     * @param radius
     *            the distance of the edge from the circle to the origin of the
     *            circle.
     * @param amount
     *            the amount of coordinates returned.
     * @param minDegree
     *            the starting degree on the circle to start the arch on.
     * @param maxDegree
     *            the ending degree on the circle to end the arch on.
     * @return a list of coordinates spread evenly around the edge of the
     *         circle.
     */
    public static List<Coordinate> getCoordinatesInArch(double originX, double originY, double radius, double amount, double minDegree, double maxDegree) {
        List<Coordinate> points = new ArrayList<>();
        for (double i = 0; i < amount; i++) {
            double x = originX + radius * Math.cos((((maxDegree - minDegree) / amount + 1D) * i) + minDegree);
            double y = originY + radius * Math.sin((((maxDegree - minDegree) / amount + 1D) * i) + minDegree);
            points.add(new Coordinate(x, y));
        }
        return points;
    }

    /**
     * Calculates the coordinates on the edge of an arch.<br>
     * The coordinates returned will be spread evenly over the edge of the arch.
     * <br>
     * This arech will be calculated as part of a circle with minimum and
     * maximum degree.<br>
     * This method uses a {@link Math#sin(double)} and {@link Math#cos(double)}
     * funtion so it is not recommended to use too fast after one another.
     *
     * @param origin
     *            the coordinates of the center of the circle on which the arch
     *            will be based.
     * @param radius
     *            the distance of the edge from the circle to the origin of the
     *            circle.
     * @param amount
     *            the amount of coordinates returned.
     * @param minDegree
     *            the starting degree on the circle to start the arch on.
     * @param maxDegree
     *            the ending degree on the circle to end the arch on.
     * @return a list of coordinates spread evenly around the edge of the
     *         circle.
     */
    public static List<Coordinate> getCoordinatesInArch(Coordinate origin, double radius, double amount, double minDegree, double maxDegree) {
        return getCoordinatesInArch(origin.getX(), origin.getZ(), radius, amount, minDegree, maxDegree);
    }

    /**
     * Calculates the coordinates on a line.<br>
     * The coordinates will be spread out with even distance to eachother over
     * the line.
     *
     * @param originx
     *            the x coordinate of the origin you want to get the blocks
     *            from.
     * @param originy
     *            the y coordinate of the origin you want to get the blocks
     *            from.
     * @param targetx
     *            the x coordinate of the target you want to get the blocks to.
     * @param targety
     *            the y coordiante of the target you want to get the blocks to.
     * @param amount
     *            the amount of coordinates calculated on the line.
     * @return a list of coordinates on the line.
     */
    public static List<Coordinate> getCoordinatesInLine(double originx, double originy, double targetx, double targety, int amount) {
        List<Coordinate> points = new ArrayList<>();
        double slope = ((targety - originy) / (targetx - originx));
        for (int i = 0; i < amount; i++) {
            double distance = ((targetx - originx) / (amount - 1) * i);
            if (targetx != originx) {
                points.add(new Coordinate((originx + distance), (originy + (slope * distance))));
            }
        }
        return points;
    }

    /**
     * Calculates the coordinates on a line.<br>
     * The coordinates will be spread out with even distance to eachother over
     * the line.
     *
     * @param origin
     *            the coordinates of the origin you want to get the blocks from.
     * @param target
     *            the coordinates of the target you want to get the blocks to.
     * @param amount
     *            the amount of coordinates calculated on the line.
     * @return a list of coordinates on the line.
     */
    public static List<Coordinate> getCoordinatesInLine(Coordinate origin, Coordinate target, int amount) {
        return getCoordinatesInLine(origin.getX(), origin.getZ(), target.getX(), target.getZ(), amount);
    }

    /**
     * Calculates the coordinates in a cilinder.<br>
     *
     * @param origin
     *            the coordinates of the origin you want to get a cylinder from.
     * @param radius
     *            the radius of the cylinder you want to create.
     * @return a list of coordinates in the cylinder.
     */
    public static List<Coordinate> getBlocksInCylinder(Coordinate origin, double radius) {
        List<Coordinate> points = new ArrayList<>();
        for (int x = (int) (origin.getX() - radius); x <= (int) (origin.getX() + radius); x++) {
            for (int z = (int) (origin.getZ() - radius); z <= (int) (origin.getZ() + radius); z++) {
                if ((origin.getX() - x) * (origin.getX() - x)
                    + (origin.getZ() - z) * (origin.getZ() - z) <= (radius * radius)) {
                    points.add(new Coordinate(x, z));
                }
            }
        }
        return points;
    }

    public static class Position {

        protected UUID w;
        protected double x, y, z;

        public Position(org.bukkit.Location loc) {
            w = loc.getWorld().getUID();
            x = loc.getX();
            y = loc.getY();
            z = loc.getZ();
        }

        public Position(UUID uuid, double x, double y, double z) {
            this.w = uuid;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public UUID getWorld() {
            return w;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public void setWorld(org.bukkit.World w) {
            this.w = w.getUID();
        }

        public void setWorld(UUID w) {
            this.w = w;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void setZ(double z) {
            this.z = z;
        }

        public org.bukkit.Location toLocation() {
            return new org.bukkit.Location(org.bukkit.Bukkit.getWorld(w), x, y, z);
        }

        @Override
        public String toString() {
            return String.format("{x: %f, y: %f, z: %f}", getX(), getY(), getZ());
        }
    }

    public static class Coordinate {

        private double x, z;

        /**
         * Creates a new Coordinate.<br>
         * The intended use of this class is an lightway wrapper of keeping two
         * doubles together. <br>
         * Because Mojang it's a x-z coordinate.
         *
         * @param x
         *            the X value of the coordinate you want to store.
         * @param z
         *            the Z value of the coordinate you want to store.
         */
        public Coordinate(double x, double z) {
            this.x = x;
            this.z = z;
        }

        public Coordinate(org.bukkit.Location loc) {
            this.x = loc.getX();
            this.z = loc.getZ();
        }

        public org.bukkit.Location toLocation(org.bukkit.World w, double y) {
            return new org.bukkit.Location(w, x, y, z);
        }

        public double getX() {
            return this.x;
        }

        public double getZ() {
            return this.z;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setZ(double z) {
            this.z = z;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Coordinate) {
                Coordinate other = (Coordinate) obj;
                return this.x == other.x && this.z == other.z;
            }
            return super.equals(obj);
        }

        @Override
        public String toString() {
            return x + ";" + z;
        }
    }
}

