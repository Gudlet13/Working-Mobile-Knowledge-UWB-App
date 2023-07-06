package com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.model;

import com.google.ar.core.Anchor;
import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.model.Position;

public class AnchorRemoteDevice {

    private Accessory accessory;
    private Position position;
    private Anchor anchor;

    public AnchorRemoteDevice () {}

    public AnchorRemoteDevice(Accessory accessory, Position position, Anchor anchor) {
        this.accessory = accessory;
        this.position = position;
        this.anchor = anchor;
    }

    public Accessory getAccessory() {
        return accessory;
    }

    public void setAccessory(Accessory accessory) {
        this.accessory = accessory;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }
}
