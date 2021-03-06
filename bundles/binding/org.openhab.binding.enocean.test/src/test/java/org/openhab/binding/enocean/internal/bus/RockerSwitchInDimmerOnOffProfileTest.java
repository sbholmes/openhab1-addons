/**
 * Copyright (c) 2010-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enocean.internal.bus;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.opencean.core.address.EnoceanId;
import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.EEPId;
import org.opencean.core.common.Parameter;
import org.opencean.core.common.values.ButtonState;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.library.types.OnOffType;

public class RockerSwitchInDimmerOnOffProfileTest extends BasicBindingTest {

    private static final String CHANNEL = "B";

    @Before
    public void setUpDefaultDevice() {
        parameterAddress = new EnoceanParameterAddress(EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID),
                CHANNEL, (String) null);
        provider.setParameterAddress(parameterAddress);
        provider.setItem(new DimmerItem("dummie"));
        provider.setEep(EEPId.EEP_F6_02_01);
        binding.addBindingProvider(provider);
    }

    @Test
    public void switchOnLightOnShortButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(
                EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), CHANNEL, Parameter.I);
        binding.valueChanged(valueParameterAddress, ButtonState.PRESSED);
        waitFor(10);
        binding.valueChanged(valueParameterAddress, ButtonState.RELEASED);
        waitFor(10);
        assertEquals("Update State", OnOffType.ON, publisher.popLastCommand());
    }

    @Test
    public void doNothingOnWrongChannel() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(
                EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), "A", Parameter.I);
        binding.valueChanged(valueParameterAddress, ButtonState.PRESSED);
        waitFor(10);
        assertNull("Update State", publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, ButtonState.RELEASED);
        waitFor(10);
        assertNull("Update State", publisher.popLastCommand());
    }

    @Test
    public void switchOffLightOnShortButtonPressUp() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(
                EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), CHANNEL, Parameter.O);
        binding.valueChanged(valueParameterAddress, ButtonState.PRESSED);
        waitFor(10);
        binding.valueChanged(valueParameterAddress, ButtonState.RELEASED);
        waitFor(10);
        assertEquals("Update State", OnOffType.OFF, publisher.popLastCommand());
    }

    @Test
    public void lightenUpDuringLongButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(
                EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), CHANNEL, Parameter.I);
        binding.valueChanged(valueParameterAddress, ButtonState.PRESSED);
        waitFor(400);
        assertEquals("Update State", IncreaseDecreaseType.INCREASE, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, ButtonState.RELEASED);
        waitFor(10);
        assertNull("Update State", publisher.popLastCommand());
    }

    @Test
    public void lightenUpDuringVeryLongButtonPressDown() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(
                EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), CHANNEL, Parameter.I);
        binding.valueChanged(valueParameterAddress, ButtonState.PRESSED);
        waitFor(400);
        assertEquals("Update State", IncreaseDecreaseType.INCREASE, publisher.popLastCommand());
        waitFor(300);
        assertEquals("Update State", IncreaseDecreaseType.INCREASE, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, ButtonState.RELEASED);
        waitFor(10);
        assertNull("Update State", publisher.popLastCommand());
    }

    @Test
    public void dimmLightDuringLongButtonPressUp() {
        EnoceanParameterAddress valueParameterAddress = new EnoceanParameterAddress(
                EnoceanId.fromString(EnoceanBindingProviderMock.DEVICE_ID), CHANNEL, Parameter.O);
        binding.valueChanged(valueParameterAddress, ButtonState.PRESSED);
        waitFor(400);
        assertEquals("Update State", IncreaseDecreaseType.DECREASE, publisher.popLastCommand());
        binding.valueChanged(valueParameterAddress, ButtonState.RELEASED);
        waitFor(10);
        assertNull("Update State", publisher.popLastCommand());
    }

    private void waitFor(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
