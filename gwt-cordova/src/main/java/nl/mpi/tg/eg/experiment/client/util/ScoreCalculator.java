/*
 * Copyright (C) 2014 Language In Interaction
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
package nl.mpi.tg.eg.experiment.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.logging.Logger;
import nl.mpi.tg.eg.frinex.common.model.Stimulus;
import nl.mpi.tg.eg.experiment.client.model.UserResults;
import nl.mpi.tg.eg.experiment.client.model.colour.ColourData;
import nl.mpi.tg.eg.experiment.client.model.colour.StimulusResponse;
import nl.mpi.tg.eg.experiment.client.model.colour.StimulusResponseGroup;
import nl.mpi.tg.eg.experiment.client.model.colour.GroupScoreData;
import nl.mpi.tg.eg.experiment.client.model.colour.NormalisedColour;
import nl.mpi.tg.eg.experiment.client.model.colour.ScoreData;

/**
 * @since Oct 21, 2014 4:49:43 PM (creation date)
 * @author Peter Withers <p.withers@psych.ru.nl>
 */
public class ScoreCalculator {

    private static final Logger logger = Logger.getLogger(ScoreCalculator.class.getName());
    double tempScoreValue = 0.5;
    private final UserResults userResults;

    public ScoreCalculator(UserResults userResults) {
        this.userResults = userResults;
    }

    public GroupScoreData calculateScores(final StimulusResponseGroup stimulusResponseGroup) {
//        System.out.println(stimulusResponseGroup.getGroupLabel() + "<table>");
        float score = 0;
        double accuracy = 0;
        double totalValidReactionTime = 0;
        int validResponseCount = 0;
        ArrayList<ScoreData> scoreList = new ArrayList<>();
        final SortedSet<Stimulus> allStimulus = stimulusResponseGroup.getStimuli();
        int columnCount = stimulusResponseGroup.getMaxResponses();
        final ArrayList<Double> validTimesList = new ArrayList<>();
        for (Stimulus stimulus : allStimulus) {
//            System.out.print("<tr><td>" + stimulus.getLabel()+ "</td>");
            int totalReactionTime = 0;
            final ArrayList<ColourData> colourList = new ArrayList<>();
            List<StimulusResponse> responseList = stimulusResponseGroup.getResults(stimulus);
            int averageLuminance = 0;
            int validCount = 0;
            boolean isValid = true;
            // set the last as the previous to provide the correct overlap
            ColourData previousColour = responseList.get(responseList.size() - 1).getColour();
            NormalisedColour difference = null;
            final ArrayList<Double> timesList = new ArrayList<>();
            // loop over all except the first which is already processed
            for (int column = 0; column < columnCount; column++) {
                final StimulusResponse response = responseList.get(column);
                final ColourData colour = response.getColour();
                colourList.add(colour);
                if (colour == null) {
                    isValid = false;
                } else {
//                    System.out.print("<td style=\"background:" + colour.getHexValue() + "\">" + colour.getHexValue() + "</td>");
                    validCount++;
                    final double durationMs = response.getDurationMs();
                    timesList.add(durationMs);
                    totalReactionTime += durationMs;
                    averageLuminance += colour.getLuminance();
                    if (previousColour != null) {
                        difference = (difference == null) ? new NormalisedColour(previousColour).difference(new NormalisedColour(colour)) : difference.add(new NormalisedColour(previousColour).difference(new NormalisedColour(colour)));
                    }
                    // update the previous with the current colour which is known to be valid
                    previousColour = colour;
                }
            }
            averageLuminance = (validCount > 0) ? averageLuminance / validCount : 0;
            Float distance = (isValid) ? difference.getSum() : null;
            scoreList.add(new ScoreData(stimulus, averageLuminance, colourList, distance));
            if (isValid) {
                score += distance;
                totalValidReactionTime += totalReactionTime;
                validTimesList.addAll(timesList);
                validResponseCount++;
//                System.out.println("<td><div style=\"background: black; width: " + (int) (distance * 10) + "px\" >&nbsp;</div>" + distance + "</td>");
            } else {
//                System.out.println("<td>invalid</td>");
            }
//            System.out.println("<td>" + score + "</td></tr>");
        }
//        System.out.println("<tr><td>score: " + score / validResponseCount + "</td><td> = " + score + "/" + validResponseCount + "</td></tr>");
        score = score / validResponseCount;
        final double meaneactionTime = totalValidReactionTime / validResponseCount;
        double timeVarianceTemp = 0;
        for (double validTime : validTimesList) {
            timeVarianceTemp += (meaneactionTime - validTime) * (meaneactionTime - validTime);
        }
        final double variance = timeVarianceTemp / validResponseCount; // this is calculating the population standandard deviation, otherwise we would use (validResponseCount - 1)
        final double reactionTimeDeviation = Math.sqrt(variance);
//        System.out.println("<tr><td>score: " + score + "</td><td>accuracy: " + accuracy + "</td></tr></table>");
        return new GroupScoreData(scoreList, score, accuracy, meaneactionTime, reactionTimeDeviation);
    }
}
