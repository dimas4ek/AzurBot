package org.dimas4ek.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.*;

@Getter
public class Equip {
    private String wikiUrl;
    private String category;
    private Map<String, String> names;
    private Type type;
    private String nationality;
    @SerializedName("image")
    private String imageUrl;
    private Fits fits;
    private Misc misc;
    private List<Tier> tiers;
    
    @Getter
    public class Type {
        private String focus;
        private String name;
    }
    
    @Getter
    public class Fits {
        private String destroyer;
        private String lightCruiser;
        private String heavyCruiser;
        private String monitor;
        private String largeCruiser;
        private String battleship;
        private String battlecruiser;
        private String aviationBattleship;
        private String aircraftCarrier;
        private String lightCarrier;
        private String repairShip;
        private String submarine;
        private String submarineCarrier;
        
        public Map<String, String> getAll() {
            Map<String, String> map = new HashMap<>();
            
            map.put(destroyer, "Destroyer");
            map.put(lightCruiser, "Light Cruiser");
            map.put(heavyCruiser, "Heavy Cruiser");
            map.put(monitor, "Monitor");
            map.put(largeCruiser, "Large Cruiser");
            map.put(battleship, "Battleship");
            map.put(battlecruiser, "Battlecruiser");
            map.put(aviationBattleship, "Aviation Battleship");
            map.put(aircraftCarrier, "Aircraft Carrier");
            map.put(lightCarrier, "Light Carrier");
            map.put(repairShip, "Repair Ship");
            map.put(submarine, "Submarine");
            map.put(submarineCarrier, "Submarine Carrier");
            
            return map;
        }
    }
    
    @Getter
    public class Misc {
        private String blueprints;
        private Set<String> madeFrom;
        private Set<String> usedFor;
        private String obtainedFrom;
        private String notes;
        @SerializedName("animation")
        private String animationUrl;
    }
    
    @Getter
    public class Tier {
        private int tier;
        private String rarity;
        private Stars stars;
        private Stats stats;
        
        @Getter
        public class Stars {
            private String stars;
            private int value;
        }
        
        @Getter
        public class Stats {
            private StatsInfo firepower;
            private StatsInfo health;
            @SerializedName("antiair")
            private StatsInfo antiAir;
            private StatsInfo damage;
            private StatsInfo oPSDamageBoost;
            private StatsInfo rateOfFire;
            private StatsInfo spread;
            private StatsInfo angle;
            private StatsInfo range;
            private StatsInfo volley;
            private StatsInfo volleyTime;
            private StatsInfo coefficient;
            private StatsInfo ammoType;
            private StatsInfo characteristic;
            
            public Map<StatsInfo, String> getAll() {
                Map<StatsInfo, String> map = new LinkedHashMap<>();
                
                map.put(firepower, "Firepower");
                map.put(health, "Health");
                map.put(antiAir, "Anti-air");
                map.put(damage, "Damage");
                map.put(oPSDamageBoost, "OPS Damage Boost");
                map.put(rateOfFire, "Rate of Fire");
                map.put(spread, "Spread");
                map.put(angle, "Angle");
                map.put(range, "Range");
                map.put(volley, "Volley");
                map.put(volleyTime, "Volley Time");
                map.put(coefficient, "Coefficient");
                map.put(ammoType, "Ammo Type");
                map.put(characteristic, "Characteristic");
                
                return map;
            }
            
            @Getter
            public class StatsInfo extends ExtraStatsInfo {
                private String type;
                private String formatted;
            }
            
            @Getter
            public class ExtraStatsInfo {
                /**
                 * User for {@link Stats#damage}, {@link Stats#oPSDamageBoost}, {@link Stats#rateOfFire}, {@link Stats#coefficient}
                 */
                private String min;
                /**
                 * User for {@link Stats#damage}, {@link Stats#oPSDamageBoost}, {@link Stats#rateOfFire}, {@link Stats#coefficient}
                 */
                private String max;
                /**
                 * User for {@link Stats#damage}, {@link Stats#volley}
                 */
                private String multiplier;
                /**
                 * User for {@link Stats#rateOfFire}
                 */
                private String per;
                /**
                 * User for {@link Stats#spread}, {@link Stats#volleyTime}, {@link Stats#ammoType}
                 */
                private String value;
                /**
                 * User for {@link Stats#spread}, {@link Stats#volley}, {@link Stats#volleyTime}, {@link Stats#ammoType}
                 */
                private String unit;
                /**
                 * User for {@link Stats#range}
                 */
                private int firing;
                /**
                 * User for {@link Stats#range}
                 */
                private int shell;
                /**
                 * User for {@link Stats#volley}
                 */
                private String count;
            }
        }
    }
    
    public String getName() {
        return names.get("wiki");
    }
}
