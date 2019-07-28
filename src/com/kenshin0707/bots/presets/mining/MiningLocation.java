package com.kenshin0707.bots.presets.mining;

import com.kenshin0707.bots.presets.Location;
import com.kenshin0707.bots.presets.point;
import org.json.JSONArray;
import org.json.JSONObject;


public class MiningLocation extends Location {
    public MiningLocation(Object obj){
        name = ((JSONObject)obj).get("name").toString();
        for(Object ore: (JSONArray)((JSONObject)obj).get("ores")){
            this.items.add(ore.toString());
        }
        for(Object p: (JSONArray)((JSONObject)obj).get("straightPath")){
            JSONObject o = (JSONObject)p;
            point pt = new point(o.getInt("x"),o.getInt("y"),o.getInt("plane"));
            this.route.add(pt);
        }
    }
}
