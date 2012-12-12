/*
 * Metadata Editor
 * 
 * Metadata Editor - Rich internet application for editing metadata.
 * Copyright (C) 2011  Matous Jobanek (matous.jobanek@mzk.cz)
 * Moravian Library in Brno
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * 
 */

package cz.mzk.editor.client.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;

import cz.mzk.editor.client.LangConstants;
import cz.mzk.editor.client.config.EditorClientConfiguration;
import cz.mzk.editor.client.dispatcher.DispatchCallback;
import cz.mzk.editor.client.other.LabelAndModelConverter;
import cz.mzk.editor.client.presenter.StatisticsPresenter;
import cz.mzk.editor.client.uihandlers.StatisticsUiHandlers;
import cz.mzk.editor.client.util.Constants;
import cz.mzk.editor.client.util.HtmlCode;
import cz.mzk.editor.client.view.other.ChartsUtils;
import cz.mzk.editor.shared.domain.DigitalObjectModel;
import cz.mzk.editor.shared.domain.NamedGraphModel;
import cz.mzk.editor.shared.event.ConfigReceivedEvent;
import cz.mzk.editor.shared.event.ConfigReceivedEvent.ConfigReceivedHandler;
import cz.mzk.editor.shared.rpc.UserInfoItem;
import cz.mzk.editor.shared.rpc.action.GetUsersInfoAction;
import cz.mzk.editor.shared.rpc.action.GetUsersInfoResult;

/**
 * @author Matous Jobanek
 * @version Oct 30, 2012
 */
