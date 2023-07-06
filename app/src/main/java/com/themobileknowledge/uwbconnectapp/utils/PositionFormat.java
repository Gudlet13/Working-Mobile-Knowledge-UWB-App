package com.themobileknowledge.uwbconnectapp.utils;

import androidx.annotation.Nullable;

import com.themobileknowledge.uwbconnectapp.model.Position;

public class PositionFormat {

    public static String getDistanceFormat(@Nullable Position position) {
        if (position == null) {
            return "";
        } else {
            if (position.getDistance() >= 100) {
                return (((double) position.getDistance()) / 100) + " m";
            } else {
                return position.getDistance() + " cm";
            }
        }
    }

    public static String getAngleAzimuthFormat(@Nullable Position position) {
        if (position == null) {
            return "";
        } else {
            return "a: " + position.getAzimuth() + "\u00B0";
        }
    }

    public static String getAngleElevationFormat(@Nullable Position position) {
        if (position == null) {
            return "";
        } else {
            return "e: " + position.getElevation() + "\u00B0";
        }
    }
}
