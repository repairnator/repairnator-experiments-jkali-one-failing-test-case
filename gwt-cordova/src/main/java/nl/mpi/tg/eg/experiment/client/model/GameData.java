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

import java.util.ArrayList;

/**
 * @since Jan 28, 2015 3:57:30 PM (creation date)
 * @author Peter Withers <p.withers@psych.ru.nl>
 */
public class GameData {

    private int roundsPlayed = 0;
    private int roundsCorrect = 0;
    private int choicesPerRound = 0;
    private final ArrayList<RoundData> gameRoundData = new ArrayList<>();

    public ArrayList<RoundData> getGameRoundData() {
        return gameRoundData;
    }

    public void addRoundData(RoundData roundData) {
        gameRoundData.add(roundData);
        if (roundData.getChosenAnswer().equals(roundData.getCorrectSample())) {
            roundsCorrect++;
        }
        roundsPlayed++;
    }

    public void clearGameCounters() {
        roundsPlayed = 0;
        roundsCorrect = 0;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public int getRoundsCorrect() {
        return roundsCorrect;
    }

    public int getChoicesPerRound() {
        return choicesPerRound;
    }

    public void setChoicesPerRound(int choicesPerRound) {
        this.choicesPerRound = choicesPerRound;
    }
}
