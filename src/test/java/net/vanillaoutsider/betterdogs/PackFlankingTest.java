// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class PackFlankingTest {

    @Test
    public void testFlankAngleCalculationUniqueness() {
        double baseAngle = 0.0;
        Set<Double> assignedAngles = new HashSet<>();

        for (int slot = 0; slot < 5; slot++) {
            double angle;
            if (slot == 0) {
                angle = baseAngle;
            } else {
                double angleOffset = (slot % 2 == 1 ? -1.0 : 1.0) * (Math.PI / 3.0) * ((slot + 1) / 2);
                angle = baseAngle + angleOffset;
            }
            assignedAngles.add(angle);
        }

        assertEquals(5, assignedAngles.size(), "Each pack slot must receive a unique flanking angle");
    }

    @Test
    public void testTrigonometricFlankOffsetCoordinates() {
        double targetX = 100.0;
        double targetZ = 100.0;
        double radius = 3.5;

        double angleSlot1 = -Math.PI / 3.0;
        double flankX1 = targetX + Math.cos(angleSlot1) * radius;
        double flankZ1 = targetZ + Math.sin(angleSlot1) * radius;

        double angleSlot2 = Math.PI / 3.0;
        double flankX2 = targetX + Math.cos(angleSlot2) * radius;
        double flankZ2 = targetZ + Math.sin(angleSlot2) * radius;

        double dist1 = Math.sqrt(Math.pow(flankX1 - targetX, 2) + Math.pow(flankZ1 - targetZ, 2));
        double dist2 = Math.sqrt(Math.pow(flankX2 - targetX, 2) + Math.pow(flankZ2 - targetZ, 2));

        assertEquals(radius, dist1, 0.001, "Flank 1 distance must match radius");
        assertEquals(radius, dist2, 0.001, "Flank 2 distance must match radius");
        assertNotEquals(flankZ1, flankZ2, "Left and right flank Z coordinates must be mirrored and distinct");
    }
}
