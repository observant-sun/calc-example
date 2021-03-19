package kriuchkov.maksim;

public class Main {

    static double enthalpy = 75000;
    static double humidity = 1;

    public static void main(String[] args) {
        double tHigh = 80, tLow = -60; // TODO: диапазон?
        double tMid = (tHigh + tLow) / 2;

        double eHigh, eLow;

        eHigh = calcEnthalpyFromTemperatureAndHumidity(tHigh, humidity);
        eLow = calcEnthalpyFromTemperatureAndHumidity(tLow, humidity);

        if (enthalpy > eHigh || enthalpy < eLow)
            throw new IllegalArgumentException("Энтальпия вне диапазона расчетов");

        Double temperature = findTemp(tLow, tHigh, enthalpy);
        System.out.println(temperature);
    }


    private static final double HEAT_CAPACITY_AIR_DRY = 1006; // Дж / (кг * К)
    private static final double HEAT_OF_FUSION_WATER_0 = 2501000; // Дж / кг
    private static final double HEAT_OF_FUSION_WATER_DELTA = 2360; // Дж / (кг * К)
    private static final double HEAT_CAPACITY_WATER_VAPOR = 1860; // Дж / (кг * К)
    private static final double MOLAR_RATIO = 0.622;


    private static double calcEnthalpyFromTemperatureAndHumidity(double temperature, double humidity) {
        double pressureSaturatedVapor = pressureSaturatedVapor(temperature);
        return HEAT_CAPACITY_AIR_DRY * temperature + (HEAT_OF_FUSION_WATER_0 - HEAT_OF_FUSION_WATER_DELTA * temperature
                + HEAT_CAPACITY_WATER_VAPOR * temperature) * MOLAR_RATIO * pressureSaturatedVapor * humidity
                / (94500 - pressureSaturatedVapor);
    }

    private static double pressureSaturatedVapor(double temperature) {
        return 1000 * Math.exp((16.57 * temperature - 115.72) / (233.77 + 0.997 * temperature));
    }

    private static double findTemp(double tLow, double tHigh, double enthalpy) {
        final double EPSILON = 0.0001;
        double tMid = tHigh + tLow;
        while (tHigh - tLow > EPSILON) {
            tMid = (tHigh + tLow)/2;
            // eHigh > eLow -- всегда (?)
            double eMid = calcEnthalpyFromTemperatureAndHumidity(tMid, humidity);
            if (eMid == enthalpy) {
                return tMid;
            } else if (eMid < enthalpy) {
                tLow = tMid;
            } else {
                tHigh = tMid;
            }
        }
        return tMid;
    }
}
