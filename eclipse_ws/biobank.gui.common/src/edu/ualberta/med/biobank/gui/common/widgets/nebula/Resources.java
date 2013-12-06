/****************************************************************************
 * Copyright (c) 2008 Jeremy Dowdall All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package edu.ualberta.med.biobank.gui.common.widgets.nebula;

import org.eclipse.swt.graphics.Image;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;

class Resources {

    public static Image getIconBullet() {
        return BgcPlugin.getDefault().getImage(BgcPlugin.Image.BULLET);
    }

    public static Image getIconCalendar() {
        return BgcPlugin.getDefault().getImage(BgcPlugin.Image.CALENDAR);
    }
}
