/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.client.ses.ui.subscribe;

import static com.smartgwt.client.types.Alignment.RIGHT;
import static com.smartgwt.client.types.Overflow.VISIBLE;
import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.event.RuleCreatedEvent.TYPE;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.util.ClientSessionManager.currentSession;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.event.CreateSimpleRuleEvent;
import org.n52.client.ses.event.RuleCreatedEvent;
import org.n52.client.ses.event.SubscribeEvent;
import org.n52.client.ses.event.handler.RuleCreatedEventHandler;
import org.n52.client.ses.ui.LoginWindow;
import org.n52.client.sos.legend.Timeseries;
import org.n52.client.ui.ApplyCancelButtonLayout;
import org.n52.shared.serializable.pojos.Rule;

import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class EventSubscriptionWindow extends LoginWindow {
	
	private static final String COMPONENT_ID = "eventSubscriptionWindow";

    private EventSubscriptionController controller = new EventSubscriptionController();

    private Layout ruleTemplateEditCanvas;

    /**
     * @param dataItem the timeseries item to create a subscription for.
     */
    public EventSubscriptionWindow(Timeseries dataItem) {
    	super(COMPONENT_ID);
        new EventSubsriptionWindowEventBroker(this);
        controller.setEventSubscription(this);
        controller.setTimeseries(dataItem);
        initializeContent();
    }

    @Override
    protected void loadWindowContent() {
        content = new HLayout();
        content.setStyleName("n52_sensorweb_client_create_abo_window_content");
        content.addMember(createSubscribeAndHelpContent());
        setTitle(i18n.createAboWindowTitle());
        addItem(content);
        markForRedraw();
    }

    private Canvas createSubscribeAndHelpContent() {
        Layout layout = new HLayout();
        layout.addMember(createNewEventAbonnementCanvas());
        layout.addMember(createContextWindowHelp());
        layout.setOverflow(VISIBLE);
        return layout;
    }

    private Canvas createNewEventAbonnementCanvas() {
        Layout subscriptionContent = new VLayout();
        subscriptionContent.setStyleName("n52_sensorweb_client_create_abo_form_content");
        subscriptionContent.addMember(createStationInfo());
        subscriptionContent.addMember(createRuleTemplateSelectionCanvas());
        subscriptionContent.addMember(new EventNameForm(controller));
        subscriptionContent.addMember(createApplyCancelCanvas());
        subscriptionContent.addMember(new TimeSeriesMetadataTable(controller));
        return subscriptionContent;
    }

    private Canvas createStationInfo() {
        Timeseries timeSeries = controller.getTimeSeries();
        StaticTextItem stationName = createStationNameItem(timeSeries);
        StaticTextItem parameter = createPhenomenonItem(timeSeries);
        HeaderItem header = createHeaderItem();
        DynamicForm form = createStationInfoForm();
        form.setFields(header, stationName, parameter);
        return form;
    }

    private StaticTextItem createStationNameItem(Timeseries timeSeries) {
        StaticTextItem stationName = new StaticTextItem();
        stationName.setTitle(i18n.station());
        stationName.setValue(timeSeries.getStationName());
        return stationName;
    }

    private StaticTextItem createPhenomenonItem(Timeseries timeSeries) {
        StaticTextItem parameter = new StaticTextItem();
        parameter.setTitle(i18n.phenomenon());
        parameter.setValue(timeSeries.getPhenomenonId());
        return parameter;
    }

    private DynamicForm createStationInfoForm() {
        DynamicForm form = new DynamicForm();
        form.setStyleName("n52_sensorweb_client_create_abo_info");
        return form;
    }

    private HeaderItem createHeaderItem() {
        HeaderItem header = new HeaderItem();
        header.setDefaultValue(i18n.createBasicRule());
        return header;
    }

    private Canvas createRuleTemplateSelectionCanvas() {
        Layout selectionCanvas = new VLayout();
        SelectSubscriptionForm selectionRadioForm = new SelectSubscriptionForm(controller);
        SubscriptionTemplate template = selectionRadioForm.getDefaultSubscriptionTemplate();
        Canvas ruleTemplateEditCanvas = createRuleTemplateEditorCanvas(template);
        selectionCanvas.addMember(selectionRadioForm);
        selectionCanvas.addMember(ruleTemplateEditCanvas);
        return selectionCanvas;
    }

    private Canvas createRuleTemplateEditorCanvas(SubscriptionTemplate template) {
        ruleTemplateEditCanvas = new VLayout();
        ruleTemplateEditCanvas.setStyleName("n52_sensorweb_client_edit_create_abo_edit");
        ruleTemplateEditCanvas.addMember(template.getTemplateContent());
        return ruleTemplateEditCanvas;
    }

    private Canvas createApplyCancelCanvas() {
        ApplyCancelButtonLayout applyCancel = new ApplyCancelButtonLayout();
        applyCancel.setStyleName("n52_sensorweb_client_create_abo_applycancel");
        applyCancel.createApplyButton(i18n.create(), i18n.create(), createApplyHandler());
        applyCancel.createCancelButton(i18n.cancel(), i18n.cancel(), createCancelHandler());
        applyCancel.setAlign(RIGHT);
        return applyCancel;
    }

    private ClickHandler createApplyHandler() {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (controller.isSelectionValid()) {
                    Rule rule = controller.createSimpleRuleFromSelection();
                    CreateSimpleRuleEvent createEvt = new CreateSimpleRuleEvent(currentSession(), rule, false, "");
                    EventBus.getMainEventBus().fireEvent(createEvt); // broker handles auto-subscribe
                    EventSubscriptionWindow.this.hide();
                } else {
                    // form validation should render error message
                    // TODO form error handling does not work yet
                	SC.warn(i18n.validateTextBoxes());
                }
            }
        };
    }
    
    private ClickHandler createCancelHandler() {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                EventSubscriptionWindow.this.hide();
            }
        };
    }

    private Canvas createContextWindowHelp() {
        HTMLPane htmlPane = new HTMLPane();
        htmlPane.setContentsURL(i18n.helpPath());
        htmlPane.setStyleName("n52_sensorweb_client_create_abo_context_help");
        htmlPane.setHeight(1050);
        return htmlPane;
    }

    public void setTimeseries(Timeseries timeseries) {
        controller.setTimeseries(timeseries);
    }

    public void updateSubscriptionEditingCanvas(SubscriptionTemplate template) {
        for (Canvas canvas : ruleTemplateEditCanvas.getMembers()) {
            canvas.removeFromParent();
            canvas.markForDestroy();
        }
        Canvas ruleEditCanvas = template.getTemplateContent();
        ruleTemplateEditCanvas.addMember(ruleEditCanvas);
        ruleTemplateEditCanvas.markForRedraw();
    }
    
    private static class EventSubsriptionWindowEventBroker implements RuleCreatedEventHandler {

        public EventSubsriptionWindowEventBroker(LoginWindow window) {
            getMainEventBus().addHandler(TYPE, this);
        }
        
        @Override
        public void onRuleCreated(RuleCreatedEvent evt) {
            Rule createdRule = evt.getCreatedRule();
            String uuid = createdRule.getUuid();
            getMainEventBus().fireEvent(new SubscribeEvent(currentSession(), uuid, "email", "Text"));
        }

    }
}