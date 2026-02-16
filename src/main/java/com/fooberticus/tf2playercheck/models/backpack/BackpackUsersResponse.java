package com.fooberticus.tf2playercheck.models.backpack;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Map;

@Data
public class BackpackUsersResponse {

    private ResponseData response;

    @Data
    public static class ResponseData {
        private int success;
        @SerializedName("current_time")
        private long currentTime;
        private Map<String, BackpackPlayer> players;
    }

    @Data
    public static class BackpackPlayer {
        @SerializedName("steamid")
        private String steamId;
        private int success;
        private String name;

        @SerializedName("backpack_value")
        private Map<String, Float> backpackValue;

        @SerializedName("backpack_update")
        private Map<String, Long> backpackUpdate;

        @SerializedName("backpack_tf_trust")
        private BackpackTrust backpackTrust;

        public String getDollarValue() {
            if (this.backpackValue == null) {
                return "--";
            }
            Float value = this.backpackValue.get("440");
            if (value != null && value > 0) {
                return String.format("$%.2f", value / 37.00);
            }
            return "--";
        }
    }

    @Data
    public static class BackpackTrust {
        private int forValue; // 'for' is a reserved keyword in Java
        private int against;

        @SerializedName("for")
        public void setForValue(int forValue) {
            this.forValue = forValue;
        }
    }


}
