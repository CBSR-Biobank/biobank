/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package edu.ualberta.med.biobank.gui.common.widgets.nebula;

import org.eclipse.swt.widgets.Event;

import edu.ualberta.med.biobank.gui.common.widgets.nebula.v.VControl;

public interface IControlPainter {

    public abstract void dispose();

    public abstract void paintBackground(VControl control, Event e);

    public abstract void paintBorders(VControl control, Event e);

    public abstract void paintContent(VControl control, Event e);

}
