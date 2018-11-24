/* 
    MIT License

    Copyright (c) 2018 Chris Mc, prince.chrismc(at)gmail(dot)com

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
package Models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cmcarthur
 */
public enum Location {
    INVALID(0, "Invalid", "Invalid"),
    CA(6000, "Canada", "CA"),
    US(6001, "United-States", "US"),
    UK(6002, "United-Kingdom", "UK");

    private final int m_UUID;
    private final String m_Name;
    private final String m_Prefix;

    private Location(int ID, String name, String prefix) {
        m_UUID = ID;
        m_Name = name;
        m_Prefix = prefix;
    }

    public static Location fromString(String location) {

        for (Location region : Location.values()) {
            if ((region.getPrefix() == null ? location == null : region.getPrefix().equalsIgnoreCase(location))
                    || (region.toString() == null ? location == null : region.toString().equalsIgnoreCase(location))) {
                return region;
            }
        }

        return INVALID;
    }

    public static boolean isValidLocation(String location) {
        for (Location loc : Location.values()) {
            if (loc.toString().equalsIgnoreCase(location)) {
                return true;
            }
        }

        return false;
    }

    public static List<String> getLocationsAsStrings() {
        List<String> locations = new ArrayList<>();

        for (Location loc : values()) {
            locations.add(loc.toString());
        }

        return locations;
    }

    public static String printLocations() {
        String out = "";

        for (Location loc : values()) {
            out += loc + ", ";
        }

        out = out.substring(0, out.length() - 2);

        return out;
    }

    public int getPort() {
        return m_UUID;
    }

    @Override
    public String toString() {
        return m_Name;
    }

    public String getPrefix() {
        return m_Prefix;
    }

}
