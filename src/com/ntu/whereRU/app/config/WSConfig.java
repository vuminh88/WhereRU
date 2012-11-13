
package com.ntu.whereRU.app.config;

public class WSConfig {
	
	public static final int JSON_RETURN_FORMAT = 1;
	public static final int XML_RETURN_FORMAT = 0;

    public static final String ROOT_URL = "http://10.0.2.2/WhereRUServer/RestfulWhereRU/index.php/api/";//"http://192.168.1.188/WhereRUServer/"

    // PersonList WS
    public static final String WS_PERSON_LIST_URL_XML = ROOT_URL + "personListXml.php";
    public static final String WS_PERSON_LISTs_URL_JSON = ROOT_URL + "users/search/";//+ "personListJson.php";

    // CityList WS
    public static final String WS_CITY_LIST_URL = ROOT_URL + "cityListJson.php";

    // CrudPhoneList WS
    public static final String WS_CRUD_PHONE_LIST_URL = ROOT_URL + "crud/list.php";

    public static final String WS_CRUD_PHONE_LIST_PROPERTY_USER_UDID = "userUdid";

    // CrudPhoneDelete WS
    public static final String WS_CRUD_PHONE_DELETE_URL = ROOT_URL + "crud/delete.php";

    public static final String WS_CRUD_PHONE_DELETE_PROPERTY_USER_UDID = "userUdid";
    public static final String WS_CRUD_PHONE_DELETE_PROPERTY_IDS = "ids";

    // CrudPhoneAddEdit WS
    public static final String WS_CRUD_PHONE_ADD_EDIT_URL = ROOT_URL + "crud/addedit.php";

    public static final String WS_CRUD_PHONE_ADD_EDIT_PROPERTY_USER_UDID = "userUdid";
    public static final String WS_CRUD_PHONE_ADD_EDIT_PROPERTY_ID = "id";
    public static final String WS_CRUD_PHONE_ADD_EDIT_PROPERTY_NAME = "name";
    public static final String WS_CRUD_PHONE_ADD_EDIT_PROPERTY_MANUFACTURER = "manufacturer";
    public static final String WS_CRUD_PHONE_ADD_EDIT_PROPERTY_ANDROID_VERSION = "androidVersion";
    public static final String WS_CRUD_PHONE_ADD_EDIT_PROPERTY_SCREEN_SIZE = "screenSize";
    public static final String WS_CRUD_PHONE_ADD_EDIT_PROPERTY_PRICE = "price";
}
