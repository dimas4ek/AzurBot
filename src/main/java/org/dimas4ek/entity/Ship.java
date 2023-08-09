package org.dimas4ek.entity;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.dimas4ek.adapters.LimitBreakAdapter;
import org.dimas4ek.adapters.ObtainedFromAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Ship {
    private String wikiUrl;
    private Map<String, String> names;
    @SerializedName("class")
    private String shipClass;
    private String nationality;
    private String hullType;
    @SerializedName("thumbnail")
    private String thumbnailUrl;
    private String rarity;
    private int stars;
    private Stats stats;
    private List<Slot> slots;
    private Enhance enhanceValue;
    private Scrap scrapValue;
    private List<Skill> skills;
    private List<DevLevel> devLevels;
    @JsonAdapter(LimitBreakAdapter.class)
    private List<LimitBreak> limitBreaks;
    private FleetTech fleetTech;
    private boolean retrofit;
    private String retrofitId;
    private RetrofitProjects retrofitProjects;
    private String retrofitHullType;
    private Construction construction;
    @JsonAdapter(ObtainedFromAdapter.class)
    private ObtainedFrom obtainedFrom;
    private Misc misc;
    private List<Skin> skins;
    private List<Gallery> gallery;
    
    public String getName() {
        return names.get("en");
    }
    
    @Getter
    public class Stats {
        private StatsInfo baseStats;
        private StatsInfo level100;
        private StatsInfo level120;
        private StatsInfo level125;
        
        @Getter
        public class StatsInfo {
            private String health;
            private String armor;
            private String reload;
            private String luck;
            private String firepower;
            private String torpedo;
            private String evasion;
            private String speed;
            @SerializedName("antiair")
            private String antiAir;
            private String aviation;
            private String oilConsumption;
            private String accuracy;
            private String antisubmarineWarfare;
        }
    }
    
    @Getter
    public class Slot {
        private int maxEfficiency;
        private int minEfficiency;
        private String type;
        private int max;
        private int kaiEfficiency;
    }
    
    @Getter
    public class Enhance {
        private int firepower;
        private int torpedo;
        private int aviation;
        private int reload;
    }
    
    @Getter
    public class Scrap {
        private int coin;
        private int oil;
        private int medal;
    }
    
    @Getter
    public class Skill {
        private String icon;
        private Map<String, String> names;
        private String description;
        private String color;
        
        public String getName() {
            return names.get("en");
        }
    }
    
    @Getter
    public class DevLevel {
        private String level;
        private List<String> buffs;
    }
    
    @Getter
    public static class LimitBreak {
        private String lb;
        private String bonus;
        
        public void setBreak(String lb) {
            this.lb = lb;
        }
        
        public void setBonus(String bonus) {
            this.bonus = bonus;
        }
    }
    
    @Getter
    public class FleetTech {
        private StatsBonus statsBonus;
        private TechPoints techPoints;
        
        @Getter
        public class StatsBonus {
            private Collection collection;
            private MaxLevel maxLevel;
            
            @Getter
            public class Collection {
                private List<String> applicable;
                private String bonus;
                private String stat;
            }
            
            @Getter
            public class MaxLevel {
                private List<String> applicable;
                private String bonus;
                private String stat;
            }
        }
        
        @Getter
        public class TechPoints {
            private int collection;
            private int maxLimitBreak;
            private int maxLevel;
            private int total;
        }
    }
    
    @Getter
    public class RetrofitProjects {
        private Project A;
        private Project B;
        private Project C;
        private Project D;
        private Project E;
        private Project F;
        private Project G;
        private Project H;
        private Project I;
        private Project J;
        private Project K;
        private Project L;
        
        @Getter
        public class Project {
            private String id;
            private String grade;
            private List<String> attributes;
            private List<String> materials;
            private int coins;
            private int level;
            private int levelBreakLevel;
            private String levelBreakStars;
            private int recurrence;
            private List<String> require;
        }
    }
    
    @Getter
    public class Construction {
        private String constructionTime;
        private Available availableIn;
        
        @Getter
        public class Available {
            private Object light;
            private Object heavy;
            private Object aviation;
            private Object limited;
            private Object exchange;
            
            public Map<String, Object> getAll() {
                Map<String, Object> map = new HashMap<>();
                map.put("Light", light);
                map.put("Heavy", heavy);
                map.put("Aviation", aviation);
                map.put("Limited", limited);
                map.put("Exchange", exchange);
                return map;
            }
        }
    }
    
    @Getter
    public static class ObtainedFrom {
        @SerializedName("obtainedFrom")
        private String description;
        private List<String> fromMaps;
        
        public void setFromMaps(List<String> fromMaps) {
            this.fromMaps = fromMaps;
        }
        
        public void setObtainedFrom(String description) {
            this.description = description;
        }
    }
    
    @Getter
    public class Misc {
        private Artist artist;
        private Voice voice;
        
        @Getter
        private class Artist {
            private String name;
            private Url urls;
            
            @Getter
            private class Url {
                private String Wiki;
                private String Pixiv;
                private String Twitter;
                private String Link;
            }
        }
        
        @Getter
        private class Voice {
            private String name;
            private String url;
        }
    }
    
    @Getter
    public class Skin {
        private String name;
        @SerializedName("image")
        private String imageUrl;
        @SerializedName("background")
        private String backgroundUrl;
        @SerializedName("chibi")
        private String chibiUrl;
        private Info info;
        private String bg;
        
        @Getter
        public class Info {
            private boolean live2dModel;
            private String obtainedFrom;
            private String category;
            private String enClient;
            private String enLimited;
            private String firstIntroduced;
            private int cost;
            private List<Icon> icons;
            
            @Getter
            public class Icon {
                private String url;
                private String urlType;
            }
        }
    }
    
    @Getter
    public class Gallery {
        private String description;
        private String url;
    }
}