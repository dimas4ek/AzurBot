package org.dimas4ek.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.*;

@Getter
public class Chapter {
    private String id;
    private Map<String, String> names;

    @SerializedName("1")
    private ChapterMap map1;
    @SerializedName("2")
    private ChapterMap map2;
    @SerializedName("3")
    private ChapterMap map3;
    @SerializedName("4")
    private ChapterMap map4;

    @Getter
    public class ChapterMap {
        private Map<String, String> names;
        private Difficulty normal;
        private Difficulty hard;

        @Getter
        public class Difficulty {
            private String title;
            private String code;
            private String introduction;
            private UnlockRequirements unlockRequirements;
            private List<ThreeStarReward> threeStarRewards;
            private EnemyLevel enemyLevel;
            private BaseXP baseXP;
            private int requiredBattles;
            private int bossKillsToClear;
            private List<String> starConditions;
            private AirSupremacy airSupremacy;
            private List<String> mapDrops;
            private List<EquipmentBlueprintDrop> equipmentBlueprintDrops;
            private Object shipDrops;
            private NodeMap nodeMap;

            @Getter
            public class UnlockRequirements {
                private String text;
                private int requiredLevel;
            }

            @Getter
            public class ThreeStarReward {
                private String item;
                private Integer count;
            }

            @Getter
            public class EnemyLevel {
                private int mobLevel;
                private int bossLevel;
                private Object boss;
            }

            @Getter
            public class BaseXP {
                private int smallFleet;
                private int mediumFleet;
                private int largeFleet;
                private int bossFleet;
            }

            @Getter
            public class AirSupremacy {
                private int actual;
                private int superiority;
                private int supremacy;
            }

            @Getter
            public class EquipmentBlueprintDrop {
                private String name;
                private String tier;
            }

            @Getter
            public class NodeMap {
                private int width;
                private int height;
                private List<List<String>> map;
                private List<Node> nodes;

                @Getter
                public class Node {
                    private int x;
                    private int y;
                    private String node;
                }
            }

            //ONLY NORMAL
            private ClearRewards clearRewards;

            @Getter
            public class ClearRewards {
                private int cube;
                private int coin;
                private int oil;
                private String ship;
            }

            //ONLY HARD
            private FleetRestrictions fleetRestrictions;
            private StatRestrictions statRestrictions;

            @Getter
            public class FleetRestrictions {
                private Fleet fleet1;
                private Fleet fleet2;

                @Getter
                public class Fleet {
                    private int destroyer;
                    private int lightCruiser;
                    private int heavyCruiser;
                    private int battlecruiser;
                    private int battleship;
                    private int aircraftCarrier;
                    private int lightAircraftCarrier;

                    public List<String> getAll() {
                        List<String> restrictions = new ArrayList<>();

                        if (destroyer > 0) restrictions.add("destroyer");
                        if (lightCruiser > 0) restrictions.add("lightCruiser");
                        if (heavyCruiser > 0) restrictions.add("heavyCruiser");
                        if (battlecruiser > 0) restrictions.add("battlecruiser");
                        if (battleship > 0) restrictions.add("battleship");
                        if (aircraftCarrier > 0) restrictions.add("aircraftCarrier");
                        if (lightAircraftCarrier > 0) restrictions.add("lightAircraftCarrier");

                        return restrictions;
                    }

                    public int get(String str) {
                        return switch (str) {
                            case "destroyer" -> destroyer;
                            case "lightCruiser" -> lightCruiser;
                            case "heavyCruiser" -> heavyCruiser;
                            case "battlecruiser" -> battlecruiser;
                            case "battleship" -> battleship;
                            case "aircraftCarrier" -> aircraftCarrier;
                            case "lightAircraftCarrier" -> lightAircraftCarrier;
                            default -> throw new IllegalArgumentException("Invalid field name: " + str);
                        };
                    }
                }
            }

            @Getter
            public class StatRestrictions {
                private int averageLevel;
                private int torpedo;
                private int evasion;
                private int firepower;
                @SerializedName("anti-Air")
                private int antiAir;
                private int aviation;

                public Map<String, Integer> getAll() {
                    Map<String, Integer> restrictions = new HashMap<>();

                    if (averageLevel > 0) restrictions.put("Average Level > ", averageLevel);
                    if (torpedo > 0) restrictions.put("Total Torpedo > ", torpedo);
                    if (evasion > 0) restrictions.put("Total Evasion > ", evasion);
                    if (firepower > 0) restrictions.put("Total Firepower > ", firepower);
                    if (antiAir > 0) restrictions.put("Total Anti-Air stat > ", antiAir);
                    if (aviation > 0) restrictions.put("Total Aviation stat > ", aviation);

                    return restrictions;
                }
            }
        }


