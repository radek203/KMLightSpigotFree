package pl.kwadratowamasakra.lightspigot.connection;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Version {

    UNDEFINED(-1),
    V1_7_2(4), // 1.7.2-1.7.5
    V1_7_6(5), // 1.7.6-1.7.10
    V1_8(47), // 1.8-1.8.8
    V1_9(107),
    V1_9_1(108),
    V1_9_2(109),
    V1_9_4(110),
    V1_10(210), // 1.10-1.10.2
    V1_11(315),
    V1_11_1(316), // 1.11.1-1.11.2
    V1_12(335),
    V1_12_1(338),
    V1_12_2(340),
    V1_13(393),
    V1_13_1(401),
    V1_13_2(404),
    V1_14(477),
    V1_14_1(480),
    V1_14_2(485),
    V1_14_3(490),
    V1_14_4(498),
    V1_15(573),
    V1_15_1(575),
    V1_15_2(578),
    V1_16(735),
    V1_16_1(736),
    V1_16_2(751),
    V1_16_3(753),
    V1_16_4(754), // 1.16.4-1.16.5
    V1_17(755),
    V1_17_1(756),
    V1_18(757), // 1.18-1.18.1
    V1_18_2(758),
    V1_19(759),
    V1_19_1(760), // 1.19.1-1.19.2
    V1_19_3(761),
    V1_19_4(762),
    V1_20(763), // 1.20-1.20.1
    V1_20_2(764),
    V1_20_3(765),
    V1_20_5(766), // 1.20.5-1.20.6
    V1_21(767);

    private static final Map<Integer, Version> VERSION_MAP = new HashMap<>();
    @Getter
    private static final Version MIN;
    @Getter
    private static final Version MAX;

    static {
        Version[] values = values();

        MIN = values[1];
        MAX = values[values.length - 1];

        for (Version version : values) {
            VERSION_MAP.put(version.getProtocolNumber(), version);
        }
    }

    @Getter
    private final int protocolNumber;

    Version(int protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    public static Version[] getVersionsRange(Version versionMin, Version versionMax) {
        int min = Math.min(versionMin.protocolNumber, versionMax.protocolNumber);
        int max = Math.max(versionMin.protocolNumber, versionMax.protocolNumber);

        return Arrays.stream(values())
                .filter(v -> v.protocolNumber >= min && v.protocolNumber <= max)
                .toArray(Version[]::new);
    }

    public static Version of(int protocolNumber) {
        return VERSION_MAP.getOrDefault(protocolNumber, UNDEFINED);
    }

    public boolean isEqual(Version version) {
        return protocolNumber == version.getProtocolNumber();
    }

    public boolean isLessThan(Version version) {
        return protocolNumber < version.getProtocolNumber();
    }

    public boolean isEqualOrHigher(Version version) {
        return protocolNumber >= version.getProtocolNumber();
    }

    public boolean isInRange(Version versionMin, Version versionMax) {
        return versionMin.getProtocolNumber() <= protocolNumber && versionMax.getProtocolNumber() >= protocolNumber;
    }

    public boolean isSupported() {
        return this != UNDEFINED;
    }
}
