package com.jakub.vaadin.sportstracker;

import com.jakub.vaadin.sportstracker.domain.RunningLog;
import com.jakub.vaadin.sportstracker.service.RunningLogManager;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 */
@Theme("mytheme")
@Widgetset("com.jakub.vaadin.sportstracker.MyAppWidgetset")
@Title("SportsTracker App")
public class MyUI extends UI {
    
    private static final long serialVersionUID = 1L;
    
    final Table logsTable = new Table("Wpisy z Biegania");
    DecimalFormat df = new DecimalFormat("#");
    final Label expectedTimeLabel = new Label();

    private final RunningLogManager runningLogManager = new RunningLogManager();

    private RunningLog runningLog = new RunningLog();
    private BeanItem<RunningLog> runningLogItem = new BeanItem<>(runningLog);

    private final BeanItemContainer<RunningLog> runningLogs = new BeanItemContainer<>(
            RunningLog.class);

    enum Action {

        EDIT, ADD;
    }  

    private class LogFormWindow extends Window {

        private static final long serialVersionUID = 1L;

        private Action action;

        public LogFormWindow(Action act) {
            this.action = act;

            setModal(true);
            center();

            switch (action) {
                case ADD:
                    setCaption("Dodaj nowy wpis");
                    break;
                case EDIT:
                    setCaption("Edytuj wpis");
                    break;
                default:
                    break;
            }
            
            if(runningLogItem.getBean().getDay() == 0){
                Calendar c = Calendar.getInstance();
                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);
                int twoDigitsOfYear = c.get(Calendar.YEAR) % 100;
                runningLog = new RunningLog(dayOfWeek,weekOfYear,twoDigitsOfYear,0.0,0.0,0.0);
                runningLogItem = new BeanItem<>(new RunningLog(dayOfWeek,weekOfYear,twoDigitsOfYear,0.0,0.0,0.0));
            }
            
            
            final FormLayout form = new FormLayout();
            final FieldGroup binder = new FieldGroup(runningLogItem);
            
            final Button saveBtn = new Button();
            if(action == Action.ADD){
                saveBtn.setCaption("Dodaj Wpis");
            } else {
                saveBtn.setCaption("Edytuj Wpis");
            }
            
            final Button cancelBtn = new Button(" Anuluj ");
            
            form.addComponent(binder.buildAndBind("Dzień", "day"));
            form.addComponent(binder.buildAndBind("Tydzień", "week"));
            form.addComponent(binder.buildAndBind("Rok", "year"));
            form.addComponent(binder.buildAndBind("Dystans", "distance"));
            form.addComponent(binder.buildAndBind("Czas faktyczny", "actualTime"));
  
            binder.setBuffered(true);
            
            binder.getField("day").addValidator(new BeanValidator(RunningLog.class, "day"));
            binder.getField("week").addValidator(new BeanValidator(RunningLog.class, "week"));
            binder.getField("year").addValidator(new BeanValidator(RunningLog.class, "year"));
            binder.getField("distance").addValidator(new BeanValidator(RunningLog.class, "distance"));
            binder.getField("actualTime").addValidator(new BeanValidator(RunningLog.class, "actualTime"));

            VerticalLayout fvl = new VerticalLayout();
            fvl.setMargin(true);
            fvl.addComponent(form);

            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(saveBtn);
            hl.addComponent(cancelBtn);
            fvl.addComponent(hl);

            setContent(fvl);

            saveBtn.addClickListener(new Button.ClickListener() {

                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(ClickEvent event) {
                    try {
                        binder.commit();

                        if (action == Action.ADD) {
                            runningLogManager.addLog(runningLogItem.getBean(), (String) VaadinSession.getCurrent().getAttribute("username"));
                        } else if (action == Action.EDIT) {
                            runningLogManager.updateLog(runningLogItem.getBean());
                        }
                        
                        expectedTimeLabel.setValue("--Twój aktualny czas do pobicia to " + df.format(runningLogManager.getAverageTime()) + "min");
                        
                        runningLogs.removeAllItems();
                        runningLogs.addAll(runningLogManager.findAll());
                        //calcAverage(logsTable);
                        close();
                    } catch (FieldGroup.CommitException e) {
                        e.printStackTrace();
                    }
                }
            });

