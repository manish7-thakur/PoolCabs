package com.poolcabs.ui.control;

import com.poolcabs.dao.SharedCarInfoFacade;
import com.poolcabs.dao.util.JsfUtil;
import com.poolcabs.dao.util.PaginationHelper;
import com.poolcabs.model.SharedCarInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.event.UnselectEvent;
import org.icefaces.ace.model.table.RowStateMap;

@ManagedBean(name = "sharedCarInfoController")
@ViewScoped
public class SharedCarInfoController implements Serializable {

    private SharedCarInfo current;
    private DataModel items = null;
    @EJB
    private SharedCarInfoFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<SharedCarInfo> sharedCarInfoList;
    private List<SharedCarInfo> groupList;
    private RowStateMap stateMap;
    private boolean sharedCarInfoOverviewRendered;
    private boolean sharingOverviewRendered;

    @PostConstruct
    public void init() {
        sharedCarInfoList = ejbFacade.findAll();
        groupList = new ArrayList<SharedCarInfo>();
        stateMap = new RowStateMap();
        sharedCarInfoOverviewRendered = true;
        sharingOverviewRendered = false;
    }

    public SharedCarInfo getSelected() {
        if (current == null) {
            current = new SharedCarInfo();
            selectedItemIndex = -1;
        }
        return current;
    }

    private SharedCarInfoFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (SharedCarInfo) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new SharedCarInfo();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SharedCarInfoCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (SharedCarInfo) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SharedCarInfoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (SharedCarInfo) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SharedCarInfoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public void carOwnerValueChangeListener(ValueChangeEvent event) {
        getSelected().setCarOwner((Boolean) event.getNewValue());
    }

    public void rowSelected(SelectEvent event) {
        groupList.add((SharedCarInfo) event.getObject());
    }

    public void rowUnselected(UnselectEvent event) {
        groupList.remove((SharedCarInfo) event.getObject());
    }

    public void createGroup() {
        SharedCarInfo carOwner = getCarOwnerFromGroup(groupList);
        for (SharedCarInfo info : groupList) {
            info.setClubbed(true);
            info.setSharedWith(carOwner);
            ejbFacade.edit(info);
        }
        renderSharedCarInfoOverviewRendered();
        undoSelection();
    }

    public void renderSharingForm() {
        groupList.addAll(stateMap.getSelected());
        boolean valid = validateGroup(groupList);
        if (valid) {
            sharedCarInfoOverviewRendered = false;
            sharingOverviewRendered = true;
        } else {
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SharedCarInfoInvalidGroup"));
            groupList.clear();
        }

    }
    
    public void cancelGroupCreation(){
        renderSharedCarInfoOverviewRendered();
        undoSelection();
    }

    private void renderSharedCarInfoOverviewRendered() {
        sharedCarInfoOverviewRendered = true;
        sharingOverviewRendered = false;
    }

    private void undoSelection() {
        stateMap.setAllSelected(false);
    }

    public boolean isSharedCarInfoOverviewRendered() {
        return sharedCarInfoOverviewRendered;
    }

    public void setSharedCarInfoOverviewRendered(boolean sharedCarInfoOverviewRendered) {
        this.sharedCarInfoOverviewRendered = sharedCarInfoOverviewRendered;
    }

    public boolean isSharingOverviewRendered() {
        return sharingOverviewRendered;
    }

    public void setSharingOverviewRendered(boolean sharingOverviewRendered) {
        this.sharingOverviewRendered = sharingOverviewRendered;
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public List<SharedCarInfo> getSharedCarInfoList() {
        return sharedCarInfoList;
    }

    public void setSharedCarInfoList(List<SharedCarInfo> sharedCarInfoList) {
        this.sharedCarInfoList = sharedCarInfoList;
    }

    public List<SharedCarInfo> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<SharedCarInfo> groupList) {
        this.groupList = groupList;
    }

    public RowStateMap getStateMap() {
        return stateMap;
    }

    public void setStateMap(RowStateMap stateMap) {
        this.stateMap = stateMap;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    private boolean validateGroup(List<SharedCarInfo> groupList) {
        int carOwner = 0;
        for (SharedCarInfo info : groupList) {
            if (info.isCarOwner()) {
                carOwner++;
            }
            if (info.isClubbed()) {
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SharedCarInfoInvalidGroup_alreadyClubbed"));
                return false;
            }

        }
        if (carOwner != 1) {
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SharedCarInfoInvalidGroup_moreThanOneOwner"));
            return false;
        }
        return true;

    }

    private SharedCarInfo getCarOwnerFromGroup(List<SharedCarInfo> groupList) {
        for (SharedCarInfo info : groupList) {
            if (info.isCarOwner()) {
                return info;
            }
        }
        return null;
    }

    @FacesConverter(forClass = SharedCarInfo.class)
    public static class SharedCarInfoControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SharedCarInfoController controller = (SharedCarInfoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sharedCarInfoController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof SharedCarInfo) {
                SharedCarInfo o = (SharedCarInfo) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + SharedCarInfo.class.getName());
            }
        }
    }
}
