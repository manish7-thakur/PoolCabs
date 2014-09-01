package com.poolcabs.ui.control;

import com.poolcabs.dao.RegularBookingFacade;
import com.poolcabs.dao.util.JsfUtil;
import com.poolcabs.dao.util.PaginationHelper;
import com.poolcabs.model.CabStatus;
import com.poolcabs.model.RegularBooking;
import com.poolcabs.model.RegularBookingType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

@ManagedBean(name = "regularBookingController")
@ViewScoped
public class RegularBookingController implements Serializable {

    private RegularBooking current;
    private DataModel items = null;
    @EJB
    private RegularBookingFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<RegularBooking> bookingList;
    private boolean listFormRendered;
    private boolean editFormRendered;
    private RowStateMap rowStateMap;

    public RegularBookingController() {
    }

    @PostConstruct
    public void init() {
        bookingList = ejbFacade.findAll();
        listFormRendered = true;
        editFormRendered = false;
    }

    public RegularBooking getSelected() {
        if (current == null) {
            current = new RegularBooking();
        }
        return current;
    }

    private RegularBookingFacade getFacade() {
        return ejbFacade;
    }

    public List<CabStatus> cabStatuses() {
        return CabStatus.list();
    }

    public List<RegularBookingType> regularBookingTypes() {
        return RegularBookingType.list();
    }

    public List<RegularBooking> getBookingList() {
        return bookingList;
    }

    public void setBookingList(List<RegularBooking> bookingList) {
        this.bookingList = bookingList;
    }

    public boolean isListFormRendered() {
        return listFormRendered;
    }

    public void setListFormRendered(boolean listFormRendered) {
        this.listFormRendered = listFormRendered;
    }

    public boolean isEditFormRendered() {
        return editFormRendered;
    }

    public void setEditFormRendered(boolean editFormRendered) {
        this.editFormRendered = editFormRendered;
    }

    public RowStateMap getRowStateMap() {
        return rowStateMap;
    }

    public void setRowStateMap(RowStateMap rowStateMap) {
        this.rowStateMap = rowStateMap;
    }

    public void rowSelectListener(SelectEvent event) {
        current = (RegularBooking) event.getObject();
    }

    public void editBooking() {
        listFormRendered = false;
        editFormRendered = true;
    }

    private void renderListForm() {
        listFormRendered = true;
        editFormRendered = false;

    }

    public void create() {
        try {
            getSelected().setCreateDate(new Date());
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RegularBookingCreated"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public String prepareEdit() {
        // current = (RegularBooking) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public void update() {
        try {
            RegularBooking updatedBooking = getFacade().edit(current);
            bookingList.set(bookingList.indexOf(current), updatedBooking);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RegularBookingUpdated"));
            undoSelection();
            renderListForm();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void undoSelection() {
        rowStateMap.setAllSelected(false);
    }

    public String destroy() {
        //current = (RegularBooking) getItems().getRowData();
        // selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        //updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RegularBookingDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }
}
