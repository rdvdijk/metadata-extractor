/*
 * Copyright 2002-2019 Drew Noakes and contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */

package com.drew.lang;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Represents a latitude and longitude pair, giving a position on earth in spherical coordinates.
 * <p>
 * Values of latitude and longitude are given in degrees.
 * <p>
 * This type is immutable.
 */
public final class GeoLocation
{
    private final double _latitude;
    private final double _longitude;
    private final Locale _locale;

    /**
     * Instantiates a new instance of {@link GeoLocation}.
     *
     * @param latitude the latitude, in degrees
     * @param longitude the longitude, in degrees
     * @param locale the locale to use
     */
    public GeoLocation(double latitude, double longitude, Locale locale)
    {
        _latitude = latitude;
        _longitude = longitude;
        _locale = locale;
    }

    /**
     * @return the latitudinal angle of this location, in degrees.
     */
    public double getLatitude()
    {
        return _latitude;
    }

    /**
     * @return the longitudinal angle of this location, in degrees.
     */
    public double getLongitude()
    {
        return _longitude;
    }

    /**
     * @return true, if both latitude and longitude are equal to zero
     */
    public boolean isZero()
    {
        return _latitude == 0 && _longitude == 0;
    }

    // TODO document this deprecation

    /**
     * Converts a decimal degree angle into its corresponding DMS (degrees-minutes-seconds) representation as a string,
     * of format: {@code -1° 23' 4.56"}
     *
     * @deprecated Use {@link #decimalToDegreesMinutesSecondsString(double, Locale)} instead
     */
    @NotNull
    @Deprecated
    public static String decimalToDegreesMinutesSecondsString(double decimal)
    {
        return decimalToDegreesMinutesSecondsString(decimal, null);
    }

    /**
     * Converts a decimal degree angle into its corresponding DMS (degrees-minutes-seconds) representation as a string,
     * of format: {@code -1° 23' 4.56"}
     */
    @NotNull
    public static String decimalToDegreesMinutesSecondsString(double decimal, Locale locale)
    {
        double[] dms = decimalToDegreesMinutesSeconds(decimal);
        DecimalFormatSymbols symbols = locale == null ? DecimalFormatSymbols.getInstance() : DecimalFormatSymbols.getInstance(locale);
        DecimalFormat format = new DecimalFormat("0.##", symbols);
        return String.format("%s\u00B0 %s' %s\"", format.format(dms[0]), format.format(dms[1]), format.format(dms[2]));
    }

    /**
     * Converts a decimal degree angle into its corresponding DMS (degrees-minutes-seconds) component values, as
     * a double array.
     */
    @NotNull
    public static double[] decimalToDegreesMinutesSeconds(double decimal)
    {
        int d = (int)decimal;
        double m = Math.abs((decimal % 1) * 60);
        double s = (m % 1) * 60;
        return new double[] { d, (int)m, s};
    }

    /**
     * Converts DMS (degrees-minutes-seconds) rational values, as given in {@link com.drew.metadata.exif.GpsDirectory},
     * into a single value in degrees, as a double.
     */
    @Nullable
    public static Double degreesMinutesSecondsToDecimal(@NotNull final Rational degs, @NotNull final Rational mins, @NotNull final Rational secs, final boolean isNegative)
    {
        double decimal = Math.abs(degs.doubleValue())
                + mins.doubleValue() / 60.0d
                + secs.doubleValue() / 3600.0d;

        if (Double.isNaN(decimal))
            return null;

        if (isNegative)
            decimal *= -1;

        return decimal;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoLocation that = (GeoLocation) o;
        if (Double.compare(that._latitude, _latitude) != 0) return false;
        if (Double.compare(that._longitude, _longitude) != 0) return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        temp = _latitude != +0.0d ? Double.doubleToLongBits(_latitude) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = _longitude != +0.0d ? Double.doubleToLongBits(_longitude) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * @return a string representation of this location, of format: {@code 1.23, 4.56}
     */
    @Override
    @NotNull
    public String toString()
    {
        return _latitude + ", " + _longitude;
    }

    /**
     * @return a string representation of this location, of format: {@code -1° 23' 4.56", 54° 32' 1.92"}
     */
    @NotNull
    public String toDMSString()
    {
        return decimalToDegreesMinutesSecondsString(_latitude, _locale)
            + ", "
            + decimalToDegreesMinutesSecondsString(_longitude, _locale);
    }
}
