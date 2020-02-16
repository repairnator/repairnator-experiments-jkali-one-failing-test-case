/*
 * #%L
 * ELK Reasoner Protege Plug-in
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/**
 * 
 */
package org.semanticweb.elk.protege.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.protege.ElkPreferences;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * UI panel for setting preferences for ELK
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class ElkPreferencesPanel extends OWLPreferencesPanel {

	private static final long serialVersionUID = -5568211860560307648L;

	private SpinnerNumberModel numberOfWorkersModel_;

	private JCheckBox incrementalCheckbox_, syncCheckbox_;

	@Override
	public void initialise() throws Exception {
		setLayout(new BorderLayout());
		PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
		add(panel, BorderLayout.NORTH);
		ElkPreferences prefs = new ElkPreferences().load();

		panel.addGroup("Number of worker threads");
		numberOfWorkersModel_ = new SpinnerNumberModel(prefs.numberOfWorkers, 1,
				999, 1);
		JComponent spinner = new JSpinner(numberOfWorkersModel_);
		spinner.setMaximumSize(spinner.getPreferredSize());
		spinner.setToolTipText(
				"The number of threads that ELK can use for performing parallel computations");
		panel.addGroupComponent(spinner);

		incrementalCheckbox_ = new JCheckBox("Incremental reasoning",
				prefs.incrementalMode);
		incrementalCheckbox_.setToolTipText(
				"If checked, ELK tries to recompute only the results caused by the changes in the ontology");
		panel.addGroupComponent(incrementalCheckbox_);

		syncCheckbox_ = new JCheckBox("Auto-syncronization",
				prefs.autoSynchronization);
		syncCheckbox_.setToolTipText(
				"If checked, ELK will always be in sync with the ontology (requires reasoner restart)");
		syncCheckbox_.setEnabled(incrementalCheckbox_.isSelected());
		incrementalCheckbox_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				syncCheckbox_.setEnabled(incrementalCheckbox_.isSelected());
			}
		});
		panel.addGroupComponent(syncCheckbox_);
	}

	@Override
	public void applyChanges() {
		ElkPreferences prefs = new ElkPreferences().load();
		prefs.numberOfWorkers = numberOfWorkersModel_.getNumber().intValue();
		prefs.incrementalMode = incrementalCheckbox_.isSelected();
		prefs.autoSynchronization = syncCheckbox_.isSelected();
		prefs.save();
	}

	@Override
	public void dispose() throws Exception {
		// if the reasoner is ELK and has already been created, load the
		// preferences
		OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager()
				.getCurrentReasoner();
		if (reasoner instanceof ElkReasoner) {
			((ElkReasoner) reasoner)
					.setConfigurationOptions(ElkPreferences.getElkConfig());
		}
	}

}
