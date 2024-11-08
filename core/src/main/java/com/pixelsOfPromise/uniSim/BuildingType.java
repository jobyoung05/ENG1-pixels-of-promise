package com.pixelsOfPromise.uniSim;

public enum BuildingType {
    PLACE_TO_SLEEP("Place to sleep"),
    PLACE_TO_LEARN("Place to learn"),
    PLACE_TO_EAT("Place to eat"),
    RECREATIONAL_ACTIVITY("Place for recreational activities");


    private final String text;

    /**
     * @param text
     */
    BuildingType(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
