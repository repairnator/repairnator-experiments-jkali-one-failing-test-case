/*
 * Copyright (C) 2017 Max Planck Institute for Psycholinguistics, Nijmegen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package nl.mpi.tg.eg.experiment.client.listener;

import com.google.gwt.user.client.ui.Widget;

/**
 * @since Nov 30, 2017 4:04:58 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public interface StimulusButton {

    Widget getWidget();

    void addStyleName(String styleName);

    void removeStyleName(String styleName);

    void setEnabled(boolean enabled);

    boolean isEnabled();

    void setVisible(boolean visible);

    void triggerSingleShotEventListner();
}