public class StatisticsView
        extends ViewWithUiHandlers<StatisticsUiHandlers>
        implements StatisticsPresenter.MyView {

    private final VStack mainLayout;
    private SelectItem users;
    private final LangConstants lang;
    private final DispatchAsync dispatcher;
    private final EventBus eventBus;
    private final EditorClientConfiguration config;
    private static String html = "<div id=\"%s\" style=\"position: absolute; z-index: 1000000\"> </div>";

    public static final String PIE_CHART_NESTED_DIV_ID = "pie_chart_nested_div_id";
    public static final String LINE_CHART_NESTED_DIV_ID = "line_chart_nested_div_id";
    private final HTMLFlow htmlFlow;

    public static final String years = "years";
    public static final String months = "months";
    public static final String weeks = "weeks";
    public static final String days = "days";

    private DateItem fromDate;
    private DateItem toDate;
    private ListGrid selectedUsersGrid;
    private IButton showButton;

    @Inject
    public StatisticsView(final EventBus eventBus,
                          final LangConstants lang,
                          DispatchAsync dispatcher,
                          final EditorClientConfiguration config) {
        this.mainLayout = new VStack();
        this.lang = lang;
        this.dispatcher = dispatcher;
        this.config = config;
        this.eventBus = eventBus;

        mainLayout.setWidth100();

        setSelectionLayout();

        htmlFlow = new HTMLFlow(html.replace("%s", PIE_CHART_NESTED_DIV_ID));
        htmlFlow.setWidth(500);
        htmlFlow.setHeight(300);
        mainLayout.addMember(htmlFlow);

        final String[] names = {"pesta", "jiranova", "viola", "sapakova"};
        final int[] pages = {10, 20, 80, 70};

        ChartsUtils.showChart(names, pages, htmlFlow, PIE_CHART_NESTED_DIV_ID, mainLayout, "Work");

    }

    private void setSelectionLayout() {

        HLayout selLayout = new HLayout();
        selLayout.setWidth100();
        selLayout.setHeight("25%");
        selLayout.setShowEdges(true);
        selLayout.setEdgeSize(3);
        selLayout.setEdgeOpacity(60);
        selLayout.setMargin(10);
        selLayout.setPadding(5);

        selLayout.addMember(setUserSelect());

        VLayout selectionAndCharts = new VLayout();
        selectionAndCharts.addMember(getSelObjDate());
        selectionAndCharts.addMember(getChartsChooser());
        selectionAndCharts.setShowEdges(true);
        selectionAndCharts.setEdgeSize(2);
        selectionAndCharts.setEdgeOpacity(60);
        selectionAndCharts.setPadding(5);
        selectionAndCharts.setWidth(450);
        selectionAndCharts.setExtraSpace(10);
        selectionAndCharts.setLayoutAlign(Alignment.LEFT);

        selLayout.addMember(selectionAndCharts);

        showButton = new IButton(lang.show());
        showButton.setLayoutAlign(VerticalAlignment.BOTTOM);
        showButton.setDisabled(true);

        selLayout.addMember(showButton);

        mainLayout.addMember(selLayout);
    }

    private HLayout getChartsChooser() {

        HLayout chartsChooserLayout = new HLayout();
        chartsChooserLayout.setWidth(200);

        final CheckboxItem showCharts = new CheckboxItem("showCharts", HtmlCode.bold(lang.showCharts()));
        showCharts.setDefaultValue(true);

        final SelectItem segmentation =
                new SelectItem("segmentation", HtmlCode.bold(lang.withSegmentation()));

        LinkedHashMap<String, String> segValues = new LinkedHashMap<String, String>();
        segValues.put(years, lang.years());
        segValues.put(months, lang.months());
        segValues.put(weeks, lang.weeks());
        segValues.put(days, lang.days());

        segmentation.setValueMap(segValues);
        segmentation.setDefaultValue(lang.days());
        segmentation.setWrapTitle(false);
        segmentation.setTitleStyle("");

        DynamicForm showChartsForm = new DynamicForm();
        showChartsForm.setItems(showCharts);
        showChartsForm.setExtraSpace(30);

        showCharts.addChangedHandler(new ChangedHandler() {

            @Override
            public void onChanged(ChangedEvent event) {
                if (showCharts.getValueAsBoolean()) {
                    segmentation.show();
                } else {
                    segmentation.hide();
                }

            }
        });

        DynamicForm segChartsForm = new DynamicForm();
        segChartsForm.setItems(segmentation);

        chartsChooserLayout.addMember(showChartsForm);
        chartsChooserLayout.addMember(segChartsForm);

        return chartsChooserLayout;
    }

    private void checkShowButton() {
        if (fromDate.getValueAsDate().getTime() <= toDate.getValueAsDate().getTime()
                && selectedUsersGrid.getRecords().length > 0) {
            showButton.setDisabled(false);
        } else {
            showButton.setDisabled(true);
        }
    }

    private HLayout getSelObjDate() {

        HLayout selObjDateLayout = new HLayout();
        selObjDateLayout.setWidth(400);
        selObjDateLayout.setExtraSpace(10);

        VLayout selObjLayout = new VLayout();
        DynamicForm objectAndTime = new DynamicForm();
        final SelectItem selObject = new SelectItem("selectObject");
        selObject.setShowTitle(false);
        selObject.setWrapTitle(false);
        objectAndTime.setItems(selObject);

        HTMLFlow selObjFlow = new HTMLFlow(HtmlCode.title(lang.showInsertedCount(), 3));
        selObjFlow.setWidth(250);
        selObjLayout.addMember(selObjFlow);
        selObjLayout.addMember(objectAndTime);

        VLayout selIntLayout = new VLayout();
        selIntLayout.setWidth(100);
        DynamicForm dates = new DynamicForm();
        fromDate = new DateItem("from", HtmlCode.bold(lang.from()));
        toDate = new DateItem("to", HtmlCode.bold(lang.to()));

        fromDate.addChangedHandler(new ChangedHandler() {

            @Override
            public void onChanged(ChangedEvent event) {
                checkShowButton();
            }
        });
        toDate.addChangedHandler(new ChangedHandler() {

            @Override
            public void onChanged(ChangedEvent event) {
                checkShowButton();
            }
        });

        dates.setItems(fromDate, toDate);
        dates.setWidth(150);

        HTMLFlow intevalFlow = new HTMLFlow(HtmlCode.title(lang.inInterval(), 3));
        selIntLayout.addMember(intevalFlow);
        selIntLayout.addMember(dates);

        eventBus.addHandler(ConfigReceivedEvent.getType(), new ConfigReceivedHandler() {

            @Override
            public void onConfigReceived(ConfigReceivedEvent event) {
                String[] documentTypes;
                if (event.isStatusOK()) {
                    documentTypes = config.getDocumentTypes();
                } else {
                    documentTypes = EditorClientConfiguration.Constants.DOCUMENT_DEFAULT_TYPES;
                }

                LinkedHashMap<String, String> models = new LinkedHashMap<String, String>();
                boolean isPage = false;

                for (String docType : documentTypes) {

                    try {
                        ArrayList<DigitalObjectModel> modelList = new ArrayList<DigitalObjectModel>();
                        modelList.add(DigitalObjectModel.parseString(docType));
                        LabelAndModelConverter.setLabelAndModelConverter(lang);

                        while (!modelList.isEmpty()) {
                            DigitalObjectModel lastObj = modelList.remove(modelList.size() - 1);
                            if (!isPage && lastObj == DigitalObjectModel.PAGE) isPage = true;
                            String labelModel =
                                    LabelAndModelConverter.getLabelFromModel().get(lastObj.getValue());

                            if (!models.containsKey(labelModel)) {
                                models.put(lastObj.getValue(), labelModel);
                            }

                            List<DigitalObjectModel> children = NamedGraphModel.getChildren(lastObj);
                            if (children != null) {
                                modelList.addAll(children);
                            }

                        }

                    } catch (RuntimeException e) {
                        SC.warn(lang.operationFailed() + ": " + e);
                    }
                }
                selObject.setValueMap(models);
                if (isPage)
                    selObject.setValue(LabelAndModelConverter.getLabelFromModel().get(DigitalObjectModel.PAGE
                            .getValue()));
            }
        });

        selObjDateLayout.addMember(selObjLayout);
        selObjDateLayout.addMember(selIntLayout);
        return selObjDateLayout;
    }

    private HLayout setUserSelect() {
        HLayout userLayout = new HLayout();
        userLayout.setExtraSpace(10);

        selectedUsersGrid = new ListGrid();
        selectedUsersGrid.setWidth(200);
        selectedUsersGrid.setShowAllRecords(true);
        selectedUsersGrid.setCanReorderRecords(true);
        ListGridField selectedUsersField = new ListGridField(Constants.ATTR_NAME, lang.selectedUsers());
        selectedUsersField.setCellFormatter(new CellFormatter() {

            @Override
            public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
                return record.getAttributeAsString(Constants.ATTR_NAME) + " "
                        + record.getAttribute(Constants.ATTR_SURNAME);
            }
        });
        selectedUsersGrid.setFields(selectedUsersField);

        final ListGrid usersGrid = new ListGrid();
        usersGrid.setWidth(350);
        usersGrid.setShowAllRecords(true);
        usersGrid.setSelectionType(SelectionStyle.SIMPLE);
        usersGrid.setSelectionAppearance(SelectionAppearance.CHECKBOX);
        ListGridField nameField = new ListGridField(Constants.ATTR_NAME, lang.name());
        ListGridField surnameField = new ListGridField(Constants.ATTR_SURNAME, lang.surname());
        usersGrid.setFields(nameField, surnameField);
        usersGrid.setSortField(Constants.ATTR_SURNAME);
        usersGrid.sort();

        GetUsersInfoAction getUsersAction = new GetUsersInfoAction();
        DispatchCallback<GetUsersInfoResult> usersCallback = new DispatchCallback<GetUsersInfoResult>() {

            @Override
            public void callback(GetUsersInfoResult result) {
                ListGridRecord[] allUsers = new ListGridRecord[result.getItems().size()];

                int index = 0;
                for (UserInfoItem userItem : result.getItems()) {
                    ListGridRecord user = new ListGridRecord();
                    user.setAttribute(Constants.ATTR_ID, userItem.getId());
                    user.setAttribute(Constants.ATTR_NAME, userItem.getName());
                    user.setAttribute(Constants.ATTR_SURNAME, userItem.getSurname());
                    allUsers[index++] = user;
                }
                usersGrid.setData(allUsers);
            }
        };
        dispatcher.execute(getUsersAction, usersCallback);

        VLayout selUserLayout = new VLayout();

        final DynamicForm showChartsForAllForm = new DynamicForm();
        CheckboxItem showChartsForAll =
                new CheckboxItem("showCharts", HtmlCode.bold(lang.showUnifyingCharts()));
        showChartsForAll.setDefaultValue(true);
        showChartsForAllForm.setItems(showChartsForAll);
        showChartsForAllForm.setDisabled(true);
        showChartsForAllForm.setWidth(100);

        usersGrid.addSelectionChangedHandler(new SelectionChangedHandler() {

            @Override
            public void onSelectionChanged(SelectionEvent event) {
                ListGridRecord[] selectedRecords = usersGrid.getSelectedRecords();
                selectedUsersGrid.setData(selectedRecords);
                if (selectedRecords != null && selectedRecords.length > 1)
                    showChartsForAllForm.setDisabled(false);
                checkShowButton();
            }
        });

        userLayout.addMember(usersGrid);

        selUserLayout.addMember(selectedUsersGrid);
        selUserLayout.addMember(showChartsForAllForm);

        userLayout.addMember(selUserLayout);
        userLayout.setWidth(550);
        return userLayout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Widget asWidget() {
        return mainLayout;
    }

}