            cancelBtn.addClickListener(new Button.ClickListener() {

                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(ClickEvent event) {
                    binder.discard();
                    close();
                }
            });
        }
    }  
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        
        final VaadinSession session = VaadinSession.getCurrent();
         
        final Button addLogFormBtn = new Button("Dodaj");
        final Button deleteLogFormBtn = new Button("Usuń");
        final Button editLogFormBtn = new Button("Edytuj");
        
        final HorizontalLayout hl = new HorizontalLayout();
        
        final HorizontalLayout hlu = new HorizontalLayout();
        final TextField usernameTxtFld = new TextField();
        final Label usernameLabel = new Label();
        
        final Button loginUserBtn = new Button("Zaloguj");
        final Button logoutUserBtn = new Button("Wyloguj");

        VerticalLayout vl = new VerticalLayout();
        setContent(vl);
            
        runningLogs.addAll(runningLogManager.findAll());
        
        addLogFormBtn.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                addWindow(new LogFormWindow(Action.ADD));
            }
        });

        editLogFormBtn.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;
            
            @Override
            public void buttonClick(ClickEvent event) {
                addWindow(new LogFormWindow(Action.EDIT));
            }
        });

        deleteLogFormBtn.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                runningLogManager.delete(runningLog);
                runningLogs.removeAllItems();
                runningLogs.addAll(runningLogManager.findAll()); 
                if(runningLogManager.getAverageTime() != 0.0){
                    expectedTimeLabel.setValue("--Twój aktualny czas do pobicia to " + df.format(runningLogManager.getAverageTime()));
                }
            }
        });
        
        loginUserBtn.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                String username = (String) usernameTxtFld.getValue();
                session.setAttribute("username", username);
                usernameLabel.setValue("Zalogowałeś się jako " + username);
                             
                hlu.removeAllComponents();
                hlu.addComponent(usernameLabel);
                if(runningLogManager.getAverageTime() != 0.0){
                    expectedTimeLabel.setValue("--Twój aktualny czas do pobicia to " + df.format(runningLogManager.getAverageTime()));
                }     
                hlu.addComponent(expectedTimeLabel);
                hl.addComponent(addLogFormBtn);
                hl.addComponent(editLogFormBtn);
                hl.addComponent(deleteLogFormBtn);
                hl.addComponent(logoutUserBtn);
            }
        });
        
        logoutUserBtn.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                session.setAttribute("username", null);
                hlu.removeAllComponents();
                hl.removeAllComponents();
                hlu.addComponent(usernameTxtFld);
                hlu.addComponent(loginUserBtn);
            }
        });
        
        if (session.getAttribute("username") != null) {
            usernameLabel.setValue("Zalogowałeś się jako " + session.getAttribute("username"));
            hlu.addComponent(usernameLabel);
            hl.addComponent(addLogFormBtn);
            hl.addComponent(editLogFormBtn);
            hl.addComponent(deleteLogFormBtn);
        } else {
            hlu.addComponent(usernameTxtFld);
            hlu.addComponent(loginUserBtn);
        }

        LinkedList columnsVisible = new LinkedList();
        columnsVisible.add("username");
        columnsVisible.add("day");
        columnsVisible.add("week");
        columnsVisible.add("year");
        columnsVisible.add("distance");
        columnsVisible.add("actualTime");
        columnsVisible.add("expectedTime");
        logsTable.setContainerDataSource(runningLogs, columnsVisible);
        
        logsTable.addGeneratedColumn("surpassed", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Table source, Object itemId, Object columnId) {
                Item i = source.getItem(itemId);
                Property actualTime = i.getItemProperty("actualTime");
                Property expectedTime = i.getItemProperty("expectedTime");

                Label label = new Label();
                if ((Double) (expectedTime.getValue()) > (Double) (actualTime.getValue())) {
                    label.setValue("Tak");
                } else {
                    label.setValue("Nie");
                }

                label.addStyleName("column-type-value");
                label.addStyleName("column-" + (String) columnId);
                return label;
            }
        });
        logsTable.setColumnHeader("username", "Biegacz");
        logsTable.setColumnHeader("day", "Dzień");
        logsTable.setColumnHeader("week", "Tydzień");
        logsTable.setColumnHeader("year", "Rok");
        logsTable.setColumnHeader("distance", "Dystans");
        logsTable.setColumnHeader("actualTime", "Faktyczny czas");
        logsTable.setColumnHeader("expectedTime", "Oczekiwany czas");
        logsTable.setColumnHeader("surpassed", "Czas pokonany");     
        logsTable.setSelectable(true);
        logsTable.setImmediate(true);
        logsTable.setColumnReorderingAllowed(true);
        logsTable.setColumnCollapsingAllowed(true);
               
        logsTable.addValueChangeListener(new Property.ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(Property.ValueChangeEvent event) {

                RunningLog selectedLog = (RunningLog) logsTable.getValue();
                if (selectedLog == null) {            
                    runningLog.setDay(0);
                    runningLog.setWeek(0);
                    runningLog.setYear(0);
                    runningLog.setDistance(0);
                    runningLog.setActualTime(0.0);
                    runningLog.setExpectedTime(0.0);
                    runningLog.setUsername(null);
                    runningLog.setId(null);

                } else {
                    runningLog.setDay(selectedLog.getDay());
                    runningLog.setWeek(selectedLog.getWeek());
                    runningLog.setYear(selectedLog.getYear());
                    runningLog.setDistance(selectedLog.getDistance());
                    runningLog.setActualTime(selectedLog.getActualTime());
                    runningLog.setExpectedTime(selectedLog.getExpectedTime());
                    runningLog.setUsername(selectedLog.getUsername());
                    runningLog.setId(selectedLog.getId());
                }
                
            }

        });
        
        vl.addComponent(hlu);

        vl.addComponent(hl);

        vl.addComponent(logsTable);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Label label = new Label();

        horizontalLayout.addComponent(label);

        label.setValue(UI.getCurrent().toString());

        vl.addComponent(horizontalLayout);             
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