        /*private Normal normal;
        private Hard hard;*/

        /*@Getter
        public class Normal extends Difficulty {
            private ClearRewards clearRewards;

            @Getter
            public class ClearRewards {
                private int cube;
                private int coin;
                private int oil;
                private String ship;
            }
        }

        @Getter
        public class Hard extends Difficulty {
            private FleetRestrictions fleetRestrictions;
            private StatRestrictions statRestrictions;

            @Getter
            public class FleetRestrictions {
                private Fleet fleet1;
                private Fleet fleet2;

                @Getter
                public class Fleet {
                    private int destroyer;
                    private int lightCruiser;
                    private int heavyCruiser;
                    private int battlecruiser;
                    private int battleship;
                    private int aircraftCarrier;
                    private int lightAircraftCarrier;

                    public List<String> getAll() {
                        List<String> restrictions = new ArrayList<>();

                        if (destroyer > 0) restrictions.add("destroyer");
                        if (lightCruiser > 0) restrictions.add("lightCruiser");
                        if (heavyCruiser > 0) restrictions.add("heavyCruiser");
                        if (battlecruiser > 0) restrictions.add("battlecruiser");
                        if (battleship > 0) restrictions.add("battleship");
                        if (aircraftCarrier > 0) restrictions.add("aircraftCarrier");
                        if (lightAircraftCarrier > 0) restrictions.add("lightAircraftCarrier");

                        return restrictions;
                    }

                    public int get(String str) {
                        return switch (str) {
                            case "destroyer" -> destroyer;
                            case "lightCruiser" -> lightCruiser;
                            case "heavyCruiser" -> heavyCruiser;
                            case "battlecruiser" -> battlecruiser;
                            case "battleship" -> battleship;
                            case "aircraftCarrier" -> aircraftCarrier;
                            case "lightAircraftCarrier" -> lightAircraftCarrier;
                            default -> throw new IllegalArgumentException("Invalid field name: " + str);
                        };
                    }
                }
            }

            @Getter
            public class StatRestrictions {
                private int averageLevel;
                private int torpedo;
                private int evasion;
                private int firepower;
                @SerializedName("anti-Air")
                private int antiAir;
                private int aviation;

                public Map<String, Integer> getAll() {
                    Map<String, Integer> restrictions = new HashMap<>();

                    if (averageLevel > 0) restrictions.put("Average Level > ", averageLevel);
                    if (torpedo > 0) restrictions.put("Total Torpedo > ", torpedo);
                    if (evasion > 0) restrictions.put("Total Evasion > ", evasion);
                    if (firepower > 0) restrictions.put("Total Firepower > ", firepower);
                    if (antiAir > 0) restrictions.put("Total Anti-Air stat > ", antiAir);
                    if (aviation > 0) restrictions.put("Total Aviation stat > ", aviation);

                    return restrictions;
                }
            }
        }

        @Getter
        public class Difficulty {
            private String title;
            private String code;
            private String introduction;
            private UnlockRequirements unlockRequirements;
            private List<ThreeStarReward> threeStarRewards;
            private EnemyLevel enemyLevel;
            private BaseXP baseXP;
            private int requiredBattles;
            private int bossKillsToClear;
            private List<String> starConditions;
            private AirSupremacy airSupremacy;
            private List<String> mapDrops;
            private List<EquipmentBlueprintDrop> equipmentBlueprintDrops;
            private Object shipDrops;
            private NodeMap nodeMap;

            @Getter
            public class UnlockRequirements {
                private String text;
                private int requiredLevel;
            }

            @Getter
            public class ThreeStarReward {
                private String item;
                private Integer count;
            }

            @Getter
            public class EnemyLevel {
                private int mobLevel;
                private int bossLevel;
                private Object boss;
            }

            @Getter
            public class BaseXP {
                private int smallFleet;
                private int mediumFleet;
                private int largeFleet;
                private int bossFleet;
            }

            @Getter
            public class AirSupremacy {
                private int actual;
                private int superiority;
                private int supremacy;
            }

            @Getter
            public class EquipmentBlueprintDrop {
                private String name;
                private String tier;
            }

            @Getter
            public class NodeMap {
                private int width;
                private int height;
                private List<List<String>> map;
                private List<Node> nodes;

                @Getter
                public class Node {
                    private int x;
                    private int y;
                    private String node;
                }
            }
        }*/

        public String getName() {
            return names.get("en");
        }
    }

    public String getName() {
        return names.get("en");
    }

    public List<ChapterMap> getChapterMaps() {
        return List.of(map1, map2, map3, map4);
    }
}
