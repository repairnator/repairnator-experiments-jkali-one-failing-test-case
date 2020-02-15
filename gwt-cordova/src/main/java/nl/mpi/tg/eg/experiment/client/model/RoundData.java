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
package nl.mpi.tg.eg.experiment.client.model;

import java.util.Date;
import java.util.List;
import nl.mpi.tg.eg.frinex.common.model.Stimulus;

/**
 * @since Jan 28, 2015 4:09:17 PM (creation date)
 * @author Peter Withers <p.withers@psych.ru.nl>
 */
public class RoundData {

    private Stimulus chosenAnswer;
    private final Stimulus correctSample;
    private final List<Stimulus> roundChoices;
    private final Date time;
    private long durationMs = 0;

    public RoundData(Stimulus correctSample, List<Stimulus> roundChoices, Date time) {
        this.correctSample = correctSample;
        this.roundChoices = roundChoices;
        this.time = time;
    }

    public void setChosenAnswer(Stimulus chosenAnswer) {
        this.chosenAnswer = chosenAnswer;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public Stimulus getChosenAnswer() {
        return chosenAnswer;
    }

    public Stimulus getCorrectSample() {
        return correctSample;
    }

    public List<Stimulus> getRoundChoices() {
        return roundChoices;
    }

    public Date getTime() {
        return time;
    }

    public double getDurationMs() {
        return durationMs;
    }
}
