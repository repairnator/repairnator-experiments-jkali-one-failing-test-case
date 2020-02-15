/*
 * Copyright (C) 2015 Language In Interaction
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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HandlesAllTouchEvents;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.Command;

/**
 * @since Feb 11, 2015 4:34:40 PM (creation date)
 * @author Peter Withers <p.withers@psych.ru.nl>
 */
public abstract class SingleShotEventListner extends HandlesAllTouchEvents implements ClickHandler {

    private boolean singleShotConsumed = false;
//    private final Button button;
    boolean sawTouchStart = false;
//    public SingleShotEventListner(Button button) {
//        this.button = button;
//    }

    final public void eventFired() {
        sawTouchStart = false;
//        button.setText("eventFired");
//        hasTargetTouch = false;
//        touchTarget = null;
        if (!singleShotConsumed) {
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
//                    button.setText("scheduleDeferred");
                    singleShotFired();
                }
            });
        }
        singleShotConsumed = true;
    }

    public void resetSingleShot() {
//        button.setText("resetSingleShot");
        singleShotConsumed = false;
    }

    @Override
    final public void onClick(ClickEvent event) {
        sawTouchStart = false;
//        button.setText("onClick");
        event.preventDefault();
        eventFired();
//        if (!singleShotConsumed) {
//            singleShotConsumed = true;
//            singleShotFired();
//        }
    }
//    private boolean hasTargetTouch = false;
//    private EventTarget touchTarget = null;

    @Override
    public void onTouchStart(TouchStartEvent event) {
        sawTouchStart = true;
        // @todo: ignore any multi touch events
        // @todo: add a setting for ignoring any touch within x ms of a touch anywhere on the screen
//        final JsArray<Touch> targetTouches = event.getTargetTouches();
//        button.setText("onTouchStart:" + targetTouches.length());
//        event.preventDefault();
//        if (targetTouches.length() > 0) {
//            touchTarget = targetTouches.shift().getTarget();
//            hasTargetTouch = true;
//        }
    }

    @Override
    public void onTouchMove(TouchMoveEvent event) {
        sawTouchStart = false;
//        final JsArray<Touch> targetTouches = event.getTargetTouches();
//        button.setText("onTouchMove:" + event.getTouches().length() + ":" + event.getTargetTouches().length());
////        event.preventDefault();
//        if (targetTouches.length() > 0) {
//            final EventTarget target = targetTouches.shift().getTarget();
//            hasTargetTouch = target.equals(touchTarget);
//            button.setText(targetTouches.length() + ":" + hasTargetTouch);
//        } else {
//            touchTarget = null;
//            hasTargetTouch = true;
//        }
    }

    @Override
    public void onTouchCancel(TouchCancelEvent event) {
        sawTouchStart = false;
//        button.setText("onTouchCanceled");
//        event.preventDefault();
//        if (event.getTargetTouches().length() == 0) {
//            hasTargetTouch = false;
//            touchTarget = null;
//        }
    }

    @Override
    public void onTouchEnd(TouchEndEvent event) {
        if (sawTouchStart) {
//        button.setText("onTouchEnd:" + event.getTouches().length() + ":" + event.getTargetTouches().length());
            event.preventDefault();
//        if (hasTargetTouch && event.getTargetTouches().length() == 0) {
            eventFired();
        }
    }

    protected abstract void singleShotFired();
}
