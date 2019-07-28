package com.kenshin0707.bots.presets.woodcut;

import com.kenshin0707.bots.presets.Location;
import com.kenshin0707.bots.presets.point;
import org.json.JSONArray;
import org.json.JSONObject;

public class WoodcutLocation extends Location {
    public WoodcutLocation(Object obj){
        name = ((JSONObject)obj).get("name").toString();
        for(Object ore: (JSONArray)((JSONObject)obj).get("trees")){
            this.items.add(ore.toString());
        }
        for(Object p: (JSONArray)((JSONObject)obj).get("route")){
            JSONObject o = (JSONObject)p;
            point pt = new point(o.getInt("x"),o.getInt("y"),o.getInt("plane"));
            this.route.add(pt);
        }
    }
}
