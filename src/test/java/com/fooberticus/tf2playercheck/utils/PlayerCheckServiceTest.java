package com.fooberticus.tf2playercheck.utils;

import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicCommunityBan;
import com.fooberticus.tf2playercheck.models.rentamedic.RentAMedicResult;
import com.fooberticus.tf2playercheck.models.steamhistory.SourceBan;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerCheckServiceTest {

    @Test
    void countActiveCommunityBans_countsOnlyPermanentAndTempBan() {
        SourceBan permanent = new SourceBan(); permanent.setCurrentState("Permanent");
        SourceBan temp = new SourceBan();      temp.setCurrentState("Temp-Ban");
        SourceBan expired = new SourceBan();   expired.setCurrentState("Expired");
        SourceBan unbanned = new SourceBan();  unbanned.setCurrentState("Unbanned");

        assertEquals(2, PlayerCheckService.countActiveCommunityBans(List.of(permanent, temp, expired, unbanned)));
    }

    @Test
    void countActiveCommunityBans_returnsZeroForEmptyList() {
        assertEquals(0, PlayerCheckService.countActiveCommunityBans(List.of()));
    }

    @Test
    void countCheatingCommunityBans_detectsCheatingKeywords() {
        SourceBan aimBan = new SourceBan();    aimBan.setBanReason("aimbot detected");
        SourceBan hackBan = new SourceBan();   hackBan.setBanReason("using lmaobox");
        SourceBan normalBan = new SourceBan(); normalBan.setBanReason("toxic behavior");

        assertEquals(2, PlayerCheckService.countCheatingCommunityBans(List.of(aimBan, hackBan, normalBan)));
    }

    @Test
    void countCheatingCommunityBans_returnsZeroWhenNoCheatKeywords() {
        SourceBan ban = new SourceBan(); ban.setBanReason("griefing");
        assertEquals(0, PlayerCheckService.countCheatingCommunityBans(List.of(ban)));
    }

    @Test
    void findLatestBan_returnsNullForEmptyList() {
        assertNull(PlayerCheckService.findLatestBan(List.of()));
    }

    @Test
    void findLatestBan_returnsLatestByTimestamp() {
        // Use real-world timestamps representing distinct dates (epoch seconds)
        // 1000000000 = 2001-09-08, 1200000000 = 2008-01-10, 900000000 = 1998-07-09
        SourceBan older = new SourceBan();  older.setBanTimestamp(1000000000L);
        SourceBan newer = new SourceBan();  newer.setBanTimestamp(1200000000L);
        SourceBan oldest = new SourceBan(); oldest.setBanTimestamp(900000000L);

        assertSame(newer, PlayerCheckService.findLatestBan(List.of(older, newer, oldest)));
    }

    @Test
    void countActiveRentAMedicBans_countsActiveBans() {
        RentAMedicCommunityBan active1 = new RentAMedicCommunityBan(); active1.setActive(true);
        RentAMedicCommunityBan active2 = new RentAMedicCommunityBan(); active2.setActive(true);
        RentAMedicCommunityBan inactive = new RentAMedicCommunityBan(); inactive.setActive(false);

        RentAMedicResult result = new RentAMedicResult();
        result.setCommunityBans(List.of(active1, active2, inactive));

        assertEquals(2, PlayerCheckService.countActiveRentAMedicBans(result));
    }

    @Test
    void countActiveRentAMedicBans_returnsZeroForNullBans() {
        RentAMedicResult result = new RentAMedicResult();
        result.setCommunityBans(null);
        assertEquals(0, PlayerCheckService.countActiveRentAMedicBans(result));
    }

    @Test
    void countActiveRentAMedicBans_returnsZeroForEmptyBans() {
        RentAMedicResult result = new RentAMedicResult();
        result.setCommunityBans(new ArrayList<>());
        assertEquals(0, PlayerCheckService.countActiveRentAMedicBans(result));
    }

    @Test
    void formatCheaterDate_extractsDatePart() {
        assertEquals("2023-01-15", PlayerCheckService.formatCheaterDate("2023-01-15T10:30:00"));
    }

    @Test
    void formatCheaterDate_returnsPlaceholderForNull() {
        assertEquals("--", PlayerCheckService.formatCheaterDate(null));
    }

    @Test
    void findLatestBan_skipsNullTimestampBans() {
        SourceBan nullTs = new SourceBan(); // no timestamp set (null)
        SourceBan dated = new SourceBan(); dated.setBanTimestamp(1000000000L);

        assertSame(dated, PlayerCheckService.findLatestBan(List.of(nullTs, dated)));
    }

    @Test
    void formatCheaterDate_handlesStringWithNoT() {
        assertEquals("2023-01-15", PlayerCheckService.formatCheaterDate("2023-01-15"));
    }
}
